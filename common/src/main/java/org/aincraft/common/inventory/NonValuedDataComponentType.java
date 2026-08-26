package org.aincraft.common.inventory;

import org.jetbrains.annotations.NotNull;

/**
 * A {@link DataComponentType} that represents only presence or absence,
 * with no associated value.
 */
public interface NonValuedDataComponentType extends DataComponentType<Void> {

  @Override
  default @NotNull Class<Void> type() {
    return Void.class;
  }

  default boolean isValued() {
    return false;
  }
}
