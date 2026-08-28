package org.aincraft.api.domain.world;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.sound.Sound;
import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.aincraft.api.domain.effect.Particle;
import org.aincraft.api.domain.entity.Entity;
import org.aincraft.api.domain.entity.Player;
import org.aincraft.api.domain.location.BoundingBox;
import org.aincraft.api.domain.location.Location;
import org.aincraft.api.domain.location.Position;
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

  default @NotNull Entity spawnEntity(@NotNull Location location, @NotNull Key entityType) {
    throw new UnsupportedCapabilityException(Capability.ENTITY_SPAWN);
  }

  default <T extends Entity> @NotNull T spawn(@NotNull Location location, @NotNull Class<T> type) {
    throw new UnsupportedCapabilityException(Capability.ENTITY_SPAWN);
  }

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

  default void playSound(
      @NotNull Location location,
      @NotNull org.aincraft.api.domain.effect.Sound sound,
      @NotNull org.aincraft.api.domain.effect.SoundCategory category,
      float volume,
      float pitch) {
    throw new UnsupportedCapabilityException(Capability.SOUND);
  }

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

  default void spawnParticle(
      @NotNull Particle particle,
      @NotNull Location location,
      int count,
      double offsetX,
      double offsetY,
      double offsetZ,
      double extra) {
    throw new UnsupportedCapabilityException(Capability.PARTICLE);
  }

  default <T> void spawnParticle(
      @NotNull Particle particle,
      @NotNull Location location,
      int count,
      double offsetX,
      double offsetY,
      double offsetZ,
      double extra,
      @Nullable T data) {
    if (data != null && !particle.dataType().isInstance(data)) {
      throw new IllegalArgumentException("Particle data must be " + particle.dataType().getName());
    }
    throw new UnsupportedCapabilityException(Capability.PARTICLE);
  }

  default @NotNull Block getHighestBlockAt(int x, int z) {
    return getHighestBlockAt(x, z, HeightMap.WORLD_SURFACE);
  }

  default @NotNull Block getHighestBlockAt(@NotNull Location location) {
    return getHighestBlockAt(location.blockX(), location.blockZ());
  }

  default @NotNull Block getHighestBlockAt(int x, int z, @NotNull HeightMap heightMap) {
    throw new UnsupportedCapabilityException(Capability.BLOCK_QUERY);
  }

  default @NotNull Block getHighestBlockAt(
      @NotNull Location location, @NotNull HeightMap heightMap) {
    return getHighestBlockAt(location.blockX(), location.blockZ(), heightMap);
  }

  // -- Ray-trace and proximity --

  default @Nullable RayTraceResult rayTraceBlocks(
      @NotNull Location start,
      @NotNull Position direction,
      double maxDistance,
      @NotNull FluidCollisionMode fluidCollisionMode,
      boolean ignorePassableBlocks) {
    throw new UnsupportedCapabilityException(Capability.RAYTRACE);
  }

  default @Nullable RayTraceResult rayTrace(
      @NotNull Location start,
      @NotNull Position direction,
      double maxDistance,
      @NotNull FluidCollisionMode fluidCollisionMode,
      boolean ignorePassableBlocks,
      double raySize) {
    throw new UnsupportedCapabilityException(Capability.RAYTRACE);
  }

  default @NotNull Collection<? extends Entity> nearbyEntities(
      @NotNull Location center, double xRadius, double yRadius, double zRadius) {
    throw new UnsupportedCapabilityException(Capability.LOCATION_NEARBY);
  }

  default @NotNull Collection<? extends Entity> nearbyEntities(@NotNull BoundingBox box) {
    throw new UnsupportedCapabilityException(Capability.LOCATION_NEARBY);
  }

  default @Nullable Entity entity(@NotNull UUID uniqueId) {
    throw new UnsupportedCapabilityException(Capability.ENTITY_LOOKUP);
  }

  // -- Weather --

  default boolean hasStorm() {
    throw new UnsupportedCapabilityException(Capability.WEATHER);
  }

  default void setStorm(boolean storm) {
    throw new UnsupportedCapabilityException(Capability.WEATHER);
  }

  default boolean isThundering() {
    throw new UnsupportedCapabilityException(Capability.WEATHER);
  }

  default void setThundering(boolean thundering) {
    throw new UnsupportedCapabilityException(Capability.WEATHER);
  }

  default int weatherDuration() {
    throw new UnsupportedCapabilityException(Capability.WEATHER);
  }

  default void setWeatherDuration(int ticks) {
    throw new UnsupportedCapabilityException(Capability.WEATHER);
  }

  // -- Time --

  default void setTime(long time) {
    throw new UnsupportedCapabilityException(Capability.TIME_SET);
  }

  default void setFullTime(long time) {
    throw new UnsupportedCapabilityException(Capability.TIME_SET);
  }

  default boolean isDayTime() {
    throw new UnsupportedCapabilityException(Capability.TIME_SET);
  }

  default long gameTime() {
    throw new UnsupportedCapabilityException(Capability.TIME_SET);
  }

  // -- Spawn / difficulty --

  default @NotNull Location spawnLocation() {
    throw new UnsupportedCapabilityException(Capability.WORLD_CONFIGURATION);
  }

  default boolean setSpawnLocation(@NotNull Location location) {
    throw new UnsupportedCapabilityException(Capability.WORLD_CONFIGURATION);
  }

  default void setDifficulty(@NotNull Difficulty difficulty) {
    throw new UnsupportedCapabilityException(Capability.WORLD_CONFIGURATION);
  }

  // -- Explosion --

  default boolean createExplosion(
      @NotNull Location location, float power, boolean setFire, boolean breakBlocks) {
    throw new UnsupportedCapabilityException(Capability.EXPLOSION);
  }
}
