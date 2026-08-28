package org.aincraft.api.domain.entity;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.aincraft.api.domain.effect.EntityEffect;
import org.aincraft.api.domain.inventory.InventoryHolder;
import org.aincraft.api.domain.inventory.InventoryView;
import org.aincraft.api.domain.inventory.ItemStack;
import org.aincraft.api.domain.inventory.PlayerInventory;
import org.aincraft.api.domain.location.Location;
import org.aincraft.api.domain.server.CommandSender;
import org.aincraft.api.domain.world.GameMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A player entity. Extends {@link HumanEntity} so it inherits the ender-chest, exhaustion, and
 * cursor-item contract alongside the Bukkit-equivalent {@code Player} methods.
 */
public interface Player extends HumanEntity, Audience, InventoryHolder, CommandSender {

  @NotNull
  String username();

  @Override
  default @NotNull String name() {
    return username();
  }

  @NotNull
  Component displayName();

  void displayName(@NotNull Component displayName);

  boolean isOnline();

  int ping();

  int foodLevel();

  void setFoodLevel(int foodLevel);

  float saturation();

  void setSaturation(float saturation);

  int level();

  void setLevel(int level);

  float exp();

  void setExp(float exp);

  @NotNull
  GameMode gameMode();

  void setGameMode(@NotNull GameMode gameMode);

  boolean isSneaking();

  void setSneaking(boolean sneaking);

  boolean isSprinting();

  void setSprinting(boolean sprinting);

  boolean isFlying();

  void setFlying(boolean flying);

  boolean allowFlight();

  void setAllowFlight(boolean allow);

  @Override
  @NotNull
  PlayerInventory inventory();

  default @NotNull InventoryView openInventory() {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.INVENTORY_VIEW);
  }

  default @NotNull InventoryView openInventory(
      @NotNull org.aincraft.api.domain.inventory.Inventory inventory) {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.INVENTORY_VIEW);
  }

  default void closeInventory() {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.INVENTORY_VIEW);
  }

  void kick(@NotNull Component reason);

  default void sendEntityEffect(@NotNull EntityEffect effect, @NotNull Entity entity) {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.ENTITY_EFFECT);
  }

  // -- HumanEntity contract implementations (Player-specific) --

  @Override
  @Nullable
  org.aincraft.api.domain.inventory.Inventory enderChest();

  @Override
  @Nullable
  ItemStack itemOnCursor();

  @Override
  void setItemOnCursor(@Nullable ItemStack item);

  @Override
  float exhaustion();

  @Override
  void setExhaustion(float exhaustion);

  /**
   * Returns the bed spawn location for this player, or {@code null} if none is set. Corresponds to
   * {@code Player.getBedSpawnLocation()}.
   */
  @Nullable
  Location bedSpawnLocation();

  /**
   * Sets the bed spawn location. The {@code force} flag forces the spawn even if the bed block is
   * missing/obstructed (Bukkit semantics).
   */
  void setBedSpawnLocation(@Nullable Location location, boolean force);
}
