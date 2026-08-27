package org.aincraft.common.world;

import org.aincraft.common.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface Chunk {

  int x();

  int z();

  @NotNull
  World world();

  @NotNull
  Block getBlock(int x, int y, int z);

  boolean isLoaded();

  boolean load();

  boolean load(boolean generate);

  boolean unload();

  boolean unload(boolean save);

  default long chunkKey() {
    return (((long) x()) << 32) | (z() & 0xFFFFFFFFL);
  }

  @NotNull
  Collection<? extends Entity> entities();
}
