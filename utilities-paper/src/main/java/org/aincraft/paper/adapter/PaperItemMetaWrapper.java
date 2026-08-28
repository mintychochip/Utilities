package org.aincraft.paper.adapter;

import net.kyori.adventure.text.Component;
import org.aincraft.bukkit.adapter.BukkitItemMetaWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PaperItemMetaWrapper extends BukkitItemMetaWrapper {

  public PaperItemMetaWrapper(@NotNull org.bukkit.inventory.meta.ItemMeta meta) {
    super(meta);
  }

  @Override
  public @Nullable Component customName() {
    return getBukkitItemMeta().customName();
  }

  @Override
  public void customName(@Nullable Component name) {
    getBukkitItemMeta().customName(name);
  }

  @Override
  public boolean hasCustomName() {
    return getBukkitItemMeta().hasCustomName();
  }

  @Override
  public @Nullable Component itemName() {
    return getBukkitItemMeta().itemName();
  }

  @Override
  public void itemName(@Nullable Component name) {
    getBukkitItemMeta().itemName(name);
  }

  @Override
  public boolean hasItemName() {
    return getBukkitItemMeta().hasItemName();
  }

  @Override
  public @Nullable Component displayName() {
    return getBukkitItemMeta().displayName();
  }

  @Override
  public void setDisplayName(@Nullable Component name) {
    getBukkitItemMeta().displayName(name);
  }

  @Override
  public @Nullable List<Component> lore() {
    return getBukkitItemMeta().lore();
  }

  @Override
  public void setLore(@Nullable List<Component> lore) {
    getBukkitItemMeta().lore(lore);
  }

  @Override
  public @NotNull java.util.Map<org.aincraft.api.domain.effect.Enchantment, Integer>
      enchantments() {
    java.util.Map<org.aincraft.api.domain.effect.Enchantment, Integer> result =
        new java.util.LinkedHashMap<>();
    for (java.util.Map.Entry<org.bukkit.enchantments.Enchantment, Integer> entry :
        getBukkitItemMeta().getEnchants().entrySet()) {
      result.put(PaperAdapters.adapt(entry.getKey()), entry.getValue());
    }
    return java.util.Map.copyOf(result);
  }

  @Override
  public boolean hasEnchant(@NotNull org.aincraft.api.domain.effect.Enchantment enchantment) {
    return getBukkitItemMeta()
        .hasEnchant(org.aincraft.bukkit.adapter.BukkitAdapters.toBukkit(enchantment));
  }

  @Override
  public int enchantLevel(@NotNull org.aincraft.api.domain.effect.Enchantment enchantment) {
    return getBukkitItemMeta()
        .getEnchantLevel(org.aincraft.bukkit.adapter.BukkitAdapters.toBukkit(enchantment));
  }

  @Override
  public void addEnchant(
      @NotNull org.aincraft.api.domain.effect.Enchantment enchantment,
      int level,
      boolean ignoreLevelRestriction) {
    getBukkitItemMeta()
        .addEnchant(
            org.aincraft.bukkit.adapter.BukkitAdapters.toBukkit(enchantment),
            level,
            ignoreLevelRestriction);
  }

  @Override
  public void removeEnchant(@NotNull org.aincraft.api.domain.effect.Enchantment enchantment) {
    getBukkitItemMeta()
        .removeEnchant(org.aincraft.bukkit.adapter.BukkitAdapters.toBukkit(enchantment));
  }
}
