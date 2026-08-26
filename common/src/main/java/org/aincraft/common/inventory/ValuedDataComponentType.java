package org.aincraft.common.inventory;

/**
 * A {@link DataComponentType} that has an associated value.
 */
public interface ValuedDataComponentType<T> extends DataComponentType<T> {

  default boolean isValued() {
    return true;
  }
}
