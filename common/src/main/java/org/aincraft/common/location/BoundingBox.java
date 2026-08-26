package org.aincraft.common.location;

import org.jetbrains.annotations.NotNull;

public record BoundingBox(
    double minX,
    double minY,
    double minZ,
    double maxX,
    double maxY,
    double maxZ
) {

  public BoundingBox {
    if (minX > maxX || minY > maxY || minZ > maxZ) {
      throw new IllegalArgumentException(
          "Minimum coordinates cannot exceed maximum coordinates: min("
              + minX + ", " + minY + ", " + minZ + ") vs max("
              + maxX + ", " + maxY + ", " + maxZ + ")");
    }
  }

  public static @NotNull BoundingBox of(@NotNull Position p1, @NotNull Position p2) {
    return new BoundingBox(
        Math.min(p1.x(), p2.x()),
        Math.min(p1.y(), p2.y()),
        Math.min(p1.z(), p2.z()),
        Math.max(p1.x(), p2.x()),
        Math.max(p1.y(), p2.y()),
        Math.max(p1.z(), p2.z())
    );
  }

  public static @NotNull BoundingBox of(
      double x1, double y1, double z1,
      double x2, double y2, double z2
  ) {
    return new BoundingBox(
        Math.min(x1, x2),
        Math.min(y1, y2),
        Math.min(z1, z2),
        Math.max(x1, x2),
        Math.max(y1, y2),
        Math.max(z1, z2)
    );
  }

  public boolean contains(double x, double y, double z) {
    return x >= minX && x <= maxX
        && y >= minY && y <= maxY
        && z >= minZ && z <= maxZ;
  }

  public boolean contains(@NotNull Position position) {
    return contains(position.x(), position.y(), position.z());
  }

  public boolean intersects(@NotNull BoundingBox other) {
    return minX <= other.maxX && maxX >= other.minX
        && minY <= other.maxY && maxY >= other.minY
        && minZ <= other.maxZ && maxZ >= other.minZ;
  }

  public double widthX() {
    return maxX - minX;
  }

  public double heightY() {
    return maxY - minY;
  }

  public double depthZ() {
    return maxZ - minZ;
  }

  public double volume() {
    return widthX() * heightY() * depthZ();
  }
}
