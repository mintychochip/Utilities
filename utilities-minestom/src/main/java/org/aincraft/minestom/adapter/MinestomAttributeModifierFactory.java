package org.aincraft.minestom.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.attribute.AttributeModifier;
import org.aincraft.api.domain.attribute.AttributeModifierFactory;
import org.aincraft.api.domain.datacomponent.item.EquipmentSlotGroup;
import org.aincraft.api.domain.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MinestomAttributeModifierFactory implements AttributeModifierFactory {

  @Override
  public @NotNull AttributeModifier create(
      @NotNull Key key,
      double amount,
      @NotNull AttributeModifier.Operation operation,
      @Nullable EquipmentSlot slot) {
    return new MinestomAttributeModifierWrapper(createModifier(key, amount, operation), slot, null);
  }

  public @NotNull AttributeModifier create(
      @NotNull Key key,
      double amount,
      @NotNull AttributeModifier.Operation operation,
      @Nullable EquipmentSlotGroup slotGroup) {
    return new MinestomAttributeModifierWrapper(
        createModifier(key, amount, operation), null, slotGroup);
  }

  private static net.minestom.server.entity.attribute.AttributeModifier createModifier(
      Key key, double amount, AttributeModifier.Operation operation) {
    return new net.minestom.server.entity.attribute.AttributeModifier(
        key,
        amount,
        switch (operation) {
          case ADD_NUMBER -> net.minestom.server.entity.attribute.AttributeOperation.ADD_VALUE;
          case ADD_SCALAR ->
              net.minestom.server.entity.attribute.AttributeOperation.ADD_MULTIPLIED_BASE;
          case MULTIPLY_SCALAR_1 ->
              net.minestom.server.entity.attribute.AttributeOperation.ADD_MULTIPLIED_TOTAL;
        });
  }
}
