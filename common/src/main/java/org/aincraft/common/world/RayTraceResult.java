package org.aincraft.common.world;

import org.aincraft.common.block.BlockFace;
import org.aincraft.common.entity.Entity;
import org.aincraft.common.location.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RayTraceResult {

  @NotNull
  Position hitPosition();

  @Nullable
  Block hitBlock();

  @Nullable
  BlockFace hitBlockFace();

  @Nullable
  Entity hitEntity();
}
