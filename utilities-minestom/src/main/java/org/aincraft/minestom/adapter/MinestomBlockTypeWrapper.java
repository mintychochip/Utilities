package org.aincraft.minestom.adapter;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.aincraft.common.block.BlockType;
import org.jetbrains.annotations.NotNull;

public class MinestomBlockTypeWrapper implements BlockType {

  private final net.minestom.server.instance.block.Block block;
  private final Key key;

  public MinestomBlockTypeWrapper(@NotNull net.minestom.server.instance.block.Block block) {
    this.block = Objects.requireNonNull(block, "block cannot be null");
    this.key = block.key();
  }

  public @NotNull net.minestom.server.instance.block.Block getMinestomBlock() {
    return block;
  }

  @Override
  public @NotNull Key key() {
    return key;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BlockType that)) return false;
    return Objects.equals(key(), that.key());
  }

  @Override
  public int hashCode() {
    return key().hashCode();
  }

  @Override
  public String toString() {
    return "MinestomBlockTypeWrapper{key=" + key() + "}";
  }
}
