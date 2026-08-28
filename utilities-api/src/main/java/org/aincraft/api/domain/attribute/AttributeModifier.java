package org.aincraft.api.domain.attribute;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.aincraft.api.domain.datacomponent.item.EquipmentSlotGroup;
import org.aincraft.api.domain.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface AttributeModifier extends Keyed {

  @NotNull
  Key key();

  @NotNull
  UUID id();

  @NotNull
  String name();

  double amount();

  @NotNull
  Operation operation();

  @Nullable
  EquipmentSlot slot();

  /**
   * Returns the equipment slot group this modifier applies to, or {@code null} if none. Paper 1.21+
   * uses slot groups (HAND, ARMOR, ANY, etc.) for attribute modifiers.
   */
  @Nullable
  EquipmentSlotGroup slotGroup();

  enum Operation {
    ADD_NUMBER,
    ADD_SCALAR,
    MULTIPLY_SCALAR_1
  }
}
