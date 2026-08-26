package org.aincraft.common.inventory;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link ValuedDataComponentType} backed by {@link StandardDataComponentType}.
 */
public final class StandardValuedDataComponentType<T>
    extends StandardDataComponentType<T> implements ValuedDataComponentType<T> {

  public StandardValuedDataComponentType(@NotNull Key key, @NotNull Class<T> type) {
    super(key, type);
  }
}
