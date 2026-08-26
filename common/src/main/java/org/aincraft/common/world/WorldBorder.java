package org.aincraft.common.world;

import org.aincraft.common.location.Location;
import org.jetbrains.annotations.NotNull;

public interface WorldBorder {

  double size();

  void setSize(double size);

  @NotNull Location center();

  void setCenter(@NotNull Location center);

  double damageBuffer();

  void setDamageBuffer(double buffer);

  double damageAmount();

  void setDamageAmount(double amount);

  int warningTime();

  void setWarningTime(int seconds);

  int warningDistance();

  void setWarningDistance(int distance);

  boolean isInside(@NotNull Location location);
}
