# DB Core Split + SQLite Fix — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make SQLite source creation work (real Hikari pool, real lifecycle, opt-in schema via caller's classpath), split DB into a Paper-free core (`org.aincraft.db`) and a Paper adapter (`org.aincraft.db.paper`), in a clean 2.0.0 break.

**Architecture:** Two layers. `org.aincraft.db` core: `ConnectionSource` + nested interfaces, `DatabaseType`, `ConnectionException`, `HikariSourceImpl`, `MongoConnectionSourceImpl`, new public `SQLiteConnectionSource` — no `org.bukkit` imports. `org.aincraft.db.paper` adapter: `ConnectionSourceFactory` + concrete `ConnectionSourceFactoryImpl` (replaces the Proxy dispatch) that resolves `Plugin` → `Path`/config and loads schemas via `plugin.getResource`. Schema execution is explicit and caller-driven; the fixed-name classpath model (`getTables()`/`ResourceExtractor`) is removed.

**Tech Stack:** Java 21, Gradle 8.10, HikariCP 5.0.1, MongoDB driver 5.2.0, Paper API 1.21.4 (compileOnly), JUnit 5 (test), MockBukkit 4.0.0 (test-plugin), sqlite-jdbc (testImplementation only).

## Global Constraints

- Package separation ≠ artifact separation: this plan keeps a single `utilities`
  publication. Moving classes to `org.aincraft.db` makes the core Paper-free at
  the package level but does NOT remove Hikari/Mongo from the shaded jar. A true
  artifact split (Mongo module) is OUT OF SCOPE (see spec).

- `org.aincraft.db` core MUST have ZERO `org.bukkit` imports.
- `sqlite-jdbc` is `testImplementation` ONLY — never `implementation`/`compileOnly`. Drivers come from the server runtime.
- No new `implementation` dependencies anywhere.
- Schema InputStream is CALLER-OWNED — the library never closes it.
- `ConnectionSource` and its nested `SQLConnectionSource`/`MongoConnectionSource`
  extend `AutoCloseable` (2.0 API change) so sources are try-with-resources compatible.
- `DatabaseType.getTables()` and `ResourceExtractor` are REMOVED (clean break).
- `ConnectionSourceFactoryImpl` replaces the Proxy — no Proxy in the codebase after this plan.
- `gradle.properties` version → `2.0.0`.
- Local wrapper MUST run on JDK 21 (machine-local Gradle property, uncommitted).
- Every task ends with a green `./gradlew build` (or the documented exception) and a commit.

---

### Task 1: Fix local build environment (JDK 21 for Gradle)

**Files:**
- Modify: `~/.gradle/gradle.properties` (machine-local, NOT committed)
- Create: `/tmp/jdk21-check.txt` (test artifact)

**Interfaces:**
- Consumes: nothing
- Produces: working `./gradlew` on this machine (JDK 21 toolchain) so all later tasks can compile/test.

- [ ] **Step 1: Detect a JDK 21 install**

```bash
ls /usr/lib/jvm/ 2>/dev/null; ls ~/.local/opt 2>/dev/null | grep -i jdk; ls ~/.sdkman/candidates/java 2>/dev/null
```
Expected: list of JVMs; if none ≤ 21 present, continue to Step 2.

- [ ] **Step 2: Install Temurin 21 (if needed) to user-local path**

```bash
mkdir -p ~/.local/opt && cd /tmp
curl -sL -o temurin21.tar.gz \
  "https://api.adoptium.net/v3/binary/latest/21/ga/linux/x64/jdk/hotspot/normal/eclipse"
tar -xzf temurin21.tar.gz -C ~/.local/opt
ls ~/.local/opt | grep -i "jdk-21"
```
Expected: a `jdk-21.*` directory under `~/.local/opt`.

- [ ] **Step 3: Point Gradle at JDK 21 (machine-local)**

```bash
echo "org.gradle.java.home=$HOME/.local/opt/<jdk-21-dir>" >> ~/.gradle/gradle.properties
grep org.gradle.java.home ~/.gradle/gradle.properties
```

- [ ] **Step 4: Verify the wrapper runs**

```bash
cd /home/jlo/dev/Utilities && ./gradlew --version 2>&1 | grep -E "Gradle|JVM:" ; echo "EXIT=${PIPESTATUS[0]}"
```
Expected: `Gradle 8.10`, `JVM:` line showing 21.x, `EXIT=0`.

- [ ] **Step 5: Full build sanity (before code changes)**

```bash
cd /home/jlo/dev/Utilities && ./gradlew :utilities:build --console=plain -q; echo "EXIT=$?"
```
Expected: `EXIT=0` (build succeeds).

- [ ] **Step 6: Commit (build-only change note; no repo diff expected)**

```bash
cd /home/jlo/dev/Utilities && git status --short
```
Expected: no repo changes (machine-local Gradle property only). If `gradlew` shows modified (pre-existing `M gradlew`), leave it untouched.

---

### Task 2: Add `ConnectionException(String, Throwable)`

**Files:**
- Modify: `utilities/src/main/java/org/aincraft/ConnectionException.java`
- Test: `utilities/src/test/java/org/aincraft/ConnectionExceptionTest.java`  (new)

**Interfaces:**
- Consumes: existing `ConnectionException(String)`
- Produces: `ConnectionException(String, Throwable)` — cause-preserving constructor used by Tasks 3/6.

- [ ] **Step 1: Write the failing test**

`utilities/src/test/java/org/aincraft/ConnectionExceptionTest.java`:
```java
package org.aincraft;

import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

class ConnectionExceptionTest {
  @Test
  void preservesCause() {
    RuntimeException cause = new RuntimeException("boom");
    ConnectionException e = new ConnectionException("failed", cause);
    assertSame(cause, e.getCause());
  }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd /home/jlo/dev/Utilities && ./gradlew :utilities:test --tests "org.aincraft.ConnectionExceptionTest" --console=plain -q; echo "EXIT=$?"`
Expected: compile error — `ConnectionException(String, Throwable)` does not exist. `EXIT=1`.

- [ ] **Step 3: Implement the constructor**

`utilities/src/main/java/org/aincraft/ConnectionException.java`:
```java
package org.aincraft;

public class ConnectionException extends RuntimeException {
  public ConnectionException(String message) {
    super(message);
  }

  public ConnectionException(String message, Throwable cause) {
    super(message, cause);
  }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd /home/jlo/dev/Utilities && ./gradlew :utilities:test --tests "org.aincraft.ConnectionExceptionTest" --console=plain -q; echo "EXIT=$?"`
Expected: test passes, `EXIT=0`.

- [ ] **Step 5: Commit**

```bash
cd /home/jlo/dev/Utilities && git add utilities/src/main/java/org/aincraft/ConnectionException.java utilities/src/test/java/org/aincraft/ConnectionExceptionTest.java && git commit -m "feat: add cause-preserving ConnectionException constructor"
```

---

### Task 3: `SQLiteConnectionSource` — public core SQLite source (Hikari pool, real lifecycle, schema)

**Files:**
- Create: `utilities/src/main/java/org/aincraft/SQLiteConnectionSource.java`
- Create: `utilities/src/test/java/org/aincraft/SQLiteConnectionSourceTest.java`
- Modify: `utilities/build.gradle.kts` (add test deps)

**Interfaces:**
- Consumes: `ConnectionSource.SQLConnectionSource`, `ConnectionException(String, Throwable)` (Task 2), `DatabaseType` (unchanged enum).
- Produces:
  ```java
  public final class SQLiteConnectionSource implements ConnectionSource.SQLConnectionSource {
    public static SQLiteConnectionSource create(Path databaseFile);
    public static SQLiteConnectionSource create(Path databaseFile, InputStream schema);
    @Override public Connection getConnection() throws ConnectionException;
    @Override public void close() throws ConnectionException;
    @Override public boolean closed() throws ConnectionException;
    @Override public DatabaseType getType(); // SQLITE
  }
  ```

- [ ] **Step 1: Make `ConnectionSource` try-with-resources compatible**

`utilities/src/main/java/org/aincraft/ConnectionSource.java` — change line 7:
```java
public interface ConnectionSource extends AutoCloseable {
```
(2.0 API change enabling `try (SQLiteConnectionSource source = …)` in the tests below; `SQLConnectionSource`/`MongoConnectionSource` inherit it.)

- [ ] **Step 2: Add test deps (testImplementation only)**

`utilities/build.gradle.kts`, inside `dependencies { … }`:
```kotlin
testImplementation(platform("org.junit:junit-bom:5.11.3"))
testImplementation("org.junit.jupiter:junit-jupiter")
testImplementation("org.xerial:sqlite-jdbc:3.45.3.0")
```
(No `implementation` change.)

- [ ] **Step 3: Write the failing tests**

`utilities/src/test/java/org/aincraft/SQLiteConnectionSourceTest.java`:
```java
package org.aincraft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SQLiteConnectionSourceTest {
  @TempDir Path tmp;

  private Path dbFile() {
    return tmp.resolve("test.db");
  }

  @Test
  void roundTripsQuery() throws Exception {
    try (SQLiteConnectionSource source = SQLiteConnectionSource.create(dbFile());
         Connection c = source.getConnection();
         Statement s = c.createStatement()) {
      s.execute("CREATE TABLE t (id INTEGER PRIMARY KEY, name TEXT)");
      s.execute("INSERT INTO t (name) VALUES ('hello')");
      try (ResultSet rs = s.executeQuery("SELECT name FROM t")) {
        assertTrue(rs.next());
        assertEquals("hello", rs.getString(1));
      }
    }
  }

  @Test
  void lifecycleTransitions() throws Exception {
    SQLiteConnectionSource source = SQLiteConnectionSource.create(dbFile());
    assertFalse(source.closed());
    source.close();
    assertTrue(source.closed());
  }

  @Test
  void appliesSchemaFromInputStream() throws Exception {
    String schema = "CREATE TABLE a (id INTEGER PRIMARY KEY);"
        + "CREATE TABLE b (id INTEGER PRIMARY KEY);";
    try (SQLiteConnectionSource source = SQLiteConnectionSource.create(
             dbFile(), new ByteArrayInputStream(schema.getBytes(StandardCharsets.UTF_8)));
         Connection c = source.getConnection();
         Statement s = c.createStatement()) {
      try (ResultSet rs = s.executeQuery(
               "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name")) {
        assertTrue(rs.next());
        assertEquals("a", rs.getString(1));
        assertTrue(rs.next());
        assertEquals("b", rs.getString(1));
        assertFalse(rs.next());
      }
    }
  }

  @Test
  void rollsBackSchemaOnFailure() throws Exception {
    String schema = "CREATE TABLE ok_t (id INTEGER PRIMARY KEY); BAD SQL;";
    assertThrows(ConnectionException.class, () ->
        SQLiteConnectionSource.create(dbFile(), new ByteArrayInputStream(
            schema.getBytes(StandardCharsets.UTF_8))));
    // The pool opens the file on create(), so the db file exists; the table
    // must not have been committed.
    try (SQLiteConnectionSource source = SQLiteConnectionSource.create(dbFile());
         Connection c = source.getConnection();
         Statement s = c.createStatement();
         ResultSet rs = s.executeQuery(
             "SELECT name FROM sqlite_master WHERE type='table' AND name='ok_t'")) {
      assertFalse(rs.next());
    }
  }
}
```

- [ ] **Step 4: Run tests to verify they fail**

Run: `cd /home/jlo/dev/Utilities && ./gradlew :utilities:test --tests "org.aincraft.SQLiteConnectionSourceTest" --console=plain -q; echo "EXIT=$?"`
Expected: compile error — `SQLiteConnectionSource` missing. `EXIT=1`.

- [ ] **Step 5: Implement `SQLiteConnectionSource`**

`utilities/src/main/java/org/aincraft/SQLiteConnectionSource.java`:
```java
package org.aincraft;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Scanner;
import org.aincraft.ConnectionSource.SQLConnectionSource;

public final class SQLiteConnectionSource implements SQLConnectionSource {

  private final HikariDataSource dataSource;

  private SQLiteConnectionSource(HikariDataSource dataSource) {
    this.dataSource = dataSource;
  }

  public static SQLiteConnectionSource create(Path databaseFile) {
    return new SQLiteConnectionSource(newPool(databaseFile));
  }

  public static SQLiteConnectionSource create(Path databaseFile, InputStream schema) {
    HikariDataSource pool = newPool(databaseFile);
    SQLiteConnectionSource source = new SQLiteConnectionSource(pool);
    try {
      source.applySchema(schema);
    } catch (RuntimeException e) {
      pool.close();
      throw e;
    }
    return source;
  }

  private static HikariDataSource newPool(Path databaseFile) {
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      throw new ConnectionException(
          "SQLite driver not found on the runtime classpath; the server must provide it",
          e);
    }
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl("jdbc:sqlite:" + databaseFile.toAbsolutePath());
    config.setMaximumPoolSize(4);
    return new HikariDataSource(config);
  }

  private void applySchema(InputStream schema) {
    try (Connection connection = dataSource.getConnection()) {
      connection.setAutoCommit(false);
      Savepoint savepoint = connection.setSavepoint();
      try (Statement statement = connection.createStatement()) {
        String sql = new Scanner(schema, "UTF-8").useDelimiter("\\A").next();
        for (String statementSql : sql.split(";")) {
          String trimmed = statementSql.trim();
          if (!trimmed.isEmpty()) {
            statement.execute(trimmed);
          }
        }
        connection.commit();
      } catch (SQLException e) {
        connection.rollback(savepoint);
        throw new ConnectionException("failed to apply schema", e);
      }
    } catch (SQLException e) {
      throw new ConnectionException("failed to apply schema", e);
    }
  }

  @Override
  public Connection getConnection() throws ConnectionException {
    try {
      return dataSource.getConnection();
    } catch (SQLException e) {
      throw new ConnectionException("failed to acquire connection", e);
    }
  }

  @Override
  public void close() {
    dataSource.close();
  }

  @Override
  public boolean closed() {
    return dataSource.isClosed();
  }

  @Override
  public DatabaseType getType() {
    return DatabaseType.SQLITE;
  }
}
```

- [ ] **Step 6: Run tests to verify they pass**

Run: `cd /home/jlo/dev/Utilities && ./gradlew :utilities:test --tests "org.aincraft.SQLiteConnectionSourceTest" --console=plain -q; echo "EXIT=$?"`
Expected: 4 tests pass, `EXIT=0`.

- [ ] **Step 7: Commit**

```bash
cd /home/jlo/dev/Utilities && git add utilities/src/main/java/org/aincraft/SQLiteConnectionSource.java utilities/src/test/java/org/aincraft/SQLiteConnectionSourceTest.java utilities/build.gradle.kts && git commit -m "feat: add SQLiteConnectionSource with Hikari pool and opt-in schema"
```

---

### Task 4: Remove `DatabaseType.getTables()` and `ResourceExtractor`

**Files:**
- Modify: `utilities/src/main/java/org/aincraft/DatabaseType.java`
- Delete: `utilities/src/main/java/org/aincraft/ResourceExtractor.java`

**Interfaces:**
- Consumes: nothing new.
- Produces: removal of the fixed-name classpath schema model. (Task 3's `SQLiteConnectionSource` is the only schema path now.)

- [ ] **Step 1: Delete `getTables()` from `DatabaseType`**

`utilities/src/main/java/org/aincraft/DatabaseType.java` — remove the `getTables()` method (and its now-unused imports `BufferedReader`, `IOException`, `InputStream`, `InputStreamReader`, `StandardCharsets`, `Arrays`, `Collectors`, `Stream`).

- [ ] **Step 2: Delete `ResourceExtractor`**

```bash
rm utilities/src/main/java/org/aincraft/ResourceExtractor.java
```

- [ ] **Step 3: Verify no references remain**

Run: `grep -rn "getTables\|ResourceExtractor" /home/jlo/dev/Utilities --include="*.java"`
Expected: no output (no references).

- [ ] **Step 4: Build to confirm green**

Run: `cd /home/jlo/dev/Utilities && ./gradlew :utilities:build --console=plain -q; echo "EXIT=$?"`
Expected: `EXIT=0`.

- [ ] **Step 5: Commit**

```bash
cd /home/jlo/dev/Utilities && git add -A utilities/ && git commit -m "refactor: remove fixed-name classpath schema model (getTables, ResourceExtractor)"
```

---

### Task 5: Move DB core to `org.aincraft.db` (Paper-free)

**Files:**
- Move (via `git mv`):
  - `utilities/src/main/java/org/aincraft/ConnectionSource.java` → `org/aincraft/db/ConnectionSource.java`
  - `utilities/src/main/java/org/aincraft/ConnectionException.java` → `org/aincraft/db/ConnectionException.java`
  - `utilities/src/main/java/org/aincraft/DatabaseType.java` → `org/aincraft/db/DatabaseType.java`
  - `utilities/src/main/java/org/aincraft/HikariSourceImpl.java` → `org/aincraft/db/HikariSourceImpl.java`
  - `utilities/src/main/java/org/aincraft/SQLiteConnectionSource.java` → `org/aincraft/db/SQLiteConnectionSource.java`
  - `utilities/src/test/java/org/aincraft/ConnectionExceptionTest.java` → `org/aincraft/db/ConnectionExceptionTest.java`
  - `utilities/src/test/java/org/aincraft/SQLiteConnectionSourceTest.java` → `org/aincraft/db/SQLiteConnectionSourceTest.java`
- Modify: package declarations in all moved files (`org.aincraft` → `org.aincraft.db`).
- Do NOT move `MongoConnectionSourceImpl.java` yet (it is referenced by the adapter Task 6).

**Interfaces:**
- Consumes: Task 2/3 outputs (now at new packages).
- Produces: Paper-free core at `org.aincraft.db`; adapter-facing API: `ConnectionSource`, `ConnectionSource.SQLConnectionSource`, `ConnectionSource.MongoConnectionSource`, `DatabaseType`, `ConnectionException`, `HikariSourceImpl`, `SQLiteConnectionSource`, `MongoConnectionSourceImpl`.

- [ ] **Step 1: `git mv` the core files (excluding Mongo impl)**

```bash
cd /home/jlo/dev/Utilities && \
git mv utilities/src/main/java/org/aincraft/ConnectionSource.java utilities/src/main/java/org/aincraft/db/ConnectionSource.java && \
git mv utilities/src/main/java/org/aincraft/ConnectionException.java utilities/src/main/java/org/aincraft/db/ConnectionException.java && \
git mv utilities/src/main/java/org/aincraft/DatabaseType.java utilities/src/main/java/org/aincraft/db/DatabaseType.java && \
git mv utilities/src/main/java/org/aincraft/HikariSourceImpl.java utilities/src/main/java/org/aincraft/db/HikariSourceImpl.java && \
git mv utilities/src/main/java/org/aincraft/SQLiteConnectionSource.java utilities/src/main/java/org/aincraft/db/SQLiteConnectionSource.java && \
git mv utilities/src/test/java/org/aincraft/ConnectionExceptionTest.java utilities/src/test/java/org/aincraft/db/ConnectionExceptionTest.java && \
git mv utilities/src/test/java/org/aincraft/SQLiteConnectionSourceTest.java utilities/src/test/java/org/aincraft/db/SQLiteConnectionSourceTest.java
```
Expected: no errors; files now under `…/db/`.

- [ ] **Step 2: Update package declarations**

In each moved file, change the first line `package org.aincraft;` → `package org.aincraft.db;`.
(For files that reference other moved classes, update those imports too — e.g. `SQLiteConnectionSource` implements `org.aincraft.db.ConnectionSource.SQLConnectionSource` now via same-package.)

- [ ] **Step 3: Verify no `org.bukkit` imports in `org.aincraft.db`**

Run: `grep -rn "import org.bukkit" utilities/src/main/java/org/aincraft/db/`
Expected: no output.

- [ ] **Step 4: Build + test**

Run: `cd /home/jlo/dev/Utilities && ./gradlew :utilities:build --console=plain -q; echo "EXIT=$?"`
Expected: `EXIT=0` (tests pass at new packages).

- [ ] **Step 5: Commit**

```bash
cd /home/jlo/dev/Utilities && git add -A utilities/ && git commit -m "refactor: move DB core to org.aincraft.db (Paper-free)"
```

---

### Task 6: Paper adapter — `ConnectionSourceFactoryImpl` (concrete, moves Mongo impl)

**Files:**
- Move: `utilities/src/main/java/org/aincraft/MongoConnectionSourceImpl.java` → `org/aincraft/db/MongoConnectionSourceImpl.java`
- Move: `utilities/src/main/java/org/aincraft/ConnectionSourceFactory.java` → `org/aincraft/db/paper/ConnectionSourceFactory.java`
- Move: `utilities/src/main/java/org/aincraft/SQLConnectionSourceFactoryImpl.java` → `org/aincraft/db/paper/SQLConnectionSourceFactoryImpl.java`
- Move: `utilities/src/main/java/org/aincraft/MongoConnectionSourceFactoryImpl.java` → `org/aincraft/db/paper/MongoConnectionSourceFactoryImpl.java`
- Create: `utilities/src/main/java/org/aincraft/db/paper/ConnectionSourceFactoryImpl.java`
- Modify: `test-plugin/build.gradle.kts` (add sqlite-jdbc testImplementation)
- Delete (after absorbing): `utilities/src/main/java/org/aincraft/SQLConnectionSourceFactoryImpl.java`, `utilities/src/main/java/org/aincraft/MongoConnectionSourceFactoryImpl.java`
- (Config classes `org.aincraft.config.*` stay untouched; factory uses Bukkit `ConfigurationSection` → Paper adapter by design.)

**Interfaces:**
- Consumes: core (Task 5): `org.aincraft.db.*` incl. `SQLiteConnectionSource`, `MongoConnectionSourceImpl`.
- Produces: `org.aincraft.db.paper.ConnectionSourceFactory` + `ConnectionSourceFactoryImpl` — stable public factory API:
  ```java
  public interface ConnectionSourceFactory {
    static ConnectionSourceFactory create(Plugin plugin);  // returns new ConnectionSourceFactoryImpl(plugin)
    ConnectionSource create(DatabaseType type, ConfigurationSection configuration)
        throws IllegalArgumentException, ConnectionException;
  }
  ```

- [ ] **Step 1: `git mv` the Mongo impl + factory interface to new packages**

```bash
cd /home/jlo/dev/Utilities && \
git mv utilities/src/main/java/org/aincraft/MongoConnectionSourceImpl.java utilities/src/main/java/org/aincraft/db/MongoConnectionSourceImpl.java && \
git mv utilities/src/main/java/org/aincraft/ConnectionSourceFactory.java utilities/src/main/java/org/aincraft/db/paper/ConnectionSourceFactory.java && \
git mv utilities/src/main/java/org/aincraft/SQLConnectionSourceFactoryImpl.java utilities/src/main/java/org/aincraft/db/paper/SQLConnectionSourceFactoryImpl.java && \
git mv utilities/src/main/java/org/aincraft/MongoConnectionSourceFactoryImpl.java utilities/src/main/java/org/aincraft/db/paper/MongoConnectionSourceFactoryImpl.java
```
Expected: no errors.

- [ ] **Step 2: Update package declarations in moved files**

- `MongoConnectionSourceImpl.java` → `package org.aincraft.db;` (imports `ConnectionSource` same-package now)
- `ConnectionSourceFactory.java` → `package org.aincraft.db.paper;` (imports `org.aincraft.db.DatabaseType`, `org.aincraft.db.ConnectionSource`, `org.aincraft.db.ConnectionException`)
- `SQLConnectionSourceFactoryImpl.java`, `MongoConnectionSourceFactoryImpl.java` → `package org.aincraft.db.paper;`, fix imports to `org.aincraft.db.*`

- [ ] **Step 3: Add sqlite-jdbc testImplementation to test-plugin**

`test-plugin/build.gradle.kts`, inside the `testImplementation` block:
```kotlin
testImplementation("org.xerial:sqlite-jdbc:3.45.3.0")
```

- [ ] **Step 4: Write the failing test (MockBukkit — factory paths + schema, in test-plugin)**

`test-plugin/src/test/java/org/aincraft/ConnectionSourceFactoryTest.java`:
```java
package org.aincraft.db.paper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.aincraft.db.ConnectionSource;
import org.aincraft.db.DatabaseType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.plugin.PluginMock;

class ConnectionSourceFactoryTest {
  private static ServerMock server;
  private static PluginMock plugin;

  @BeforeAll
  static void setup() {
    server = MockBukkit.mock();
    plugin = MockBukkit.createMockPlugin();
  }

  @AfterAll
  static void teardown() {
    MockBukkit.unmock();
  }

  @Test
  void createsSqliteWithoutSchema() throws Exception {
    YamlConfiguration config = new YamlConfiguration();
    config.set("path", "test.db");
    ConnectionSource source = ConnectionSourceFactory.create(plugin)
        .create(DatabaseType.SQLITE, config);
    assertTrue(source instanceof ConnectionSource.SQLConnectionSource);
    Connection c = ((ConnectionSource.SQLConnectionSource) source).getConnection();
    try (Statement s = c.createStatement()) {
      s.execute("CREATE TABLE t (id INTEGER)");
      s.execute("INSERT INTO t VALUES (1)");
      try (ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM t")) {
        rs.next();
        assertEquals(1, rs.getInt(1));
      }
    }
    source.close();
  }

  @Test
  void appliesSchemaFileFromPluginResources() throws Exception {
    YamlConfiguration config = new YamlConfiguration();
    config.set("path", "schema.db");
    config.set("schema-file", "schemas/init.sql");
    ConnectionSource source = ConnectionSourceFactory.create(plugin)
        .create(DatabaseType.SQLITE, config);
    Connection c = ((ConnectionSource.SQLConnectionSource) source).getConnection();
    try (Statement s = c.createStatement()) {
      try (ResultSet rs = s.executeQuery(
               "SELECT name FROM sqlite_master WHERE type='table' AND name='init_t'")) {
        assertTrue(rs.next());
      }
    }
    source.close();
  }

  @Test
  void missingSchemaFileThrows() {
    YamlConfiguration config = new YamlConfiguration();
    config.set("path", "bad.db");
    config.set("schema-file", "does-not-exist.sql");
    assertThrows(IllegalArgumentException.class, () ->
        ConnectionSourceFactory.create(plugin).create(DatabaseType.SQLITE, config));
  }
}
```
Also create test resource `test-plugin/src/test/resources/schemas/init.sql`:
```sql
CREATE TABLE init_t (id INTEGER PRIMARY KEY);
```

- [ ] **Step 4: Run test to verify it fails**

Run: `cd /home/jlo/dev/Utilities && ./gradlew :test-plugin:test --tests "org.aincraft.db.paper.ConnectionSourceFactoryTest" --console=plain -q; echo "EXIT=$?"`
Expected: compile error — `ConnectionSourceFactory` not resolvable (or `ConnectionSourceFactoryImpl` missing). `EXIT=1`. (test-plugin already has MockBukkit + junit + `testImplementation(project(":utilities"))`; no build file change needed.)

- [ ] **Step 5: Implement the concrete factory**

`utilities/src/main/java/org/aincraft/db/paper/ConnectionSourceFactoryImpl.java`:
```java
package org.aincraft.db.paper;

import com.google.common.base.Preconditions;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import org.aincraft.db.ConnectionSource;
import org.aincraft.db.DatabaseType;
import org.aincraft.db.HikariSourceImpl;
import org.aincraft.db.MongoConnectionSourceImpl;
import org.aincraft.db.SQLiteConnectionSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

public final class ConnectionSourceFactoryImpl implements ConnectionSourceFactory {

  private final Plugin plugin;

  public ConnectionSourceFactoryImpl(Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public ConnectionSource create(DatabaseType type, ConfigurationSection configuration)
      throws IllegalArgumentException, ConnectionException {
    Preconditions.checkNotNull(type, "type");
    Preconditions.checkNotNull(configuration, "configuration");
    return switch (type) {
      case MYSQL, POSTGRES -> new HikariSourceImpl(
          new HikariDataSource(parseHikariConfig(configuration)), type);
      case SQLITE -> sqlite(configuration);
      case MONGO -> new MongoConnectionSourceImpl(
          MongoConnectionSourceFactoryImpl.parseClientSettings(configuration));
    };
  }

  private ConnectionSource sqlite(ConfigurationSection configuration) {
    String path = configuration.getString("path");
    Preconditions.checkNotNull(path, "missing required field: database.path");
    java.nio.file.Path dbFile = plugin.getDataFolder().toPath().resolve(path);
    java.nio.file.Path parent = dbFile.getParent();
    if (parent != null) {
      parent.toFile().mkdirs();
    }
    String schemaFile = configuration.getString("schema-file");
    if (schemaFile == null) {
      return SQLiteConnectionSource.create(dbFile);
    }
    InputStream schema = plugin.getResource(schemaFile);
    if (schema == null) {
      throw new IllegalArgumentException("schema resource not found: " + schemaFile);
    }
    return SQLiteConnectionSource.create(dbFile, schema);
  }

  private static HikariConfig parseHikariConfig(ConfigurationSection configuration) {
    HikariConfig hikariConfig = new HikariConfig();
    String jdbcUrl = configuration.getString("jdbc-url");
    String username = configuration.getString("username");
    String password = configuration.getString("password");
    Preconditions.checkNotNull(jdbcUrl, "missing required field: database.jdbc-url");
    Preconditions.checkNotNull(username, "missing required field: database.username");
    Preconditions.checkNotNull(password, "missing required field: database.password");
    hikariConfig.setJdbcUrl(jdbcUrl);
    hikariConfig.setUsername(username);
    hikariConfig.setPassword(password);
    int maxPoolSize = configuration.getInt("maximum-pool-size", -1);
    if (maxPoolSize > 0) {
      hikariConfig.setMaximumPoolSize(maxPoolSize);
    }
    int minIdle = configuration.getInt("minimum-idle", -1);
    if (minIdle >= 0) {
      hikariConfig.setMinimumIdle(minIdle);
    }
    long connectionTimeout = configuration.getLong("connection-timeout", -1);
    if (connectionTimeout > 0) {
      hikariConfig.setConnectionTimeout(connectionTimeout);
    }
    long idleTimeout = configuration.getLong("idle-timeout", -1);
    if (idleTimeout > 0) {
      hikariConfig.setIdleTimeout(idleTimeout);
    }
    long maxLifetime = configuration.getLong("max-lifetime", -1);
    if (maxLifetime > 0) {
      hikariConfig.setMaxLifetime(maxLifetime);
    }
    return hikariConfig;
  }
}
```

`utilities/src/main/java/org/aincraft/db/paper/ConnectionSourceFactory.java` (rewrite — replaces Proxy with concrete impl):
```java
package org.aincraft.db.paper;

import org.aincraft.db.ConnectionSource;
import org.aincraft.db.ConnectionException;
import org.aincraft.db.DatabaseType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

public interface ConnectionSourceFactory {

  static ConnectionSourceFactory create(Plugin plugin) {
    return new ConnectionSourceFactoryImpl(plugin);
  }

  ConnectionSource create(DatabaseType type, ConfigurationSection configuration)
      throws IllegalArgumentException, ConnectionException;
}
```

`utilities/src/main/java/org/aincraft/db/paper/SQLConnectionSourceFactoryImpl.java` and `MongoConnectionSourceFactoryImpl.java`: update package + imports; `MongoConnectionSourceFactoryImpl` loses its `implements ConnectionSourceFactory` and keeps only `parseClientSettings(configuration)` as a static method (the factory's MONGO branch calls `MongoConnectionSourceFactoryImpl.parseClientSettings`). `SQLConnectionSourceFactoryImpl` is no longer needed (absorbed by the concrete factory) → delete it.

- [ ] **Step 6: Run tests to verify they pass**

Run: `cd /home/jlo/dev/Utilities && ./gradlew :test-plugin:test --tests "org.aincraft.db.paper.ConnectionSourceFactoryTest" --console=plain -q; echo "EXIT=$?"`
Expected: 3 tests pass, `EXIT=0`. (test-plugin needs `testImplementation("org.xerial:sqlite-jdbc:3.45.3.0")` — add it to `test-plugin/build.gradle.kts` test deps in this step if not present, since the factory test connects via sqlite; test scope only.)

- [ ] **Step 7: Delete absorbed helper + verify no Proxy remains**

```bash
cd /home/jlo/dev/Utilities && rm utilities/src/main/java/org/aincraft/db/paper/SQLConnectionSourceFactoryImpl.java
grep -rn "Proxy.newProxyInstance\|SQLConnectionSourceFactoryImpl" utilities/src/main/java/
```
Expected: no output.

- [ ] **Step 8: Full build**

Run: `cd /home/jlo/dev/Utilities && ./gradlew :utilities:build --console=plain -q; echo "EXIT=$?"`
Expected: `EXIT=0`.

- [ ] **Step 9: Commit**

```bash
cd /home/jlo/dev/Utilities && git add -A utilities/ test-plugin/ && git commit -m "feat: concrete Paper adapter ConnectionSourceFactoryImpl (org.aincraft.db.paper)"
```

---

### Task 7: Migrate test-plugin to new packages + factory

**Files:**
- Modify: `test-plugin/src/main/java/org/aincraft/TestPlugin.java`
- Modify: `test-plugin/src/test/java/org/aincraft/YamlConfigurationTest.java`
- Modify: `test-plugin/build.gradle.kts` (add sqlite-jdbc testImplementation if needed)

**Interfaces:**
- Consumes: `org.aincraft.db.paper.ConnectionSourceFactory`, `org.aincraft.db.*` (Tasks 5/6), `org.aincraft.config.YamlConfiguration` (unchanged).
- Produces: test-plugin compiling against new packages.

- [ ] **Step 1: Update imports in TestPlugin**

`test-plugin/src/main/java/org/aincraft/TestPlugin.java` — change `import org.aincraft.config.YamlConfiguration;` (unchanged — config stays `org.aincraft.config`). No DB reference in TestPlugin today. Verify it compiles.

- [ ] **Step 2: Add a DB smoke usage to TestPlugin (Paper runtime path)**

`test-plugin/src/main/java/org/aincraft/TestPlugin.java`:
```java
package org.aincraft;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.aincraft.config.YamlConfiguration;
import org.aincraft.db.ConnectionSource;
import org.aincraft.db.DatabaseType;
import org.aincraft.db.paper.ConnectionSourceFactory;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    YamlConfiguration configuration = YamlConfiguration.single(this, "config.yml");
    String test = configuration.getString("test");
    Bukkit.getLogger().info(test);

    ConnectionSourceFactory factory = ConnectionSourceFactory.create(this);
    org.bukkit.configuration.ConfigurationSection dbSection = configuration.getConfigurationSection("database");
    if (dbSection == null) {
      dbSection = createDefaultDatabaseSection();
    }
    try (ConnectionSource source = factory.create(DatabaseType.SQLITE, dbSection)) {
      Connection c = ((ConnectionSource.SQLConnectionSource) source).getConnection();
      try (Statement s = c.createStatement()) {
        s.execute("CREATE TABLE IF NOT EXISTS smoke (id INTEGER PRIMARY KEY, v TEXT)");
        s.execute("INSERT OR REPLACE INTO smoke (id, v) VALUES (1, 'ok')");
        try (ResultSet rs = s.executeQuery("SELECT v FROM smoke WHERE id=1")) {
          rs.next();
          Bukkit.getLogger().info("smoke=" + rs.getString(1));
        }
      }
      source.close();
    } catch (Exception e) {
      Bukkit.getLogger().severe("smoke failed: " + e);
    }
  }

  private org.bukkit.configuration.ConfigurationSection createDefaultDatabaseSection() {
    org.bukkit.configuration.file.YamlConfiguration c = new org.bukkit.configuration.file.YamlConfiguration();
    c.set("path", "smoke.db");
    return c;
  }
}
```

- [ ] **Step 3: Verify test-plugin builds**

Run: `cd /home/jlo/dev/Utilities && ./gradlew :test-plugin:build --console=plain -q; echo "EXIT=$?"`
Expected: `EXIT=0`.

- [ ] **Step 4: Commit**

```bash
cd /home/jlo/dev/Utilities && git add -A test-plugin/ && git commit -m "test: exercise SQLite source via Paper factory in test-plugin"
```

---

### Task 8: Paper runtime smoke test (runServer)

**Files:**
- Modify: `test-plugin/build.gradle.kts` (if `runServer` needs config)
- No source changes expected.

**Interfaces:**
- Consumes: Task 7 TestPlugin (which creates the SQLite source at enable).
- Produces: evidence that driver discovery works on real Paper 1.21.4 runtime.

- [ ] **Step 1: Start the Paper server**

Run: `cd /home/jlo/dev/Utilities && ./gradlew :test-plugin:runServer --console=plain -q; echo "EXIT=$?"` (runs Paper 1.21.4 with test-plugin).
Expected: server boots; log includes `smoke=ok` (from TestPlugin.onEnable); timeout after ~90s or use a `timeout 90` wrapper.

- [ ] **Step 2: Confirm smoke log line**

Grep the server log / console output for `smoke=ok`.
Expected: `smoke=ok` present; no `smoke failed:`.

- [ ] **Step 3: Stop the server**

If still running, stop it (Ctrl-C / `hub stop` on the runServer process).

- [ ] **Step 4: Commit (no expected diff)**

```bash
cd /home/jlo/dev/Utilities && git status --short
```
Expected: nothing new (smoke.db is generated in run dir; if tracked, add to `.gitignore`).

---

### Task 9: Docs + version bump

**Files:**
- Modify: `gradle.properties` (version → 2.0.0)
- Create: `README.md` (Runtime requirements section) — only if a README doesn't exist; if it does, append the section.

**Interfaces:**
- Consumes: everything above.
- Produces: user-facing runtime requirements + 2.0.0 release marker.

- [ ] **Step 1: Update version**

`gradle.properties`: `version=2.0.0`

- [ ] **Step 2: Add README runtime requirements (or append)**

`README.md` (create if absent):
```markdown
# utilities

Runtime requirements:

- **JDBC drivers are NOT bundled.** SQLite (`org.sqlite.JDBC`) and MySQL
  drivers are provided by Paper's server runtime (versions vary by server
  release). To pin a specific driver version, use `plugin.yml` `libraries:`.
- The PostgreSQL driver is not guaranteed by this library; the runtime or
  environment must provide it.
- Schema initialization is opt-in: the Paper factory executes the SQL file
  named by `schema-file` (loaded from the plugin jar via `plugin.getResource`).
- The `org.aincraft.db` core is Paper-free; `org.aincraft.db.paper` is the
  Bukkit/Paper adapter.
```

- [ ] **Step 3: Full build + tests**

Run: `cd /home/jlo/dev/Utilities && ./gradlew build --console=plain -q; echo "EXIT=$?"`
Expected: `EXIT=0` (both modules).

- [ ] **Step 4: Commit**

```bash
cd /home/jlo/dev/Utilities && git add gradle.properties README.md && git commit -m "release: bump to 2.0.0 with runtime driver documentation"
```

---

## Self-Review Notes

- **Spec coverage:** every spec section maps to a task — Context defects → Tasks 2–4 (SQLite fix, removals, constructor); Goals → Task 5 (Paper-free core), Task 6 (adapter), Task 7 (migration), Task 8 (Paper smoke), Task 9 (docs/version); Verification section → each task's steps.
- **No placeholders:** all code blocks are complete; no TBD/TODO; commands have expected output.
- **Type consistency:** `SQLiteConnectionSource.create(Path)`, `create(Path, InputStream)` match Task 3 Interfaces and used verbatim in Tasks 6/7. `ConnectionSourceFactory.create(plugin)` static returns `ConnectionSourceFactoryImpl`. `ConnectionException(String, Throwable)` used in Task 3. `MongoConnectionSourceImpl.parseClientSettings` referenced correctly in the factory.
- **Package moves:** git mv preserves history; imports updated in-place; `org.aincraft.db` verified Paper-free (grep gate).
