package org.aincraft.minestom.adapter;

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

public class MinestomInventoryWrapper implements Inventory {

  private final net.minestom.server.inventory.AbstractInventory inventory;
  private final InventoryHolder holder;

  public MinestomInventoryWrapper(
      @NotNull net.minestom.server.inventory.AbstractInventory inventory,
      @Nullable InventoryHolder holder) {
    this.inventory = Objects.requireNonNull(inventory, "inventory cannot be null");
    this.holder = holder;
  }

  public @NotNull net.minestom.server.inventory.AbstractInventory getMinestomInventory() {
    return inventory;
  }

  @Override
  public int size() {
    return inventory.getSize();
  }

  @Override
  public @NotNull InventoryType type() {
    if (!(inventory instanceof net.minestom.server.inventory.Inventory menu)) {
      return InventoryType.PLAYER;
    }
    return switch (menu.getInventoryType()) {
      case ANVIL -> InventoryType.ANVIL;
      case BEACON -> InventoryType.BEACON;
      case BLAST_FURNACE -> InventoryType.BLAST_FURNACE;
      case BREWING_STAND -> InventoryType.BREWING;
      case CRAFTING -> InventoryType.WORKBENCH;
      case ENCHANTMENT -> InventoryType.ENCHANTING;
      case FURNACE -> InventoryType.FURNACE;
      case GRINDSTONE -> InventoryType.GRINDSTONE;
      case HOPPER -> InventoryType.HOPPER;
      case LECTERN -> InventoryType.LECTERN;
      case LOOM -> InventoryType.LOOM;
      case MERCHANT -> InventoryType.MERCHANT;
      case SHULKER_BOX -> InventoryType.SHULKER_BOX;
      case SMITHING -> InventoryType.SMITHING;
      case SMOKER -> InventoryType.SMOKER;
      case CARTOGRAPHY -> InventoryType.CARTOGRAPHY;
      case STONE_CUTTER -> InventoryType.STONECUTTER;
      case CRAFTER_3X3 -> InventoryType.CRAFTER;
      case WINDOW_3X3 -> InventoryType.DISPENSER;
      case CHEST_1_ROW, CHEST_2_ROW, CHEST_3_ROW, CHEST_4_ROW, CHEST_5_ROW, CHEST_6_ROW ->
          InventoryType.CHEST;
    };
  }

  @Override
  public @Nullable ItemStack getItem(int slot) {
    checkSlot(slot);
    return fromMinestom(inventory.getItemStack(slot));
  }

  @Override
  public void setItem(int slot, @Nullable ItemStack item) {
    checkSlot(slot);
    inventory.setItemStack(slot, item == null ? air() : MinestomAdapters.toMinestom(item));
  }

  @Override
  public @Nullable ItemStack[] contents() {
    net.minestom.server.item.ItemStack[] source = inventory.getItemStacks();
    ItemStack[] result = new ItemStack[source.length];
    for (int i = 0; i < source.length; i++) result[i] = fromMinestom(source[i]);
    return result;
  }

  @Override
  public void setContents(@NotNull ItemStack[] items) {
    Objects.requireNonNull(items, "items cannot be null");
    if (items.length != size()) {
      throw new IllegalArgumentException("Expected " + size() + " items, got " + items.length);
    }
    for (int i = 0; i < items.length; i++) setItem(i, items[i]);
  }

  @Override
  public @NotNull Map<Integer, ItemStack> addItem(@NotNull ItemStack... items) {
    Objects.requireNonNull(items, "items cannot be null");
    Map<Integer, ItemStack> leftovers = new HashMap<>();
    for (int inputIndex = 0; inputIndex < items.length; inputIndex++) {
      ItemStack remaining =
          Objects.requireNonNull(items[inputIndex], "items contains null").clone();
      int amount = remaining.amount();
      for (int slot = 0; slot < size() && amount > 0; slot++) {
        ItemStack current = getItem(slot);
        if (current == null || !current.isSimilar(remaining)) continue;
        int accepted = Math.min(amount, Math.max(0, current.maxStackSize() - current.amount()));
        if (accepted > 0) {
          current.setAmount(current.amount() + accepted);
          setItem(slot, current);
          amount -= accepted;
        }
      }
      for (int slot = 0; slot < size() && amount > 0; slot++) {
        if (getItem(slot) != null) continue;
        int accepted = Math.min(amount, remaining.maxStackSize());
        setItem(slot, remaining.withAmount(accepted));
        amount -= accepted;
      }
      if (amount > 0) leftovers.put(inputIndex, remaining.withAmount(amount));
    }
    return leftovers;
  }

  @Override
  public @NotNull Map<Integer, ItemStack> removeItem(@NotNull ItemStack... items) {
    Objects.requireNonNull(items, "items cannot be null");
    Map<Integer, ItemStack> leftovers = new HashMap<>();
    for (int inputIndex = 0; inputIndex < items.length; inputIndex++) {
      ItemStack requested = Objects.requireNonNull(items[inputIndex], "items contains null");
      int remaining = requested.amount();
      for (int slot = 0; slot < size() && remaining > 0; slot++) {
        ItemStack current = getItem(slot);
        if (current == null || !current.isSimilar(requested)) continue;
        int removed = Math.min(remaining, current.amount());
        if (removed == current.amount()) setItem(slot, null);
        else {
          current.setAmount(current.amount() - removed);
          setItem(slot, current);
        }
        remaining -= removed;
      }
      if (remaining > 0) leftovers.put(inputIndex, requested.withAmount(remaining));
    }
    return leftovers;
  }

  @Override
  public boolean contains(@NotNull ItemType type) {
    Objects.requireNonNull(type, "type cannot be null");
    for (int slot = 0; slot < size(); slot++) {
      ItemStack item = getItem(slot);
      if (item != null && type.equals(item.type())) return true;
    }
    return false;
  }

  @Override
  public boolean contains(@NotNull ItemStack item) {
    return first(item) >= 0;
  }

  @Override
  public boolean containsAtLeast(@NotNull ItemStack item, int amount) {
    if (amount <= 0) return true;
    int found = 0;
    for (int slot = 0; slot < size(); slot++) {
      ItemStack current = getItem(slot);
      if (current != null && current.isSimilar(item)) {
        found += current.amount();
        if (found >= amount) return true;
      }
    }
    return false;
  }

  @Override
  public int first(@NotNull ItemStack item) {
    Objects.requireNonNull(item, "item cannot be null");
    for (int slot = 0; slot < size(); slot++) {
      ItemStack current = getItem(slot);
      if (current != null && current.isSimilar(item)) return slot;
    }
    return -1;
  }

  @Override
  public int firstEmpty() {
    for (int slot = 0; slot < size(); slot++) if (getItem(slot) == null) return slot;
    return -1;
  }

  @Override
  public void clear() {
    inventory.clear();
  }

  @Override
  public boolean isEmpty() {
    for (int slot = 0; slot < size(); slot++) if (getItem(slot) != null) return false;
    return true;
  }

  @Override
  public @Nullable Location location() {
    return null;
  }

  @Override
  public @Nullable InventoryHolder holder() {
    return holder;
  }

  private void checkSlot(int slot) {
    if (slot < 0 || slot >= size()) throw new IndexOutOfBoundsException("slot=" + slot);
  }

  private static net.minestom.server.item.ItemStack air() {
    return net.minestom.server.item.ItemStack.AIR;
  }

  private static @Nullable ItemStack fromMinestom(net.minestom.server.item.ItemStack item) {
    return item == null || item.isAir() || item.amount() <= 0 ? null : MinestomAdapters.adapt(item);
  }
}
