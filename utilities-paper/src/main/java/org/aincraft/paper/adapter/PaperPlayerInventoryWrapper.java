package org.aincraft.paper.adapter;

import org.aincraft.api.domain.inventory.EquipmentSlot;
import org.aincraft.api.domain.inventory.ItemStack;
import org.aincraft.bukkit.adapter.BukkitPlayerInventoryWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PaperPlayerInventoryWrapper extends BukkitPlayerInventoryWrapper {

  public PaperPlayerInventoryWrapper(@NotNull org.bukkit.inventory.PlayerInventory inventory) {
    super(inventory);
  }

  @Override
  public @Nullable ItemStack getItem(int slot) {
    org.bukkit.inventory.ItemStack item = getBukkitPlayerInventory().getItem(slot);
    return item == null ? null : PaperAdapters.adapt(item);
  }

  @Override
  public void setItem(int slot, @Nullable ItemStack item) {
    getBukkitPlayerInventory().setItem(slot, item == null ? null : PaperAdapters.toBukkit(item));
  }

  @Override
  public @Nullable ItemStack[] contents() {
    org.bukkit.inventory.ItemStack[] items = getBukkitPlayerInventory().getContents();
    if (items == null) return null;
    ItemStack[] result = new ItemStack[items.length];
    for (int i = 0; i < items.length; i++) {
      result[i] = items[i] == null ? null : PaperAdapters.adapt(items[i]);
    }
    return result;
  }

  @Override
  public @Nullable ItemStack getItem(@NotNull EquipmentSlot slot) {
    org.aincraft.api.domain.inventory.ItemStack item = super.getItem(slot);
    if (!(item instanceof org.aincraft.bukkit.adapter.BukkitItemStackWrapper wrapper)) {
      return item;
    }
    return PaperAdapters.adapt(wrapper.getBukkitItemStack());
  }
}
