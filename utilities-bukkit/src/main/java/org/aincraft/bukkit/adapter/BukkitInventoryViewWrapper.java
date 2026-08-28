package org.aincraft.bukkit.adapter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.aincraft.api.domain.entity.Player;
import org.aincraft.api.domain.inventory.Inventory;
import org.aincraft.api.domain.inventory.InventoryType;
import org.aincraft.api.domain.inventory.InventoryView;
import org.aincraft.api.domain.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitInventoryViewWrapper implements InventoryView {

  private final org.bukkit.inventory.InventoryView view;

  public BukkitInventoryViewWrapper(@NotNull org.bukkit.inventory.InventoryView view) {
    this.view = view;
  }

  public @NotNull org.bukkit.inventory.InventoryView getBukkitInventoryView() {
    return view;
  }

  @Override
  public @NotNull Inventory topInventory() {
    return BukkitAdapters.adapt(view.getTopInventory());
  }

  @Override
  public @NotNull Inventory bottomInventory() {
    return BukkitAdapters.adapt(view.getBottomInventory());
  }

  @Override
  public @Nullable Player player() {
    org.bukkit.entity.HumanEntity human = view.getPlayer();
    if (human instanceof org.bukkit.entity.Player player) {
      return BukkitAdapters.adapt(player);
    }
    return null;
  }

  @Override
  public @NotNull InventoryType type() {
    return BukkitAdapters.adapt(view.getType());
  }

  @Override
  public @Nullable ItemStack getItem(int rawSlot) {
    org.bukkit.inventory.ItemStack item = view.getItem(rawSlot);
    return item != null ? BukkitAdapters.adapt(item) : null;
  }

  @Override
  public void setItem(int rawSlot, @Nullable ItemStack item) {
    view.setItem(rawSlot, item != null ? BukkitAdapters.toBukkit(item) : null);
  }

  @Override
  public @Nullable ItemStack cursor() {
    org.bukkit.inventory.ItemStack item = view.getCursor();
    return item != null ? BukkitAdapters.adapt(item) : null;
  }

  @Override
  public void setCursor(@Nullable ItemStack item) {
    view.setCursor(item != null ? BukkitAdapters.toBukkit(item) : null);
  }

  @Override
  public @NotNull Component title() {
    return LegacyComponentSerializer.legacySection().deserialize(view.getTitle());
  }

  @Override
  public @NotNull Component originalTitle() {
    return LegacyComponentSerializer.legacySection().deserialize(view.getOriginalTitle());
  }

  @Override
  public void title(@NotNull Component title) {
    view.setTitle(LegacyComponentSerializer.legacySection().serialize(title));
  }

  @Override
  public void close() {
    view.close();
  }
}
