package org.aincraft.bukkit.adapter;

import java.util.Objects;
import org.aincraft.common.location.Location;
import org.aincraft.common.location.Position;
import org.aincraft.common.world.World;
import org.jetbrains.annotations.NotNull;

public class BukkitLocationWrapper implements Location {

  private final org.bukkit.Location bukkitLocation;
  private final World world;
  private final Position position;

  public BukkitLocationWrapper(@NotNull org.bukkit.Location bukkitLocation) {
    this.bukkitLocation = Objects.requireNonNull(bukkitLocation, "bukkitLocation cannot be null");
    org.bukkit.World bWorld = bukkitLocation.getWorld();
    if (bWorld == null) {
      throw new IllegalArgumentException("Bukkit location world cannot be null");
    }
    this.world = BukkitAdapters.adapt(bWorld);
    this.position = new BukkitPositionWrapper(bukkitLocation.toVector());
  }

  public BukkitLocationWrapper(@NotNull org.bukkit.Location bukkitLocation, @NotNull World world) {
    this.bukkitLocation = Objects.requireNonNull(bukkitLocation, "bukkitLocation cannot be null");
    this.world = Objects.requireNonNull(world, "world cannot be null");
    this.position = new BukkitPositionWrapper(bukkitLocation.toVector());
  }

  public @NotNull org.bukkit.Location getBukkitLocation() {
    return bukkitLocation;
  }

  @Override
  public @NotNull World world() {
    return world;
  }

  @Override
  public @NotNull Position position() {
    return position;
  }

  @Override
  public float yaw() {
    return bukkitLocation.getYaw();
  }

  @Override
  public float pitch() {
    return bukkitLocation.getPitch();
  }

  @Override
  public double x() {
    return bukkitLocation.getX();
  }

  @Override
  public double y() {
    return bukkitLocation.getY();
  }

  @Override
  public double z() {
    return bukkitLocation.getZ();
  }

  @Override
  public int blockX() {
    return bukkitLocation.getBlockX();
  }

  @Override
  public int blockY() {
    return bukkitLocation.getBlockY();
  }

  @Override
  public int blockZ() {
    return bukkitLocation.getBlockZ();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Location that)) return false;
    return Float.compare(that.yaw(), yaw()) == 0
        && Float.compare(that.pitch(), pitch()) == 0
        && Double.compare(that.x(), x()) == 0
        && Double.compare(that.y(), y()) == 0
        && Double.compare(that.z(), z()) == 0
        && Objects.equals(world, that.world());
  }

  @Override
  public int hashCode() {
    return Objects.hash(world, x(), y(), z(), yaw(), pitch());
  }

  @Override
  public String toString() {
    return "BukkitLocationWrapper{world=" + world + ", x=" + x() + ", y=" + y() + ", z=" + z() + ", yaw=" + yaw() + ", pitch=" + pitch() + "}";
  }
}
