package org.aincraft.common.attribute;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.aincraft.common.inventory.EquipmentSlot;
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

  enum Operation {
    ADD_NUMBER,
    ADD_SCALAR,
    MULTIPLY_SCALAR_1
  }
}
