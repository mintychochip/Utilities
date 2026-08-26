# DB Core Split + SQLite Fix — Design

Date: 2026-08-26
Status: Approved (implementation not started)
Version bump: 2.0.0 (clean break)

## Context

`utilities` is a runtime-bundled utility library for Minecraft Paper plugins
(shadow-jar consumers). Audit findings that drive this work:

1. `SQLiteSourceImpl.create()` always calls `DatabaseType.getTables()`, which
   reads `<identifier>.sql` from the **library's own classloader**. No `.sql`
   resources ship in the jar, so every SQLite source creation throws
   `FileNotFoundException` at runtime. (MySQL/Postgres branch never calls it.)
2. `SQLiteSourceImpl.close()` is empty and `closed()` always false — the
   `ConnectionSource` lifecycle contract is silently unimplemented.
3. `SQLiteSourceImpl` is package-private with a private constructor + no public
   factory — unreachable from consumers.
4. No JDBC drivers are declared in Gradle. This is correct by design: **Paper
   bundles `sqlite-jdbc` and `mysql-connector-j` in the server runtime**
   (Paper "Using databases" docs). A published library must not assume
   server-internal classpaths are a contract — document it and verify against
   real Paper.
5. `ConnectionSourceFactory` is a dynamic Java Proxy dispatching between two
   package-private factories. Works, but untestable and opaque.
6. Local Gradle 8.10 wrapper cannot run on JDK 25 (`25.0.3` configuration
   error, `EXIT=1`). CI runs JDK 21 already.

## Goals

- Make SQLite source creation work: real Hikari pool, real close/closed,
  public construction from `org.aincraft.db` core.
- Make the core **Paper-free**: no `org.bukkit` imports in `org.aincraft.db`.
  (Not dependency-free: Hikari + Mongo driver remain external deps.)
- Opt-in schema initialization, loaded through the caller's classpath
  (`plugin.getResource` in the adapter), executed explicitly. Remove the
  competing fixed-name classpath schema model.
- Clean package move to `org.aincraft.db` / `org.aincraft.db.paper` (2.0.0).
- Verify against real Paper runtime (`runServer`) for driver discovery.
- Keep bundle weight unchanged: no new runtime (`implementation`) deps.

## Architecture

```
org.aincraft.db               core — NO org.bukkit imports
  ConnectionSource            (moved) incl. SQLConnectionSource, MongoConnectionSource
  DatabaseType                (moved; getTables() removed)
  ConnectionException         (moved)
  HikariSourceImpl            (moved, public already)
  MongoConnectionSourceImpl   (moved, public; constructor takes MongoClient)
  SQLiteConnectionSource      (new, replaces package-private SQLiteSourceImpl)

org.aincraft.db.paper         Bukkit/Paper adapter
  ConnectionSourceFactory     (moved, same public contract)
  ConnectionSourceFactoryImpl (new concrete class, replaces Proxy dispatch;
                               absorbs both factory helpers, which are deleted)
```

## Core: SQLiteConnectionSource (public, new)

```java
public final class SQLiteConnectionSource implements ConnectionSource.SQLConnectionSource {
  public static SQLiteConnectionSource create(Path databaseFile);
  public static SQLiteConnectionSource create(Path databaseFile, InputStream schema);
  // getConnection(), close(), closed(), getType() == SQLITE
}
```

- Hikari pool (`HikariDataSource`) over `jdbc:sqlite:<absolute path>`.
- `Class.forName("org.sqlite.JDBC")` before pool construction; on
  `ClassNotFoundException` throw `ConnectionException` explaining the runtime
  must provide the SQLite driver.
- `close()` → `dataSource.close()`; `closed()` → `dataSource.isClosed()`.
- Schema execution (both create overloads with schema):
  - `connection.setAutoCommit(false)`, `setSavepoint()`, execute statements
    split on `;` (filter empties), `commit()`; on `SQLException` rollback to
    savepoint then rethrow as `ConnectionException`.
  - **Caller owns the InputStream; the library never closes it.** Documented.
- No `plugin` reference anywhere; file path is explicit.

## Core: removals

- `DatabaseType.getTables()` — removed (fixed-name classpath schema model;
  no callers remain after this work).
- `ResourceExtractor` — removed (was only used by `getTables()`).
- `ConnectionException` — add `ConnectionException(String, Throwable)` so
  driver/schema failures preserve their root cause.

## Adapter: ConnectionSourceFactoryImpl (concrete, replaces Proxy)

- Same interface: `ConnectionSource create(DatabaseType, ConfigurationSection)`.
- MYSQL/POSTGRES → `new HikariSourceImpl(new HikariDataSource(parseHikariConfig(config)), type)`
  (same keys: jdbc-url, username, password, maximum-pool-size, minimum-idle,
  connection-timeout, idle-timeout, max-lifetime).
- SQLITE:
  - `path` key (required) → `plugin.getDataFolder().toPath().resolve(path)`;
    parent dirs created.
  - optional `schema-file` key → `plugin.getResource(schemaFile)` (Bukkit
    resource API); `null` → `IllegalArgumentException("schema resource not
    found: <path>")`. If key absent, no schema init.
  - delegates to `SQLiteConnectionSource.create(file, stream)`.
- MONGO → `MongoConnectionSourceImpl(MongoClients.create(parseClientSettings(config)))`
  (same keys: connection-uri | host/port/username/password/auth-database).
- No Proxy.

## Verification

- utilities `testImplementation` only: `org.junit.jupiter:junit-jupiter` +
  `org.xerial:sqlite-jdbc` (test scope, never shipped).
  - pool round-trip (insert/select),
  - close/closed transitions,
  - schema init from InputStream (multi-statement),
  - rollback on bad schema,
  - missing-driver error path (simulate absent driver class).
- test-plugin MockBukkit: factory with `path` + `schema-file` (resource in
  test resources), missing schema-file error, no-schema creation.
- Paper smoke test via `runServer` 1.21.4: TestPlugin creates SQLite source
  with schema, round-trips a row, closes. Proves driver discovery on real
  Paper runtime.

## Build/runtime

- No new `implementation` dependencies. `testImplementation` additions only.
- Drivers come from the server runtime. Paper bundles sqlite/mysql; versions
  vary by server release. Consumers pin versions via `plugin.yml` `libraries:`.
  The Postgres runtime driver requirement is not guaranteed by this library;
  require the runtime/environment to provide it. State in javadoc on the
  factory and in README "Runtime requirements".
- Local wrapper in this workspace runs on JDK 21 via machine-local Gradle
  property (`org.gradle.java.home`) — not committed. CI untouched (already 21).
- `gradle.properties` version → `2.0.0`.

## Out of scope (follow-ups)

- `FolderYamlConfigurationImpl` / `isFolder()` unfinished config API.
- `UtilAccessor`, `Utils` dead surface.
- Mongo module split (bundles ~2.4 MB for all consumers today).
