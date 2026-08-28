package org.aincraft.paper.adapter;

import net.kyori.adventure.text.Component;
import org.aincraft.api.domain.entity.Player;
import org.aincraft.api.domain.inventory.Inventory;
import org.aincraft.api.domain.inventory.ItemStack;
import org.aincraft.bukkit.adapter.BukkitInventoryViewWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PaperInventoryViewWrapper extends BukkitInventoryViewWrapper {

  public PaperInventoryViewWrapper(@NotNull org.bukkit.inventory.InventoryView view) {
    super(view);
  }

  @Override
  public @NotNull Inventory topInventory() {
    return PaperAdapters.adapt(getBukkitInventoryView().getTopInventory());
  }

  @Override
  public @NotNull Inventory bottomInventory() {
    return PaperAdapters.adapt(getBukkitInventoryView().getBottomInventory());
  }

  @Override
  public @Nullable Player player() {
    org.bukkit.entity.HumanEntity player = getBukkitInventoryView().getPlayer();
    return player instanceof org.bukkit.entity.Player p ? PaperAdapters.adapt(p) : null;
  }

  @Override
  public @Nullable ItemStack getItem(int rawSlot) {
    org.bukkit.inventory.ItemStack item = getBukkitInventoryView().getItem(rawSlot);
    return item == null ? null : PaperAdapters.adapt(item);
  }

  @Override
  public void setItem(int rawSlot, @Nullable ItemStack item) {
    getBukkitInventoryView().setItem(rawSlot, item == null ? null : PaperAdapters.toBukkit(item));
  }

  @Override
  public @Nullable ItemStack cursor() {
    org.bukkit.inventory.ItemStack item = getBukkitInventoryView().getCursor();
    return item == null ? null : PaperAdapters.adapt(item);
  }

  @Override
  public void setCursor(@Nullable ItemStack item) {
    getBukkitInventoryView().setCursor(item == null ? null : PaperAdapters.toBukkit(item));
  }

  @Override
  public @NotNull Component title() {
    return getBukkitInventoryView().title();
  }

  @Override
  public @NotNull Component originalTitle() {
    return net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
        .deserialize(getBukkitInventoryView().getOriginalTitle());
  }

  @Override
  public void title(@NotNull Component title) {
    getBukkitInventoryView()
        .setTitle(
            net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                .serialize(title));
  }
}
