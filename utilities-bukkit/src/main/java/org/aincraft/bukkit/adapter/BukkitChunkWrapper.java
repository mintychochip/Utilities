package org.aincraft.bukkit.adapter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import org.aincraft.common.entity.Entity;
import org.aincraft.common.world.Block;
import org.aincraft.common.world.Chunk;
import org.aincraft.common.world.World;
import org.jetbrains.annotations.NotNull;

public class BukkitChunkWrapper implements Chunk {

  private final org.bukkit.Chunk chunk;

  public BukkitChunkWrapper(@NotNull org.bukkit.Chunk chunk) {
    this.chunk = Objects.requireNonNull(chunk, "chunk cannot be null");
  }

  public @NotNull org.bukkit.Chunk getBukkitChunk() {
    return chunk;
  }

  @Override
  public int x() {
    return chunk.getX();
  }

  @Override
  public int z() {
    return chunk.getZ();
  }

  @Override
  public @NotNull World world() {
    return BukkitAdapters.adapt(chunk.getWorld());
  }

  @Override
  public @NotNull Block getBlock(int x, int y, int z) {
    return BukkitAdapters.adapt(chunk.getBlock(x, y, z));
  }

  @Override
  public boolean isLoaded() {
    return chunk.isLoaded();
  }

  @Override
  public boolean load() {
    return chunk.load();
  }

  @Override
  public boolean load(boolean generate) {
    return chunk.load(generate);
  }

  @Override
  public boolean unload() {
    return chunk.unload();
  }

  @Override
  public boolean unload(boolean save) {
    return chunk.unload(save);
  }

  @Override
  public @NotNull Collection<? extends Entity> entities() {
    return Arrays.stream(chunk.getEntities())
        .map(BukkitAdapters::adapt)
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
    return "BukkitChunkWrapper{world=" + world().name() + ", x=" + x() + ", z=" + z() + "}";
  }
}
