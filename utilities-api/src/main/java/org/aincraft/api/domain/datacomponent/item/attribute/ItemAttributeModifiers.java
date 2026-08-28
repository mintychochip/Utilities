package org.aincraft.api.domain.datacomponent.item.attribute;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.attribute.AttributeModifier;
import org.aincraft.api.domain.datacomponent.item.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Common contract for the attribute modifiers attached to an item.
 *
 * <p>Mirrors Paper's {@code ItemAttributeModifiers}.
 */
public interface ItemAttributeModifiers {

  /**
   * Returns the attribute modifier entries.
   *
   * @return the list of modifier entries
   */
  @NotNull
  List<Entry> modifiers();

  /**
   * Common contract for a single attribute modifier entry.
   *
   * <p>Mirrors Paper's {@code ItemAttributeModifiers.Entry}.
   */
  interface Entry {

    /**
     * Returns the attribute being modified.
     *
     * @return the attribute
     */
    @NotNull
    Key attribute();

    /**
     * Returns the modifier applied to the attribute.
     *
     * @return the modifier
     */
    @NotNull
    AttributeModifier modifier();

    /**
     * Returns the equipment-slot group the modifier applies to.
     *
     * @return the slot group
     */
    @NotNull
    EquipmentSlotGroup group();

    /**
     * Returns how the attribute line should be displayed.
     *
     * @return the display mode
     */
    @NotNull
    AttributeModifierDisplay display();
  }
}
