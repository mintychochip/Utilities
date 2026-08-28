package org.aincraft.api.domain.inventory;

import net.kyori.adventure.text.Component;
import org.aincraft.api.domain.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface InventoryView {

  @NotNull
  Inventory topInventory();

  @NotNull
  Inventory bottomInventory();

  @Nullable
  Player player();

  @NotNull
  InventoryType type();

  @Nullable
  ItemStack getItem(int rawSlot);

  void setItem(int rawSlot, @Nullable ItemStack item);

  @Nullable
  ItemStack cursor();

  void setCursor(@Nullable ItemStack item);

  @NotNull
  Component title();

  @NotNull
  Component originalTitle();

  void title(@NotNull Component title);

  void close();
}
