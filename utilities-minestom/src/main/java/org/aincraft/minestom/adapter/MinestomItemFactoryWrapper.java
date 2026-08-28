package org.aincraft.minestom.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.inventory.ItemFactory;
import org.aincraft.api.domain.inventory.ItemMeta;
import org.aincraft.api.domain.inventory.ItemStack;
import org.aincraft.api.domain.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class MinestomItemFactoryWrapper implements ItemFactory {

  @Override
  public @NotNull ItemMeta createItemMeta(@NotNull ItemType type) {
    Objects.requireNonNull(type, "type cannot be null");
    return new MinestomItemMetaWrapper(
        new MinestomItemStackWrapper(
            net.minestom.server.item.ItemStack.of(MinestomAdapters.toMinestom(type))));
  }

  @Override
  public @NotNull ItemStack createItemStack(@NotNull Key materialKey) {
    Objects.requireNonNull(materialKey, "materialKey cannot be null");
    net.minestom.server.item.Material material =
        net.minestom.server.item.Material.fromKey(materialKey);
    if (material == null) throw new IllegalArgumentException("Unknown item type: " + materialKey);
    return MinestomAdapters.adapt(net.minestom.server.item.ItemStack.of(material));
  }

  @Override
  public boolean metaEquals(@NotNull ItemMeta first, @NotNull ItemMeta second) {
    if (!(first instanceof MinestomItemMetaWrapper firstWrapper)
        || !(second instanceof MinestomItemMetaWrapper secondWrapper)) {
      throw new IllegalArgumentException("Item metadata is not backed by Minestom");
    }
    return firstWrapper
        .snapshot()
        .componentPatch()
        .equals(secondWrapper.snapshot().componentPatch());
  }
}
