package org.aincraft.minestom.adapter;

import org.aincraft.common.entity.Entity;
import org.aincraft.common.world.Block;
import org.aincraft.common.world.Chunk;
import org.aincraft.common.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;

public class MinestomChunkWrapper implements Chunk {

  private final net.minestom.server.instance.Chunk chunk;

  public MinestomChunkWrapper(@NotNull net.minestom.server.instance.Chunk chunk) {
    this.chunk = Objects.requireNonNull(chunk, "chunk cannot be null");
  }

  public @NotNull net.minestom.server.instance.Chunk getMinestomChunk() {
    return chunk;
  }

  @Override
  public int x() {
    return chunk.getChunkX();
  }

  @Override
  public int z() {
    return chunk.getChunkZ();
  }

  @Override
  public @NotNull World world() {
    return MinestomAdapters.adapt(chunk.getInstance());
  }

  @Override
  public @NotNull Block getBlock(int x, int y, int z) {
    return MinestomAdapters.adapt(
        chunk.getInstance(), (this.x() << 4) + (x & 15), y, (this.z() << 4) + (z & 15));
  }

  @Override
  public boolean isLoaded() {
    return chunk.isLoaded();
  }

  @Override
  public boolean load() {
    return chunk.isLoaded();
  }

  @Override
  public boolean load(boolean generate) {
    return chunk.isLoaded();
  }

  @Override
  public boolean unload() {
    return false;
  }

  @Override
  public boolean unload(boolean save) {
    return false;
  }

  @Override
  public @NotNull Collection<? extends Entity> entities() {
    return chunk.getInstance().getEntities().stream()
        .filter(
            e -> (e.getPosition().blockX() >> 4) == x() && (e.getPosition().blockZ() >> 4) == z())
        .map(MinestomAdapters::adapt)
        .toList();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Chunk that)) return false;
    return x() == that.x() && z() == that.z() && Objects.equals(world(), that.world());
  }

  @Override
  public int hashCode() {
    return Objects.hash(world(), x(), z());
  }

  @Override
  public String toString() {
    return "MinestomChunkWrapper{world=" + world().name() + ", x=" + x() + ", z=" + z() + "}";
  }
}
