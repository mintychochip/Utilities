package org.aincraft.common.world;

import org.jetbrains.annotations.NotNull;

public interface Chunk {

  int x();

  int z();

  @NotNull World world();

  @NotNull Block getBlock(int x, int y, int z);

  boolean isLoaded();
}
