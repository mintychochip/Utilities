package org.aincraft.paper.adapter;

import org.aincraft.api.domain.inventory.ItemStack;
import org.aincraft.bukkit.adapter.BukkitInventoryWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaperInventoryWrapper extends BukkitInventoryWrapper {

  public PaperInventoryWrapper(@NotNull org.bukkit.inventory.Inventory inventory) {
    super(inventory);
  }

  @Override
  public @Nullable ItemStack getItem(int slot) {
    org.bukkit.inventory.ItemStack item = getBukkitInventory().getItem(slot);
    return item == null ? null : PaperAdapters.adapt(item);
  }

  @Override
  public void setItem(int slot, @Nullable ItemStack item) {
    getBukkitInventory().setItem(slot, item == null ? null : PaperAdapters.toBukkit(item));
  }

  @Override
  public @Nullable ItemStack[] contents() {
    org.bukkit.inventory.ItemStack[] items = getBukkitInventory().getContents();
    if (items == null) return null;
    ItemStack[] result = new ItemStack[items.length];
    for (int i = 0; i < items.length; i++) {
      result[i] = items[i] == null ? null : PaperAdapters.adapt(items[i]);
    }
    return result;
  }
}
