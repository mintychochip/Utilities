package org.aincraft.bukkit.adapter;

import java.util.Objects;
import org.aincraft.common.location.BoundingBox;
import org.jetbrains.annotations.NotNull;

public class BukkitBoundingBoxWrapper implements BoundingBox {

  private final org.bukkit.util.BoundingBox boundingBox;

  public BukkitBoundingBoxWrapper(@NotNull org.bukkit.util.BoundingBox boundingBox) {
    this.boundingBox = Objects.requireNonNull(boundingBox, "boundingBox cannot be null");
  }

  public @NotNull org.bukkit.util.BoundingBox getBukkitBoundingBox() {
    return boundingBox;
  }

  @Override
  public double minX() {
    return boundingBox.getMinX();
  }

  @Override
  public double minY() {
    return boundingBox.getMinY();
  }

  @Override
  public double minZ() {
    return boundingBox.getMinZ();
  }

  @Override
  public double maxX() {
    return boundingBox.getMaxX();
  }

  @Override
  public double maxY() {
    return boundingBox.getMaxY();
  }

  @Override
  public double maxZ() {
    return boundingBox.getMaxZ();
  }

  @Override
  public boolean contains(double x, double y, double z) {
    return boundingBox.contains(x, y, z);
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
    return "BukkitBoundingBoxWrapper{" + minX() + "," + minY() + "," + minZ() + " -> " + maxX() + "," + maxY() + "," + maxZ() + "}";
  }
}
