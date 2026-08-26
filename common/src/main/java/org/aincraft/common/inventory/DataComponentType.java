package org.aincraft.common.inventory;

import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

public interface DataComponentType<T> extends Keyed {

  @NotNull Class<T> type();
}
