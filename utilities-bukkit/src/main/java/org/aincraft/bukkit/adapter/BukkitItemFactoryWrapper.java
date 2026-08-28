package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.inventory.ItemFactory;
import org.aincraft.api.domain.inventory.ItemMeta;
import org.aincraft.api.domain.inventory.ItemStack;
import org.aincraft.api.domain.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class BukkitItemFactoryWrapper implements ItemFactory {

  private final org.bukkit.inventory.ItemFactory factory;

  public BukkitItemFactoryWrapper(@NotNull org.bukkit.inventory.ItemFactory factory) {
    this.factory = Objects.requireNonNull(factory, "factory cannot be null");
  }

  public @NotNull org.bukkit.inventory.ItemFactory getBukkitItemFactory() {
    return factory;
  }

  @Override
  public @NotNull ItemMeta createItemMeta(@NotNull ItemType type) {
    org.bukkit.inventory.meta.ItemMeta meta = factory.getItemMeta(BukkitAdapters.toBukkit(type));
    if (meta == null) {
      throw new IllegalArgumentException("Bukkit has no item meta for " + type.key());
    }
    return BukkitAdapters.adapt(meta);
  }

  @Override
  public @NotNull ItemStack createItemStack(@NotNull Key materialKey) {
    return BukkitAdapters.adapt(factory.createItemStack(materialKey.asString()));
  }

  @Override
  public boolean metaEquals(@NotNull ItemMeta first, @NotNull ItemMeta second) {
    return factory.equals(toBukkit(first), toBukkit(second));
  }

  private static org.bukkit.inventory.meta.ItemMeta toBukkit(@NotNull ItemMeta meta) {
    if (meta instanceof BukkitItemMetaWrapper wrapper) return wrapper.getBukkitItemMeta();
    throw new IllegalArgumentException(
        "Cannot unwrap foreign ItemMeta implementation: " + meta.getClass().getName());
  }
}
