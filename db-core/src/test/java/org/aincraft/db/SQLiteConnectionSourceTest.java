package org.aincraft.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

class SQLiteConnectionSourceTest {

  @TempDir Path tmp;

  private Path dbFile() {
    return tmp.resolve("test.db");
  }

  @Test
  void roundTripsQuery() throws Exception {
    try (SQLiteConnectionSource source = SQLiteConnectionSource.create(dbFile());
        Connection c = source.getConnection();
        Statement s = c.createStatement()) {
      s.execute("CREATE TABLE t (id INTEGER PRIMARY KEY, name TEXT)");
      s.execute("INSERT INTO t (name) VALUES ('hello')");
      try (ResultSet rs = s.executeQuery("SELECT name FROM t")) {
        assertTrue(rs.next());
        assertEquals("hello", rs.getString(1));
      }
    }
  }

  @Test
  void initializesPragmas() throws Exception {
    try (SQLiteConnectionSource source = SQLiteConnectionSource.create(dbFile());
        Connection c = source.getConnection();
        Statement s = c.createStatement()) {
      try (ResultSet rs = s.executeQuery("PRAGMA foreign_keys")) {
        assertTrue(rs.next());
        assertEquals(1, rs.getInt(1));
      }
      try (ResultSet rs = s.executeQuery("PRAGMA synchronous")) {
        assertTrue(rs.next());
        assertEquals(1, rs.getInt(1)); // NORMAL == 1
      }
    }
  }

  @Test
  void lifecycleTransitions() {
    SQLiteConnectionSource source = SQLiteConnectionSource.create(dbFile());
    assertFalse(source.closed());
    source.close();
    assertTrue(source.closed());
  }

  @Test
  void appliesSchemaFromInputStream() throws Exception {
    String schema =
        "CREATE TABLE a (id INTEGER PRIMARY KEY);" + "CREATE TABLE b (id INTEGER PRIMARY KEY);";
    try (SQLiteConnectionSource source =
            SQLiteConnectionSource.create(
                dbFile(), new ByteArrayInputStream(schema.getBytes(StandardCharsets.UTF_8)));
        Connection c = source.getConnection();
        Statement s = c.createStatement();
        ResultSet rs =
            s.executeQuery("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name")) {
      assertTrue(rs.next());
      assertEquals("a", rs.getString(1));
      assertTrue(rs.next());
      assertEquals("b", rs.getString(1));
      assertFalse(rs.next());
    }
  }

  @Test
  void rollsBackSchemaOnFailure() throws Exception {
    String schema = "CREATE TABLE ok_t (id INTEGER PRIMARY KEY); BAD SQL;";
    assertThrows(
        ConnectionException.class,
        () ->
            SQLiteConnectionSource.create(
                dbFile(), new ByteArrayInputStream(schema.getBytes(StandardCharsets.UTF_8))));
    try (SQLiteConnectionSource source = SQLiteConnectionSource.create(dbFile());
        Connection c = source.getConnection();
        Statement s = c.createStatement();
        ResultSet rs =
            s.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='ok_t'")) {
      assertFalse(rs.next());
    }
  }
}
