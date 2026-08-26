package org.aincraft.common.inventory;

import java.util.Map;
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

  @NotNull Map<Integer, ItemStack> addItem(@NotNull ItemStack... items);

  @NotNull Map<Integer, ItemStack> removeItem(@NotNull ItemStack... items);

  boolean contains(@NotNull ItemType type);

  boolean contains(@NotNull ItemStack item);

  boolean containsAtLeast(@NotNull ItemStack item, int amount);

  int first(@NotNull ItemStack item);

  int firstEmpty();

  void clear();

  boolean isEmpty();

  @Nullable Location location();

  @Nullable InventoryHolder holder();
}
