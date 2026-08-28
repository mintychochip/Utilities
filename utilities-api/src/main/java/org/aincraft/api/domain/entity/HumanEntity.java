package org.aincraft.api.domain.entity;

import org.aincraft.api.domain.inventory.Inventory;
import org.aincraft.api.domain.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Extends {@link LivingEntity} with human-specific gameplay state: ender chest access, cursor item,
 * and hunger/exhaustion.
 *
 * <p>In Bukkit/Paper, {@code Player} directly implements this. Here {@code Player} extends {@link
 * LivingEntity} and also extends this interface so adapters can expose the full {@code HumanEntity}
 * contract for both players and non-player human entities.
 *
 * @see Player
 */
public interface HumanEntity extends LivingEntity {

  /** Returns the ender chest inventory for this player. */
  @NotNull
  Inventory enderChest();

  /** Returns the item currently on the player's cursor. */
  @Nullable
  ItemStack itemOnCursor();

  /** Sets the item on the player's cursor. */
  void setItemOnCursor(@Nullable ItemStack item);

  /** Returns the current exhaustion level (hunger drain rate). */
  float exhaustion();

  /** Sets the exhaustion level. */
  void setExhaustion(float exhaustion);
}
