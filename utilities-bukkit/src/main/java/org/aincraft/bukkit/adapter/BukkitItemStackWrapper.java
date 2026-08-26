package org.aincraft.bukkit.adapter;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.aincraft.common.inventory.ItemMeta;
import org.aincraft.common.inventory.ItemStack;
import org.aincraft.common.inventory.ItemType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    return bMeta != null ? new BukkitItemMetaWrapper(bMeta) : null;
  }

  @Override
  public void setMeta(@Nullable ItemMeta meta) {
    if (meta instanceof BukkitItemMetaWrapper wrapper) {
      item.setItemMeta(wrapper.getBukkitItemMeta());
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
    if (bMeta == null) {
      return false;
    }
    BukkitItemMetaWrapper wrapper = new BukkitItemMetaWrapper(bMeta);
    consumer.accept(wrapper);
    return item.setItemMeta(bMeta);
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
