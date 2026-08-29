package org.aincraft.db.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.zaxxer.hikari.HikariConfig;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

class SqlDatabaseTest {

  @TempDir Path temporaryDirectory;

  public interface SmokeDao {
    @SqlUpdate("INSERT INTO smoke (id, value) VALUES (:id, :value)")
    void insert(@Bind("id") long id, @Bind("value") String value);

    @SqlQuery("SELECT value FROM smoke WHERE id = :id")
    String findValue(@Bind("id") long id);

    @SqlQuery("SELECT COUNT(*) FROM smoke")
    long count();
  }

  @Test
  void migratesQueriesAndReportsDriverCapabilities() throws Exception {
    try (SqlDatabase database = createDatabase()) {
      database.migrate();

      assertNotNull(database.jdbi());
      assertEquals("SQLite", database.capabilities().databaseProductName());
      assertTrue(database.capabilities().supports(SqlFeature.TRANSACTIONS));

      try (Handle handle = database.jdbi().open()) {
        DatabaseMetaData metadata = handle.getConnection().getMetaData();
        assertEquals(
            metadata.supportsTransactions(),
            database.capabilities().supports(SqlFeature.TRANSACTIONS));
        assertEquals(
            metadata.supportsSavepoints(), database.capabilities().supports(SqlFeature.SAVEPOINTS));
        assertEquals(
            metadata.supportsBatchUpdates(),
            database.capabilities().supports(SqlFeature.BATCH_UPDATES));
        assertEquals(
            metadata.supportsTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE),
            database
                .capabilities()
                .supportsTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE));
      }

      long smokeCount =
          database
              .jdbi()
              .withHandle(
                  handle ->
                      handle.createQuery("SELECT COUNT(*) FROM smoke").mapTo(long.class).one());
      assertEquals(0L, smokeCount);
    }
  }

  @Test
  void delegatesTransactionCommitAndRollbackToJdbi() throws Exception {
    try (SqlDatabase database = createDatabase()) {
      database.migrate();

      database
          .jdbi()
          .useTransaction(
              handle ->
                  handle
                      .createUpdate("INSERT INTO smoke (value) VALUES (:value)")
                      .bind("value", "committed")
                      .execute());

      try {
        database
            .jdbi()
            .useTransaction(
                handle -> {
                  handle
                      .createUpdate("INSERT INTO smoke (value) VALUES (:value)")
                      .bind("value", "rolled_back")
                      .execute();
                  throw new IllegalStateException("force rollback");
                });
      } catch (IllegalStateException e) {
        assertEquals("force rollback", e.getMessage());
      }

      long committedCount =
          database
              .jdbi()
              .withHandle(
                  handle ->
                      handle
                          .createQuery("SELECT COUNT(*) FROM smoke WHERE value = :value")
                          .bind("value", "committed")
                          .mapTo(long.class)
                          .one());
      long rolledBackCount =
          database
              .jdbi()
              .withHandle(
                  handle ->
                      handle
                          .createQuery("SELECT COUNT(*) FROM smoke WHERE value = :value")
                          .bind("value", "rolled_back")
                          .mapTo(long.class)
                          .one());
      assertEquals(1L, committedCount);
      assertEquals(0L, rolledBackCount);
    }
  }

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
      assertThrows(NullPointerException.class, () -> database.useTransaction(SmokeDao.class, null));
      assertThrows(NullPointerException.class, () -> database.inTransaction(SmokeDao.class, null));
    }
  }

  @Test
  void closesItsPool() {
    SqlDatabase database = createDatabase();
    assertFalse(database.closed());
    database.close();
    assertTrue(database.closed());
  }

  private SqlDatabase createDatabase() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl("jdbc:sqlite:" + temporaryDirectory.resolve("test.db").toAbsolutePath());
    config.setMaximumPoolSize(2);
    return SqlDatabase.create(config, "classpath:db/migration");
  }
}
