package org.aincraft;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import org.aincraft.ConnectionSource.SQLConnectionSource;

public final class HikariSourceImpl implements SQLConnectionSource {

  private final HikariDataSource dataSource;
  private final DatabaseType type;

  public HikariSourceImpl(HikariDataSource dataSource, DatabaseType type) {
    this.dataSource = dataSource;
    this.type = type;
  }

  @Override
  public void close() throws ConnectionException {
    if (dataSource != null) {
      dataSource.close();
    }
  }

  @Override
  public boolean closed() throws ConnectionException {
    return dataSource.isClosed();
  }

  @Override
  public DatabaseType getType() {
    return type;
  }

  @Override
  public Connection getConnection() throws ConnectionException {
    try {
      return dataSource.getConnection();
    } catch (SQLException e) {
      throw new ConnectionException("");
    }
  }
}
