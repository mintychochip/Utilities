package org.aincraft.bukkit.adapter;

import org.aincraft.common.inventory.ItemStack;
import org.aincraft.common.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitPlayerInventoryWrapper extends BukkitInventoryWrapper implements PlayerInventory {

  private final org.bukkit.inventory.PlayerInventory inventory;

  public BukkitPlayerInventoryWrapper(@NotNull org.bukkit.inventory.PlayerInventory inventory) {
    super(inventory);
    this.inventory = inventory;
  }

  public @NotNull org.bukkit.inventory.PlayerInventory getBukkitPlayerInventory() {
    return inventory;
  }

  @Override
  public @Nullable ItemStack helmet() {
    org.bukkit.inventory.ItemStack item = inventory.getHelmet();
    return item != null ? BukkitAdapters.adapt(item) : null;
  }

  @Override
  public void setHelmet(@Nullable ItemStack helmet) {
    inventory.setHelmet(helmet != null ? BukkitAdapters.toBukkit(helmet) : null);
  }

  @Override
  public @Nullable ItemStack chestplate() {
    org.bukkit.inventory.ItemStack item = inventory.getChestplate();
    return item != null ? BukkitAdapters.adapt(item) : null;
  }

  @Override
  public void setChestplate(@Nullable ItemStack chestplate) {
    inventory.setChestplate(chestplate != null ? BukkitAdapters.toBukkit(chestplate) : null);
  }

  @Override
  public @Nullable ItemStack leggings() {
    org.bukkit.inventory.ItemStack item = inventory.getLeggings();
    return item != null ? BukkitAdapters.adapt(item) : null;
  }

  @Override
  public void setLeggings(@Nullable ItemStack leggings) {
    inventory.setLeggings(leggings != null ? BukkitAdapters.toBukkit(leggings) : null);
  }

  @Override
  public @Nullable ItemStack boots() {
    org.bukkit.inventory.ItemStack item = inventory.getBoots();
    return item != null ? BukkitAdapters.adapt(item) : null;
  }

  @Override
  public void setBoots(@Nullable ItemStack boots) {
    inventory.setBoots(boots != null ? BukkitAdapters.toBukkit(boots) : null);
  }

  @Override
  public @Nullable ItemStack itemInMainHand() {
    org.bukkit.inventory.ItemStack item = inventory.getItemInMainHand();
    return item != null ? BukkitAdapters.adapt(item) : null;
  }

  @Override
  public void setItemInMainHand(@Nullable ItemStack item) {
    inventory.setItemInMainHand(item != null ? BukkitAdapters.toBukkit(item) : null);
  }

  @Override
  public @Nullable ItemStack itemInOffHand() {
    org.bukkit.inventory.ItemStack item = inventory.getItemInOffHand();
    return item != null ? BukkitAdapters.adapt(item) : null;
  }

  @Override
  public void setItemInOffHand(@Nullable ItemStack item) {
    inventory.setItemInOffHand(item != null ? BukkitAdapters.toBukkit(item) : null);
  }

  @Override
  public int heldItemSlot() {
    return inventory.getHeldItemSlot();
  }

  @Override
  public void setHeldItemSlot(int slot) {
    inventory.setHeldItemSlot(slot);
  }
}
