package org.aincraft;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import java.sql.Connection;

public interface ConnectionSource extends AutoCloseable {

  void close() throws ConnectionException;

  boolean closed() throws ConnectionException;

  DatabaseType getType();

  interface SQLConnectionSource extends ConnectionSource {

    Connection getConnection() throws ConnectionException;
  }

  interface MongoConnectionSource extends ConnectionSource {

    MongoClient getClient();

    MongoDatabase getDatabase(String databaseName);
  }
}
