package org.aincraft.db.paper;

import com.google.common.base.Preconditions;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ClusterSettings;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.aincraft.db.ConnectionException;
import org.aincraft.db.ConnectionSource;
import org.aincraft.db.DatabaseType;
import org.aincraft.db.HikariSourceImpl;
import org.aincraft.db.MongoConnectionSourceImpl;
import org.aincraft.db.SQLiteConnectionSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;

public final class ConnectionSourceFactoryImpl implements ConnectionSourceFactory {

  private static final int DEFAULT_MONGO_TCP_PORT = 27017;
  private final Plugin plugin;

  public ConnectionSourceFactoryImpl(Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public ConnectionSource create(DatabaseType type, ConfigurationSection configuration)
      throws IllegalArgumentException, ConnectionException {
    Preconditions.checkNotNull(type, "missing required parameter: type");
    Preconditions.checkNotNull(configuration, "missing required parameter: configuration");
    return switch (type) {
      case MYSQL, POSTGRES ->
          new HikariSourceImpl(new HikariDataSource(parseHikariConfig(configuration)), type);
      case SQLITE -> sqlite(configuration);
      case MONGO ->
          new MongoConnectionSourceImpl(MongoClients.create(parseClientSettings(configuration)));
    };
  }

  private ConnectionSource sqlite(ConfigurationSection configuration) {
    String path = configuration.getString("path");
    Preconditions.checkNotNull(path, "missing required field: database.path");
    Path dbFile = plugin.getDataFolder().toPath().resolve(path);
    Path parent = dbFile.getParent();
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

  private static MongoClientSettings parseClientSettings(ConfigurationSection configuration) {
    String uri = configuration.getString("connection-uri");
    String host = configuration.getString("host", "localhost");
    int port = configuration.getInt("port", DEFAULT_MONGO_TCP_PORT);
    String username = configuration.getString("username");
    String password = configuration.getString("password");
    String authDb = configuration.getString("auth-database", "admin");
    if (uri != null && !uri.isEmpty()) {
      return MongoClientSettings.builder().applyConnectionString(new ConnectionString(uri)).build();
    }
    MongoClientSettings.Builder builder = MongoClientSettings.builder();
    builder.applyToClusterSettings(
        (ClusterSettings.Builder cluster) ->
            cluster.hosts(Collections.singletonList(new ServerAddress(host, port))));
    if (username != null && !username.isEmpty() && password != null) {
      MongoCredential credential =
          MongoCredential.createCredential(username, authDb, password.toCharArray());
      builder.credential(credential);
    }
    return builder.build();
  }
}
