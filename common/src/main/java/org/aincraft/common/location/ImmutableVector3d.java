package org.aincraft.common.location;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public final class ImmutableVector3d implements Vector3d {

  private final double x;
  private final double y;
  private final double z;

  public ImmutableVector3d(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Vector3d that)) return false;
    return Double.compare(that.x(), x) == 0
        && Double.compare(that.y(), y) == 0
        && Double.compare(that.z(), z) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y, z);
  }

  @Override
  public String toString() {
    return "ImmutableVector3d{x=" + x + ", y=" + y + ", z=" + z + "}";
  }
}
