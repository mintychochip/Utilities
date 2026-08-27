package org.aincraft.common.inventory;

import org.jetbrains.annotations.NotNull;

public enum EquipmentSlot {
  HAND,
  OFF_HAND,
  FEET,
  LEGS,
  CHEST,
  HEAD,
  BODY,
  SADDLE;

  public boolean isHand() {
    return this == HAND || this == OFF_HAND;
  }

  public boolean isArmor() {
    return this == HEAD || this == CHEST || this == LEGS || this == FEET || this == BODY;
  }

  public @NotNull EquipmentSlot oppositeHand() {
    return switch (this) {
      case HAND -> OFF_HAND;
      case OFF_HAND -> HAND;
      default -> throw new IllegalArgumentException("Slot " + this + " is not a hand slot");
    };
  }
}
