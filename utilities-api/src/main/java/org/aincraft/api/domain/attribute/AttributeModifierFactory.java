package org.aincraft.api.domain.attribute;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.datacomponent.item.EquipmentSlotGroup;
import org.aincraft.api.domain.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Factory for creating platform-neutral {@link AttributeModifier} instances. Adapters provide
 * implementations that construct the correct platform modifier (Bukkit {@code AttributeModifier} or
 * Minestom equivalent) from these parameters.
 *
 * @see AttributeModifier
 */
public interface AttributeModifierFactory {

  /**
   * Creates an attribute modifier with a single-slot attachment.
   *
   * @param key unique key identifying this modifier within the attribute
   * @param amount the modifier magnitude
   * @param operation the arithmetic operation applied
   * @param slot the equipment slot this applies to, or {@code null} for any slot
   * @return a new modifier ready for {@link AttributeInstance#addModifier}
   */
  @NotNull
  AttributeModifier create(
      @NotNull Key key,
      double amount,
      @NotNull AttributeModifier.Operation operation,
      @Nullable EquipmentSlot slot);

  default @NotNull AttributeModifier create(
      @NotNull Key key, double amount, @NotNull AttributeModifier.Operation operation) {
    return create(key, amount, operation, (EquipmentSlot) null);
  }

  default @NotNull AttributeModifier create(
      @NotNull java.util.UUID id,
      @NotNull String name,
      double amount,
      @NotNull AttributeModifier.Operation operation,
      @Nullable EquipmentSlot slot) {
    return new AttributeModifier() {
      @Override
      public @NotNull Key key() {
        return Key.key("aincraft", name);
      }

      @Override
      public @NotNull java.util.UUID id() {
        return id;
      }

      @Override
      public @NotNull String name() {
        return name;
      }

      @Override
      public double amount() {
        return amount;
      }

      @Override
      public @NotNull AttributeModifier.Operation operation() {
        return operation;
      }

      @Override
      public @Nullable EquipmentSlot slot() {
        return slot;
      }

      @Override
      public @Nullable EquipmentSlotGroup slotGroup() {
        return null;
      }
    };
  }

  /**
   * Creates an attribute modifier with a slot-group attachment (Paper 1.21+ style).
   *
   * @param key unique key identifying this modifier
   * @param amount the modifier magnitude
   * @param operation the arithmetic operation
   * @param slotGroup the equipment slot group this applies to, or {@code null}
   * @return a new modifier for {@link AttributeInstance#addModifier}
   */
  @NotNull
  AttributeModifier create(
      @NotNull Key key,
      double amount,
      @NotNull AttributeModifier.Operation operation,
      @Nullable EquipmentSlotGroup slotGroup);
}
