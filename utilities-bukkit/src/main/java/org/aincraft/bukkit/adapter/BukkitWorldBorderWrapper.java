package org.aincraft.bukkit.adapter;

import org.aincraft.api.domain.location.Location;
import org.aincraft.api.domain.world.World;
import org.aincraft.api.domain.world.WorldBorder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BukkitWorldBorderWrapper implements WorldBorder {

  private final org.bukkit.WorldBorder worldBorder;

  public BukkitWorldBorderWrapper(@NotNull org.bukkit.WorldBorder worldBorder) {
    this.worldBorder = Objects.requireNonNull(worldBorder, "worldBorder cannot be null");
  }

  public @NotNull org.bukkit.WorldBorder getBukkitWorldBorder() {
    return worldBorder;
  }

  @Override
  public double size() {
    return worldBorder.getSize();
  }

  @Override
  public void setSize(double size) {
    worldBorder.setSize(size);
  }

  @Override
  public @NotNull Location center() {
    return BukkitAdapters.adapt(worldBorder.getCenter());
  }

  @Override
  public void setCenter(@NotNull Location center) {
    worldBorder.setCenter(center.x(), center.z());
  }

  @Override
  public double damageBuffer() {
    return worldBorder.getDamageBuffer();
  }

  @Override
  public void setDamageBuffer(double buffer) {
    worldBorder.setDamageBuffer(buffer);
  }

  @Override
  public double damageAmount() {
    return worldBorder.getDamageAmount();
  }

  @Override
  public void setDamageAmount(double amount) {
    worldBorder.setDamageAmount(amount);
  }

  @Override
  public int warningTime() {
    return worldBorder.getWarningTime();
  }

  @Override
  public void setWarningTime(int seconds) {
    worldBorder.setWarningTime(seconds);
  }

  @Override
  public int warningDistance() {
    return worldBorder.getWarningDistance();
  }

  @Override
  public void setWarningDistance(int distance) {
    worldBorder.setWarningDistance(distance);
  }

  /** Spigot exposes the equivalent animation as setSize(double, long seconds). */
  @Override
  public void changeSize(double newSize, long seconds) {
    worldBorder.setSize(newSize, seconds);
  }

  /**
   * Border reset is Paper-only. Spigot's {@code org.bukkit.WorldBorder} has no analogue; throw the
   * capability gap.
   */
  @Override
  public void reset() {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.WORLD_BORDER_ANIMATE,
        "Spigot's org.bukkit.WorldBorder has no reset; use utilities-paper.");
  }

  @Override
  public @NotNull World world() {
    return BukkitAdapters.adapt(worldBorder.getWorld());
  }

  @Override
  public boolean isInside(@NotNull Location location) {
    return worldBorder.isInside(BukkitAdapters.toBukkit(location));
  }
}
