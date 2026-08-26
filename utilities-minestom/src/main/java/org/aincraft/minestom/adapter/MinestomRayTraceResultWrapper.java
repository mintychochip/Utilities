package org.aincraft.minestom.adapter;

import java.util.Objects;
import net.minestom.server.coordinate.Point;
import org.aincraft.common.block.BlockFace;
import org.aincraft.common.entity.Entity;
import org.aincraft.common.location.Position;
import org.aincraft.common.world.Block;
import org.aincraft.common.world.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MinestomRayTraceResultWrapper implements RayTraceResult {

  private final Position hitPosition;

  public MinestomRayTraceResultWrapper(@NotNull Point hitPosition) {
    this(MinestomAdapters.adapt(hitPosition));
  }

  public MinestomRayTraceResultWrapper(@NotNull Position hitPosition) {
    this.hitPosition = Objects.requireNonNull(hitPosition, "hitPosition cannot be null");
  }

  @Override
  public @NotNull Position hitPosition() {
    return hitPosition;
  }

  @Override
  public @Nullable Block hitBlock() {
    return null;
  }

  @Override
  public @Nullable BlockFace hitBlockFace() {
    return null;
  }

  @Override
  public @Nullable Entity hitEntity() {
    return null;
  }

  @Override
  public String toString() {
    return "MinestomRayTraceResultWrapper{" + hitPosition + "}";
  }
}
