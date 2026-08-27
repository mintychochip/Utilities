package org.aincraft.common.world;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.sound.Sound;
import org.aincraft.common.effect.Particle;
import org.aincraft.common.entity.Entity;
import org.aincraft.common.entity.Player;
import org.aincraft.common.location.Location;
import org.aincraft.common.location.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface World extends Keyed, Identified, Audience {

  @NotNull
  UUID uid();

  @NotNull
  String name();

  @Override
  default @NotNull Identity identity() {
    return Identity.identity(uid());
  }

  @NotNull
  Block getBlockAt(int x, int y, int z);

  default @NotNull Block getBlockAt(@NotNull Position position) {
    return getBlockAt(position.blockX(), position.blockY(), position.blockZ());
  }

  default @NotNull Block getBlockAt(@NotNull Location location) {
    return getBlockAt(location.position());
  }

  @NotNull
  Chunk getChunkAt(int chunkX, int chunkZ);

  default @NotNull Chunk getChunkAt(@NotNull Block block) {
    return getChunkAt(block.x() >> 4, block.z() >> 4);
  }

  default @NotNull Chunk getChunkAt(@NotNull Position position) {
    return getChunkAt(position.blockX() >> 4, position.blockZ() >> 4);
  }

  default @NotNull Chunk getChunkAt(@NotNull Location location) {
    return getChunkAt(location.blockX() >> 4, location.blockZ() >> 4);
  }

  boolean isChunkLoaded(int chunkX, int chunkZ);

  default boolean isChunkLoaded(@NotNull Chunk chunk) {
    return isChunkLoaded(chunk.x(), chunk.z());
  }

  int minHeight();

  int maxHeight();

  @NotNull
  WorldBorder worldBorder();

  @NotNull
  Environment environment();

  @NotNull
  Difficulty difficulty();

  long time();

  long fullTime();

  @NotNull
  Collection<? extends Player> players();

  @NotNull
  Collection<? extends Entity> entities();

  @NotNull
  Collection<? extends Chunk> loadedChunks();

  default void playSound(
      @NotNull Location location, @NotNull Sound.Type sound, float volume, float pitch) {
    playSound(location, sound, null, volume, pitch);
  }

  void playSound(
      @NotNull Location location,
      @NotNull Sound.Type sound,
      @Nullable Sound.Source source,
      float volume,
      float pitch);

  default void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count) {
    spawnParticle(particle, location, count, 0.0, 0.0, 0.0, 0.0);
  }

  default void spawnParticle(
      @NotNull Particle particle,
      @NotNull Location location,
      int count,
      double offsetX,
      double offsetY,
      double offsetZ) {
    spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, 0.0);
  }

  void spawnParticle(
      @NotNull Particle particle,
      @NotNull Location location,
      int count,
      double offsetX,
      double offsetY,
      double offsetZ,
      double extra);

  default @NotNull Block getHighestBlockAt(int x, int z) {
    return getHighestBlockAt(x, z, HeightMap.WORLD_SURFACE);
  }

  default @NotNull Block getHighestBlockAt(@NotNull Location location) {
    return getHighestBlockAt(location.blockX(), location.blockZ());
  }

  default @NotNull Block getHighestBlockAt(int x, int z, @NotNull HeightMap heightMap) {
    throw new UnsupportedOperationException();
  }

  default @NotNull Block getHighestBlockAt(
      @NotNull Location location, @NotNull HeightMap heightMap) {
    return getHighestBlockAt(location.blockX(), location.blockZ(), heightMap);
  }
}
