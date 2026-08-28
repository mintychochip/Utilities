package org.aincraft.bukkit.adapter;

import org.aincraft.api.domain.location.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BukkitBoundingBoxWrapper implements BoundingBox {

  private final org.bukkit.util.BoundingBox boundingBox;

  public BukkitBoundingBoxWrapper(@NotNull org.bukkit.util.BoundingBox boundingBox) {
    this.boundingBox = Objects.requireNonNull(boundingBox, "boundingBox cannot be null");
  }

  public @NotNull org.bukkit.util.BoundingBox getBukkitBoundingBox() {
    return boundingBox;
  }

  @Override
  public double minX() {
    return boundingBox.getMinX();
  }

  @Override
  public double minY() {
    return boundingBox.getMinY();
  }

  @Override
  public double minZ() {
    return boundingBox.getMinZ();
  }

  @Override
  public double maxX() {
    return boundingBox.getMaxX();
  }

  @Override
  public double maxY() {
    return boundingBox.getMaxY();
  }

  @Override
  public double maxZ() {
    return boundingBox.getMaxZ();
  }

  @Override
  public boolean contains(double x, double y, double z) {
    return boundingBox.contains(x, y, z);
  }

  @Override
  public boolean intersects(@NotNull BoundingBox other) {
    return boundingBox.overlaps(toBukkit(other));
  }

  @Override
  public @NotNull BoundingBox expand(
      double negativeX,
      double negativeY,
      double negativeZ,
      double positiveX,
      double positiveY,
      double positiveZ) {
    org.bukkit.util.BoundingBox expanded = boundingBox.clone();
    expanded.expand(negativeX, negativeY, negativeZ, positiveX, positiveY, positiveZ);
    return new BukkitBoundingBoxWrapper(expanded);
  }

  @Override
  public @NotNull BoundingBox expand(
      @NotNull org.aincraft.api.domain.block.BlockFace face, double amount) {
    return new BukkitBoundingBoxWrapper(
        boundingBox.clone().expand(BukkitAdapters.toBukkit(face), amount));
  }

  @Override
  public @NotNull BoundingBox shift(double dx, double dy, double dz) {
    return new BukkitBoundingBoxWrapper(boundingBox.clone().shift(dx, dy, dz));
  }

  @Override
  public @NotNull BoundingBox union(@NotNull BoundingBox other) {
    return new BukkitBoundingBoxWrapper(boundingBox.clone().union(toBukkit(other)));
  }

  @Override
  public @org.jetbrains.annotations.Nullable BoundingBox intersection(@NotNull BoundingBox other) {
    if (!intersects(other)) return null;
    return new BukkitBoundingBoxWrapper(boundingBox.clone().intersection(toBukkit(other)));
  }

  @Override
  public @org.jetbrains.annotations.Nullable org.aincraft.api.domain.world.RayTraceResult rayTrace(
      @NotNull org.joml.Vector3dc origin,
      @NotNull org.joml.Vector3dc direction,
      double maxDistance) {
    org.bukkit.util.RayTraceResult result =
        boundingBox.rayTrace(
            new org.bukkit.util.Vector(origin.x(), origin.y(), origin.z()),
            new org.bukkit.util.Vector(direction.x(), direction.y(), direction.z()),
            maxDistance);
    return result == null ? null : BukkitAdapters.adapt(result);
  }

  private static org.bukkit.util.BoundingBox toBukkit(@NotNull BoundingBox box) {
    return box instanceof BukkitBoundingBoxWrapper wrapper
        ? wrapper.getBukkitBoundingBox()
        : new org.bukkit.util.BoundingBox(
            box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BoundingBox that)) return false;
    return Double.compare(that.minX(), minX()) == 0
        && Double.compare(that.minY(), minY()) == 0
        && Double.compare(that.minZ(), minZ()) == 0
        && Double.compare(that.maxX(), maxX()) == 0
        && Double.compare(that.maxY(), maxY()) == 0
        && Double.compare(that.maxZ(), maxZ()) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(minX(), minY(), minZ(), maxX(), maxY(), maxZ());
  }

  @Override
  public String toString() {
    return "BukkitBoundingBoxWrapper{"
        + minX()
        + ","
        + minY()
        + ","
        + minZ()
        + " -> "
        + maxX()
        + ","
        + maxY()
        + ","
        + maxZ()
        + "}";
  }
}
