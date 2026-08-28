package org.aincraft.api.domain.location;

import org.aincraft.api.domain.block.BlockFace;
import org.aincraft.api.domain.world.RayTraceResult;
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
    return of(
        minX() - negativeX,
        minY() - negativeY,
        minZ() - negativeZ,
        maxX() + positiveX,
        maxY() + positiveY,
        maxZ() + positiveZ);
  }

  default @NotNull BoundingBox expand(double x, double y, double z) {
    return expand(x, y, z, x, y, z);
  }

  default @NotNull BoundingBox expand(@NotNull BlockFace face, double amount) {
    double dx = face.modX() * amount;
    double dy = face.modY() * amount;
    double dz = face.modZ() * amount;
    return of(
        minX() + Math.min(dx, 0.0),
        minY() + Math.min(dy, 0.0),
        minZ() + Math.min(dz, 0.0),
        maxX() + Math.max(dx, 0.0),
        maxY() + Math.max(dy, 0.0),
        maxZ() + Math.max(dz, 0.0));
  }

  default @NotNull BoundingBox shift(double dx, double dy, double dz) {
    return of(minX() + dx, minY() + dy, minZ() + dz, maxX() + dx, maxY() + dy, maxZ() + dz);
  }

  default @NotNull BoundingBox shift(@NotNull Vector3dc offset) {
    return shift(offset.x(), offset.y(), offset.z());
  }

  default @NotNull BoundingBox union(@NotNull BoundingBox other) {
    return of(
        Math.min(minX(), other.minX()),
        Math.min(minY(), other.minY()),
        Math.min(minZ(), other.minZ()),
        Math.max(maxX(), other.maxX()),
        Math.max(maxY(), other.maxY()),
        Math.max(maxZ(), other.maxZ()));
  }

  default @Nullable BoundingBox intersection(@NotNull BoundingBox other) {
    double minX = Math.max(minX(), other.minX());
    double minY = Math.max(minY(), other.minY());
    double minZ = Math.max(minZ(), other.minZ());
    double maxX = Math.min(maxX(), other.maxX());
    double maxY = Math.min(maxY(), other.maxY());
    double maxZ = Math.min(maxZ(), other.maxZ());
    return minX <= maxX && minY <= maxY && minZ <= maxZ
        ? of(minX, minY, minZ, maxX, maxY, maxZ)
        : null;
  }

  default @Nullable RayTraceResult rayTrace(
      @NotNull Vector3dc origin, @NotNull Vector3dc direction, double maxDistance) {
    if (maxDistance < 0.0) return null;
    double near = 0.0;
    double far = maxDistance;
    double[] origins = {origin.x(), origin.y(), origin.z()};
    double[] directions = {direction.x(), direction.y(), direction.z()};
    double[] mins = {minX(), minY(), minZ()};
    double[] maxes = {maxX(), maxY(), maxZ()};
    for (int axis = 0; axis < 3; axis++) {
      double component = directions[axis];
      if (component == 0.0) {
        if (origins[axis] < mins[axis] || origins[axis] > maxes[axis]) return null;
        continue;
      }
      double first = (mins[axis] - origins[axis]) / component;
      double second = (maxes[axis] - origins[axis]) / component;
      if (first > second) {
        double swap = first;
        first = second;
        second = swap;
      }
      near = Math.max(near, first);
      far = Math.min(far, second);
      if (near > far) return null;
    }
    double hitX = origin.x() + direction.x() * near;
    double hitY = origin.y() + direction.y() * near;
    double hitZ = origin.z() + direction.z() * near;
    Position hit = position(hitX, hitY, hitZ);
    return new RayTraceResult() {
      @Override
      public @NotNull Position hitPosition() {
        return hit;
      }

      @Override
      public @Nullable org.aincraft.api.domain.world.Block hitBlock() {
        return null;
      }

      @Override
      public @Nullable BlockFace hitBlockFace() {
        return null;
      }

      @Override
      public @Nullable org.aincraft.api.domain.entity.Entity hitEntity() {
        return null;
      }
    };
  }

  private static @NotNull BoundingBox of(
      double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
    return new BoundingBox() {
      @Override
      public double minX() {
        return minX;
      }

      @Override
      public double minY() {
        return minY;
      }

      @Override
      public double minZ() {
        return minZ;
      }

      @Override
      public double maxX() {
        return maxX;
      }

      @Override
      public double maxY() {
        return maxY;
      }

      @Override
      public double maxZ() {
        return maxZ;
      }

      @Override
      public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof BoundingBox box)) return false;
        return Double.compare(minX(), box.minX()) == 0
            && Double.compare(minY(), box.minY()) == 0
            && Double.compare(minZ(), box.minZ()) == 0
            && Double.compare(maxX(), box.maxX()) == 0
            && Double.compare(maxY(), box.maxY()) == 0
            && Double.compare(maxZ(), box.maxZ()) == 0;
      }

      @Override
      public int hashCode() {
        return java.util.Objects.hash(minX(), minY(), minZ(), maxX(), maxY(), maxZ());
      }

      @Override
      public String toString() {
        return "BoundingBox{"
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
    };
  }

  private static @NotNull Position position(double x, double y, double z) {
    return new Position() {
      @Override
      public double x() {
        return x;
      }

      @Override
      public double y() {
        return y;
      }

      @Override
      public double z() {
        return z;
      }

      @Override
      public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Position position)) return false;
        return Double.compare(x(), position.x()) == 0
            && Double.compare(y(), position.y()) == 0
            && Double.compare(z(), position.z()) == 0;
      }

      @Override
      public int hashCode() {
        return java.util.Objects.hash(x(), y(), z());
      }

      @Override
      public String toString() {
        return "Position{" + x() + ", " + y() + ", " + z() + "}";
      }
    };
  }
}
