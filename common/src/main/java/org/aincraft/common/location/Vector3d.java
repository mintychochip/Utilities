package org.aincraft.common.location;

import org.jetbrains.annotations.NotNull;

public record Vector3d(double x, double y, double z) {

  public static final Vector3d ZERO = new Vector3d(0, 0, 0);

  public @NotNull Vector3d add(@NotNull Vector3d other) {
    return new Vector3d(this.x + other.x, this.y + other.y, this.z + other.z);
  }

  public @NotNull Vector3d add(double dx, double dy, double dz) {
    return new Vector3d(this.x + dx, this.y + dy, this.z + dz);
  }

  public @NotNull Vector3d subtract(@NotNull Vector3d other) {
    return new Vector3d(this.x - other.x, this.y - other.y, this.z - other.z);
  }

  public @NotNull Vector3d multiply(double factor) {
    return new Vector3d(this.x * factor, this.y * factor, this.z * factor);
  }

  public double lengthSquared() {
    return this.x * this.x + this.y * this.y + this.z * this.z;
  }

  public double length() {
    return Math.sqrt(lengthSquared());
  }

  public double distanceSquared(@NotNull Vector3d other) {
    double dx = this.x - other.x;
    double dy = this.y - other.y;
    double dz = this.z - other.z;
    return dx * dx + dy * dy + dz * dz;
  }

  public double distance(@NotNull Vector3d other) {
    return Math.sqrt(distanceSquared(other));
  }
}
