package org.aincraft.common.inventory;

import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import org.aincraft.common.effect.Enchantment;
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

  default boolean hasEnchant(@NotNull Enchantment enchantment) {
    throw new UnsupportedOperationException();
  }

  default int enchantLevel(@NotNull Enchantment enchantment) {
    throw new UnsupportedOperationException();
  }

  default @NotNull Map<Enchantment, Integer> enchantments() {
    throw new UnsupportedOperationException();
  }

  default void addEnchant(@NotNull Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
    throw new UnsupportedOperationException();
  }

  default void addEnchant(@NotNull Enchantment enchantment, int level) {
    addEnchant(enchantment, level, false);
  }

  default int removeEnchant(@NotNull Enchantment enchantment) {
    throw new UnsupportedOperationException();
  }
}
