package org.aincraft.api.domain.inventory;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.aincraft.api.domain.attribute.AttributeModifier;
import org.aincraft.api.domain.effect.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ItemMeta {

  @Nullable
  Component displayName();

  void setDisplayName(@Nullable Component name);

  /** Returns the explicit custom name component, when present. */
  default @Nullable Component customName() {
    return displayName();
  }

  /** Sets the explicit custom name component. */
  default void customName(@Nullable Component name) {
    setDisplayName(name);
  }

  default boolean hasCustomName() {
    return hasDisplayName();
  }

  /** Returns the item-name component, when present. */
  default @Nullable Component itemName() {
    return displayName();
  }

  /** Sets the item-name component. */
  default void itemName(@Nullable Component name) {
    setDisplayName(name);
  }

  default boolean hasItemName() {
    return hasDisplayName();
  }

  @Nullable
  List<Component> lore();

  void setLore(@Nullable List<Component> lore);

  boolean hasDisplayName();

  boolean hasLore();

  boolean isUnbreakable();

  void setUnbreakable(boolean unbreakable);

  int customModelData();

  void setCustomModelData(int data);

  @NotNull
  Map<Enchantment, Integer> enchantments();

  boolean hasEnchant(@NotNull Enchantment enchantment);

  int enchantLevel(@NotNull Enchantment enchantment);

  void addEnchant(@NotNull Enchantment enchantment, int level, boolean ignoreLevelRestriction);

  void removeEnchant(@NotNull Enchantment enchantment);

  @NotNull
  Map<Key, Collection<AttributeModifier>> attributeModifiers();

  @Nullable
  Collection<AttributeModifier> getAttributeModifiers(@NotNull Key attribute);

  boolean hasAttributeModifiers();

  void addAttributeModifier(@NotNull Key attribute, @NotNull AttributeModifier modifier);

  void removeAttributeModifier(@NotNull Key attribute);

  void removeAttributeModifier(@NotNull Key attribute, @NotNull AttributeModifier modifier);

  /** Returns all attribute modifiers associated with a single equipment slot. */
  default @Nullable Map<Key, Collection<AttributeModifier>> getAttributeModifiers(
      @NotNull EquipmentSlot slot) {
    throw new UnsupportedCapabilityException(Capability.ATTRIBUTE_MODIFIER);
  }

  /** Replaces the complete attribute-modifier map. */
  default void setAttributeModifiers(@NotNull Map<Key, Collection<AttributeModifier>> modifiers) {
    throw new UnsupportedCapabilityException(Capability.ATTRIBUTE_MODIFIER);
  }

  /** Removes all modifiers targeting a single equipment slot. */
  default boolean removeAttributeModifier(@NotNull EquipmentSlot slot) {
    throw new UnsupportedCapabilityException(Capability.ATTRIBUTE_MODIFIER);
  }

  boolean hasData(@NotNull DataComponentType<?> type);

  <T> @Nullable T getData(@NotNull DataComponentType<T> type);

  <T> void setData(@NotNull DataComponentType<T> type, @Nullable T value);

  void resetData(@NotNull DataComponentType<?> type);

  @NotNull
  Set<DataComponentType<?>> dataComponentTypes();

  @NotNull
  Set<ItemFlag> itemFlags();

  boolean hasItemFlag(@NotNull ItemFlag flag);

  void addItemFlags(@NotNull ItemFlag... flags);

  void removeItemFlags(@NotNull ItemFlag... flags);

  default <T> boolean has(@NotNull DataComponentType<T> type) {
    return hasData(type);
  }

  default <T> @Nullable T get(@NotNull DataComponentType<T> type) {
    return getData(type);
  }

  default <T> @NotNull T getOrDefault(@NotNull DataComponentType<T> type, @NotNull T defaultValue) {
    T val = getData(type);
    return val != null ? val : defaultValue;
  }

  default <T> void set(@NotNull DataComponentType<T> type, @Nullable T value) {
    setData(type, value);
  }

  default void unset(@NotNull DataComponentType<?> type) {
    resetData(type);
  }
}
