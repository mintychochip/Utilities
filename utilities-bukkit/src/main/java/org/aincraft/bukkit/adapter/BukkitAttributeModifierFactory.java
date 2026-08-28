package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.attribute.AttributeModifier;
import org.aincraft.api.domain.attribute.AttributeModifierFactory;
import org.aincraft.api.domain.datacomponent.item.EquipmentSlotGroup;
import org.aincraft.api.domain.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class BukkitAttributeModifierFactory implements AttributeModifierFactory {

  @Override
  public @NotNull AttributeModifier create(
      @NotNull Key key,
      double amount,
      @NotNull AttributeModifier.Operation operation,
      @Nullable EquipmentSlot slot) {
    org.bukkit.attribute.AttributeModifier modifier =
        new org.bukkit.attribute.AttributeModifier(
            UUID.nameUUIDFromBytes(("aincraft:" + key).getBytes(StandardCharsets.UTF_8)),
            key.value(),
            amount,
            toBukkitOperation(operation),
            slot == null ? null : toBukkitSlot(slot));
    return BukkitAdapters.adapt(modifier);
  }

  @Override
  public @NotNull AttributeModifier create(
      @NotNull Key key,
      double amount,
      @NotNull AttributeModifier.Operation operation,
      @Nullable EquipmentSlotGroup slotGroup) {
    org.bukkit.attribute.AttributeModifier modifier =
        new org.bukkit.attribute.AttributeModifier(
            new org.bukkit.NamespacedKey(key.namespace(), key.value()),
            amount,
            toBukkitOperation(operation),
            slotGroup == null
                ? org.bukkit.inventory.EquipmentSlotGroup.ANY
                : toBukkitGroup(slotGroup));
    return BukkitAdapters.adapt(modifier);
  }

  @Override
  public @NotNull AttributeModifier create(
      @NotNull UUID id,
      @NotNull String name,
      double amount,
      @NotNull AttributeModifier.Operation operation,
      @Nullable EquipmentSlot slot) {
    org.bukkit.attribute.AttributeModifier modifier =
        new org.bukkit.attribute.AttributeModifier(
            id,
            name,
            amount,
            toBukkitOperation(operation),
            slot == null ? null : toBukkitSlot(slot));
    return BukkitAdapters.adapt(modifier);
  }

  private static org.bukkit.attribute.AttributeModifier.Operation toBukkitOperation(
      AttributeModifier.Operation operation) {
    return switch (operation) {
      case ADD_NUMBER -> org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER;
      case ADD_SCALAR -> org.bukkit.attribute.AttributeModifier.Operation.ADD_SCALAR;
      case MULTIPLY_SCALAR_1 -> org.bukkit.attribute.AttributeModifier.Operation.MULTIPLY_SCALAR_1;
    };
  }

  private static org.bukkit.inventory.EquipmentSlot toBukkitSlot(EquipmentSlot slot) {
    return switch (slot) {
      case HAND -> org.bukkit.inventory.EquipmentSlot.HAND;
      case OFF_HAND -> org.bukkit.inventory.EquipmentSlot.OFF_HAND;
      case FEET -> org.bukkit.inventory.EquipmentSlot.FEET;
      case LEGS -> org.bukkit.inventory.EquipmentSlot.LEGS;
      case CHEST -> org.bukkit.inventory.EquipmentSlot.CHEST;
      case HEAD -> org.bukkit.inventory.EquipmentSlot.HEAD;
      case BODY, SADDLE ->
          throw new org.aincraft.api.UnsupportedCapabilityException(
              org.aincraft.api.Capability.ATTRIBUTE_MODIFIER,
              "Spigot has no single equipment slot for " + slot);
    };
  }

  private static org.bukkit.inventory.EquipmentSlotGroup toBukkitGroup(EquipmentSlotGroup group) {
    org.bukkit.inventory.EquipmentSlotGroup result =
        org.bukkit.inventory.EquipmentSlotGroup.getByName(group.name());
    return result == null ? org.bukkit.inventory.EquipmentSlotGroup.ANY : result;
  }
}
