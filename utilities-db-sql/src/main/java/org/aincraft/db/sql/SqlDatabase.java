package org.aincraft.db.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;

/**
 * Owns a Hikari-backed Jdbi instance and an explicitly-run Flyway migration set.
 *
 * <p>Jdbi remains the SQL access API. This class only wires the pool, runtime capability discovery,
 * and migration lifecycle together; it does not define a repository or query abstraction.
 */
public final class SqlDatabase implements AutoCloseable {

  private final HikariDataSource dataSource;
  private final Jdbi jdbi;
  private final Flyway flyway;
  private final SqlCapabilities capabilities;

  private SqlDatabase(
      HikariDataSource dataSource, Jdbi jdbi, Flyway flyway, SqlCapabilities capabilities) {
    this.dataSource = dataSource;
    this.jdbi = jdbi;
    this.flyway = flyway;
    this.capabilities = capabilities;
  }

  /**
   * Creates a SQL database backed by the supplied Hikari configuration.
   *
   * <p>Migration execution is explicit: call {@link #migrate()} after creation when migrations are
   * desired. With no locations, Flyway uses its default {@code classpath:db/migration} location.
   *
   * @param hikariConfig pool and JDBC configuration
   * @param migrationLocations optional Flyway migration locations
   * @return a ready-to-use database
   * @throws NullPointerException if the configuration or a migration location is null
   */
  public static SqlDatabase create(HikariConfig hikariConfig, String... migrationLocations) {
    Objects.requireNonNull(hikariConfig, "hikariConfig");
    Objects.requireNonNull(migrationLocations, "migrationLocations");
    String[] locations = migrationLocations.clone();
    for (String location : locations) {
      Objects.requireNonNull(location, "migration location");
    }

    HikariDataSource dataSource = new HikariDataSource(hikariConfig);
    try {
      Jdbi jdbi = Jdbi.create(dataSource);
      SqlCapabilities capabilities = inspectCapabilities(jdbi);
      var flywayConfiguration = Flyway.configure().dataSource(dataSource);
      if (locations.length > 0) {
        flywayConfiguration.locations(locations);
      }
      Flyway flyway = flywayConfiguration.load();
      return new SqlDatabase(dataSource, jdbi, flyway, capabilities);
    } catch (RuntimeException e) {
      dataSource.close();
      throw e;
    }
  }

  public Jdbi jdbi() {
    return jdbi;
  }

  public SqlCapabilities capabilities() {
    return capabilities;
  }

  /** Applies pending Flyway migrations. */
  public void migrate() {
    flyway.migrate();
  }

  /** Validates the configured Flyway migrations against the database. */
  public void validate() {
    flyway.validate();
  }

  public boolean closed() {
    return dataSource.isClosed();
  }

  @Override
  public void close() {
    dataSource.close();
  }

  private static SqlCapabilities inspectCapabilities(Jdbi jdbi) {
    return jdbi.withHandle(
        handle -> {
          try {
            return DefaultSqlCapabilities.from(handle.getConnection().getMetaData());
          } catch (SQLException e) {
            throw new IllegalStateException("failed to inspect SQL database capabilities", e);
          }
        });
  }

  private record DefaultSqlCapabilities(
      String databaseProductName,
      String databaseProductVersion,
      boolean supportsTransactions,
      boolean supportsSavepoints,
      boolean supportsBatchUpdates,
      Set<Integer> transactionIsolationLevels)
      implements SqlCapabilities {

    private static DefaultSqlCapabilities from(DatabaseMetaData metadata) throws SQLException {
      Set<Integer> isolationLevels =
          Set.of(
              Connection.TRANSACTION_NONE,
              Connection.TRANSACTION_READ_UNCOMMITTED,
              Connection.TRANSACTION_READ_COMMITTED,
              Connection.TRANSACTION_REPEATABLE_READ,
              Connection.TRANSACTION_SERIALIZABLE);
      isolationLevels =
          isolationLevels.stream()
              .filter(
                  level -> {
                    try {
                      return metadata.supportsTransactionIsolationLevel(level);
                    } catch (SQLException e) {
                      throw new IllegalStateException(
                          "failed to inspect transaction isolation levels", e);
                    }
                  })
              .collect(java.util.stream.Collectors.toUnmodifiableSet());
      return new DefaultSqlCapabilities(
          metadata.getDatabaseProductName(),
          metadata.getDatabaseProductVersion(),
          metadata.supportsTransactions(),
          metadata.supportsSavepoints(),
          metadata.supportsBatchUpdates(),
          isolationLevels);
    }

    @Override
    public boolean supports(SqlFeature feature) {
      Objects.requireNonNull(feature, "feature");
      return switch (feature) {
        case TRANSACTIONS -> supportsTransactions;
        case SAVEPOINTS -> supportsSavepoints;
        case BATCH_UPDATES -> supportsBatchUpdates;
      };
    }

    @Override
    public boolean supportsTransactionIsolationLevel(int level) {
      return transactionIsolationLevels.contains(level);
    }
  }
}
