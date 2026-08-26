package org.aincraft.db;

public enum DatabaseType {
  MYSQL("mysql"),
  MONGO("mongo"),
  POSTGRES("postgres"),
  SQLITE("sqlite");
  private final String identifier;

  DatabaseType(String identifier) {
    this.identifier = identifier;
  }

  public boolean isRelational() {
    return this != MONGO;
  }

  public String getIdentifier() {
    return identifier;
  }
}
