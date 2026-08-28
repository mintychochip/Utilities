package org.aincraft.api.domain.world;

import org.aincraft.api.domain.location.Location;
import org.jetbrains.annotations.NotNull;

public interface WorldBorder {

  double size();

  void setSize(double size);

  @NotNull
  Location center();

  void setCenter(@NotNull Location center);

  double damageBuffer();

  void setDamageBuffer(double buffer);

  double damageAmount();

  void setDamageAmount(double amount);

  int warningTime();

  void setWarningTime(int seconds);

  int warningDistance();

  void setWarningDistance(int distance);

  /**
   * Animate the border shrinking/growing to {@code newSize} over {@code seconds}. Paper-only at the
   * adapter level; Bukkit/Spigot throw {@link org.aincraft.api.UnsupportedCapabilityException}.
   */
  void changeSize(double newSize, long seconds);

  /**
   * Restore the border to its world default. Paper-only at the adapter level; Bukkit/Spigot throw
   * {@link org.aincraft.api.UnsupportedCapabilityException}.
   */
  void reset();

  /** The world this border belongs to. */
  @NotNull
  World world();

  boolean isInside(@NotNull Location location);
}
