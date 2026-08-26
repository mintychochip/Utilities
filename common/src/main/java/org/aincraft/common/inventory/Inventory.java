package org.aincraft.common.inventory;

import org.aincraft.common.location.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Inventory {

  int size();

  @NotNull InventoryType type();

  @Nullable ItemStack getItem(int slot);

  void setItem(int slot, @Nullable ItemStack item);

  @Nullable ItemStack[] contents();

  void setContents(@NotNull ItemStack[] items);

  void clear();

  boolean isEmpty();

  @Nullable Location location();

  @Nullable InventoryHolder holder();
}
