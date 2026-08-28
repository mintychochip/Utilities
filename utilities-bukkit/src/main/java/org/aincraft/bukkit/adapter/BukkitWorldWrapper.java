package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.aincraft.api.domain.effect.Particle;
import org.aincraft.api.domain.entity.Entity;
import org.aincraft.api.domain.entity.Player;
import org.aincraft.api.domain.location.Location;
import org.aincraft.api.domain.world.Block;
import org.aincraft.api.domain.world.Chunk;
import org.aincraft.api.domain.world.Difficulty;
import org.aincraft.api.domain.world.Environment;
import org.aincraft.api.domain.world.HeightMap;
import org.aincraft.api.domain.world.World;
import org.aincraft.api.domain.world.WorldBorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

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
  public void sendMessage(@NotNull net.kyori.adventure.text.Component message) {
    for (org.bukkit.entity.Player player : world.getPlayers()) {
      BukkitAdapters.adapt(player).sendMessage(message);
    }
  }

  @Override
  public void sendActionBar(@NotNull net.kyori.adventure.text.Component message) {
    for (org.bukkit.entity.Player player : world.getPlayers()) {
      BukkitAdapters.adapt(player).sendActionBar(message);
    }
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
  public @NotNull WorldBorder worldBorder() {
    return new BukkitWorldBorderWrapper(world.getWorldBorder());
  }

  @Override
  public @NotNull Environment environment() {
    try {
      return Environment.valueOf(world.getEnvironment().name());
    } catch (IllegalArgumentException e) {
      return Environment.NORMAL;
    }
  }

  @Override
  public @NotNull Difficulty difficulty() {
    try {
      return Difficulty.valueOf(world.getDifficulty().name());
    } catch (IllegalArgumentException e) {
      return Difficulty.NORMAL;
    }
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
    return world.getPlayers().stream().map(BukkitAdapters::adapt).toList();
  }

  @Override
  public @NotNull Collection<? extends Entity> entities() {
    return world.getEntities().stream().map(BukkitAdapters::adapt).toList();
  }

  @Override
  public @NotNull Entity spawnEntity(@NotNull Location location, @NotNull Key entityType) {
    org.bukkit.Location bLoc = BukkitAdapters.toBukkit(location);
    org.bukkit.entity.EntityType bType =
        org.bukkit.Registry.ENTITY_TYPE.get(
            org.bukkit.NamespacedKey.fromString(entityType.asString()));
    if (bType == null) bType = org.bukkit.entity.EntityType.fromName(entityType.value());
    if (bType == null) {
      throw new IllegalArgumentException("Unknown entity type: " + entityType);
    }
    org.bukkit.entity.Entity bEntity = world.spawnEntity(bLoc, bType);
    return BukkitAdapters.adapt(bEntity);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Entity> @NotNull T spawn(@NotNull Location location, @NotNull Class<T> type) {
    Objects.requireNonNull(type, "type cannot be null");
    if (type != Entity.class
        && type != org.aincraft.api.domain.entity.LivingEntity.class
        && type != Player.class) {
      throw new org.aincraft.api.UnsupportedCapabilityException(
          org.aincraft.api.Capability.ENTITY_SPAWN,
          "Bukkit class-based spawning supports Entity, LivingEntity, and Player only.");
    }
    Class<? extends org.bukkit.entity.LivingEntity> bukkitType =
        type == Player.class
            ? org.bukkit.entity.Player.class
            : org.bukkit.entity.LivingEntity.class;
    org.bukkit.entity.LivingEntity spawned =
        world.spawn(BukkitAdapters.toBukkit(location), bukkitType);
    return (T) BukkitAdapters.adapt(spawned);
  }

  @Override
  public @NotNull Collection<? extends Chunk> loadedChunks() {
    return Arrays.stream(world.getLoadedChunks()).map(BukkitAdapters::adapt).toList();
  }

  @Override
  public void playSound(
      @NotNull Location location,
      @NotNull Sound.Type sound,
      @Nullable Sound.Source source,
      float volume,
      float pitch) {
    org.bukkit.Sound bSound = BukkitAdapters.toBukkit(sound);
    org.bukkit.Location bLoc = BukkitAdapters.toBukkit(location);
    if (source == null) {
      world.playSound(bLoc, bSound, volume, pitch);
    } else {
      String bukkitCategoryName =
          switch (source) {
            case MASTER -> "MASTER";
            case MUSIC -> "MUSIC";
            case RECORD -> "RECORDS";
            case WEATHER -> "WEATHER";
            case BLOCK -> "BLOCKS";
            case HOSTILE -> "HOSTILE";
            case NEUTRAL -> "NEUTRAL";
            case PLAYER -> "PLAYERS";
            case AMBIENT -> "AMBIENT";
            case VOICE -> "VOICE";
            default -> source.name();
          };
      world.playSound(
          bLoc, bSound, org.bukkit.SoundCategory.valueOf(bukkitCategoryName), volume, pitch);
    }
  }

  @Override
  public void playSound(
      @NotNull Location location,
      @NotNull org.aincraft.api.domain.effect.Sound sound,
      @NotNull org.aincraft.api.domain.effect.SoundCategory category,
      float volume,
      float pitch) {
    world.playSound(
        BukkitAdapters.toBukkit(location),
        BukkitAdapters.toBukkit(sound),
        org.bukkit.SoundCategory.valueOf(category.name()),
        volume,
        pitch);
  }

  @Override
  public void spawnParticle(
      @NotNull Particle particle,
      @NotNull Location location,
      int count,
      double offsetX,
      double offsetY,
      double offsetZ,
      double extra) {
    org.bukkit.Particle bParticle = BukkitAdapters.toBukkit(particle);
    org.bukkit.Location bLoc = BukkitAdapters.toBukkit(location);
    world.spawnParticle(bParticle, bLoc, count, offsetX, offsetY, offsetZ, extra);
  }

  @Override
  public <T> void spawnParticle(
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
    world.spawnParticle(
        BukkitAdapters.toBukkit(particle),
        BukkitAdapters.toBukkit(location),
        count,
        offsetX,
        offsetY,
        offsetZ,
        extra,
        data);
  }

  @Override
  public @NotNull Block getHighestBlockAt(int x, int z) {
    return BukkitAdapters.adapt(world.getHighestBlockAt(x, z));
  }

  @Override
  public @NotNull Block getHighestBlockAt(@NotNull Location location) {
    return BukkitAdapters.adapt(world.getHighestBlockAt(BukkitAdapters.toBukkit(location)));
  }

  @Override
  public @NotNull Block getHighestBlockAt(int x, int z, @NotNull HeightMap heightMap) {
    org.bukkit.HeightMap bHeightMap = BukkitAdapters.toBukkit(heightMap);
    org.bukkit.block.Block bBlock = world.getHighestBlockAt(x, z, bHeightMap);
    return BukkitAdapters.adapt(bBlock);
  }

  @Override
  public @Nullable org.aincraft.api.domain.world.RayTraceResult rayTraceBlocks(
      @NotNull Location start,
      @NotNull org.aincraft.api.domain.location.Position direction,
      double maxDistance,
      @NotNull org.aincraft.api.domain.world.FluidCollisionMode fluidCollisionMode,
      boolean ignorePassableBlocks) {
    org.bukkit.util.RayTraceResult result =
        world.rayTraceBlocks(
            BukkitAdapters.toBukkit(start),
            BukkitAdapters.toBukkit(direction),
            maxDistance,
            BukkitAdapters.toBukkit(fluidCollisionMode),
            ignorePassableBlocks);
    return result == null ? null : BukkitAdapters.adapt(result);
  }

  @Override
  public @Nullable org.aincraft.api.domain.world.RayTraceResult rayTrace(
      @NotNull Location start,
      @NotNull org.aincraft.api.domain.location.Position direction,
      double maxDistance,
      @NotNull org.aincraft.api.domain.world.FluidCollisionMode fluidCollisionMode,
      boolean ignorePassableBlocks,
      double raySize) {
    org.bukkit.util.RayTraceResult result =
        world.rayTrace(
            BukkitAdapters.toBukkit(start),
            BukkitAdapters.toBukkit(direction),
            maxDistance,
            BukkitAdapters.toBukkit(fluidCollisionMode),
            ignorePassableBlocks,
            raySize,
            entity -> true);
    return result == null ? null : BukkitAdapters.adapt(result);
  }

  @Override
  public @NotNull Collection<? extends Entity> nearbyEntities(
      @NotNull Location center, double xRadius, double yRadius, double zRadius) {
    return world
        .getNearbyEntities(BukkitAdapters.toBukkit(center), xRadius, yRadius, zRadius)
        .stream()
        .map(BukkitAdapters::adapt)
        .toList();
  }

  @Override
  public @NotNull Collection<? extends Entity> nearbyEntities(
      @NotNull org.aincraft.api.domain.location.BoundingBox box) {
    return world.getNearbyEntities(BukkitAdapters.toBukkit(box)).stream()
        .map(BukkitAdapters::adapt)
        .toList();
  }

  @Override
  public @Nullable Entity entity(@NotNull UUID uniqueId) {
    for (org.bukkit.entity.Entity entity : world.getEntities()) {
      if (uniqueId.equals(entity.getUniqueId())) return BukkitAdapters.adapt(entity);
    }
    return null;
  }

  @Override
  public boolean hasStorm() {
    return world.hasStorm();
  }

  @Override
  public void setStorm(boolean storm) {
    world.setStorm(storm);
  }

  @Override
  public boolean isThundering() {
    return world.isThundering();
  }

  @Override
  public void setThundering(boolean thundering) {
    world.setThundering(thundering);
  }

  @Override
  public int weatherDuration() {
    return world.getWeatherDuration();
  }

  @Override
  public void setWeatherDuration(int ticks) {
    world.setWeatherDuration(ticks);
  }

  @Override
  public void setTime(long time) {
    world.setTime(time);
  }

  @Override
  public void setFullTime(long time) {
    world.setFullTime(time);
  }

  @Override
  public boolean isDayTime() {
    return Math.floorMod(time(), 24000L) < 12000L;
  }

  @Override
  public long gameTime() {
    return world.getGameTime();
  }

  @Override
  public @NotNull Location spawnLocation() {
    return BukkitAdapters.adapt(world.getSpawnLocation());
  }

  @Override
  public boolean setSpawnLocation(@NotNull Location location) {
    return world.setSpawnLocation(BukkitAdapters.toBukkit(location));
  }

  @Override
  public void setDifficulty(@NotNull Difficulty difficulty) {
    world.setDifficulty(org.bukkit.Difficulty.valueOf(difficulty.name()));
  }

  @Override
  public boolean createExplosion(
      @NotNull Location location, float power, boolean setFire, boolean breakBlocks) {
    org.bukkit.Location bukkitLocation = BukkitAdapters.toBukkit(location);
    return world.createExplosion(bukkitLocation, power, setFire, breakBlocks);
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
