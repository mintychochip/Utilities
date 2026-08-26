package org.aincraft.common.location;

import org.jetbrains.annotations.NotNull;

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
    return x >= minX() && x <= maxX()
        && y >= minY() && y <= maxY()
        && z >= minZ() && z <= maxZ();
  }

  default boolean contains(@NotNull Position position) {
    return contains(position.x(), position.y(), position.z());
  }

  default boolean intersects(@NotNull BoundingBox other) {
    return minX() <= other.maxX() && maxX() >= other.minX()
        && minY() <= other.maxY() && maxY() >= other.minY()
        && minZ() <= other.maxZ() && maxZ() >= other.minZ();
  }
}
