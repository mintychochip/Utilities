package org.aincraft.bukkit.adapter;

import org.aincraft.common.block.BlockState;
import org.aincraft.common.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BukkitBlockStateWrapper implements BlockState {

  private final BlockData blockData;
  private final BlockType type;

  public BukkitBlockStateWrapper(@NotNull BlockData blockData) {
    this.blockData = Objects.requireNonNull(blockData, "blockData cannot be null");
    this.type = new BukkitBlockTypeWrapper(blockData.getMaterial());
  }

  public @NotNull BlockData getBukkitBlockData() {
    return blockData;
  }

  @Override
  public @NotNull BlockType type() {
    return type;
  }

  @Override
  public @NotNull String asString() {
    return blockData.getAsString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BlockState that)) return false;
    return Objects.equals(asString(), that.asString());
  }

  @Override
  public int hashCode() {
    return blockData.hashCode();
  }

  @Override
  public String toString() {
    return asString();
  }
}
