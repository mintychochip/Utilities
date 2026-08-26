package org.aincraft.common.location;

import org.jetbrains.annotations.NotNull;

public interface Vector3i {

  int x();

  int y();

  int z();

  default double lengthSquared() {
    double x = x();
    double y = y();
    double z = z();
    return x * x + y * y + z * z;
  }

  default double length() {
    return Math.sqrt(lengthSquared());
  }

  default double distanceSquared(@NotNull Vector3i other) {
    double dx = x() - other.x();
    double dy = y() - other.y();
    double dz = z() - other.z();
    return dx * dx + dy * dy + dz * dz;
  }

  default double distance(@NotNull Vector3i other) {
    return Math.sqrt(distanceSquared(other));
  }
}
