package org.aincraft.minestom.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.block.BlockType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class MinestomBlockTypeWrapper implements BlockType {

  private final net.minestom.server.instance.block.Block block;
  private final Key key;

  public MinestomBlockTypeWrapper(@NotNull net.minestom.server.instance.block.Block block) {
    this.block = Objects.requireNonNull(block, "block cannot be null");
    this.key = block.key();
  }

  @Override
  public @NotNull Key key() {
    return key;
  }

  @Override
  public @NotNull String translationKey() {
    return block.translationKey();
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
