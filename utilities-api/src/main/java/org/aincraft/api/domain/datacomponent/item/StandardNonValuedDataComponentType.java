package org.aincraft.api.domain.datacomponent.item;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.inventory.DataComponentType;
import org.jetbrains.annotations.NotNull;

/** A {@link DataComponentType.NonValued} backed by {@link StandardDataComponentType}. */
public final class StandardNonValuedDataComponentType extends StandardDataComponentType<Void>
    implements DataComponentType.NonValued {

  public StandardNonValuedDataComponentType(@NotNull Key key) {
    super(key, Void.class);
  }
}
