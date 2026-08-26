package org.aincraft.common.inventory;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link NonValuedDataComponentType} backed by {@link StandardDataComponentType}.
 */
public final class StandardNonValuedDataComponentType
    extends StandardDataComponentType<Void> implements NonValuedDataComponentType {

  public StandardNonValuedDataComponentType(@NotNull Key key) {
    super(key, Void.class);
  }
}
