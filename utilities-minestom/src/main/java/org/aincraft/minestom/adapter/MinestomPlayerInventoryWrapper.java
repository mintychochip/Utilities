package org.aincraft.minestom.adapter;

import org.aincraft.common.inventory.EquipmentSlot;
import org.aincraft.common.inventory.InventoryHolder;
import org.aincraft.common.inventory.InventoryType;
import org.aincraft.common.inventory.ItemStack;
import org.aincraft.common.inventory.PlayerInventory;
import org.aincraft.common.location.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MinestomPlayerInventoryWrapper implements PlayerInventory {

  private final net.minestom.server.inventory.PlayerInventory inventory;
  private final InventoryHolder holder;

  public MinestomPlayerInventoryWrapper(
      @NotNull net.minestom.server.inventory.PlayerInventory inventory,
      @Nullable InventoryHolder holder) {
    this.inventory = Objects.requireNonNull(inventory, "inventory cannot be null");
    this.holder = holder;
  }

  public @NotNull net.minestom.server.inventory.PlayerInventory getMinestomPlayerInventory() {
    return inventory;
  }

  @Override
  public int size() {
    return net.minestom.server.inventory.PlayerInventory.INVENTORY_SIZE;
  }

  @Override
  public @NotNull InventoryType type() {
    return InventoryType.PLAYER;
  }

  @Override
  public @Nullable ItemStack getItem(int slot) {
    return null;
  }

  @Override
  public void setItem(int slot, @Nullable ItemStack item) {}

  @Override
  public @Nullable ItemStack[] contents() {
    return new ItemStack[0];
  }

  @Override
  public void setContents(@NotNull ItemStack[] items) {}

  @Override
  public void clear() {
    inventory.clear();
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public @Nullable Location location() {
    return null;
  }

  @Override
  public @Nullable InventoryHolder holder() {
    return holder;
  }

  @Override
  public @Nullable ItemStack helmet() {
    return null;
  }

  @Override
  public void setHelmet(@Nullable ItemStack helmet) {}

  @Override
  public @Nullable ItemStack chestplate() {
    return null;
  }

  @Override
  public void setChestplate(@Nullable ItemStack chestplate) {}

  @Override
  public @Nullable ItemStack leggings() {
    return null;
  }

  @Override
  public void setLeggings(@Nullable ItemStack leggings) {}

  @Override
  public @Nullable ItemStack boots() {
    return null;
  }

  @Override
  public void setBoots(@Nullable ItemStack boots) {}

  @Override
  public @Nullable ItemStack itemInMainHand() {
    return null;
  }

  @Override
  public void setItemInMainHand(@Nullable ItemStack item) {}

  @Override
  public @Nullable ItemStack itemInOffHand() {
    return null;
  }

  @Override
  public void setItemInOffHand(@Nullable ItemStack item) {}

  @Override
  public int heldItemSlot() {
    if (holder instanceof MinestomPlayerWrapper playerWrapper) {
      return playerWrapper.getMinestomPlayer().getHeldSlot();
    }
    return 0;
  }

  @Override
  public void setHeldItemSlot(int slot) {
    if (holder instanceof MinestomPlayerWrapper playerWrapper) {
      playerWrapper.getMinestomPlayer().setHeldItemSlot((byte) slot);
    }
  }

  @Override
  public @org.jetbrains.annotations.Nullable ItemStack getItem(@NotNull EquipmentSlot slot) {
    return null;
  }

  @Override
  public void setItem(
      @NotNull EquipmentSlot slot, @org.jetbrains.annotations.Nullable ItemStack item) {
    throw new UnsupportedOperationException();
  }

  @Override
  public @NotNull java.util.Collection<@org.jetbrains.annotations.Nullable ItemStack>
      armorContents() {
    return java.util.Collections.emptyList();
  }

  @Override
  public void setArmorContents(
      @NotNull java.util.Collection<@org.jetbrains.annotations.Nullable ItemStack> items) {
    throw new UnsupportedOperationException();
  }

  @Override
  public @NotNull java.util.Collection<@org.jetbrains.annotations.Nullable ItemStack>
      extraContents() {
    return java.util.Collections.emptyList();
  }

  @Override
  public void setExtraContents(
      @NotNull java.util.Collection<@org.jetbrains.annotations.Nullable ItemStack> items) {
    throw new UnsupportedOperationException();
  }

  @Override
  public @NotNull java.util.Map<Integer, ItemStack> addItem(@NotNull ItemStack... items) {
    throw new UnsupportedOperationException();
  }

  @Override
  public @NotNull java.util.Map<Integer, ItemStack> removeItem(@NotNull ItemStack... items) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean contains(@NotNull org.aincraft.common.inventory.ItemType type) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean contains(@NotNull ItemStack item) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsAtLeast(@NotNull ItemStack item, int amount) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int first(@NotNull ItemStack item) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int firstEmpty() {
    throw new UnsupportedOperationException();
  }
}
