package org.aincraft.common.inventory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.aincraft.common.attribute.Attribute;
import org.aincraft.common.attribute.AttributeModifier;
import org.aincraft.common.effect.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemMeta {

  @Nullable Component displayName();

  void setDisplayName(@Nullable Component name);

  @Nullable List<Component> lore();

  void setLore(@Nullable List<Component> lore);

  boolean hasDisplayName();

  boolean hasLore();

  boolean isUnbreakable();

  void setUnbreakable(boolean unbreakable);

  int customModelData();

  void setCustomModelData(int data);

  @NotNull Map<Enchantment, Integer> enchantments();

  boolean hasEnchant(@NotNull Enchantment enchantment);

  int enchantLevel(@NotNull Enchantment enchantment);

  void addEnchant(@NotNull Enchantment enchantment, int level, boolean ignoreLevelRestriction);

  void removeEnchant(@NotNull Enchantment enchantment);

  @NotNull Map<Attribute, Collection<AttributeModifier>> attributeModifiers();

  @Nullable Collection<AttributeModifier> getAttributeModifiers(@NotNull Attribute attribute);

  boolean hasAttributeModifiers();

  void addAttributeModifier(@NotNull Attribute attribute, @NotNull AttributeModifier modifier);

  void removeAttributeModifier(@NotNull Attribute attribute);

  void removeAttributeModifier(@NotNull Attribute attribute, @NotNull AttributeModifier modifier);

  boolean hasData(@NotNull DataComponentType<?> type);

  <T> @Nullable T getData(@NotNull DataComponentType<T> type);

  <T> void setData(@NotNull DataComponentType<T> type, @Nullable T value);

  void resetData(@NotNull DataComponentType<?> type);

  @NotNull Set<DataComponentType<?>> dataComponentTypes();

  @NotNull Set<ItemFlag> itemFlags();

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
