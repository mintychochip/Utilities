package org.aincraft.minestom.adapter;

import net.minestom.server.coordinate.Point;
import org.aincraft.api.domain.block.BlockFace;
import org.aincraft.api.domain.entity.Entity;
import org.aincraft.api.domain.location.Position;
import org.aincraft.api.domain.world.Block;
import org.aincraft.api.domain.world.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class MinestomRayTraceResultWrapper implements RayTraceResult {

  private final Position hitPosition;
  private final Block hitBlock;
  private final BlockFace hitBlockFace;
  private final Entity hitEntity;

  public MinestomRayTraceResultWrapper(@NotNull Point hitPosition) {
    this(MinestomAdapters.adapt(hitPosition), null, null, null);
  }

  public MinestomRayTraceResultWrapper(@NotNull Position hitPosition) {
    this(hitPosition, null, null, null);
  }

  public MinestomRayTraceResultWrapper(
      @NotNull Position hitPosition,
      @Nullable Block hitBlock,
      @Nullable BlockFace hitBlockFace,
      @Nullable Entity hitEntity) {
    this.hitPosition = Objects.requireNonNull(hitPosition, "hitPosition cannot be null");
    this.hitBlock = hitBlock;
    this.hitBlockFace = hitBlockFace;
    this.hitEntity = hitEntity;
  }

  @Override
  public @NotNull Position hitPosition() {
    return hitPosition;
  }

  @Override
  public @Nullable Block hitBlock() {
    return hitBlock;
  }

  @Override
  public @Nullable BlockFace hitBlockFace() {
    return hitBlockFace;
  }

  @Override
  public @Nullable Entity hitEntity() {
    return hitEntity;
  }

  @Override
  public String toString() {
    return "MinestomRayTraceResultWrapper{" + hitPosition + "}";
  }
}
