package org.aincraft.common.entity;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.aincraft.common.inventory.InventoryHolder;
import org.aincraft.common.inventory.PlayerInventory;
import org.aincraft.common.server.CommandSender;
import org.aincraft.common.world.GameMode;
import org.jetbrains.annotations.NotNull;

public interface Player extends LivingEntity, Audience, InventoryHolder, CommandSender {

  @NotNull String username();

  @Override
  default @NotNull String name() {
    return username();
  }

  @NotNull Component displayName();

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

  @NotNull GameMode gameMode();

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
  @NotNull PlayerInventory inventory();

  void kick(@NotNull Component reason);
}
