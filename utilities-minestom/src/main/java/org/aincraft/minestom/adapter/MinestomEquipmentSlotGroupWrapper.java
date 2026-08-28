package org.aincraft.minestom.adapter;

import org.aincraft.api.domain.datacomponent.item.EquipmentSlotGroup;
import org.aincraft.api.domain.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class MinestomEquipmentSlotGroupWrapper implements EquipmentSlotGroup {

  private final net.minestom.server.entity.EquipmentSlotGroup group;

  public MinestomEquipmentSlotGroupWrapper(
      @NotNull net.minestom.server.entity.EquipmentSlotGroup group) {
    this.group = Objects.requireNonNull(group, "group cannot be null");
  }

  public @NotNull net.minestom.server.entity.EquipmentSlotGroup getMinestomGroup() {
    return group;
  }

  @Override
  public boolean test(@NotNull EquipmentSlot slot) {
    return group.contains(toMinestom(slot));
  }

  @Override
  public @Nullable EquipmentSlot example() {
    java.util.List<net.minestom.server.entity.EquipmentSlot> slots = group.equipmentSlots();
    return slots.isEmpty() ? null : fromMinestom(slots.getFirst());
  }

  @Override
  public @NotNull String name() {
    return group.nbtName();
  }

  private static net.minestom.server.entity.EquipmentSlot toMinestom(EquipmentSlot slot) {
    return switch (slot) {
      case HAND -> net.minestom.server.entity.EquipmentSlot.MAIN_HAND;
      case OFF_HAND -> net.minestom.server.entity.EquipmentSlot.OFF_HAND;
      case FEET -> net.minestom.server.entity.EquipmentSlot.BOOTS;
      case LEGS -> net.minestom.server.entity.EquipmentSlot.LEGGINGS;
      case CHEST -> net.minestom.server.entity.EquipmentSlot.CHESTPLATE;
      case HEAD -> net.minestom.server.entity.EquipmentSlot.HELMET;
      case BODY -> net.minestom.server.entity.EquipmentSlot.BODY;
      case SADDLE -> net.minestom.server.entity.EquipmentSlot.SADDLE;
    };
  }

  private static @Nullable EquipmentSlot fromMinestom(
      net.minestom.server.entity.EquipmentSlot slot) {
    return switch (slot) {
      case MAIN_HAND -> EquipmentSlot.HAND;
      case OFF_HAND -> EquipmentSlot.OFF_HAND;
      case BOOTS -> EquipmentSlot.FEET;
      case LEGGINGS -> EquipmentSlot.LEGS;
      case CHESTPLATE -> EquipmentSlot.CHEST;
      case HELMET -> EquipmentSlot.HEAD;
      case BODY -> EquipmentSlot.BODY;
      case SADDLE -> EquipmentSlot.SADDLE;
    };
  }
}
