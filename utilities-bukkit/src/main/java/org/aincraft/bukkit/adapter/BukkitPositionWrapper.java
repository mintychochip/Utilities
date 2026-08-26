package org.aincraft.bukkit.adapter;

import java.util.Objects;
import org.aincraft.common.location.Position;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class BukkitPositionWrapper implements Position {

  private final Vector vector;

  public BukkitPositionWrapper(@NotNull Vector vector) {
    this.vector = Objects.requireNonNull(vector, "vector cannot be null");
  }

  public @NotNull Vector getBukkitVector() {
    return vector;
  }

  @Override
  public double x() {
    return vector.getX();
  }

  @Override
  public double y() {
    return vector.getY();
  }

  @Override
  public double z() {
    return vector.getZ();
  }

  @Override
  public int blockX() {
    return vector.getBlockX();
  }

  @Override
  public int blockY() {
    return vector.getBlockY();
  }

  @Override
  public int blockZ() {
    return vector.getBlockZ();
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
    return "BukkitPositionWrapper{x=" + x() + ", y=" + y() + ", z=" + z() + "}";
  }
}
