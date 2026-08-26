package org.aincraft.minestom.adapter;

import java.util.Objects;
import org.aincraft.common.location.BoundingBox;
import org.jetbrains.annotations.NotNull;

public class MinestomBoundingBoxWrapper implements BoundingBox {

  private final net.minestom.server.collision.BoundingBox boundingBox;

  public MinestomBoundingBoxWrapper(@NotNull net.minestom.server.collision.BoundingBox boundingBox) {
    this.boundingBox = Objects.requireNonNull(boundingBox, "boundingBox cannot be null");
  }

  public @NotNull net.minestom.server.collision.BoundingBox getMinestomBoundingBox() {
    return boundingBox;
  }

  @Override
  public double minX() {
    return boundingBox.minX();
  }

  @Override
  public double minY() {
    return boundingBox.minY();
  }

  @Override
  public double minZ() {
    return boundingBox.minZ();
  }

  @Override
  public double maxX() {
    return boundingBox.maxX();
  }

  @Override
  public double maxY() {
    return boundingBox.maxY();
  }

  @Override
  public double maxZ() {
    return boundingBox.maxZ();
  }

  @Override
  public boolean contains(double x, double y, double z) {
    return x >= minX() && x <= maxX()
        && y >= minY() && y <= maxY()
        && z >= minZ() && z <= maxZ();
  }

  @Override
  public boolean intersects(@NotNull BoundingBox other) {
    return minX() <= other.maxX() && maxX() >= other.minX()
        && minY() <= other.maxY() && maxY() >= other.minY()
        && minZ() <= other.maxZ() && maxZ() >= other.minZ();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BoundingBox that)) return false;
    return Double.compare(that.minX(), minX()) == 0
        && Double.compare(that.minY(), minY()) == 0
        && Double.compare(that.minZ(), minZ()) == 0
        && Double.compare(that.maxX(), maxX()) == 0
        && Double.compare(that.maxY(), maxY()) == 0
        && Double.compare(that.maxZ(), maxZ()) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(minX(), minY(), minZ(), maxX(), maxY(), maxZ());
  }

  @Override
  public String toString() {
    return "MinestomBoundingBoxWrapper{" + minX() + "," + minY() + "," + minZ() + " -> " + maxX() + "," + maxY() + "," + maxZ() + "}";
  }
}
