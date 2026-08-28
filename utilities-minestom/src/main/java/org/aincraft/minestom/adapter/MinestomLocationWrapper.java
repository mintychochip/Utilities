package org.aincraft.minestom.adapter;

import net.minestom.server.coordinate.Pos;
import org.aincraft.api.domain.location.Location;
import org.aincraft.api.domain.location.Position;
import org.aincraft.api.domain.world.HeightMap;
import org.aincraft.api.domain.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MinestomLocationWrapper implements Location {

  private final Pos pos;
  private final World world;
  private final Position position;

  public MinestomLocationWrapper(@NotNull World world, @NotNull Pos pos) {
    this.world = Objects.requireNonNull(world, "world cannot be null");
    this.pos = Objects.requireNonNull(pos, "pos cannot be null");
    this.position = new MinestomPositionWrapper(pos);
  }

  public @NotNull Pos getPos() {
    return pos;
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
    return pos.yaw();
  }

  @Override
  public float pitch() {
    return pos.pitch();
  }

  @Override
  public double x() {
    return pos.x();
  }

  @Override
  public double y() {
    return pos.y();
  }

  @Override
  public double z() {
    return pos.z();
  }

  @Override
  public int blockX() {
    return pos.blockX();
  }

  @Override
  public int blockY() {
    return pos.blockY();
  }

  @Override
  public int blockZ() {
    return pos.blockZ();
  }

  @Override
  public @NotNull org.joml.Vector3dc direction() {
    double yawRad = Math.toRadians(pos.yaw());
    double pitchRad = Math.toRadians(pos.pitch());
    double x = -Math.sin(yawRad) * Math.cos(pitchRad);
    double y = -Math.sin(pitchRad);
    double z = Math.cos(yawRad) * Math.cos(pitchRad);
    return new org.joml.Vector3d(x, y, z);
  }

  @Override
  public @NotNull Location withOffset(double dx, double dy, double dz) {
    return new MinestomLocationWrapper(
        world, new Pos(pos.x() + dx, pos.y() + dy, pos.z() + dz, pos.yaw(), pos.pitch()));
  }

  @Override
  public @NotNull Location withOffset(@NotNull org.joml.Vector3dc offset) {
    return withOffset(offset.x(), offset.y(), offset.z());
  }

  @Override
  public @NotNull Location withRotation(float yaw, float pitch) {
    return new MinestomLocationWrapper(world, new Pos(pos.x(), pos.y(), pos.z(), yaw, pitch));
  }

  @Override
  public @NotNull Location toBlockLocation() {
    return new MinestomLocationWrapper(
        world, new Pos(blockX(), blockY(), blockZ(), pos.yaw(), pos.pitch()));
  }

  @Override
  public @NotNull Location toCenterLocation() {
    return new MinestomLocationWrapper(
        world, new Pos(blockX() + 0.5, blockY(), blockZ() + 0.5, pos.yaw(), pos.pitch()));
  }

  @Override
  public @NotNull java.util.Collection<? extends org.aincraft.api.domain.entity.Entity>
      nearbyEntities(double radius) {
    return nearbyEntities(radius, radius, radius);
  }

  @Override
  public @NotNull java.util.Collection<? extends org.aincraft.api.domain.entity.Entity>
      nearbyEntities(double xRadius, double yRadius, double zRadius) {
    double x = Math.abs(xRadius);
    double y = Math.abs(yRadius);
    double z = Math.abs(zRadius);
    net.minestom.server.instance.Instance instance = MinestomAdapters.toMinestom(world);
    double radius = Math.max(x, Math.max(y, z));
    return instance.getNearbyEntities(pos, radius).stream()
        .filter(
            entity ->
                Math.abs(entity.getPosition().x() - x()) <= x
                    && Math.abs(entity.getPosition().y() - y()) <= y
                    && Math.abs(entity.getPosition().z() - z()) <= z)
        .map(MinestomAdapters::adapt)
        .toList();
  }

  @Override
  public @NotNull java.util.Collection<? extends org.aincraft.api.domain.entity.Player>
      nearbyPlayers(double radius) {
    return nearbyPlayers(radius, radius, radius);
  }

  @Override
  public @NotNull java.util.Collection<? extends org.aincraft.api.domain.entity.Player>
      nearbyPlayers(double xRadius, double yRadius, double zRadius) {
    return nearbyEntities(xRadius, yRadius, zRadius).stream()
        .filter(org.aincraft.api.domain.entity.Player.class::isInstance)
        .map(org.aincraft.api.domain.entity.Player.class::cast)
        .toList();
  }

  @Override
  public @NotNull java.util.Collection<? extends org.aincraft.api.domain.entity.LivingEntity>
      nearbyLivingEntities(double xRadius, double yRadius, double zRadius) {
    return nearbyEntities(xRadius, yRadius, zRadius).stream()
        .filter(org.aincraft.api.domain.entity.LivingEntity.class::isInstance)
        .map(org.aincraft.api.domain.entity.LivingEntity.class::cast)
        .toList();
  }

  @Override
  public boolean isChunkLoaded() {
    return MinestomAdapters.toMinestom(world).isChunkLoaded(blockX() >> 4, blockZ() >> 4);
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
    org.aincraft.api.domain.world.Block highest =
        world.getHighestBlockAt(blockX(), blockZ(), heightMap);
    // Return location at highest block with original yaw/pitch
    return new MinestomLocationWrapper(
        world, new Pos(highest.x(), highest.y(), highest.z(), yaw(), pitch()));
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
        && Objects.equals(that.world(), world());
  }

  @Override
  public int hashCode() {
    return Objects.hash(world, x(), y(), z(), yaw(), pitch());
  }

  @Override
  public String toString() {
    return "MinestomLocationWrapper{world="
        + world
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
