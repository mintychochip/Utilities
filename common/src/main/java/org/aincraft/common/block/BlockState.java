package org.aincraft.common.block;

import org.jetbrains.annotations.NotNull;

public interface BlockState {

  @NotNull BlockType type();

  @NotNull String asString();
}
