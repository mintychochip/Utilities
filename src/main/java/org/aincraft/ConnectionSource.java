package org.aincraft;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import java.sql.Connection;

public interface ConnectionSource {

  void close() throws ConnectionException;

  boolean closed() throws ConnectionException;

  DatabaseType type();

  interface SQLConnectionSource extends ConnectionSource {

    Connection connection() throws ConnectionException;
  }
}
