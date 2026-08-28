package org.aincraft.bukkit.adapter;

import org.aincraft.api.domain.datacomponent.item.EquipmentSlotGroup;
import org.aincraft.api.domain.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class BukkitEquipmentSlotGroupWrapper implements EquipmentSlotGroup {

  private final org.bukkit.inventory.EquipmentSlotGroup delegate;

  public BukkitEquipmentSlotGroupWrapper(
      @NotNull org.bukkit.inventory.EquipmentSlotGroup delegate) {
    this.delegate = delegate;
  }

  @Override
  public boolean test(@NotNull EquipmentSlot slot) {
    org.bukkit.inventory.EquipmentSlot bukkitSlot = toBukkit(slot);
    return bukkitSlot != null && delegate.test(bukkitSlot);
  }

  @Override
  public @Nullable EquipmentSlot example() {
    EquipmentSlot slot = toCommon(delegate.getExample());
    return slot;
  }

  @Override
  public @NotNull String name() {
    // The Bukkit EquipmentSlotGroup is a keyed interface, not an enum.
    // Resolve the lowercase group key by reverse-mapping from the static
    // accessor on the delegate's own class.
    org.bukkit.inventory.EquipmentSlotGroup self = delegate;
    if (self == null) return "";
    String bukkitName = delegate.toString();
    return bukkitName == null ? "" : bukkitName.toLowerCase(Locale.ROOT);
  }

  private static @Nullable org.bukkit.inventory.EquipmentSlot toBukkit(
      @NotNull EquipmentSlot slot) {
    return switch (slot) {
      case HEAD -> org.bukkit.inventory.EquipmentSlot.HEAD;
      case CHEST -> org.bukkit.inventory.EquipmentSlot.CHEST;
      case LEGS -> org.bukkit.inventory.EquipmentSlot.LEGS;
      case FEET -> org.bukkit.inventory.EquipmentSlot.FEET;
      case HAND -> org.bukkit.inventory.EquipmentSlot.HAND;
      case OFF_HAND -> org.bukkit.inventory.EquipmentSlot.OFF_HAND;
      default -> null;
    };
  }

  private static @Nullable EquipmentSlot toCommon(
      @Nullable org.bukkit.inventory.EquipmentSlot slot) {
    if (slot == null) return null;
    return switch (slot) {
      case HEAD -> EquipmentSlot.HEAD;
      case CHEST -> EquipmentSlot.CHEST;
      case LEGS -> EquipmentSlot.LEGS;
      case FEET -> EquipmentSlot.FEET;
      case HAND -> EquipmentSlot.HAND;
      case OFF_HAND -> EquipmentSlot.OFF_HAND;
      default -> null;
    };
  }
}
