package org.aincraft.bukkit.adapter;

import org.aincraft.api.domain.inventory.Inventory;
import org.aincraft.api.domain.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitInventoryHolderWrapper implements InventoryHolder {

  private final org.bukkit.inventory.InventoryHolder holder;

  public BukkitInventoryHolderWrapper(@NotNull org.bukkit.inventory.InventoryHolder holder) {
    this.holder = holder;
  }

  public @NotNull org.bukkit.inventory.InventoryHolder getBukkitInventoryHolder() {
    return holder;
  }

  @Override
  public @Nullable Inventory inventory() {
    org.bukkit.inventory.Inventory inv = holder.getInventory();
    return inv != null ? BukkitAdapters.adapt(inv) : null;
  }
}
