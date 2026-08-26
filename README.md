# utilities

A utilities library for Minecraft Paper plugins, published as separate domain artifacts:

| Gradle project | Artifact | Package |
| --- | --- | --- |
| `:common` | `org.aincraft:common` | `org.aincraft.common` |
| `:config` | `org.aincraft:config` | `org.aincraft.config` |
| `:db-core` | `org.aincraft:db-core` | `org.aincraft.db` |
| `:db-paper` | `org.aincraft:db-paper` | `org.aincraft.db.paper` |
| `:math` | `org.aincraft:math` | `org.aincraft.math` |
| `:registry` | `org.aincraft:registry` | `org.aincraft.registry` |

`common`, `db-core`, and `math` compile without Paper. `config`, `db-paper`, and `registry` take Paper as `compileOnly`. Depend on only the domains you use; `db-paper` pulls `db-core` transitively.

## Runtime Requirements

- **JDBC drivers are NOT bundled.** SQLite (`org.sqlite.JDBC`) and MySQL drivers are provided by Paper's server runtime (versions vary by server release). To pin a specific driver version, use `plugin.yml` `libraries:`.
- The PostgreSQL runtime driver requirement is not guaranteed by this library; the runtime or environment must provide it.
- **Schema initialization is opt-in:** the Paper factory executes the SQL file named by `schema-file` (loaded from the plugin jar via `plugin.getResource(path)`).
- The `org.aincraft.db` core is Paper-free (`ConnectionSource`, `SQLiteConnectionSource`, `HikariSourceImpl`, `MongoConnectionSourceImpl`).
- The `org.aincraft.db.paper` package provides the Bukkit/Paper adapter (`ConnectionSourceFactory`).
