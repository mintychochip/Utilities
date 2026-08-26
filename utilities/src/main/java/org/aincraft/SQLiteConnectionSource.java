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
        for (String stmt : sql.split(";")) {
          String trimmed = stmt.trim();
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
