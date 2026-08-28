package org.aincraft.db.sql;

/**
 * Runtime capabilities reported by the JDBC driver and database server.
 *
 * <p>Capabilities describe the connected database, not every SQL statement or schema-specific
 * behavior.
 */
public interface SqlCapabilities {

  String databaseProductName();

  String databaseProductVersion();

  boolean supports(SqlFeature feature);

  boolean supportsTransactionIsolationLevel(int level);
}
