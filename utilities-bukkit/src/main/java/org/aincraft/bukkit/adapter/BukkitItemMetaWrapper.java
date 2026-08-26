package org.aincraft.bukkit.adapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.aincraft.common.effect.Enchantment;
import org.aincraft.common.inventory.DataComponentType;
import org.aincraft.common.inventory.ItemMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitItemMetaWrapper implements ItemMeta {

  private final org.bukkit.inventory.meta.ItemMeta meta;

  public BukkitItemMetaWrapper(@NotNull org.bukkit.inventory.meta.ItemMeta meta) {
    this.meta = Objects.requireNonNull(meta, "meta cannot be null");
  }

  public @NotNull org.bukkit.inventory.meta.ItemMeta getBukkitItemMeta() {
    return meta;
  }

  @Override
  public @Nullable Component displayName() {
    return meta.hasDisplayName() ? LegacyComponentSerializer.legacySection().deserialize(meta.getDisplayName()) : null;
  }

  @Override
  public void setDisplayName(@Nullable Component name) {
    meta.setDisplayName(name != null ? LegacyComponentSerializer.legacySection().serialize(name) : null);
  }

  @Override
  public @Nullable List<Component> lore() {
    List<String> bLore = meta.getLore();
    return bLore != null ? bLore.stream().<Component>map(LegacyComponentSerializer.legacySection()::deserialize).toList() : null;
  }

  @Override
  public void setLore(@Nullable List<Component> lore) {
    meta.setLore(lore != null ? lore.stream().map(LegacyComponentSerializer.legacySection()::serialize).toList() : null);
  }

  @Override
  public boolean hasDisplayName() {
    return meta.hasDisplayName();
  }

  @Override
  public boolean hasLore() {
    return meta.hasLore();
  }

  @Override
  public boolean isUnbreakable() {
    return meta.isUnbreakable();
  }

  @Override
  public void setUnbreakable(boolean unbreakable) {
    meta.setUnbreakable(unbreakable);
  }

  @Override
  public int customModelData() {
    return meta.hasCustomModelData() ? meta.getCustomModelData() : 0;
  }

  @Override
  public void setCustomModelData(int data) {
    meta.setCustomModelData(data);
  }

  @Override
  public @NotNull Map<Enchantment, Integer> enchantments() {
    Map<Enchantment, Integer> result = new HashMap<>();
    for (Map.Entry<org.bukkit.enchantments.Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
      org.bukkit.enchantments.Enchantment bEnch = entry.getKey();
      Key key = Key.key(bEnch.getKey().getNamespace(), bEnch.getKey().getKey());
      result.put(new Enchantment() {
        @Override public Key key() { return key; }
        @Override public int maxLevel() { return bEnch.getMaxLevel(); }
        @Override public int startLevel() { return bEnch.getStartLevel(); }
        @Override public boolean isCursed() { return bEnch.isCursed(); }
        @Override public boolean isTreasure() { return bEnch.isTreasure(); }
      }, entry.getValue());
    }
    return result;
  }

  @Override
  public boolean hasEnchant(@NotNull Enchantment enchantment) {
    NamespacedKey nKey = new NamespacedKey(enchantment.key().namespace(), enchantment.key().value());
    org.bukkit.enchantments.Enchantment bEnch = org.bukkit.enchantments.Enchantment.getByKey(nKey);
    return bEnch != null && meta.hasEnchant(bEnch);
  }

  @Override
  public int enchantLevel(@NotNull Enchantment enchantment) {
    NamespacedKey nKey = new NamespacedKey(enchantment.key().namespace(), enchantment.key().value());
    org.bukkit.enchantments.Enchantment bEnch = org.bukkit.enchantments.Enchantment.getByKey(nKey);
    return bEnch != null ? meta.getEnchantLevel(bEnch) : 0;
  }

  @Override
  public void addEnchant(@NotNull Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
    NamespacedKey nKey = new NamespacedKey(enchantment.key().namespace(), enchantment.key().value());
    org.bukkit.enchantments.Enchantment bEnch = org.bukkit.enchantments.Enchantment.getByKey(nKey);
    if (bEnch != null) {
      meta.addEnchant(bEnch, level, ignoreLevelRestriction);
    }
  }

  @Override
  public void removeEnchant(@NotNull Enchantment enchantment) {
    NamespacedKey nKey = new NamespacedKey(enchantment.key().namespace(), enchantment.key().value());
    org.bukkit.enchantments.Enchantment bEnch = org.bukkit.enchantments.Enchantment.getByKey(nKey);
    if (bEnch != null) {
      meta.removeEnchant(bEnch);
    }
  }

  private static <T> @Nullable PersistentDataType<?, ?> resolveDataType(@NotNull Class<T> typeClass) {
    if (typeClass == String.class) return PersistentDataType.STRING;
    if (typeClass == Integer.class || typeClass == int.class) return PersistentDataType.INTEGER;
    if (typeClass == Long.class || typeClass == long.class) return PersistentDataType.LONG;
    if (typeClass == Double.class || typeClass == double.class) return PersistentDataType.DOUBLE;
    if (typeClass == Float.class || typeClass == float.class) return PersistentDataType.FLOAT;
    if (typeClass == Byte.class || typeClass == byte.class) return PersistentDataType.BYTE;
    if (typeClass == Short.class || typeClass == short.class) return PersistentDataType.SHORT;
    if (typeClass == byte[].class) return PersistentDataType.BYTE_ARRAY;
    if (typeClass == int[].class) return PersistentDataType.INTEGER_ARRAY;
    if (typeClass == long[].class) return PersistentDataType.LONG_ARRAY;
    if (typeClass == Boolean.class || typeClass == boolean.class) return PersistentDataType.BYTE;
    return null;
  }

  @Override
  public boolean hasData(@NotNull DataComponentType<?> type) {
    NamespacedKey nKey = new NamespacedKey(type.key().namespace(), type.key().value());
    PersistentDataType<?, ?> pType = resolveDataType(type.type());
    if (pType == null) {
      throw new UnsupportedOperationException("Unsupported DataComponentType value type: " + type.type().getName());
    }
    return hasPdcData(nKey, pType);
  }

  @SuppressWarnings("unchecked")
  private <Z> boolean hasPdcData(NamespacedKey key, PersistentDataType<Z, ?> type) {
    return meta.getPersistentDataContainer().has(key, (PersistentDataType<Z, Z>) type);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> @Nullable T getData(@NotNull DataComponentType<T> type) {
    NamespacedKey nKey = new NamespacedKey(type.key().namespace(), type.key().value());
    Class<T> clazz = type.type();
    if (clazz == Boolean.class || clazz == boolean.class) {
      Byte b = meta.getPersistentDataContainer().get(nKey, PersistentDataType.BYTE);
      return b != null ? (T) Boolean.valueOf(b != 0) : null;
    }
    PersistentDataType<?, ?> pType = resolveDataType(clazz);
    if (pType == null) {
      throw new UnsupportedOperationException("Unsupported DataComponentType value type: " + clazz.getName());
    }
    return (T) getPdcData(nKey, (PersistentDataType<Object, Object>) pType);
  }

  private <Z> Z getPdcData(NamespacedKey key, PersistentDataType<Z, Z> type) {
    return meta.getPersistentDataContainer().get(key, type);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> void setData(@NotNull DataComponentType<T> type, @Nullable T value) {
    NamespacedKey nKey = new NamespacedKey(type.key().namespace(), type.key().value());
    if (value == null) {
      meta.getPersistentDataContainer().remove(nKey);
      return;
    }
    Class<T> clazz = type.type();
    if (clazz == Boolean.class || clazz == boolean.class) {
      meta.getPersistentDataContainer().set(nKey, PersistentDataType.BYTE, (byte) (((Boolean) value) ? 1 : 0));
      return;
    }
    PersistentDataType<?, ?> pType = resolveDataType(clazz);
    if (pType == null) {
      throw new UnsupportedOperationException("Unsupported DataComponentType value type: " + clazz.getName());
    }
    setPdcData(nKey, (PersistentDataType<Object, Object>) pType, value);
  }

  private <Z> void setPdcData(NamespacedKey key, PersistentDataType<Z, Z> type, Object value) {
    meta.getPersistentDataContainer().set(key, type, type.getComplexType().cast(value));
  }

  @Override
  public void resetData(@NotNull DataComponentType<?> type) {
    NamespacedKey nKey = new NamespacedKey(type.key().namespace(), type.key().value());
    meta.getPersistentDataContainer().remove(nKey);
  }

  @Override
  public @NotNull Set<DataComponentType<?>> dataComponentTypes() {
    Set<DataComponentType<?>> result = new HashSet<>();
    for (NamespacedKey key : meta.getPersistentDataContainer().getKeys()) {
      Key cKey = Key.key(key.getNamespace(), key.getKey());
      result.add(new DataComponentType<Object>() {
        @Override public @NotNull Key key() { return cKey; }
        @Override public @NotNull Class<Object> type() { return Object.class; }
      });
    }
    return result;
  }
}
