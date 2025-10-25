package org.aincraft;

public enum DatabaseType {
  MYSQL("mysql"),
  POSTGRES("postgres"),
  SQLITE("sqlite");
  private final String identifier;

  DatabaseType(String identifier) {
    this.identifier = identifier;
  }

  public String getIdentifier() {
    return identifier;
  }
}
