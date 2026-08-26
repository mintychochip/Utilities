package org.aincraft.common.block;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

record BlockTypeImpl(@NotNull Key key) implements BlockType {

  BlockTypeImpl {
    if (key == null) {
      throw new NullPointerException("Key cannot be null");
    }
  }

  @Override
  public String toString() {
    return key.asString();
  }
}
