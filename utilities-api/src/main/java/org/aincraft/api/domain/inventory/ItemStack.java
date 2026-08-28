package org.aincraft.api.domain.inventory;

import net.kyori.adventure.text.Component;
import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.aincraft.api.domain.effect.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface ItemStack {

  @NotNull
  ItemType type();

  int amount();

  void setAmount(int amount);

  @Nullable
  Component displayName();

  @Nullable
  List<Component> lore();

  boolean hasItemMeta();

  @Nullable
  ItemMeta meta();

  void setMeta(@Nullable ItemMeta meta);

  boolean isSimilar(@Nullable ItemStack other);

  boolean isEmpty();

  @NotNull
  ItemStack clone();

  int maxStackSize();

  boolean editMeta(@NotNull java.util.function.Consumer<ItemMeta> consumer);

  @NotNull
  ItemStack asOne();

  @NotNull
  ItemStack asQuantity(int amount);

  @NotNull
  ItemStack withAmount(int amount);

  default boolean hasData(@NotNull DataComponentType<?> type) {
    throw new UnsupportedCapabilityException(Capability.ITEM_DATA_COMPONENT);
  }

  default <T> @Nullable T getData(@NotNull DataComponentType<T> type) {
    throw new UnsupportedCapabilityException(Capability.ITEM_DATA_COMPONENT);
  }

  default <T> void setData(@NotNull DataComponentType<T> type, @Nullable T value) {
    throw new UnsupportedCapabilityException(Capability.ITEM_DATA_COMPONENT);
  }

  default void setData(@NotNull DataComponentType.NonValued type) {
    throw new UnsupportedCapabilityException(Capability.ITEM_DATA_COMPONENT);
  }

  default void resetData(@NotNull DataComponentType<?> type) {
    throw new UnsupportedCapabilityException(Capability.ITEM_DATA_COMPONENT);
  }

  @NotNull
  default java.util.Set<DataComponentType<?>> dataComponentTypes() {
    throw new UnsupportedCapabilityException(Capability.ITEM_DATA_COMPONENT);
  }

  @NotNull
  default java.util.Set<DataComponentType<?>> dataTypes() {
    return dataComponentTypes();
  }

  default boolean hasEnchant(@NotNull Enchantment enchantment) {
    throw new UnsupportedOperationException();
  }

  default int enchantLevel(@NotNull Enchantment enchantment) {
    throw new UnsupportedOperationException();
  }

  default @NotNull Map<Enchantment, Integer> enchantments() {
    throw new UnsupportedOperationException();
  }

  default void addEnchant(
      @NotNull Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
    throw new UnsupportedOperationException();
  }

  default void addEnchant(@NotNull Enchantment enchantment, int level) {
    addEnchant(enchantment, level, false);
  }

  default int removeEnchant(@NotNull Enchantment enchantment) {
    throw new UnsupportedOperationException();
  }
}
