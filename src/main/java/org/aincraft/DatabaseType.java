package org.aincraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

  public String[] getTables() {
    try (InputStream resourceStream = ResourceExtractor.getResourceStream(
        "%s.sql".formatted(identifier));
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(resourceStream, StandardCharsets.UTF_8))) {
      Stream<String> lines = reader.lines();
      String tables = lines.collect(Collectors.joining("\n"));
      return Arrays.stream(tables.split(";"))
          .map(s -> s.trim() + ";")
          .filter(s -> !s.equals(";"))
          .toArray(String[]::new);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
