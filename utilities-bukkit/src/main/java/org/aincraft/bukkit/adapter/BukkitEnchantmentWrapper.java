package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.aincraft.api.domain.effect.Enchantment;
import org.aincraft.api.domain.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BukkitEnchantmentWrapper implements Enchantment {

  private final org.bukkit.enchantments.Enchantment enchantment;

  public BukkitEnchantmentWrapper(@NotNull org.bukkit.enchantments.Enchantment enchantment) {
    this.enchantment = enchantment;
  }

  public @NotNull org.bukkit.enchantments.Enchantment getBukkitEnchantment() {
    return enchantment;
  }

  @Override
  public @NotNull Key key() {
    return Key.key(enchantment.getKey().getNamespace(), enchantment.getKey().getKey());
  }

  @Override
  public int maxLevel() {
    return enchantment.getMaxLevel();
  }

  @Override
  public int startLevel() {
    return enchantment.getStartLevel();
  }

  @Override
  public boolean isCursed() {
    return enchantment.isCursed();
  }

  @Override
  public boolean isTreasure() {
    return enchantment.isTreasure();
  }

  @Override
  public boolean conflictsWith(@NotNull Enchantment other) {
    return enchantment.conflictsWith(BukkitAdapters.toBukkit(other));
  }

  @Override
  public boolean canEnchant(@NotNull ItemStack item) {
    return enchantment.canEnchantItem(BukkitAdapters.toBukkit(item));
  }

  @Override
  public @NotNull Component displayName(int level) {
    return Component.text(enchantment.getName());
  }

  @Override
  public @NotNull Component description() {
    throw new UnsupportedCapabilityException(
        Capability.ENCHANTMENT_METADATA, "Spigot Enchantment has no description metadata.");
  }
}
