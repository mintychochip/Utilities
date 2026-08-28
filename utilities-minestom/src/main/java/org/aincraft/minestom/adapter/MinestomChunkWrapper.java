package org.aincraft.minestom.adapter;

import org.aincraft.api.domain.entity.Entity;
import org.aincraft.api.domain.world.Block;
import org.aincraft.api.domain.world.Chunk;
import org.aincraft.api.domain.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;

public final class MinestomChunkWrapper implements Chunk {

  private static final java.util.Map<net.minestom.server.instance.Chunk, Boolean> FORCE_LOADED =
      java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

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
    return load(true);
  }

  @Override
  public boolean load(boolean generate) {
    if (chunk.isLoaded()) return true;
    net.minestom.server.instance.Chunk loaded =
        (generate
                ? chunk.getInstance().loadChunk(x(), z())
                : chunk.getInstance().loadOptionalChunk(x(), z()))
            .join();
    return loaded != null && loaded.isLoaded();
  }

  @Override
  public boolean unload() {
    return unload(true);
  }

  @Override
  public boolean unload(boolean save) {
    if (!chunk.isLoaded()) return false;
    if (save) chunk.getInstance().saveChunkToStorage(chunk).join();
    chunk.getInstance().unloadChunk(chunk);
    return true;
  }

  @Override
  public boolean isGenerated() {
    return !chunk.shouldGenerate();
  }

  @Override
  public boolean isForceLoaded() {
    return FORCE_LOADED.getOrDefault(chunk, false);
  }

  @Override
  public void setForceLoaded(boolean forceLoaded) {
    FORCE_LOADED.put(chunk, forceLoaded);
  }

  @Override
  public @NotNull Collection<? extends Entity> entities() {
    return chunk.getInstance().getChunkEntities(chunk).stream()
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
