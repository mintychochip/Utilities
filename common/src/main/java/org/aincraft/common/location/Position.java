package org.aincraft.common.location;

import org.jetbrains.annotations.NotNull;

public interface Position {

  double x();

  double y();

  double z();

  default int blockX() {
    return (int) Math.floor(x());
  }

  default int blockY() {
    return (int) Math.floor(y());
  }

  default int blockZ() {
    return (int) Math.floor(z());
  }

  default double distanceSquared(@NotNull Position other) {
    double dx = x() - other.x();
    double dy = y() - other.y();
    double dz = z() - other.z();
    return dx * dx + dy * dy + dz * dz;
  }

  default double distance(@NotNull Position other) {
    return Math.sqrt(distanceSquared(other));
  }

  default double distanceSquared(double ox, double oy, double oz) {
    double dx = x() - ox;
    double dy = y() - oy;
    double dz = z() - oz;
    return dx * dx + dy * dy + dz * dz;
  }

  default double distance(double ox, double oy, double oz) {
    return Math.sqrt(distanceSquared(ox, oy, oz));
  }
}
