package org.aincraft.bukkit.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.aincraft.common.inventory.EntityEquipment;
import org.aincraft.common.inventory.EquipmentSlot;
import org.aincraft.common.inventory.ItemStack;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitEntityEquipmentWrapper implements EntityEquipment {

  private final org.bukkit.inventory.EntityEquipment equipment;

  public BukkitEntityEquipmentWrapper(@NotNull org.bukkit.inventory.EntityEquipment equipment) {
    this.equipment = equipment;
  }

  public @NotNull org.bukkit.inventory.EntityEquipment getBukkitEntityEquipment() {
    return equipment;
  }

  @Override
  public @Nullable ItemStack get(@NotNull EquipmentSlot slot) {
    org.bukkit.inventory.ItemStack item = equipment.getItem(toBukkit(slot));
    return isEmpty(item) ? null : BukkitAdapters.adapt(item);
  }

  @Override
  public void set(@NotNull EquipmentSlot slot, @Nullable ItemStack item) {
    equipment.setItem(toBukkit(slot), item != null ? BukkitAdapters.toBukkit(item) : null);
  }

  @Override
  public @NotNull Collection<@NotNull ItemStack> armorContents() {
    return adaptArray(equipment.getArmorContents());
  }

  @Override
  public void setArmorContents(@NotNull Collection<@Nullable ItemStack> items) {
    org.bukkit.inventory.ItemStack[] array = new org.bukkit.inventory.ItemStack[items.size()];
    int i = 0;
    for (ItemStack item : items) {
      array[i++] = item != null ? BukkitAdapters.toBukkit(item) : null;
    }
    equipment.setArmorContents(array);
  }

  @Override
  public @Nullable ItemStack helmet() {
    return get(EquipmentSlot.HEAD);
  }

  @Override
  public void setHelmet(@Nullable ItemStack item) {
    set(EquipmentSlot.HEAD, item);
  }

  @Override
  public @Nullable ItemStack chestplate() {
    return get(EquipmentSlot.CHEST);
  }

  @Override
  public void setChestplate(@Nullable ItemStack item) {
    set(EquipmentSlot.CHEST, item);
  }

  @Override
  public @Nullable ItemStack leggings() {
    return get(EquipmentSlot.LEGS);
  }

  @Override
  public void setLeggings(@Nullable ItemStack item) {
    set(EquipmentSlot.LEGS, item);
  }

  @Override
  public @Nullable ItemStack boots() {
    return get(EquipmentSlot.FEET);
  }

  @Override
  public void setBoots(@Nullable ItemStack item) {
    set(EquipmentSlot.FEET, item);
  }

  @Override
  public @Nullable ItemStack itemInMainHand() {
    return get(EquipmentSlot.HAND);
  }

  @Override
  public void setItemInMainHand(@Nullable ItemStack item) {
    set(EquipmentSlot.HAND, item);
  }

  @Override
  public @Nullable ItemStack itemInOffHand() {
    return get(EquipmentSlot.OFF_HAND);
  }

  @Override
  public void setItemInOffHand(@Nullable ItemStack item) {
    set(EquipmentSlot.OFF_HAND, item);
  }

  private static boolean isEmpty(@Nullable org.bukkit.inventory.ItemStack item) {
    return item == null || item.getType() == org.bukkit.Material.AIR;
  }

  private @NotNull Collection<@NotNull ItemStack> adaptArray(@Nullable org.bukkit.inventory.ItemStack[] items) {
    List<ItemStack> result = new ArrayList<>();
    if (items != null) {
      for (org.bukkit.inventory.ItemStack item : items) {
        if (!isEmpty(item)) {
          result.add(BukkitAdapters.adapt(item));
        }
      }
    }
    return result;
  }

  private static org.bukkit.inventory.EquipmentSlot toBukkit(@NotNull EquipmentSlot slot) {
    return switch (slot) {
      case HEAD -> org.bukkit.inventory.EquipmentSlot.HEAD;
      case CHEST -> org.bukkit.inventory.EquipmentSlot.CHEST;
      case LEGS -> org.bukkit.inventory.EquipmentSlot.LEGS;
      case FEET -> org.bukkit.inventory.EquipmentSlot.FEET;
      case HAND -> org.bukkit.inventory.EquipmentSlot.HAND;
      case OFF_HAND -> org.bukkit.inventory.EquipmentSlot.OFF_HAND;
      case BODY, SADDLE -> toBukkitReflective(slot);
    };
  }

  private static org.bukkit.inventory.EquipmentSlot toBukkitReflective(@NotNull EquipmentSlot slot) {
    try {
      return org.bukkit.inventory.EquipmentSlot.valueOf(slot.name());
    } catch (IllegalArgumentException e) {
      throw new UnsupportedOperationException(slot + " requires Paper/modern Spigot", e);
    }
  }
}
