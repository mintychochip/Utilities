package org.aincraft.minestom.adapter;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.WorldBorder;
import org.aincraft.api.domain.location.Location;
import org.aincraft.api.domain.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class MinestomWorldBorderWrapper implements org.aincraft.api.domain.world.WorldBorder {

  private final Instance instance;
  private final World world;
  private volatile double damageBuffer;
  private volatile double damageAmount;

  public MinestomWorldBorderWrapper(@NotNull Instance instance, @NotNull World world) {
    this.instance = Objects.requireNonNull(instance, "instance cannot be null");
    this.world = Objects.requireNonNull(world, "world cannot be null");
  }

  @Override
  public double size() {
    return instance.getWorldBorder().diameter();
  }

  @Override
  public void setSize(double size) {
    instance.setWorldBorder(instance.getWorldBorder().withDiameter(size));
  }

  @Override
  public @NotNull Location center() {
    WorldBorder wb = instance.getWorldBorder();
    return new MinestomLocationWrapper(world, new Pos(wb.centerX(), 0, wb.centerZ()));
  }

  @Override
  public void setCenter(@NotNull Location center) {
    instance.setWorldBorder(instance.getWorldBorder().withCenter(center.x(), center.z()));
  }

  @Override
  public double damageBuffer() {
    return damageBuffer;
  }

  @Override
  public void setDamageBuffer(double buffer) {
    if (buffer < 0.0) throw new IllegalArgumentException("Damage buffer cannot be negative");
    damageBuffer = buffer;
  }

  @Override
  public double damageAmount() {
    return damageAmount;
  }

  @Override
  public void setDamageAmount(double amount) {
    if (amount < 0.0) throw new IllegalArgumentException("Damage amount cannot be negative");
    damageAmount = amount;
  }

  @Override
  public int warningTime() {
    return instance.getWorldBorder().warningTime();
  }

  @Override
  public void setWarningTime(int seconds) {
    instance.setWorldBorder(instance.getWorldBorder().withWarningTime(seconds));
  }

  @Override
  public int warningDistance() {
    return instance.getWorldBorder().warningDistance();
  }

  @Override
  public void setWarningDistance(int distance) {
    instance.setWorldBorder(instance.getWorldBorder().withWarningDistance(distance));
  }

  @Override
  public void changeSize(double newSize, long seconds) {
    if (seconds < 0) throw new IllegalArgumentException("Duration cannot be negative");
    instance.setWorldBorder(instance.getWorldBorder().withDiameter(newSize), (double) seconds);
  }

  @Override
  public void reset() {
    instance.setWorldBorder(WorldBorder.DEFAULT_BORDER);
    damageBuffer = 0.0;
    damageAmount = 0.0;
  }

  @Override
  public @NotNull World world() {
    return world;
  }

  @Override
  public boolean isInside(@NotNull Location location) {
    return instance.getWorldBorder().inBounds(new Pos(location.x(), location.y(), location.z()));
  }
}
