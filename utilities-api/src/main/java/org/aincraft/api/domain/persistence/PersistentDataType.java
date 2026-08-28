package org.aincraft.api.domain.persistence;

import org.jetbrains.annotations.NotNull;

/**
 * Describes the value type of an entry in a {@link PersistentDataContainer}.
 *
 * @param <T> the persisted Java type
 */
public interface PersistentDataType<T> {

  /**
   * Returns the value class this type represents.
   *
   * @return the value class
   */
  @NotNull
  Class<T> type();
}
