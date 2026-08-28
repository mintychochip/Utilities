package org.aincraft.minestom.adapter;

import net.kyori.adventure.text.Component;
import org.aincraft.api.domain.entity.Player;
import org.aincraft.api.domain.inventory.Inventory;
import org.aincraft.api.domain.inventory.InventoryType;
import org.aincraft.api.domain.inventory.InventoryView;
import org.aincraft.api.domain.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class MinestomInventoryViewWrapper implements InventoryView {

  private final net.minestom.server.entity.Player player;
  private final net.minestom.server.inventory.AbstractInventory top;

  public MinestomInventoryViewWrapper(@NotNull net.minestom.server.entity.Player player) {
    this.player = Objects.requireNonNull(player, "player cannot be null");
    this.top = player.getOpenInventory();
  }

  @Override
  public @NotNull Inventory topInventory() {
    return top == null
        ? new MinestomPlayerInventoryWrapper(player.getInventory(), MinestomAdapters.adapt(player))
        : MinestomAdapters.adapt(top);
  }

  @Override
  public @NotNull Inventory bottomInventory() {
    return new MinestomPlayerInventoryWrapper(
        player.getInventory(), MinestomAdapters.adapt(player));
  }

  @Override
  public @Nullable Player player() {
    return MinestomAdapters.adapt(player);
  }

  @Override
  public @NotNull InventoryType type() {
    return top == null ? InventoryType.PLAYER : topInventory().type();
  }

  @Override
  public @Nullable ItemStack getItem(int rawSlot) {
    if (top != null && rawSlot >= 0 && rawSlot < top.getSize()) {
      return topInventory().getItem(rawSlot);
    }
    int bottomSlot = rawSlot - (top == null ? 0 : top.getSize());
    return bottomSlot < 0 || bottomSlot >= player.getInventory().getSize()
        ? null
        : bottomInventory().getItem(bottomSlot);
  }

  @Override
  public void setItem(int rawSlot, @Nullable ItemStack item) {
    if (top != null && rawSlot >= 0 && rawSlot < top.getSize()) {
      topInventory().setItem(rawSlot, item);
      return;
    }
    int bottomSlot = rawSlot - (top == null ? 0 : top.getSize());
    if (bottomSlot < 0 || bottomSlot >= player.getInventory().getSize()) {
      throw new IndexOutOfBoundsException("rawSlot=" + rawSlot);
    }
    bottomInventory().setItem(bottomSlot, item);
  }

  @Override
  public @Nullable ItemStack cursor() {
    net.minestom.server.item.ItemStack item =
        top instanceof net.minestom.server.inventory.Inventory menu
            ? menu.getCursorItem(player)
            : player.getInventory().getCursorItem();
    return item == null || item.isAir() || item.amount() <= 0 ? null : MinestomAdapters.adapt(item);
  }

  @Override
  public void setCursor(@Nullable ItemStack item) {
    net.minestom.server.item.ItemStack minestomItem =
        item == null ? net.minestom.server.item.ItemStack.AIR : MinestomAdapters.toMinestom(item);
    if (top instanceof net.minestom.server.inventory.Inventory menu) {
      menu.setCursorItem(player, minestomItem);
    } else {
      player.getInventory().setCursorItem(minestomItem);
    }
  }

  @Override
  public @NotNull Component title() {
    return top instanceof net.minestom.server.inventory.Inventory menu
        ? menu.getTitle()
        : Component.empty();
  }

  @Override
  public @NotNull Component originalTitle() {
    return title();
  }

  @Override
  public void title(@NotNull Component title) {
    Objects.requireNonNull(title, "title cannot be null");
    if (top instanceof net.minestom.server.inventory.Inventory menu) menu.setTitle(title);
  }

  @Override
  public void close() {
    player.closeInventory();
  }
}
