package org.aincraft.common.inventory;

import java.util.List;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemStack {

  @NotNull ItemType type();

  int amount();

  void setAmount(int amount);

  @Nullable Component displayName();

  @Nullable List<Component> lore();

  boolean hasItemMeta();

  @Nullable ItemMeta meta();

  void setMeta(@Nullable ItemMeta meta);

  boolean isSimilar(@Nullable ItemStack other);
  boolean isEmpty();

  @NotNull ItemStack clone();

  int maxStackSize();

  boolean editMeta(@NotNull java.util.function.Consumer<ItemMeta> consumer);

  @NotNull ItemStack asOne();

  @NotNull ItemStack asQuantity(int amount);

  @NotNull ItemStack withAmount(int amount);
}
