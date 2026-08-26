package org.aincraft.common.location;

import org.jetbrains.annotations.NotNull;

public record Vector3i(int x, int y, int z) {

  public static final Vector3i ZERO = new Vector3i(0, 0, 0);

  public @NotNull Vector3i add(@NotNull Vector3i other) {
    return new Vector3i(this.x + other.x, this.y + other.y, this.z + other.z);
  }

  public @NotNull Vector3i add(int dx, int dy, int dz) {
    return new Vector3i(this.x + dx, this.y + dy, this.z + dz);
  }

  public @NotNull Vector3i subtract(@NotNull Vector3i other) {
    return new Vector3i(this.x - other.x, this.y - other.y, this.z - other.z);
  }

  public @NotNull Vector3i multiply(int factor) {
    return new Vector3i(this.x * factor, this.y * factor, this.z * factor);
  }

  public double distanceSquared(@NotNull Vector3i other) {
    double dx = this.x - other.x;
    double dy = this.y - other.y;
    double dz = this.z - other.z;
    return dx * dx + dy * dy + dz * dz;
  }

  public double distance(@NotNull Vector3i other) {
    return Math.sqrt(distanceSquared(other));
  }
}
