package org.aincraft.bukkit.adapter;

import org.aincraft.api.domain.inventory.Inventory;
import org.aincraft.api.domain.inventory.InventoryHolder;
import org.aincraft.api.domain.inventory.InventoryType;
import org.aincraft.api.domain.inventory.ItemStack;
import org.aincraft.api.domain.inventory.ItemType;
import org.aincraft.api.domain.location.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BukkitInventoryWrapper implements Inventory {

  private final org.bukkit.inventory.Inventory inventory;

  public BukkitInventoryWrapper(@NotNull org.bukkit.inventory.Inventory inventory) {
    this.inventory = Objects.requireNonNull(inventory, "inventory cannot be null");
  }

  public @NotNull org.bukkit.inventory.Inventory getBukkitInventory() {
    return inventory;
  }

  @Override
  public int size() {
    return inventory.getSize();
  }

  @Override
  public @NotNull InventoryType type() {
    try {
      return InventoryType.valueOf(inventory.getType().name());
    } catch (IllegalArgumentException e) {
      return InventoryType.CHEST;
    }
  }

  @Override
  public @Nullable ItemStack getItem(int slot) {
    org.bukkit.inventory.ItemStack item = inventory.getItem(slot);
    return item != null ? BukkitAdapters.adapt(item) : null;
  }

  @Override
  public void setItem(int slot, @Nullable ItemStack item) {
    inventory.setItem(slot, item != null ? BukkitAdapters.toBukkit(item) : null);
  }

  @Override
  public @Nullable ItemStack[] contents() {
    org.bukkit.inventory.ItemStack[] bContents = inventory.getContents();
    if (bContents == null) return null;
    ItemStack[] result = new ItemStack[bContents.length];
    for (int i = 0; i < bContents.length; i++) {
      result[i] = bContents[i] != null ? BukkitAdapters.adapt(bContents[i]) : null;
    }
    return result;
  }

  @Override
  public void setContents(@NotNull ItemStack[] items) {
    org.bukkit.inventory.ItemStack[] bContents = new org.bukkit.inventory.ItemStack[items.length];
    for (int i = 0; i < items.length; i++) {
      bContents[i] = items[i] != null ? BukkitAdapters.toBukkit(items[i]) : null;
    }
    inventory.setContents(bContents);
  }

  @Override
  public @NotNull Map<Integer, ItemStack> addItem(@NotNull ItemStack... items) {
    org.bukkit.inventory.ItemStack[] bItems = new org.bukkit.inventory.ItemStack[items.length];
    for (int i = 0; i < items.length; i++) {
      bItems[i] = BukkitAdapters.toBukkit(items[i]);
    }
    Map<Integer, org.bukkit.inventory.ItemStack> leftovers = inventory.addItem(bItems);
    Map<Integer, ItemStack> result = new HashMap<>();
    for (Map.Entry<Integer, org.bukkit.inventory.ItemStack> entry : leftovers.entrySet()) {
      result.put(entry.getKey(), BukkitAdapters.adapt(entry.getValue()));
    }
    return result;
  }

  @Override
  public @NotNull Map<Integer, ItemStack> removeItem(@NotNull ItemStack... items) {
    org.bukkit.inventory.ItemStack[] bItems = new org.bukkit.inventory.ItemStack[items.length];
    for (int i = 0; i < items.length; i++) {
      bItems[i] = BukkitAdapters.toBukkit(items[i]);
    }
    Map<Integer, org.bukkit.inventory.ItemStack> leftovers = inventory.removeItem(bItems);
    Map<Integer, ItemStack> result = new HashMap<>();
    for (Map.Entry<Integer, org.bukkit.inventory.ItemStack> entry : leftovers.entrySet()) {
      result.put(entry.getKey(), BukkitAdapters.adapt(entry.getValue()));
    }
    return result;
  }

  @Override
  public boolean contains(@NotNull ItemType type) {
    return inventory.contains(BukkitAdapters.toBukkit(type));
  }

  @Override
  public boolean contains(@NotNull ItemStack item) {
    return inventory.contains(BukkitAdapters.toBukkit(item));
  }

  @Override
  public boolean containsAtLeast(@NotNull ItemStack item, int amount) {
    return inventory.containsAtLeast(BukkitAdapters.toBukkit(item), amount);
  }

  @Override
  public int first(@NotNull ItemStack item) {
    return inventory.first(BukkitAdapters.toBukkit(item));
  }

  @Override
  public int firstEmpty() {
    return inventory.firstEmpty();
  }

  @Override
  public void clear() {
    inventory.clear();
  }

  @Override
  public boolean isEmpty() {
    return inventory.isEmpty();
  }

  @Override
  public @Nullable Location location() {
    org.bukkit.Location loc = inventory.getLocation();
    return loc != null && loc.getWorld() != null ? BukkitAdapters.adapt(loc) : null;
  }

  @Override
  public @Nullable InventoryHolder holder() {
    org.bukkit.inventory.InventoryHolder bHolder = inventory.getHolder();
    if (bHolder instanceof org.bukkit.entity.Player player) {
      return BukkitAdapters.adapt(player);
    }
    return bHolder != null ? () -> this : null;
  }
}
