package org.aincraft.common.entity;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface Player extends Entity, Audience {

  @NotNull String username();

  boolean isOnline();

  int ping();

  double health();

  double maxHealth();

  int foodLevel();

  float saturation();

  int level();

  float exp();

  @NotNull Key gameMode();

  boolean isSneaking();

  boolean isSprinting();

  boolean isFlying();

  void setFlying(boolean flying);

  void setSneaking(boolean sneaking);

  void setSprinting(boolean sprinting);

  void kick(@NotNull Component reason);
}
