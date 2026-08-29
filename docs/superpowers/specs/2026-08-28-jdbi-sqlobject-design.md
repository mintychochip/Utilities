# Jdbi SQL Object DAO Layer — Design

Date: 2026-08-28  
Status: Approved

## Context

`:utilities-db-sql` already owns the database integration boundary. `SqlDatabase` creates a HikariCP pool, exposes a Jdbi instance, configures Flyway, inspects JDBC capabilities, and closes the pool. Consumers currently have to construct DAO behavior directly through `database.jdbi()`.

HikariCP is a connection pool, not a data-access abstraction. Jdbi is already the repository's SQL-first data-access layer and explicitly avoids ORM session state and hidden schema behavior. Its SQL Object extension provides declarative DAO interfaces while preserving handwritten SQL and direct Jdbi access.

## Goals

- Bundle Jdbi's SQL Object extension with `:utilities-db-sql`.
- Install the extension automatically for every `SqlDatabase` instance.
- Provide concise typed DAO access for ordinary plugin code.
- Provide typed transaction helpers that attach a DAO to one managed transaction handle.
- Preserve `jdbi()` as an escape hatch for advanced queries, custom mappers, streaming, and Jdbi features not represented by the façade.
- Keep Flyway migration execution explicit and Hikari implementation-private.
- Verify DAO mapping, commit, rollback, lifecycle, and existing SQL behavior against SQLite.
- Document the supported DAO lifecycle and the limitation on streaming results from on-demand DAOs.

## Non-goals

- No Hibernate/JPA or other full ORM.
- No generic repository, CRUD generator, entity scanner, or schema introspection.
- No custom connection pool, transaction manager, or exception hierarchy.
- No automatic migration execution.
- No new Gradle module or platform-specific dependency.
- No promise that on-demand DAO methods can return lazy streams after the method exits.

## Options considered

### Install SQL Object only

Install `SqlObjectPlugin` and leave all access through `database.jdbi()`. This is the smallest API, but every consumer repeats the same extension and transaction plumbing.

### Jdbi SQL Object façade — selected

Add `jdbi3-sqlobject`, install its plugin, and expose typed DAO and transaction helpers from `SqlDatabase`. This is a thin layer over the existing stack, keeps SQL visible, adds no entity lifecycle, and retains the raw Jdbi escape hatch.

### jOOQ or Hibernate

jOOQ offers a stronger generated SQL type system but requires schema code generation and adds a larger build/runtime surface. Hibernate/JPA adds session state, entity lifecycle, proxies, and ORM configuration that are disproportionate for a reusable Minecraft plugin utility module. Neither is selected.

## Architecture

The existing `:utilities-db-sql` artifact remains the only changed runtime module.

### Dependencies

Add a `jdbi-sqlobject` version-catalog alias for `org.jdbi:jdbi3-sqlobject`, aligned to the existing `jdbi` version (`3.54.0`). Declare it as an `api` dependency of `:utilities-db-sql`, alongside `jdbi3-core`, because consumer DAO interfaces use SQL Object annotations and types at compile time.

No JDBC driver is added to the published artifact. The existing test-only SQLite driver remains test-only.

### `SqlDatabase` creation

After constructing the Jdbi instance, install `new SqlObjectPlugin()` before the instance is returned. The creation sequence remains:

```text
HikariConfig
  -> HikariDataSource
  -> Jdbi.create(dataSource)
  -> install SqlObjectPlugin
  -> inspect JDBC capabilities
  -> configure Flyway
  -> SqlDatabase
```

If Jdbi setup, capability inspection, or Flyway setup fails, the existing `try/catch` closes the data source before rethrowing.

### Public API

Add these methods to `org.aincraft.db.sql.SqlDatabase`:

```java
public <D> D onDemand(Class<D> daoType);

public <D> void useTransaction(
    Class<D> daoType, Consumer<? super D> callback);

public <D, R> R inTransaction(
    Class<D> daoType, Function<? super D, ? extends R> callback);
```

All arguments are required and are checked with `Objects.requireNonNull`. `onDemand` delegates to `Jdbi#onDemand`, returning Jdbi's reusable, thread-safe extension proxy. The transaction methods use Jdbi's managed transaction callback and `Handle#attach(daoType)`, ensuring every DAO call inside the callback shares the same handle and transaction.

The raw `jdbi()` method remains unchanged. `capabilities()`, `migrate()`, `validate()`, `closed()`, and `close()` retain their current contracts.

## Runtime behavior

### On-demand DAO calls

An on-demand DAO obtains and releases a handle for each method invocation. It is appropriate for independent reads and writes:

```java
public interface UserDao {
  @SqlQuery("select id, name from users where id = :id")
  @RegisterConstructorMapper(User.class)
  Optional<User> findById(@Bind("id") long id);

  @SqlUpdate("insert into users (id, name) values (:id, :name)")
  void insert(@Bind("id") long id, @Bind("name") String name);
}

UserDao users = database.onDemand(UserDao.class);
users.insert(1, "Ada");
Optional<User> user = users.findById(1);
```

The SQL Object interface and its mappings belong to the consumer. The utility library does not infer table names or generate SQL.

### Typed transactions

The transaction helpers attach the DAO to Jdbi's callback-managed handle:

```java
database.useTransaction(UserDao.class, users -> {
  users.insert(1, "Ada");
  users.insert(2, "Grace");
});
```

Successful callbacks commit. Runtime failures from the callback or DAO cause Jdbi to roll back and propagate the original failure. `inTransaction` additionally returns the callback result. Nested transaction behavior follows Jdbi's existing transaction semantics; the façade does not implement a second transaction state machine.

### Streaming and handle-bound results

Consumers must not use an on-demand DAO for a lazy `Stream`, `ResultIterator`, or other handle-bound result that outlives the DAO method. The on-demand handle is closed when the method returns. For streaming, consumers use the raw escape hatch and attach a DAO to a handle whose lifecycle they manage:

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

## Error handling and lifecycle

- Null database configuration and migration locations retain the existing validation behavior.
- Null DAO types and callbacks fail immediately with `NullPointerException` and a parameter-specific message.
- Invalid DAO definitions, SQL errors, mapping errors, and transaction failures remain Jdbi runtime failures; the façade does not obscure their causes.
- DAO creation and transaction setup after `SqlDatabase.close()` are not given a custom error contract; Hikari/Jdbi remains the source of the failure.
- `SqlDatabase.close()` remains the owner of the pool lifecycle. The façade does not cache or close DAO proxies separately.

## Verification

Extend `SqlDatabaseTest` with a small annotated DAO and use the existing SQLite migration setup:

1. An on-demand DAO inserts and reads a mapped value.
2. `useTransaction` commits multiple DAO writes.
3. `useTransaction` rolls back all writes when the callback throws.
4. `inTransaction` returns a callback result.
5. Existing raw Jdbi query/transaction tests, Flyway migration, capability inspection, and pool close tests remain valid.

Run the module's test task and the repository's normal verification task after implementation. The README must include the dependency's role, a DAO example, and the on-demand versus attached-handle streaming rule.

## External references

- [Jdbi 3 Developer Guide](https://jdbi.org/)
- [Jdbi 3 release 3.54.0 documentation](https://jdbi.org/releases/3.54.0)
- [Jdbi SQL Object API](https://jdbi.org/apidocs/org/jdbi/v3/sqlobject/SqlObjectPlugin.html)
