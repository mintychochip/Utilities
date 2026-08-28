package org.aincraft.minestom.adapter;

import net.minestom.server.collision.Shape;
import org.aincraft.api.domain.block.VoxelShape;
import org.aincraft.api.domain.location.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class MinestomVoxelShapeWrapper implements VoxelShape {

  private final Shape shape;
  private final BoundingBox bounds;

  public MinestomVoxelShapeWrapper(@NotNull Shape shape) {
    this.shape = Objects.requireNonNull(shape, "shape cannot be null");
    net.minestom.server.coordinate.Point start = shape.relativeStart();
    net.minestom.server.coordinate.Point end = shape.relativeEnd();
    this.bounds =
        MinestomAdapters.adapt(
            new net.minestom.server.collision.BoundingBox(
                end.x() - start.x(),
                end.y() - start.y(),
                end.z() - start.z(),
                new net.minestom.server.coordinate.Vec(start.x(), start.y(), start.z())));
  }

  public @NotNull Shape getMinestomShape() {
    return shape;
  }

  @Override
  public @NotNull BoundingBox boundingBox() {
    return bounds;
  }

  @Override
  public boolean isEmpty() {
    return bounds.widthX() <= 0.0 || bounds.heightY() <= 0.0 || bounds.depthZ() <= 0.0;
  }

  @Override
  public boolean contains(double x, double y, double z) {
    return bounds.contains(x, y, z);
  }
}
