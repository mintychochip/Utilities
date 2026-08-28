package org.aincraft.paper.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.inventory.ItemFactory;
import org.aincraft.api.domain.inventory.ItemMeta;
import org.aincraft.api.domain.inventory.ItemStack;
import org.aincraft.api.domain.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class PaperItemFactoryWrapper implements ItemFactory {

  private final org.bukkit.inventory.ItemFactory factory;

  public PaperItemFactoryWrapper(@NotNull org.bukkit.inventory.ItemFactory factory) {
    this.factory = Objects.requireNonNull(factory, "factory cannot be null");
  }

  @Override
  public @NotNull ItemMeta createItemMeta(@NotNull ItemType type) {
    org.bukkit.inventory.meta.ItemMeta meta =
        factory.getItemMeta(org.aincraft.bukkit.adapter.BukkitAdapters.toBukkit(type));
    if (meta == null) throw new IllegalArgumentException("No item meta for " + type.key());
    return PaperAdapters.adapt(meta);
  }

  @Override
  public @NotNull ItemStack createItemStack(@NotNull Key materialKey) {
    return PaperAdapters.adapt(factory.createItemStack(materialKey.asString()));
  }

  @Override
  public boolean metaEquals(@NotNull ItemMeta first, @NotNull ItemMeta second) {
    if (!(first instanceof org.aincraft.bukkit.adapter.BukkitItemMetaWrapper firstWrapper)
        || !(second instanceof org.aincraft.bukkit.adapter.BukkitItemMetaWrapper secondWrapper)) {
      throw new IllegalArgumentException("Item metadata is not backed by Paper");
    }
    return factory.equals(firstWrapper.getBukkitItemMeta(), secondWrapper.getBukkitItemMeta());
  }
}
