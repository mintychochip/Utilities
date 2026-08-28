package org.aincraft.bukkit.adapter;

import org.aincraft.api.domain.location.Location;
import org.aincraft.api.domain.location.Position;
import org.aincraft.api.domain.world.HeightMap;
import org.aincraft.api.domain.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BukkitLocationWrapper implements Location {

  private final org.bukkit.Location bukkitLoc;
  private final World world;
  private final Position position;

  public BukkitLocationWrapper(@NotNull org.bukkit.Location bukkitLoc) {
    this.bukkitLoc = Objects.requireNonNull(bukkitLoc, "bukkitLoc cannot be null");
    org.bukkit.World bWorld = bukkitLoc.getWorld();
    if (bWorld == null) {
      throw new IllegalArgumentException("Bukkit location world cannot be null");
    }
    this.world = BukkitAdapters.adapt(bWorld);
    this.position = new BukkitPositionWrapper(bukkitLoc.toVector());
  }

  public BukkitLocationWrapper(@NotNull org.bukkit.Location bukkitLoc, @NotNull World world) {
    this.bukkitLoc = Objects.requireNonNull(bukkitLoc, "bukkitLoc cannot be null");
    this.world = Objects.requireNonNull(world, "world cannot be null");
    this.position = new BukkitPositionWrapper(bukkitLoc.toVector());
  }

  public @NotNull org.bukkit.Location getBukkitLocation() {
    return bukkitLoc;
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
    return bukkitLoc.getYaw();
  }

  @Override
  public float pitch() {
    return bukkitLoc.getPitch();
  }

  @Override
  public double x() {
    return bukkitLoc.getX();
  }

  @Override
  public double y() {
    return bukkitLoc.getY();
  }

  @Override
  public double z() {
    return bukkitLoc.getZ();
  }

  @Override
  public int blockX() {
    return bukkitLoc.getBlockX();
  }

  @Override
  public int blockY() {
    return bukkitLoc.getBlockY();
  }

  @Override
  public int blockZ() {
    return bukkitLoc.getBlockZ();
  }

  @Override
  public @NotNull org.joml.Vector3dc direction() {
    org.bukkit.util.Vector d = bukkitLoc.getDirection();
    return new org.joml.Vector3d(d.getX(), d.getY(), d.getZ());
  }

  @Override
  public @NotNull Location withOffset(double dx, double dy, double dz) {
    return new BukkitLocationWrapper(bukkitLoc.clone().add(dx, dy, dz), world);
  }

  @Override
  public @NotNull Location withOffset(@NotNull org.joml.Vector3dc offset) {
    return withOffset(offset.x(), offset.y(), offset.z());
  }

  @Override
  public @NotNull Location withRotation(float yaw, float pitch) {
    org.bukkit.Location copy = bukkitLoc.clone();
    copy.setYaw(yaw);
    copy.setPitch(pitch);
    return new BukkitLocationWrapper(copy, world);
  }

  /** Integer-aligned block coordinates (floor of x/y/z), yaw/pitch preserved. */
  @Override
  public @NotNull Location toBlockLocation() {
    org.bukkit.Location copy = bukkitLoc.clone();
    copy.setX(blockX());
    copy.setY(blockY());
    copy.setZ(blockZ());
    return new BukkitLocationWrapper(copy, world);
  }

  /** Center of the block column (x+0.5, z+0.5, y unchanged). */
  @Override
  public @NotNull Location toCenterLocation() {
    org.bukkit.Location copy = bukkitLoc.clone();
    copy.setX(blockX() + 0.5);
    copy.setZ(blockZ() + 0.5);
    return new BukkitLocationWrapper(copy, world);
  }

  @Override
  public @NotNull java.util.Collection<? extends org.aincraft.api.domain.entity.Entity>
      nearbyEntities(double radius) {
    return nearbyEntities(radius, radius, radius);
  }

  @Override
  public @NotNull java.util.Collection<? extends org.aincraft.api.domain.entity.Entity>
      nearbyEntities(double xRadius, double yRadius, double zRadius) {
    org.bukkit.World bukkitWorld = bukkitLoc.getWorld();
    if (bukkitWorld == null) return java.util.List.of();
    java.util.List<org.aincraft.api.domain.entity.Entity> out = new java.util.ArrayList<>();
    for (org.bukkit.entity.Entity entity :
        bukkitWorld.getNearbyEntities(bukkitLoc, xRadius, yRadius, zRadius)) {
      out.add(BukkitAdapters.adapt(entity));
    }
    return out;
  }

  @Override
  public @NotNull java.util.Collection<? extends org.aincraft.api.domain.entity.Player>
      nearbyPlayers(double radius) {
    return nearbyPlayers(radius, radius, radius);
  }

  @Override
  public @NotNull java.util.Collection<? extends org.aincraft.api.domain.entity.Player>
      nearbyPlayers(double xRadius, double yRadius, double zRadius) {
    java.util.List<org.aincraft.api.domain.entity.Player> out = new java.util.ArrayList<>();
    for (org.bukkit.entity.Entity entity :
        bukkitLoc.getWorld().getNearbyEntities(bukkitLoc, xRadius, yRadius, zRadius)) {
      if (entity instanceof org.bukkit.entity.Player player) {
        out.add(BukkitAdapters.adapt(player));
      }
    }
    return out;
  }

  @Override
  public @NotNull java.util.Collection<? extends org.aincraft.api.domain.entity.LivingEntity>
      nearbyLivingEntities(double xRadius, double yRadius, double zRadius) {
    java.util.List<org.aincraft.api.domain.entity.LivingEntity> out = new java.util.ArrayList<>();
    for (org.bukkit.entity.Entity entity :
        bukkitLoc.getWorld().getNearbyEntities(bukkitLoc, xRadius, yRadius, zRadius)) {
      if (entity instanceof org.bukkit.entity.LivingEntity living) {
        out.add(BukkitAdapters.adapt(living));
      }
    }
    return out;
  }

  @Override
  public boolean isChunkLoaded() {
    org.bukkit.World w = bukkitLoc.getWorld();
    if (w == null) return false;
    return w.isChunkLoaded(blockX() >> 4, blockZ() >> 4);
  }

  @Override
  public long toBlockKey() {
    long key = ((long) blockX() & 0x3FFFFFFL) << 38;
    key |= ((long) blockZ() & 0x3FFFFFFL) << 12;
    key |= ((long) blockY() & 0xFFFL);
    return key;
  }

  @Override
  public @NotNull Location toHighestLocation() {
    return toHighestLocation(HeightMap.WORLD_SURFACE);
  }

  @Override
  public @NotNull Location toHighestLocation(@NotNull HeightMap heightMap) {
    org.bukkit.World w = bukkitLoc.getWorld();
    if (w == null) return this;
    org.bukkit.block.Block highest =
        w.getHighestBlockAt(bukkitLoc, BukkitAdapters.toBukkit(heightMap));
    if (highest == null) return this;
    org.bukkit.Location highestLoc = highest.getLocation().clone();
    highestLoc.setYaw(bukkitLoc.getYaw());
    highestLoc.setPitch(bukkitLoc.getPitch());
    return new BukkitLocationWrapper(highestLoc, world);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Location that)) return false;
    return Double.compare(that.x(), x()) == 0
        && Double.compare(that.y(), y()) == 0
        && Double.compare(that.z(), z()) == 0
        && Float.compare(that.yaw(), yaw()) == 0
        && Float.compare(that.pitch(), pitch()) == 0
        && world.equals(that.world());
  }

  @Override
  public int hashCode() {
    return Objects.hash(world, x(), y(), z(), yaw(), pitch());
  }

  @Override
  public String toString() {
    return "BukkitLocationWrapper{world="
        + world.name()
        + ", x="
        + x()
        + ", y="
        + y()
        + ", z="
        + z()
        + ", yaw="
        + yaw()
        + ", pitch="
        + pitch()
        + "}";
  }
}
