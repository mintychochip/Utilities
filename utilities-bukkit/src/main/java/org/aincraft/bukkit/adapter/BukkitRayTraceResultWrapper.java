package org.aincraft.bukkit.adapter;

import java.util.Objects;
import org.aincraft.common.block.BlockFace;
import org.aincraft.common.entity.Entity;
import org.aincraft.common.location.Position;
import org.aincraft.common.world.Block;
import org.aincraft.common.world.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitRayTraceResultWrapper implements RayTraceResult {

  private final org.bukkit.util.RayTraceResult result;

  public BukkitRayTraceResultWrapper(@NotNull org.bukkit.util.RayTraceResult result) {
    this.result = Objects.requireNonNull(result, "result cannot be null");
  }

  public @NotNull org.bukkit.util.RayTraceResult getBukkitRayTraceResult() {
    return result;
  }

  @Override
  public @NotNull Position hitPosition() {
    return BukkitAdapters.adapt(result.getHitPosition());
  }

  @Override
  public @Nullable Block hitBlock() {
    org.bukkit.block.Block block = result.getHitBlock();
    return block != null ? BukkitAdapters.adapt(block) : null;
  }

  @Override
  public @Nullable BlockFace hitBlockFace() {
    org.bukkit.block.BlockFace face = result.getHitBlockFace();
    return face != null ? BukkitAdapters.adapt(face) : null;
  }

  @Override
  public @Nullable Entity hitEntity() {
    org.bukkit.entity.Entity entity = result.getHitEntity();
    return entity != null ? BukkitAdapters.adapt(entity) : null;
  }

  @Override
  public String toString() {
    return "BukkitRayTraceResultWrapper{" + result + "}";
  }
}
