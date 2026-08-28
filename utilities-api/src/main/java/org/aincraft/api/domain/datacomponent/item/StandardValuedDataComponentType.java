package org.aincraft.api.domain.datacomponent.item;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.inventory.DataComponentType;
import org.jetbrains.annotations.NotNull;

/** A {@link DataComponentType.Valued} backed by {@link StandardDataComponentType}. */
public final class StandardValuedDataComponentType<T> extends StandardDataComponentType<T>
    implements DataComponentType.Valued<T> {

  public StandardValuedDataComponentType(@NotNull Key key, @NotNull Class<T> type) {
    super(key, type);
  }
}
