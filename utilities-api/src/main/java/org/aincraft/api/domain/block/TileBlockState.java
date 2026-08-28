package org.aincraft.api.domain.block;

import org.aincraft.api.domain.world.Block;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the state of a placed tile entity block (chest, sign, spawner, etc.), distinct from
 * the property-only {@link BlockState} (Bukkit {@code BlockData}).
 */
public interface TileBlockState {

  @NotNull
  Block block();

  @NotNull
  BlockState blockData();

  boolean update();

  boolean update(boolean force);

  boolean update(boolean force, boolean applyPhysics);

  @NotNull
  TileBlockState copy();

  boolean isPlaced();
}
