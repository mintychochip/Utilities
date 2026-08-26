package org.aincraft.bukkit.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.aincraft.common.effect.Enchantment;
import org.aincraft.common.inventory.ItemMeta;
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
    org.bukkit.NamespacedKey nKey = new org.bukkit.NamespacedKey(enchantment.key().namespace(), enchantment.key().value());
    org.bukkit.enchantments.Enchantment bEnch = org.bukkit.enchantments.Enchantment.getByKey(nKey);
    return bEnch != null && meta.hasEnchant(bEnch);
  }

  @Override
  public int enchantLevel(@NotNull Enchantment enchantment) {
    org.bukkit.NamespacedKey nKey = new org.bukkit.NamespacedKey(enchantment.key().namespace(), enchantment.key().value());
    org.bukkit.enchantments.Enchantment bEnch = org.bukkit.enchantments.Enchantment.getByKey(nKey);
    return bEnch != null ? meta.getEnchantLevel(bEnch) : 0;
  }

  @Override
  public void addEnchant(@NotNull Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
    org.bukkit.NamespacedKey nKey = new org.bukkit.NamespacedKey(enchantment.key().namespace(), enchantment.key().value());
    org.bukkit.enchantments.Enchantment bEnch = org.bukkit.enchantments.Enchantment.getByKey(nKey);
    if (bEnch != null) {
      meta.addEnchant(bEnch, level, ignoreLevelRestriction);
    }
  }

  @Override
  public void removeEnchant(@NotNull Enchantment enchantment) {
    org.bukkit.NamespacedKey nKey = new org.bukkit.NamespacedKey(enchantment.key().namespace(), enchantment.key().value());
    org.bukkit.enchantments.Enchantment bEnch = org.bukkit.enchantments.Enchantment.getByKey(nKey);
    if (bEnch != null) {
      meta.removeEnchant(bEnch);
    }
  }
}
