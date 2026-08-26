# utilities

A utilities library for Minecraft Paper plugins.

## Runtime Requirements

- **JDBC drivers are NOT bundled.** SQLite (`org.sqlite.JDBC`) and MySQL drivers are provided by Paper's server runtime (versions vary by server release). To pin a specific driver version, use `plugin.yml` `libraries:`.
- The PostgreSQL runtime driver requirement is not guaranteed by this library; the runtime or environment must provide it.
- **Schema initialization is opt-in:** the Paper factory executes the SQL file named by `schema-file` (loaded from the plugin jar via `plugin.getResource(path)`).
- The `org.aincraft.db` core is Paper-free (`ConnectionSource`, `SQLiteConnectionSource`, `HikariSourceImpl`, `MongoConnectionSourceImpl`).
- The `org.aincraft.db.paper` package provides the Bukkit/Paper adapter (`ConnectionSourceFactory`).
