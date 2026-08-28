package org.aincraft.paper.adapter;

import net.kyori.adventure.text.Component;
import org.aincraft.bukkit.adapter.BukkitEnchantmentWrapper;
import org.jetbrains.annotations.NotNull;

public final class PaperEnchantmentWrapper extends BukkitEnchantmentWrapper {

  public PaperEnchantmentWrapper(@NotNull org.bukkit.enchantments.Enchantment enchantment) {
    super(enchantment);
  }

  @Override
  public @NotNull Component displayName(int level) {
    return getBukkitEnchantment().displayName(level);
  }

  @Override
  public @NotNull Component description() {
    return getBukkitEnchantment().description();
  }

  @Override
  public boolean isTradeable() {
    return getBukkitEnchantment().isTradeable();
  }

  @Override
  public boolean isDiscoverable() {
    return getBukkitEnchantment().isDiscoverable();
  }
}
