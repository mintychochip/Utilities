package org.aincraft.bukkit.adapter;

import net.kyori.adventure.text.Component;
import org.aincraft.api.domain.effect.Enchantment;
import org.aincraft.api.domain.inventory.ItemMeta;
import org.aincraft.api.domain.inventory.ItemStack;
import org.aincraft.api.domain.inventory.ItemType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class BukkitItemStackWrapper implements ItemStack {

  private final org.bukkit.inventory.ItemStack item;

  public BukkitItemStackWrapper(@NotNull org.bukkit.inventory.ItemStack item) {
    this.item = Objects.requireNonNull(item, "item cannot be null");
  }

  public @NotNull org.bukkit.inventory.ItemStack getBukkitItemStack() {
    return item;
  }

  @Override
  public @NotNull ItemType type() {
    return new BukkitItemTypeWrapper(item.getType());
  }

  @Override
  public int amount() {
    return item.getAmount();
  }

  @Override
  public void setAmount(int amount) {
    item.setAmount(amount);
  }

  @Override
  public @Nullable Component displayName() {
    return meta() != null ? meta().displayName() : null;
  }

  @Override
  public @Nullable List<Component> lore() {
    return meta() != null ? meta().lore() : null;
  }

  @Override
  public boolean hasItemMeta() {
    return item.hasItemMeta();
  }

  @Override
  public @Nullable ItemMeta meta() {
    org.bukkit.inventory.meta.ItemMeta bMeta = item.getItemMeta();
    return bMeta != null ? BukkitAdapters.adapt(bMeta) : null;
  }

  @Override
  public void setMeta(@Nullable ItemMeta meta) {
    if (meta == null) {
      item.setItemMeta(null);
    } else if (meta instanceof BukkitItemMetaWrapper wrapper) {
      item.setItemMeta(wrapper.getBukkitItemMeta());
    } else {
      throw new IllegalArgumentException(
          "ItemMeta is not backed by Bukkit: " + meta.getClass().getName());
    }
  }

  @Override
  public boolean isSimilar(@Nullable ItemStack other) {
    if (other == null) return false;
    if (other instanceof BukkitItemStackWrapper wrapper) {
      return item.isSimilar(wrapper.getBukkitItemStack());
    }
    return false;
  }

  @Override
  public boolean isEmpty() {
    return item.getType() == Material.AIR || item.getAmount() <= 0;
  }

  @Override
  public @NotNull ItemStack clone() {
    return new BukkitItemStackWrapper(item.clone());
  }

  @Override
  public int maxStackSize() {
    return item.getMaxStackSize();
  }

  @Override
  public boolean editMeta(@NotNull Consumer<ItemMeta> consumer) {
    org.bukkit.inventory.meta.ItemMeta bMeta = item.getItemMeta();
    if (bMeta == null) return false;
    ItemMeta wrapper = BukkitAdapters.adapt(bMeta);
    consumer.accept(wrapper);
    return item.setItemMeta(
        wrapper instanceof BukkitItemMetaWrapper bukkitWrapper
            ? bukkitWrapper.getBukkitItemMeta()
            : bMeta);
  }

  @Override
  public @NotNull ItemStack asOne() {
    return withAmount(1);
  }

  @Override
  public @NotNull ItemStack asQuantity(int amount) {
    return withAmount(amount);
  }

  @Override
  public @NotNull ItemStack withAmount(int amount) {
    org.bukkit.inventory.ItemStack clone = item.clone();
    clone.setAmount(amount);
    return new BukkitItemStackWrapper(clone);
  }

  @Override
  public boolean hasEnchant(@NotNull Enchantment enchantment) {
    return item.containsEnchantment(BukkitAdapters.toBukkit(enchantment));
  }

  @Override
  public int enchantLevel(@NotNull Enchantment enchantment) {
    return item.getEnchantmentLevel(BukkitAdapters.toBukkit(enchantment));
  }

  @Override
  public @NotNull Map<Enchantment, Integer> enchantments() {
    Map<org.bukkit.enchantments.Enchantment, Integer> bMap = item.getEnchantments();
    Map<Enchantment, Integer> result = new HashMap<>();
    bMap.forEach((bEnch, level) -> result.put(BukkitAdapters.adapt(bEnch), level));
    return Map.copyOf(result);
  }

  @Override
  public void addEnchant(
      @NotNull Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
    org.bukkit.enchantments.Enchantment bEnch = BukkitAdapters.toBukkit(enchantment);
    if (ignoreLevelRestriction) {
      item.addUnsafeEnchantment(bEnch, level);
    } else {
      item.addEnchantment(bEnch, level);
    }
  }

  @Override
  public int removeEnchant(@NotNull Enchantment enchantment) {
    org.bukkit.enchantments.Enchantment bEnch = BukkitAdapters.toBukkit(enchantment);
    int level = item.getEnchantmentLevel(bEnch);
    if (level > 0) {
      item.removeEnchantment(bEnch);
    }
    return level;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BukkitItemStackWrapper that)) return false;
    return item.equals(that.item);
  }

  @Override
  public int hashCode() {
    return item.hashCode();
  }

  @Override
  public String toString() {
    return "BukkitItemStackWrapper{type=" + type() + ", amount=" + amount() + "}";
  }
}
