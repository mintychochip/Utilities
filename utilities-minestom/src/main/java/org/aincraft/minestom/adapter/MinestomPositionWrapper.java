package org.aincraft.minestom.adapter;

import java.util.Objects;
import net.minestom.server.coordinate.Point;
import org.aincraft.common.location.Position;
import org.jetbrains.annotations.NotNull;

public class MinestomPositionWrapper implements Position {

  private final Point point;

  public MinestomPositionWrapper(@NotNull Point point) {
    this.point = Objects.requireNonNull(point, "point cannot be null");
  }

  public @NotNull Point getMinestomPoint() {
    return point;
  }

  @Override
  public double x() {
    return point.x();
  }

  @Override
  public double y() {
    return point.y();
  }

  @Override
  public double z() {
    return point.z();
  }

  @Override
  public int blockX() {
    return point.blockX();
  }

  @Override
  public int blockY() {
    return point.blockY();
  }

  @Override
  public int blockZ() {
    return point.blockZ();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Position that)) return false;
    return Double.compare(that.x(), x()) == 0
        && Double.compare(that.y(), y()) == 0
        && Double.compare(that.z(), z()) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x(), y(), z());
  }

  @Override
  public String toString() {
    return "MinestomPositionWrapper{x=" + x() + ", y=" + y() + ", z=" + z() + "}";
  }
}
