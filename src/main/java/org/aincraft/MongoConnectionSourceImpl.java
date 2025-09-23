package org.aincraft;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.aincraft.ConnectionSource.MongoConnectionSource;

public record MongoConnectionSourceImpl(MongoClient mongoClient) implements MongoConnectionSource {

  @Override
  public MongoDatabase database(String name) {
    return mongoClient.getDatabase(name);
  }

  @Override
  public void close() throws ConnectionException {

  }

  @Override
  public boolean closed() throws ConnectionException {
    return false;
  }

  @Override
  public DatabaseType type() {
    return null;
  }
}
