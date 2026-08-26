package org.aincraft.minestom.adapter;

import java.util.Objects;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.WorldBorder;
import org.aincraft.common.location.Location;
import org.aincraft.common.world.World;
import org.jetbrains.annotations.NotNull;

public class MinestomWorldBorderWrapper implements org.aincraft.common.world.WorldBorder {

  private final Instance instance;
  private final World world;

  public MinestomWorldBorderWrapper(@NotNull Instance instance, @NotNull World world) {
    this.instance = Objects.requireNonNull(instance, "instance cannot be null");
    this.world = Objects.requireNonNull(world, "world cannot be null");
  }

  public @NotNull WorldBorder getMinestomWorldBorder() {
    return instance.getWorldBorder();
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
    return 0.0;
  }

  @Override
  public void setDamageBuffer(double buffer) {
  }

  @Override
  public double damageAmount() {
    return 0.0;
  }

  @Override
  public void setDamageAmount(double amount) {
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
  public boolean isInside(@NotNull Location location) {
    return instance.getWorldBorder().inBounds(new Pos(location.x(), location.y(), location.z()));
  }
}
