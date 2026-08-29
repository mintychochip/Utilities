# utilities

A utilities library for Minecraft Paper plugins, published as tiered runtime artifacts:

| Gradle project | Artifact | Package | Purpose |
| `:utilities-api` | `org.aincraft:utilities-api` | `org.aincraft.api.domain` | Platform-agnostic API interfaces and contracts |
| `:utilities-common` | `org.aincraft:utilities-common` | `org.aincraft.event`, `org.aincraft.math`, `org.aincraft.ui.scoreboard` | Shared event, domain-agnostic, and scoreboard UI utilities |
| `:utilities-db-sql` | `org.aincraft:utilities-db-sql` | `org.aincraft.db.sql` | Typed Jdbi SQL Object DAOs, Jdbi SQL access, HikariCP pooling, and Flyway migrations |
| `:utilities-bukkit` | `org.aincraft:utilities-bukkit` | `org.aincraft.bukkit`, `org.aincraft.config`, `org.aincraft.registry` | Bukkit/Spigot runtime adapter |
| `:utilities-paper` | `org.aincraft:utilities-paper` | `org.aincraft.paper` | Paper runtime adapter |
| `:utilities-minestom` | `org.aincraft:utilities-minestom` | `org.aincraft.minestom` | Minestom runtime adapter |
| `:utilities-bom` | `org.aincraft:utilities-bom` | — | Bill of Materials for version alignment |

Runtime adapters (`:utilities-bukkit`, `:utilities-paper`, `:utilities-minestom`) depend on `:utilities-common`, which in turn depends on `:utilities-api` for the platform-neutral contracts. SQL utilities are isolated in `:utilities-db-sql` so platform and API consumers do not inherit database dependencies.

## Scoreboard API and UI

The scoreboard contracts live in `:utilities-api` under
`org.aincraft.api.domain.scoreboard`. `server.scoreboardManager()` exposes the platform
scoreboard manager, and `player.scoreboard()` reads or assigns a player's current scoreboard.
The Bukkit and Paper modules adapt those contracts to their native scoreboard APIs. Paper
preserves Adventure components for objective and team text; the Bukkit adapter converts them to
legacy text for platforms that only expose legacy scoreboard methods.

`:utilities-common` provides `ScoreboardController` and immutable sidebar layouts. The controller
creates one private scoreboard per viewer, supports at most 15 lines, keeps line IDs stable while
layouts change, and restores each player's previous scoreboard when hidden or closed. It does not
schedule updates; call it from the platform's valid server thread.

```java
import net.kyori.adventure.text.Component;
import org.aincraft.api.domain.entity.Player;
import org.aincraft.api.domain.server.Server;
import org.aincraft.ui.scoreboard.ScoreboardController;
import org.aincraft.ui.scoreboard.ScoreboardLayout;

ScoreboardController sidebar =
    new ScoreboardController(server.scoreboardManager(), "lobby_sidebar");

sidebar.show(
    player,
    ScoreboardLayout.builder(Component.text("Lobby"))
        .line("online", Component.text("Online: " + onlinePlayers))
        .line("rank", Component.text("Rank: " + rank))
        .build());

sidebar.refresh(
    player,
    viewer -> ScoreboardLayout.builder(Component.text("Lobby"))
        .line("online", Component.text("Online: " + onlinePlayers(viewer)))
        .build());

sidebar.hide(player);
sidebar.close();
```

Use a stable, non-blank ID for each logical line. Reordering or changing a line with the same ID
updates its existing native entry instead of allocating a new one. `ScoreboardLayout` defensively
copies its lines and rejects duplicate IDs or more than 15 lines. There is no Minestom scoreboard
adapter in this first portable API cut.

## Runtime Requirements

- `:utilities-db-sql` uses Jdbi, HikariCP, and Flyway for SQL databases.
- JDBC drivers are not bundled by the SQL module. Consumers must provide the driver for their configured database.
- Schema migration is explicit: call `SqlDatabase.migrate()` after creating the database when migrations are desired.
- With no custom migration location, Flyway uses `classpath:db/migration`.
- `SqlDatabase.capabilities()` reports runtime JDBC support for transactions, savepoints, batch updates, and transaction isolation levels.

## Publishing

Artifacts are published to GitHub Packages under `org.aincraft`:
`https://maven.pkg.github.com/mintychochip/Utilities`. CI publishes
automatically when a `vYYYY.MM.DD` (or bare `YYYY.MM.DD`) tag is pushed, or
manually via the *Release* workflow with the `version` input and the
`publish` checkbox.

Local publish from a checkout:

```bash
GITHUB_ACTOR=<user> GITHUB_TOKEN=<token> ./gradlew clean check publish -Pversion=2026.08.28
```

requires a GitHub token with `packages:write` scope, or
`gpr.user`/`gpr.key` in `~/.gradle/gradle.properties`. `publish` uploads to
GitHub Packages while `publishToMavenLocal` installs into the local Maven
repository.

Consumers add the repository (see the Bags build for the exclusive-content
pattern):

```kotlin
maven {
    url = uri("https://maven.pkg.github.com/mintychochip/Utilities")
    credentials {
        username = ... // gpr.user or GITHUB_ACTOR
        password = ... // gpr.key or GITHUB_TOKEN
    }
}
```

## SQL Object DAOs

Define SQL Object DAOs in the consumer application. SQL Object annotations and SQL remain
consumer-owned:

```java
public interface UserDao {
  @SqlQuery("SELECT value FROM users WHERE id = :id")
  String findValue(@Bind("id") long id);

  @SqlUpdate("INSERT INTO users (id, value) VALUES (:id, :value)")
  void insert(@Bind("id") long id, @Bind("value") String value);
}

try (SqlDatabase database = SqlDatabase.create(config)) {
  database.migrate();
  UserDao users = database.onDemand(UserDao.class);
  users.insert(1, "Ada");

  database.useTransaction(
      UserDao.class,
      dao -> {
        dao.insert(2, "Grace");
        dao.insert(3, "Katherine");
      });
}
```

`onDemand` manages one handle per DAO method. `useTransaction` and `inTransaction` attach the
DAO to one Jdbi-managed transaction, with typed transaction helpers for transaction-scoped DAO
work. Call `migrate()` explicitly when applying Flyway migrations; it is not run automatically.

### Streaming queries

Lazy streams and iterators must not be returned from an on-demand method and consumed after that
method returns; use `jdbi().open()` and `Handle#attach` instead. The attached handle must remain
open while values are consumed.

```java
public interface StreamingUserDao {
  @SqlQuery("SELECT value FROM users ORDER BY id")
  Stream<String> streamValues();
}

try (Handle handle = database.jdbi().open()) {
  StreamingUserDao users = handle.attach(StreamingUserDao.class);
  try (Stream<String> stream = users.streamValues()) {
    stream.forEach(System.out::println);
  }
}
```

## Cross-platform plugin lifecycle

Implement plugin logic once with `org.aincraft.api.plugin.PluginLifecycle`:

```java
public final class MyBukkitPlugin extends BukkitPluginEntrypoint {
  @Override
  protected PluginLifecycle createLifecycle() {
    return new SharedLifecycle();
  }
}

public final class MyPaperPlugin extends PaperPluginEntrypoint {
  @Override
  protected PluginLifecycle createLifecycle() {
    return new SharedLifecycle();
  }
}
```

Both entrypoints supply `onLoad`, `onEnable`, and `onDisable` through the shared lifecycle
controller. The callbacks receive a platform-neutral `PluginContext` containing the plugin name,
data directory, and logger.

Minestom has no `JavaPlugin` base class, so drive the same lifecycle explicitly:

```java
var plugin = new MinestomPluginEntrypoint(new SharedLifecycle());
plugin.onEnable(); // loads first when needed
// ...
plugin.onDisable();
```