package org.aincraft.api.domain.datacomponent.item;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Common contract for a group of {@link EquipmentSlot}s.
 *
 * <p>Mirrors {@code org.bukkit.inventory.EquipmentSlotGroup} without any Bukkit types.
 */
public interface EquipmentSlotGroup {

  /**
   * Returns whether the given slot is contained in this group.
   *
   * @param slot the equipment slot to test
   * @return true if the slot is in the group
   */
  boolean test(@NotNull EquipmentSlot slot);

  /**
   * Returns a representative example slot for this group, or {@code null} if the group does not
   * correspond to a concrete common slot.
   *
   * @return a representative equipment slot, or {@code null}
   */
  @Nullable
  EquipmentSlot example();

  /**
   * Returns the name of this slot group (e.g. {@code "any"}, {@code "hand"}).
   *
   * @return the group name
   */
  @NotNull
  String name();

  default @NotNull Key key() {
    return Key.key("minecraft", name());
  }

  default @NotNull java.util.Collection<EquipmentSlot> slots() {
    return java.util.Arrays.stream(EquipmentSlot.values()).filter(this::test).toList();
  }
}
