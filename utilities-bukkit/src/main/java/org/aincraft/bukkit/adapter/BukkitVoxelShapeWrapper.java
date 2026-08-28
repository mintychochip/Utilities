package org.aincraft.bukkit.adapter;

import org.aincraft.api.domain.block.VoxelShape;
import org.aincraft.api.domain.location.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public final class BukkitVoxelShapeWrapper implements VoxelShape {

  private final org.bukkit.util.VoxelShape shape;

  public BukkitVoxelShapeWrapper(@NotNull org.bukkit.util.VoxelShape shape) {
    this.shape = Objects.requireNonNull(shape, "shape cannot be null");
  }

  public @NotNull org.bukkit.util.VoxelShape getBukkitVoxelShape() {
    return shape;
  }

  private @NotNull List<BoundingBox> boxes() {
    return shape.getBoundingBoxes().stream().map(BukkitAdapters::adapt).toList();
  }

  @Override
  public @NotNull BoundingBox boundingBox() {
    List<BoundingBox> boxes = boxes();
    if (boxes.isEmpty()) {
      return new BoundingBox() {
        @Override
        public double minX() {
          return 0;
        }

        @Override
        public double minY() {
          return 0;
        }

        @Override
        public double minZ() {
          return 0;
        }

        @Override
        public double maxX() {
          return 0;
        }

        @Override
        public double maxY() {
          return 0;
        }

        @Override
        public double maxZ() {
          return 0;
        }
      };
    }
    BoundingBox result = boxes.getFirst();
    for (int i = 1; i < boxes.size(); i++) result = result.union(boxes.get(i));
    return result;
  }

  @Override
  public boolean isEmpty() {
    return shape.getBoundingBoxes().isEmpty();
  }

  @Override
  public boolean contains(double x, double y, double z) {
    for (BoundingBox box : boxes()) if (box.contains(x, y, z)) return true;
    return false;
  }
}
