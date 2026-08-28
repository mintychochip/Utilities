package org.aincraft.paper.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.inventory.DataComponentType;
import org.aincraft.api.domain.inventory.ItemMeta;
import org.aincraft.api.domain.inventory.ItemStack;
import org.aincraft.bukkit.adapter.BukkitItemStackWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Consumer;

public final class PaperItemStackWrapper extends BukkitItemStackWrapper {

  public PaperItemStackWrapper(@NotNull org.bukkit.inventory.ItemStack item) {
    super(item);
  }

  @Override
  public @NotNull ItemStack clone() {
    return new PaperItemStackWrapper(paperItem().clone());
  }

  @Override
  public @NotNull ItemStack withAmount(int amount) {
    org.bukkit.inventory.ItemStack copy = paperItem().clone();
    copy.setAmount(amount);
    return new PaperItemStackWrapper(copy);
  }

  @Override
  public boolean hasEnchant(@NotNull org.aincraft.api.domain.effect.Enchantment enchantment) {
    return paperItem()
        .containsEnchantment(org.aincraft.bukkit.adapter.BukkitAdapters.toBukkit(enchantment));
  }

  @Override
  public int enchantLevel(@NotNull org.aincraft.api.domain.effect.Enchantment enchantment) {
    return paperItem()
        .getEnchantmentLevel(org.aincraft.bukkit.adapter.BukkitAdapters.toBukkit(enchantment));
  }

  @Override
  public @NotNull java.util.Map<org.aincraft.api.domain.effect.Enchantment, Integer>
      enchantments() {
    java.util.Map<org.aincraft.api.domain.effect.Enchantment, Integer> result =
        new java.util.LinkedHashMap<>();
    for (java.util.Map.Entry<org.bukkit.enchantments.Enchantment, Integer> entry :
        paperItem().getEnchantments().entrySet()) {
      result.put(PaperAdapters.adapt(entry.getKey()), entry.getValue());
    }
    return java.util.Map.copyOf(result);
  }

  @Override
  public void addEnchant(
      @NotNull org.aincraft.api.domain.effect.Enchantment enchantment,
      int level,
      boolean ignoreLevelRestriction) {
    org.bukkit.enchantments.Enchantment bukkit =
        org.aincraft.bukkit.adapter.BukkitAdapters.toBukkit(enchantment);
    if (ignoreLevelRestriction) {
      paperItem().addUnsafeEnchantment(bukkit, level);
    } else {
      paperItem().addEnchantment(bukkit, level);
    }
  }

  @Override
  public int removeEnchant(@NotNull org.aincraft.api.domain.effect.Enchantment enchantment) {
    int level = enchantLevel(enchantment);
    if (level > 0) {
      paperItem()
          .removeEnchantment(org.aincraft.bukkit.adapter.BukkitAdapters.toBukkit(enchantment));
    }
    return level;
  }

  private org.bukkit.inventory.ItemStack paperItem() {
    return getBukkitItemStack();
  }

  @Override
  public @Nullable ItemMeta meta() {
    org.bukkit.inventory.meta.ItemMeta meta = paperItem().getItemMeta();
    if (meta == null) return null;
    return meta instanceof org.bukkit.inventory.meta.Damageable damageable
        ? new PaperDamageableItemMetaWrapper(damageable)
        : new PaperItemMetaWrapper(meta);
  }

  @Override
  public boolean editMeta(@NotNull Consumer<ItemMeta> consumer) {
    org.bukkit.inventory.meta.ItemMeta meta = paperItem().getItemMeta();
    if (meta == null) return false;
    ItemMeta wrapper =
        meta instanceof org.bukkit.inventory.meta.Damageable damageable
            ? new PaperDamageableItemMetaWrapper(damageable)
            : new PaperItemMetaWrapper(meta);
    consumer.accept(wrapper);
    return paperItem().setItemMeta(meta);
  }

  private static @NotNull io.papermc.paper.datacomponent.DataComponentType paperType(
      @NotNull DataComponentType<?> type) {
    io.papermc.paper.datacomponent.DataComponentType result =
        org.bukkit.Registry.DATA_COMPONENT_TYPE.get(type.key());
    if (result == null) {
      throw new UnsupportedOperationException("Unknown Paper data component: " + type.key());
    }
    return result;
  }

  @Override
  public boolean hasData(@NotNull DataComponentType<?> type) {
    return paperItem().hasData(paperType(type));
  }

  @Override
  public <T> @Nullable T getData(@NotNull DataComponentType<T> type) {
    io.papermc.paper.datacomponent.DataComponentType paperType = paperType(type);
    if (!(paperType instanceof io.papermc.paper.datacomponent.DataComponentType.Valued<?> valued)) {
      return null;
    }
    Object value =
        paperItem()
            .getData((io.papermc.paper.datacomponent.DataComponentType.Valued<Object>) valued);
    return value == null ? null : PaperDataComponentAdapter.adapt(value, type.type());
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> void setData(@NotNull DataComponentType<T> type, @Nullable T value) {
    io.papermc.paper.datacomponent.DataComponentType paperType = paperType(type);
    if (value == null) {
      paperItem().unsetData(paperType);
    } else if (paperType
        instanceof io.papermc.paper.datacomponent.DataComponentType.Valued<?> valued) {
      Object converted = PaperDataComponentAdapter.toPaper(type.key(), value);
      paperItem()
          .setData(
              (io.papermc.paper.datacomponent.DataComponentType.Valued<Object>) valued, converted);
    } else {
      paperItem().setData((io.papermc.paper.datacomponent.DataComponentType.NonValued) paperType);
    }
  }

  @Override
  public void setData(@NotNull org.aincraft.api.domain.inventory.DataComponentType.NonValued type) {
    paperItem()
        .setData((io.papermc.paper.datacomponent.DataComponentType.NonValued) paperType(type));
  }

  @Override
  public void resetData(@NotNull DataComponentType<?> type) {
    paperItem().resetData(paperType(type));
  }

  @Override
  public @NotNull Set<DataComponentType<?>> dataComponentTypes() {
    return paperItem().getDataTypes().stream()
        .map(
            type ->
                new DataComponentType<Object>() {
                  @Override
                  public @NotNull Key key() {
                    org.bukkit.NamespacedKey key = type.getKey();
                    return Key.key(key.getNamespace(), key.getKey());
                  }

                  @Override
                  public @NotNull Class<Object> type() {
                    return Object.class;
                  }
                })
        .collect(java.util.stream.Collectors.toUnmodifiableSet());
  }
}
