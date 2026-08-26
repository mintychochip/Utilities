package org.aincraft.bukkit.adapter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import net.kyori.adventure.key.Key;
import org.aincraft.common.entity.Entity;
import org.aincraft.common.entity.Player;
import org.aincraft.common.world.Block;
import org.aincraft.common.world.Chunk;
import org.aincraft.common.world.World;
import org.jetbrains.annotations.NotNull;

public class BukkitWorldWrapper implements World {

  private final org.bukkit.World world;
  private final Key key;

  public BukkitWorldWrapper(@NotNull org.bukkit.World world) {
    this.world = Objects.requireNonNull(world, "world cannot be null");
    this.key = Key.key(world.getKey().getNamespace(), world.getKey().getKey());
  }

  public @NotNull org.bukkit.World getBukkitWorld() {
    return world;
  }

  @Override
  public @NotNull UUID uid() {
    return world.getUID();
  }

  @Override
  public @NotNull String name() {
    return world.getName();
  }

  @Override
  public @NotNull Key key() {
    return key;
  }

  @Override
  public @NotNull Block getBlockAt(int x, int y, int z) {
    return BukkitAdapters.adapt(world.getBlockAt(x, y, z));
  }

  @Override
  public @NotNull Chunk getChunkAt(int chunkX, int chunkZ) {
    return BukkitAdapters.adapt(world.getChunkAt(chunkX, chunkZ));
  }

  @Override
  public boolean isChunkLoaded(int chunkX, int chunkZ) {
    return world.isChunkLoaded(chunkX, chunkZ);
  }

  @Override
  public int minHeight() {
    return world.getMinHeight();
  }

  @Override
  public int maxHeight() {
    return world.getMaxHeight();
  }

  @Override
  public long time() {
    return world.getTime();
  }

  @Override
  public long fullTime() {
    return world.getFullTime();
  }

  @Override
  public @NotNull Collection<? extends Player> players() {
    return world.getPlayers().stream()
        .map(BukkitAdapters::adapt)
        .toList();
  }

  @Override
  public @NotNull Collection<? extends Entity> entities() {
    return world.getEntities().stream()
        .map(BukkitAdapters::adapt)
        .toList();
  }

  @Override
  public @NotNull Collection<? extends Chunk> loadedChunks() {
    return Arrays.stream(world.getLoadedChunks())
        .map(BukkitAdapters::adapt)
        .toList();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof World that)) return false;
    return Objects.equals(uid(), that.uid());
  }

  @Override
  public int hashCode() {
    return uid().hashCode();
  }

  @Override
  public String toString() {
    return "BukkitWorldWrapper{name=" + name() + ", uid=" + uid() + "}";
  }
}
