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

  default @NotNull Position add(double dx, double dy, double dz) {
    return Position.of(x() + dx, y() + dy, z() + dz);
  }

  default @NotNull Position add(@NotNull Position other) {
    return add(other.x(), other.y(), other.z());
  }

  default @NotNull Position subtract(double dx, double dy, double dz) {
    return Position.of(x() - dx, y() - dy, z() - dz);
  }

  default @NotNull Position subtract(@NotNull Position other) {
    return subtract(other.x(), other.y(), other.z());
  }

  default @NotNull Position multiply(double factor) {
    return Position.of(x() * factor, y() * factor, z() * factor);
  }

  default @NotNull Vector3d toVector() {
    return new Vector3d(x(), y(), z());
  }

  default @NotNull Vector3i toBlockVector() {
    return new Vector3i(blockX(), blockY(), blockZ());
  }

  static @NotNull Position of(double x, double y, double z) {
    return new PositionImpl(x, y, z);
  }
}
