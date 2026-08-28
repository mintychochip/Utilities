package org.aincraft.api.domain.world;

import org.aincraft.api.domain.block.BlockFace;
import org.aincraft.api.domain.entity.Entity;
import org.aincraft.api.domain.location.Position;
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
