package org.aincraft.api.domain.inventory;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

/** Creates platform-backed item stacks and metadata. */
public interface ItemFactory {

  @NotNull
  ItemMeta createItemMeta(@NotNull ItemType type);

  @NotNull
  ItemStack createItemStack(@NotNull Key materialKey);

  boolean metaEquals(@NotNull ItemMeta first, @NotNull ItemMeta second);
}
