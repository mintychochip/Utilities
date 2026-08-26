package org.aincraft.minestom.adapter;

import java.util.Objects;
import org.aincraft.common.block.BlockState;
import org.aincraft.common.block.BlockType;
import org.jetbrains.annotations.NotNull;

public class MinestomBlockStateWrapper implements BlockState {

  private final net.minestom.server.instance.block.Block block;
  private final BlockType type;

  public MinestomBlockStateWrapper(@NotNull net.minestom.server.instance.block.Block block) {
    this.block = Objects.requireNonNull(block, "block cannot be null");
    this.type = new MinestomBlockTypeWrapper(block);
  }

  public @NotNull net.minestom.server.instance.block.Block getMinestomBlock() {
    return block;
  }

  @Override
  public @NotNull BlockType type() {
    return type;
  }

  @Override
  public @NotNull String asString() {
    return block.state() != null ? block.state() : block.name();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BlockState that)) return false;
    return Objects.equals(type(), that.type()) && Objects.equals(asString(), that.asString());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), asString());
  }

  @Override
  public String toString() {
    return "MinestomBlockStateWrapper{" + asString() + "}";
  }
}
