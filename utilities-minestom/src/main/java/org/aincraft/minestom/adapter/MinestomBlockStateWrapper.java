package org.aincraft.minestom.adapter;

import org.aincraft.common.block.BlockState;
import org.aincraft.common.block.BlockType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MinestomBlockStateWrapper implements BlockState {

  private final BlockType type;
  private final String stateString;

  public MinestomBlockStateWrapper(@NotNull net.minestom.server.instance.block.Block block) {
    Objects.requireNonNull(block, "block cannot be null");
    this.type = new MinestomBlockTypeWrapper(block);
    this.stateString = block.state() != null ? block.state() : block.name();
  }

  @Override
  public @NotNull BlockType type() {
    return type;
  }

  @Override
  public @NotNull String asString() {
    return stateString;
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
