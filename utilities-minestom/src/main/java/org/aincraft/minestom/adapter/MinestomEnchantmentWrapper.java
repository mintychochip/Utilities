package org.aincraft.minestom.adapter;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.registry.Registries;
import net.minestom.server.registry.RegistryKey;
import org.aincraft.api.domain.effect.Enchantment;
import org.aincraft.api.domain.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class MinestomEnchantmentWrapper implements Enchantment {

  private final RegistryKey<net.minestom.server.item.enchant.Enchantment> key;
  private final net.minestom.server.item.enchant.Enchantment enchantment;

  public MinestomEnchantmentWrapper(
      @NotNull RegistryKey<net.minestom.server.item.enchant.Enchantment> key) {
    this.key = Objects.requireNonNull(key, "key cannot be null");
    this.enchantment = Registries.vanilla().enchantment().get(key);
    if (enchantment == null) {
      throw new IllegalArgumentException("Unknown Minestom enchantment: " + key.key());
    }
  }

  public @NotNull RegistryKey<net.minestom.server.item.enchant.Enchantment> getMinestomKey() {
    return key;
  }

  @Override
  public @NotNull Key key() {
    return key.key();
  }

  @Override
  public int maxLevel() {
    return enchantment.maxLevel();
  }

  @Override
  public int startLevel() {
    return 1;
  }

  @Override
  public boolean isCursed() {
    return key.key().value().endsWith("_curse");
  }

  @Override
  public boolean isTreasure() {
    return false;
  }

  @Override
  public boolean conflictsWith(@NotNull Enchantment other) {
    if (!(other instanceof MinestomEnchantmentWrapper wrapper)) return false;
    return enchantment.exclusiveSet().contains(wrapper.key);
  }

  @Override
  public boolean canEnchant(@NotNull ItemStack item) {
    if (!(item instanceof MinestomItemStackWrapper wrapper)) return false;
    net.minestom.server.item.Material material = wrapper.getMinestomItemStack().material();
    RegistryKey<net.minestom.server.item.Material> materialKey =
        Registries.vanilla().material().getKey(material);
    return enchantment.supportedItems().contains(materialKey);
  }

  @Override
  public @NotNull Component displayName(int level) {
    return Component.translatable("enchantment." + key.key().namespace() + "." + key.key().value());
  }

  @Override
  public @NotNull Component description() {
    return enchantment.description();
  }

  @Override
  public boolean isTradeable() {
    return false;
  }

  @Override
  public boolean isDiscoverable() {
    return true;
  }

  @Override
  public boolean equals(Object other) {
    return this == other
        || (other instanceof Enchantment enchantment && key().equals(enchantment.key()));
  }

  @Override
  public int hashCode() {
    return key().hashCode();
  }

  @Override
  public String toString() {
    return "MinestomEnchantmentWrapper{key=" + key() + "}";
  }
}
