package org.aincraft.bukkit.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.aincraft.common.inventory.EquipmentSlot;
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
    return item != null && item.getType() != org.bukkit.Material.AIR ? BukkitAdapters.adapt(item) : null;
  }

  @Override
  public void setHelmet(@Nullable ItemStack helmet) {
    inventory.setHelmet(helmet != null ? BukkitAdapters.toBukkit(helmet) : null);
  }

  @Override
  public @Nullable ItemStack chestplate() {
    org.bukkit.inventory.ItemStack item = inventory.getChestplate();
    return item != null && item.getType() != org.bukkit.Material.AIR ? BukkitAdapters.adapt(item) : null;
  }

  @Override
  public void setChestplate(@Nullable ItemStack chestplate) {
    inventory.setChestplate(chestplate != null ? BukkitAdapters.toBukkit(chestplate) : null);
  }

  @Override
  public @Nullable ItemStack leggings() {
    org.bukkit.inventory.ItemStack item = inventory.getLeggings();
    return item != null && item.getType() != org.bukkit.Material.AIR ? BukkitAdapters.adapt(item) : null;
  }

  @Override
  public void setLeggings(@Nullable ItemStack leggings) {
    inventory.setLeggings(leggings != null ? BukkitAdapters.toBukkit(leggings) : null);
  }

  @Override
  public @Nullable ItemStack boots() {
    org.bukkit.inventory.ItemStack item = inventory.getBoots();
    return item != null && item.getType() != org.bukkit.Material.AIR ? BukkitAdapters.adapt(item) : null;
  }

  @Override
  public void setBoots(@Nullable ItemStack boots) {
    inventory.setBoots(boots != null ? BukkitAdapters.toBukkit(boots) : null);
  }

  @Override
  public @Nullable ItemStack itemInMainHand() {
    org.bukkit.inventory.ItemStack item = inventory.getItemInMainHand();
    return item != null && item.getType() != org.bukkit.Material.AIR ? BukkitAdapters.adapt(item) : null;
  }

  @Override
  public void setItemInMainHand(@Nullable ItemStack item) {
    inventory.setItemInMainHand(item != null ? BukkitAdapters.toBukkit(item) : null);
  }

  @Override
  public @Nullable ItemStack itemInOffHand() {
    org.bukkit.inventory.ItemStack item = inventory.getItemInOffHand();
    return item != null && item.getType() != org.bukkit.Material.AIR ? BukkitAdapters.adapt(item) : null;
  }

  @Override
  public void setItemInOffHand(@Nullable ItemStack item) {
    inventory.setItemInOffHand(item != null ? BukkitAdapters.toBukkit(item) : null);
  }

  @Override
  public @NotNull Collection<@Nullable ItemStack> armorContents() {
    return adaptArray(inventory.getArmorContents());
  }

  @Override
  public void setArmorContents(@NotNull Collection<@Nullable ItemStack> items) {
    org.bukkit.inventory.ItemStack[] array = new org.bukkit.inventory.ItemStack[items.size()];
    int i = 0;
    for (ItemStack item : items) {
      array[i++] = item != null ? BukkitAdapters.toBukkit(item) : null;
    }
    inventory.setArmorContents(array);
  }

  @Override
  public @NotNull Collection<@Nullable ItemStack> extraContents() {
    return adaptArray(inventory.getExtraContents());
  }

  @Override
  public void setExtraContents(@NotNull Collection<@Nullable ItemStack> items) {
    org.bukkit.inventory.ItemStack[] array = new org.bukkit.inventory.ItemStack[items.size()];
    int i = 0;
    for (ItemStack item : items) {
      array[i++] = item != null ? BukkitAdapters.toBukkit(item) : null;
    }
    inventory.setExtraContents(array);
  }

  @Override
  public @Nullable ItemStack getItem(@NotNull EquipmentSlot slot) {
    return switch (slot) {
      case HEAD -> helmet();
      case CHEST -> chestplate();
      case LEGS -> leggings();
      case FEET -> boots();
      case HAND -> itemInMainHand();
      case OFF_HAND -> itemInOffHand();
      case BODY, SADDLE -> throw new UnsupportedOperationException(
          "EquipmentSlot." + slot + " is not supported for PlayerInventory on Spigot");
    };
  }

  @Override
  public void setItem(@NotNull EquipmentSlot slot, @Nullable ItemStack item) {
    switch (slot) {
      case HEAD -> setHelmet(item);
      case CHEST -> setChestplate(item);
      case LEGS -> setLeggings(item);
      case FEET -> setBoots(item);
      case HAND -> setItemInMainHand(item);
      case OFF_HAND -> setItemInOffHand(item);
      case BODY, SADDLE -> throw new UnsupportedOperationException(
          "EquipmentSlot." + slot + " is not supported for PlayerInventory on Spigot");
    }
  }

  @Override
  public int heldItemSlot() {
    return inventory.getHeldItemSlot();
  }

  @Override
  public void setHeldItemSlot(int slot) {
    inventory.setHeldItemSlot(slot);
  }

  private @NotNull Collection<@Nullable ItemStack> adaptArray(org.bukkit.inventory.ItemStack[] items) {
    List<ItemStack> result = new ArrayList<>();
    if (items != null) {
      for (org.bukkit.inventory.ItemStack item : items) {
        result.add(item != null && item.getType() != org.bukkit.Material.AIR ? BukkitAdapters.adapt(item) : null);
      }
    }
    return result;
  }
}
