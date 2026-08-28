package org.aincraft.minestom.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.attribute.AttributeModifier;
import org.aincraft.api.domain.datacomponent.item.EquipmentSlotGroup;
import org.aincraft.api.domain.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

public final class MinestomAttributeModifierWrapper implements AttributeModifier {

  private final net.minestom.server.entity.attribute.AttributeModifier modifier;
  private final UUID id;
  private final EquipmentSlot slot;
  private final EquipmentSlotGroup slotGroup;

  public MinestomAttributeModifierWrapper(
      @NotNull net.minestom.server.entity.attribute.AttributeModifier modifier) {
    this(modifier, null, null);
  }

  public MinestomAttributeModifierWrapper(
      @NotNull net.minestom.server.entity.attribute.AttributeModifier modifier,
      @Nullable EquipmentSlot slot,
      @Nullable EquipmentSlotGroup slotGroup) {
    this.modifier = Objects.requireNonNull(modifier, "modifier cannot be null");
    this.id =
        UUID.nameUUIDFromBytes(("minestom:" + modifier.id()).getBytes(StandardCharsets.UTF_8));
    this.slot = slot;
    this.slotGroup = slotGroup;
  }

  public @NotNull net.minestom.server.entity.attribute.AttributeModifier getMinestomModifier() {
    return modifier;
  }

  @Override
  public @NotNull Key key() {
    return modifier.id();
  }

  @Override
  public @NotNull UUID id() {
    return id;
  }

  @Override
  public @NotNull String name() {
    return modifier.id().asString();
  }

  @Override
  public double amount() {
    return modifier.amount();
  }

  @Override
  public @NotNull Operation operation() {
    return switch (modifier.operation()) {
      case ADD_VALUE -> Operation.ADD_NUMBER;
      case ADD_MULTIPLIED_BASE -> Operation.ADD_SCALAR;
      case ADD_MULTIPLIED_TOTAL -> Operation.MULTIPLY_SCALAR_1;
    };
  }

  @Override
  public @Nullable EquipmentSlot slot() {
    return slot;
  }

  @Override
  public @Nullable EquipmentSlotGroup slotGroup() {
    return slotGroup;
  }

  @Override
  public boolean equals(Object other) {
    return this == other || (other instanceof AttributeModifier value && key().equals(value.key()));
  }

  @Override
  public int hashCode() {
    return key().hashCode();
  }

  @Override
  public String toString() {
    return "MinestomAttributeModifierWrapper{" + key() + "}";
  }
}
