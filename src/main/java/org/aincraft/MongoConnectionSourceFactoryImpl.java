package org.aincraft;

import com.google.common.base.Preconditions;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ClusterSettings;
import java.util.Collections;
import org.bukkit.configuration.ConfigurationSection;

final class MongoConnectionSourceFactoryImpl implements ConnectionSourceFactory {

  private static final int DEFAULT_MONGO_TCP_PORT = 27017;
  @Override
  public ConnectionSource create(DatabaseType type, ConfigurationSection configuration)
      throws IllegalArgumentException {
    if (type != DatabaseType.MONGO) {
      throw new IllegalArgumentException("failed to create connection source for this type");
    }
    try {
      return new MongoConnectionSourceImpl(MongoClients.create(parseClientSettings(configuration)));
    } catch (Exception e) {
      throw new ConnectionException("failed to create mongo client");
    }
  }

  private static MongoClientSettings parseClientSettings(ConfigurationSection configuration) {
    String uri = configuration.getString("connection-uri");
    String host = configuration.getString("host", "localhost");
    int port = configuration.getInt("port", DEFAULT_MONGO_TCP_PORT);
    String username = configuration.getString("username");
    String password = configuration.getString("password");
    String authDb = configuration.getString("auth-database", "admin");
    if (uri != null && !uri.isEmpty()) {
      return MongoClientSettings.builder()
          .applyConnectionString(new ConnectionString(uri))
          .build();
    }
    MongoClientSettings.Builder builder = MongoClientSettings.builder();
    builder.applyToClusterSettings((ClusterSettings.Builder cluster) ->
        cluster.hosts(Collections.singletonList(new ServerAddress(host, port)))
    );
    if (username != null && !username.isEmpty() && password != null) {
      MongoCredential credential = MongoCredential.createCredential(
          username,
          authDb,
          password.toCharArray()
      );
      builder.credential(credential);
    }
    return builder.build();
  }
}
