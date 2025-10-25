package org.aincraft;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import java.util.concurrent.atomic.AtomicBoolean;
import org.aincraft.ConnectionSource.MongoConnectionSource;

final class MongoConnectionSourceImpl implements MongoConnectionSource {

  private final MongoClient client;
  private final AtomicBoolean closed = new AtomicBoolean(false);

  MongoConnectionSourceImpl(MongoClient client) {
    this.client = client;
  }

  @Override
  public void close() throws ConnectionException {
    try {
      client.close();
      closed.set(true);
    } catch (Exception e) {
      throw new ConnectionException("failed to close mongo client");
    }
  }

  @Override
  public boolean closed() throws ConnectionException {
   return closed.get();
  }

  @Override
  public DatabaseType getType() {
    return DatabaseType.MONGO;
  }

  @Override
  public MongoClient getClient() {
    return client;
  }

  @Override
  public MongoDatabase getDatabase(String databaseName) {
    return client.getDatabase(databaseName);
  }
}
