package org.aincraft.common.location;

import org.aincraft.common.block.BlockFace;
import org.aincraft.common.world.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3dc;

public interface BoundingBox {

  double minX();

  double minY();

  double minZ();

  double maxX();

  double maxY();

  double maxZ();

  default double widthX() {
    return maxX() - minX();
  }

  default double heightY() {
    return maxY() - minY();
  }

  default double depthZ() {
    return maxZ() - minZ();
  }

  default double volume() {
    return widthX() * heightY() * depthZ();
  }

  default double centerX() {
    return minX() + widthX() * 0.5;
  }

  default double centerY() {
    return minY() + heightY() * 0.5;
  }

  default double centerZ() {
    return minZ() + depthZ() * 0.5;
  }

  default boolean contains(double x, double y, double z) {
    return x >= minX() && x <= maxX() && y >= minY() && y <= maxY() && z >= minZ() && z <= maxZ();
  }

  default boolean contains(@NotNull Position position) {
    return contains(position.x(), position.y(), position.z());
  }

  default boolean contains(@NotNull BoundingBox other) {
    return other.minX() >= minX()
        && other.maxX() <= maxX()
        && other.minY() >= minY()
        && other.maxY() <= maxY()
        && other.minZ() >= minZ()
        && other.maxZ() <= maxZ();
  }

  default boolean intersects(@NotNull BoundingBox other) {
    return minX() <= other.maxX()
        && maxX() >= other.minX()
        && minY() <= other.maxY()
        && maxY() >= other.minY()
        && minZ() <= other.maxZ()
        && maxZ() >= other.minZ();
  }

  default @NotNull BoundingBox expand(
      double negativeX,
      double negativeY,
      double negativeZ,
      double positiveX,
      double positiveY,
      double positiveZ) {
    throw new UnsupportedOperationException("expand");
  }

  default @NotNull BoundingBox expand(double x, double y, double z) {
    return expand(x, y, z, x, y, z);
  }

  default @NotNull BoundingBox expand(@NotNull BlockFace face, double amount) {
    throw new UnsupportedOperationException("expand by BlockFace");
  }

  default @NotNull BoundingBox shift(double dx, double dy, double dz) {
    throw new UnsupportedOperationException("shift");
  }

  default @NotNull BoundingBox shift(@NotNull Vector3dc offset) {
    return shift(offset.x(), offset.y(), offset.z());
  }

  default @NotNull BoundingBox union(@NotNull BoundingBox other) {
    throw new UnsupportedOperationException("union");
  }

  default @Nullable BoundingBox intersection(@NotNull BoundingBox other) {
    throw new UnsupportedOperationException("intersection");
  }

  default @Nullable RayTraceResult rayTrace(
      @NotNull Vector3dc origin, @NotNull Vector3dc direction, double maxDistance) {
    throw new UnsupportedOperationException("rayTrace");
  }
}
