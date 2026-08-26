package org.aincraft.common.world;

import org.aincraft.common.block.BlockState;
import org.aincraft.common.block.BlockType;
import org.aincraft.common.location.Position;
import org.jetbrains.annotations.NotNull;

public interface Block {

  @NotNull World world();

  @NotNull Position position();

  default int x() {
    return position().blockX();
  }

  default int y() {
    return position().blockY();
  }

  default int z() {
    return position().blockZ();
  }

  @NotNull BlockType type();

  @NotNull BlockState state();
}
