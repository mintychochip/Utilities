# Jdbi SQL Object DAO Layer Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a lightweight Jdbi SQL Object DAO and transaction façade to `utilities-db-sql` while preserving direct Jdbi, HikariCP, and Flyway access.

**Architecture:** Keep the existing `SqlDatabase` as the lifecycle owner. Add Jdbi's SQL Object module and install `SqlObjectPlugin` during database creation, then delegate typed on-demand DAO calls and callback-managed transactions to Jdbi without adding ORM or repository abstractions.

**Tech Stack:** Java 25, Gradle Kotlin DSL, HikariCP 5.0.1, Jdbi 3.54.0, Flyway 11.19.1, SQLite JDBC test fixture, JUnit 5.

**Spec:** `docs/superpowers/specs/2026-08-28-jdbi-sqlobject-design.md`

## Global Constraints

- Change only the existing `:utilities-db-sql` runtime boundary plus its version-catalog entry, tests, and README documentation.
- Use `org.jdbi:jdbi3-sqlobject` at the existing Jdbi version `3.54.0`.
- Keep `jdbi3-sqlobject` as an `api` dependency because consumers compile DAO annotations against it.
- Do not add JDBC drivers to published runtime dependencies; SQLite remains test-only.
- Keep Flyway migration execution explicit; do not call `migrate()` from `SqlDatabase.create()`.
- Keep Hikari implementation-private and retain `jdbi()` as the advanced-use escape hatch.
- DAO methods own SQL and mapping; do not add generic repositories, entity scanning, CRUD generation, or ORM state.
- On-demand DAOs manage a handle per method; streaming requires a caller-managed attached handle.
- Every implementation task must use focused tests or compile checks and skip formatters, linters, and project-wide verification until the final verification task.

## File Map

- Modify `gradle/libs.versions.toml`: add the aligned `jdbi3-sqlobject` dependency alias.
- Modify `utilities-db-sql/build.gradle.kts`: expose the SQL Object module transitively from the SQL utility artifact.
- Modify `utilities-db-sql/src/main/java/org/aincraft/db/sql/SqlDatabase.java`: install the SQL Object plugin and add typed DAO/transaction delegates.
- Modify `utilities-db-sql/src/test/java/org/aincraft/db/sql/SqlDatabaseTest.java`: exercise on-demand DAOs, typed transaction commit/rollback/results, and null argument validation using the existing SQLite fixture.
- Modify `README.md`: document the SQL Object dependency, DAO usage, typed transactions, and streaming handle lifetime.

---

### Task 1: Add SQL Object dependency and plugin setup

**Files:**
- Modify: `gradle/libs.versions.toml:20-27`
- Modify: `utilities-db-sql/build.gradle.kts:22-27`
- Modify: `utilities-db-sql/src/main/java/org/aincraft/db/sql/SqlDatabase.java:3-7,54-63`

**Interfaces:**
- Consumes: the existing `jdbi` version catalog entry and `SqlDatabase.create(HikariConfig, String...)` creation path.
- Produces: a `SqlDatabase` whose Jdbi instance has `SqlObjectPlugin` installed before capability inspection and Flyway setup; the public dependency alias `libs.jdbi.sqlobject`.

- [ ] **Step 1: Add the aligned version-catalog alias**

Add this entry immediately after `jdbi-core` in `gradle/libs.versions.toml`:

```toml
jdbi-sqlobject = { module = "org.jdbi:jdbi3-sqlobject", version.ref = "jdbi" }
```

- [ ] **Step 2: Expose the SQL Object module from the SQL artifact**

Add this dependency immediately after `api(libs.jdbi.core)` in `utilities-db-sql/build.gradle.kts`:

```kotlin
api(libs.jdbi.sqlobject)
```

Keep `libs.flyway.core` as `implementation` and the SQLite driver as `testImplementation`.

- [ ] **Step 3: Install the plugin during database creation**

Import `org.jdbi.v3.sqlobject.SqlObjectPlugin`, then change the Jdbi creation block in `SqlDatabase.create` to install the plugin before capability inspection:

```java
Jdbi jdbi = Jdbi.create(dataSource);
jdbi.installPlugin(new SqlObjectPlugin());
SqlCapabilities capabilities = inspectCapabilities(jdbi);
```

Do not change the existing pool cleanup or Flyway configuration logic.

- [ ] **Step 4: Compile the SQL module**

Run:

```bash
./gradlew :utilities-db-sql:compileJava
```

Expected: `BUILD SUCCESSFUL`; the new dependency resolves and `SqlObjectPlugin` compiles. Do not run `check` yet because the feature tests are added in Task 2.

- [ ] **Step 5: Commit the dependency and plugin setup**

```bash
git add gradle/libs.versions.toml utilities-db-sql/build.gradle.kts utilities-db-sql/src/main/java/org/aincraft/db/sql/SqlDatabase.java
git commit -m "feat: enable Jdbi SQL Object support"
```

### Task 2: Add typed DAO and transaction façade

**Files:**
- Test: `utilities-db-sql/src/test/java/org/aincraft/db/sql/SqlDatabaseTest.java`
- Modify: `utilities-db-sql/src/main/java/org/aincraft/db/sql/SqlDatabase.java:7-12,70-76`

**Interfaces:**
- Consumes: the configured SQL Object plugin from Task 1 and the existing `jdbi()`/`createDatabase()` test helpers.
- Produces: `SqlDatabase.onDemand(Class<D>)`, `SqlDatabase.useTransaction(Class<D>, Consumer<? super D>)`, and `SqlDatabase.inTransaction(Class<D>, Function<? super D, ? extends R>)`.

- [ ] **Step 1: Write the failing DAO behavior tests**

Add these static imports and SQL Object imports to `SqlDatabaseTest`:

```java
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
```

Add this nested DAO to the test class:

```java
public interface SmokeDao {
  @SqlUpdate("INSERT INTO smoke (id, value) VALUES (:id, :value)")
  void insert(@Bind("id") long id, @Bind("value") String value);

  @SqlQuery("SELECT value FROM smoke WHERE id = :id")
  String findValue(@Bind("id") long id);

  @SqlQuery("SELECT COUNT(*) FROM smoke")
  long count();
}
```

Add these tests. Each test must create and migrate its own `SqlDatabase` through the existing `createDatabase()` helper:

```java
@Test
void exposesOnDemandSqlObjectDao() {
  try (SqlDatabase database = createDatabase()) {
    database.migrate();

    SmokeDao dao = database.onDemand(SmokeDao.class);
    dao.insert(1L, "on-demand");

    assertEquals("on-demand", dao.findValue(1L));
  }
}

@Test
void commitsTypedDaoTransactionAndReturnsResult() {
  try (SqlDatabase database = createDatabase()) {
    database.migrate();

    long count =
        database.inTransaction(
            SmokeDao.class,
            dao -> {
              dao.insert(1L, "first");
              dao.insert(2L, "second");
              return dao.count();
            });

    assertEquals(2L, count);
    assertEquals(2L, database.onDemand(SmokeDao.class).count());
  }
}

@Test
void rollsBackTypedDaoTransactionWhenCallbackFails() {
  try (SqlDatabase database = createDatabase()) {
    database.migrate();

    IllegalStateException failure =
        assertThrows(
            IllegalStateException.class,
            () ->
                database.useTransaction(
                    SmokeDao.class,
                    dao -> {
                      dao.insert(1L, "rolled-back");
                      throw new IllegalStateException("force rollback");
                    }));

    assertEquals("force rollback", failure.getMessage());
    assertEquals(0L, database.onDemand(SmokeDao.class).count());
  }
}

@Test
void rejectsNullDaoTypesAndCallbacks() {
  try (SqlDatabase database = createDatabase()) {
    assertThrows(NullPointerException.class, () -> database.onDemand(null));
    assertThrows(
        NullPointerException.class,
        () -> database.useTransaction(SmokeDao.class, null));
    assertThrows(
        NullPointerException.class,
        () -> database.inTransaction(SmokeDao.class, null));
  }
}
```

- [ ] **Step 2: Run the focused tests and verify they fail for the missing API**

Run:

```bash
./gradlew :utilities-db-sql:test --tests org.aincraft.db.sql.SqlDatabaseTest
```

Expected: test compilation fails because `SqlDatabase` does not yet declare `onDemand`, `useTransaction`, or `inTransaction`. Do not treat this expected red run as a completed verification.

- [ ] **Step 3: Implement the smallest typed delegates**

Add imports for `java.util.function.Consumer` and `java.util.function.Function`. Add these methods after `jdbi()` in `SqlDatabase`:

```java
public <D> D onDemand(Class<D> daoType) {
  Objects.requireNonNull(daoType, "daoType");
  return jdbi.onDemand(daoType);
}

public <D> void useTransaction(
    Class<D> daoType, Consumer<? super D> callback) {
  Objects.requireNonNull(daoType, "daoType");
  Objects.requireNonNull(callback, "callback");
  jdbi.useTransaction(handle -> callback.accept(handle.attach(daoType)));
}

public <D, R> R inTransaction(
    Class<D> daoType, Function<? super D, ? extends R> callback) {
  Objects.requireNonNull(daoType, "daoType");
  Objects.requireNonNull(callback, "callback");
  return jdbi.inTransaction(handle -> callback.apply(handle.attach(daoType)));
}
```

The transaction delegates must attach the DAO to the callback handle, not call `onDemand` inside the callback. This is what guarantees that multiple DAO calls participate in the same transaction.

- [ ] **Step 4: Run the focused tests and verify the behavior passes**

Run:

```bash
./gradlew :utilities-db-sql:test --tests org.aincraft.db.sql.SqlDatabaseTest
```

Expected: `SqlDatabaseTest` passes, including on-demand SQL Object dispatch, mapped scalar reads, transaction result, commit, rollback, and null argument checks.

- [ ] **Step 5: Commit the DAO façade and tests**

```bash
git add utilities-db-sql/src/main/java/org/aincraft/db/sql/SqlDatabase.java utilities-db-sql/src/test/java/org/aincraft/db/sql/SqlDatabaseTest.java
git commit -m "feat: add typed Jdbi DAO transactions"
```

### Task 3: Document the consumer-facing API

**Files:**
- Modify: `README.md:5-22`

**Interfaces:**
- Consumes: the public methods and lifecycle semantics produced by Task 2.
- Produces: consumer documentation that accurately distinguishes Hikari pooling, Jdbi SQL Object DAOs, explicit Flyway migration, and handle-bound streaming.

- [ ] **Step 1: Update the SQL module description and runtime requirements**

Change the SQL artifact purpose to mention typed Jdbi SQL Object DAOs, and retain the statements that JDBC drivers are consumer-provided and migrations are explicit. Add a `## SQL Object DAOs` section after the runtime requirements with this example:

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

Explain that `onDemand` manages one handle per DAO method and that `useTransaction`/`inTransaction` attach the DAO to one Jdbi-managed transaction. State that SQL Object annotations and SQL remain consumer-owned.

- [ ] **Step 2: Document the streaming escape hatch**

Add the exact lifecycle warning and attached-handle pattern:

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

State that lazy streams and iterators must not be returned from an on-demand method and consumed after that method returns; use `jdbi().open()` and `Handle#attach` instead.

- [ ] **Step 3: Run the module check**

Run:

```bash
./gradlew :utilities-db-sql:check
```

Expected: SQL module tests, Spotless checks, jar isolation, and paper/bukkit-free checks pass. Do not run the full repository check until Task 4.

- [ ] **Step 4: Commit the documentation**

```bash
git add README.md
git commit -m "docs: document Jdbi SQL Object DAOs"
```

### Task 4: Run final verification and review the delivered surface

**Files:**
- Inspect: `gradle/libs.versions.toml`
- Inspect: `utilities-db-sql/build.gradle.kts`
- Inspect: `utilities-db-sql/src/main/java/org/aincraft/db/sql/SqlDatabase.java`
- Inspect: `utilities-db-sql/src/test/java/org/aincraft/db/sql/SqlDatabaseTest.java`
- Inspect: `README.md`

**Interfaces:**
- Consumes: all implementation and documentation changes from Tasks 1–3.
- Produces: verified SQL module and repository state with the approved DAO contract.

- [ ] **Step 1: Run the focused behavioral test once more**

Run:

```bash
./gradlew :utilities-db-sql:test --tests org.aincraft.db.sql.SqlDatabaseTest
```

Expected: all SQL database tests pass without filtering out any existing test methods.

- [ ] **Step 2: Run repository verification**

Run:

```bash
./gradlew check
```

Expected: all subproject tests, formatting checks, jar isolation checks, and platform-free checks pass. If the local toolchain prevents Gradle from starting, record the exact Gradle/JDK error rather than claiming verification.

- [ ] **Step 3: Review the final diff for contract drift**

Confirm the final source has all of the following and none of the excluded abstractions:

```text
SqlObjectPlugin installed during SqlDatabase.create
SqlDatabase.onDemand(Class<D>)
SqlDatabase.useTransaction(Class<D>, Consumer<? super D>)
SqlDatabase.inTransaction(Class<D>, Function<? super D, ? extends R>)
raw SqlDatabase.jdbi() retained
Flyway migration remains explicit
no Hibernate, jOOQ, repository generator, or custom connection state
```

- [ ] **Step 4: Commit only if final verification changed files**

If verification or formatting changed tracked files, commit those exact changes with:

```bash
git add gradle/libs.versions.toml utilities-db-sql/build.gradle.kts utilities-db-sql/src/main/java/org/aincraft/db/sql/SqlDatabase.java utilities-db-sql/src/test/java/org/aincraft/db/sql/SqlDatabaseTest.java README.md
git commit -m "chore: finalize Jdbi SQL Object integration"
```

If no files changed, do not create an empty commit.
