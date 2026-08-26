package org.aincraft;

import com.google.common.base.Preconditions;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.aincraft.db.ConnectionSource;
import org.aincraft.db.DatabaseType;
import org.aincraft.db.HikariSourceImpl;
import org.aincraft.db.SQLiteConnectionSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

final class SQLConnectionSourceFactoryImpl implements ConnectionSourceFactory {

  private final Plugin plugin;

  public SQLConnectionSourceFactoryImpl(Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public ConnectionSource create(DatabaseType type, ConfigurationSection configuration)
      throws IllegalArgumentException {
    Preconditions.checkArgument(type.isRelational());
    return switch (type) {
      case MYSQL, POSTGRES -> new HikariSourceImpl(new HikariDataSource(parseHikariConfig(configuration)), type);
      case SQLITE -> SQLiteConnectionSource.create(
          plugin.getDataFolder().toPath().resolve(configuration.getString("path")));
      default -> throw new IllegalArgumentException("failed to create a source for this database type");
    };
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
