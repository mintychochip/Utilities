package org.aincraft.common.block;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

public interface BlockType extends Keyed {

  static @NotNull BlockType of(@NotNull Key key) {
    return new BlockTypeImpl(key);
  }
}
