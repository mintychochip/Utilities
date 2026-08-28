package org.aincraft.db.sql;

/** Features that a configured SQL database may expose. */
public enum SqlFeature {
  TRANSACTIONS,
  SAVEPOINTS,
  BATCH_UPDATES
}
