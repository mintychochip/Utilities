package org.aincraft.minestom.adapter;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.minestom.server.instance.Instance;
import org.aincraft.api.domain.entity.Entity;
import org.aincraft.api.domain.entity.Player;
import org.aincraft.api.domain.location.Location;
import org.aincraft.api.domain.location.Position;
import org.aincraft.api.domain.world.Block;
import org.aincraft.api.domain.world.Chunk;
import org.aincraft.api.domain.world.Difficulty;
import org.aincraft.api.domain.world.Environment;
import org.aincraft.api.domain.world.HeightMap;
import org.aincraft.api.domain.world.World;
import org.aincraft.api.domain.world.WorldBorder;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class MinestomWorldWrapper implements World, ForwardingAudience.Single {

  private final Instance instance;
  private final String name;
  private final Key key;
  private final MinestomWorldBorderWrapper worldBorder;
  private volatile Difficulty difficulty = Difficulty.NORMAL;
  private volatile int weatherDuration;
  private volatile Location spawnLocation;

  public MinestomWorldWrapper(@NotNull Instance instance) {
    this.instance = Objects.requireNonNull(instance, "instance cannot be null");
    this.name = instance.getUuid().toString();
    this.key = Key.key("minecraft", instance.getUuid().toString());
    this.worldBorder = new MinestomWorldBorderWrapper(instance, this);
    this.spawnLocation = Location.of(this, 0.0, 0.0, 0.0);
  }

  public MinestomWorldWrapper(@NotNull Instance instance, @NotNull String name, @NotNull Key key) {
    this.instance = Objects.requireNonNull(instance, "instance cannot be null");
    this.name = Objects.requireNonNull(name, "name cannot be null");
    this.key = Objects.requireNonNull(key, "key cannot be null");
    this.worldBorder = new MinestomWorldBorderWrapper(instance, this);
    this.spawnLocation = Location.of(this, 0.0, 0.0, 0.0);
  }

  public @NotNull Instance getMinestomInstance() {
    return instance;
  }

  @Override
  public @NotNull Audience audience() {
    return instance;
  }

  @Override
  public @NotNull UUID uid() {
    return instance.getUuid();
  }

  @Override
  public @NotNull String name() {
    return name;
  }

  @Override
  public @NotNull Key key() {
    return key;
  }

  @Override
  public @NotNull Block getBlockAt(int x, int y, int z) {
    return MinestomAdapters.adapt(instance, x, y, z);
  }

  @Override
  public @NotNull Chunk getChunkAt(int chunkX, int chunkZ) {
    return MinestomAdapters.adapt(instance.getChunk(chunkX, chunkZ));
  }

  @Override
  public boolean isChunkLoaded(int chunkX, int chunkZ) {
    return instance.isChunkLoaded(chunkX, chunkZ);
  }

  private @NotNull net.minestom.server.world.DimensionType dimensionType() {
    net.minestom.server.world.DimensionType dimension = instance.getCachedDimensionType();
    if (dimension == null) {
      dimension = instance.getDimensionType().resolve(instance.registries().dimensionType());
    }
    if (dimension == null) throw new IllegalStateException("Instance dimension is not registered");
    return dimension;
  }

  @Override
  public int minHeight() {
    return dimensionType().minY();
  }

  @Override
  public int maxHeight() {
    return dimensionType().maxY();
  }

  @Override
  public @NotNull WorldBorder worldBorder() {
    return worldBorder;
  }

  @Override
  public @NotNull Environment environment() {
    String dimension = instance.getDimensionName().toLowerCase(java.util.Locale.ROOT);
    if (dimension.contains("nether")) return Environment.NETHER;
    if (dimension.contains("end")) return Environment.THE_END;
    return Environment.NORMAL;
  }

  @Override
  public @NotNull Difficulty difficulty() {
    return difficulty;
  }

  @Override
  public long time() {
    return instance.getTime();
  }

  @Override
  public long fullTime() {
    return instance.getWorldAge();
  }

  @Override
  public @NotNull Collection<? extends Player> players() {
    return instance.getPlayers().stream().map(MinestomAdapters::adapt).toList();
  }

  @Override
  public @NotNull Collection<? extends Entity> entities() {
    return instance.getEntities().stream().map(MinestomAdapters::adapt).toList();
  }

  @Override
  public @NotNull Entity spawnEntity(@NotNull Location location, @NotNull Key entityType) {
    net.minestom.server.entity.EntityType mType =
        net.minestom.server.entity.EntityType.fromKey(entityType);
    if (mType == null) throw new IllegalArgumentException("Unknown entity type: " + entityType);
    net.minestom.server.entity.Entity mEntity =
        "player".equals(entityType.value()) || mType.defaultAttributes().isEmpty()
            ? new net.minestom.server.entity.Entity(mType)
            : new net.minestom.server.entity.EntityCreature(mType);
    Instance target =
        location.world() instanceof MinestomWorldWrapper wrapper
            ? wrapper.getMinestomInstance()
            : instance;
    mEntity.setInstance(target, MinestomAdapters.toMinestomPos(location)).join();
    return MinestomAdapters.adapt(mEntity);
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
          "Minestom class-based spawning supports Entity and LivingEntity only.");
    }
    if (type == Player.class) {
      throw new org.aincraft.api.UnsupportedCapabilityException(
          org.aincraft.api.Capability.ENTITY_SPAWN,
          "Minestom player spawning requires a PlayerConnection and GameProfile.");
    }
    net.minestom.server.entity.Entity spawned =
        type == org.aincraft.api.domain.entity.LivingEntity.class
            ? new net.minestom.server.entity.EntityCreature(
                net.minestom.server.entity.EntityType.ZOMBIE)
            : new net.minestom.server.entity.Entity(net.minestom.server.entity.EntityType.ZOMBIE);
    spawned.setInstance(instance, MinestomAdapters.toMinestomPos(location)).join();
    return (T) MinestomAdapters.adapt(spawned);
  }

  @Override
  public @NotNull Collection<? extends Chunk> loadedChunks() {
    return instance.getChunks().stream().map(MinestomAdapters::adapt).toList();
  }

  @Override
  public void sendMessage(@NotNull Component message) {
    instance.sendMessage(message);
  }

  @Override
  public void sendActionBar(@NotNull Component message) {
    instance.sendActionBar(message);
  }

  @Override
  public void showTitle(@NotNull Title title) {
    instance.showTitle(title);
  }

  @Override
  public void clearTitle() {
    instance.clearTitle();
  }

  @Override
  public void resetTitle() {
    instance.resetTitle();
  }

  @Override
  public void playSound(
      @NotNull Location location,
      @NotNull Sound.Type sound,
      Sound.Source source,
      float volume,
      float pitch) {
    Sound.Source s = source != null ? source : Sound.Source.MASTER;
    instance.playSound(
        Sound.sound(sound, s, volume, pitch), location.x(), location.y(), location.z());
  }

  @Override
  public void playSound(
      @NotNull Location location,
      @NotNull org.aincraft.api.domain.effect.Sound sound,
      @NotNull org.aincraft.api.domain.effect.SoundCategory category,
      float volume,
      float pitch) {
    Sound.Source source =
        switch (category) {
          case MASTER -> Sound.Source.MASTER;
          case MUSIC -> Sound.Source.MUSIC;
          case RECORDS -> Sound.Source.RECORD;
          case WEATHER -> Sound.Source.WEATHER;
          case BLOCKS -> Sound.Source.BLOCK;
          case HOSTILE -> Sound.Source.HOSTILE;
          case NEUTRAL -> Sound.Source.NEUTRAL;
          case PLAYERS -> Sound.Source.PLAYER;
          case AMBIENT -> Sound.Source.AMBIENT;
          case VOICE -> Sound.Source.VOICE;
        };
    instance.playSound(
        Sound.sound(MinestomAdapters.toMinestomSound(sound), source, volume, pitch),
        location.x(),
        location.y(),
        location.z());
  }

  @Override
  public <T> void spawnParticle(
      @NotNull org.aincraft.api.domain.effect.Particle particle,
      @NotNull Location location,
      int count,
      double offsetX,
      double offsetY,
      double offsetZ,
      double extra,
      @org.jetbrains.annotations.Nullable T data) {
    if (data != null && !particle.dataType().isInstance(data)) {
      throw new IllegalArgumentException("Particle data must be " + particle.dataType().getName());
    }
    if (data instanceof net.minestom.server.particle.Particle dataParticle) {
      instance.sendGroupedPacket(
          new net.minestom.server.network.packet.server.play.ParticlePacket(
              dataParticle,
              location.x(),
              location.y(),
              location.z(),
              (float) offsetX,
              (float) offsetY,
              (float) offsetZ,
              (float) extra,
              count));
      return;
    }
    spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra);
  }

  @Override
  public void stopSound(@NotNull SoundStop stop) {
    instance.stopSound(stop);
  }

  @Override
  public void spawnParticle(
      @NotNull org.aincraft.api.domain.effect.Particle particle,
      @NotNull Location location,
      int count,
      double offsetX,
      double offsetY,
      double offsetZ,
      double extra) {
    net.minestom.server.particle.Particle minestomParticle = MinestomAdapters.toMinestom(particle);
    instance.sendGroupedPacket(
        new net.minestom.server.network.packet.server.play.ParticlePacket(
            minestomParticle,
            location.x(),
            location.y(),
            location.z(),
            (float) offsetX,
            (float) offsetY,
            (float) offsetZ,
            (float) extra,
            count));
  }

  @Override
  public @NotNull Block getHighestBlockAt(int x, int z, @NotNull HeightMap heightMap) {
    for (int y = maxHeight() - 1; y >= minHeight(); y--) {
      net.minestom.server.instance.block.Block block = instance.getBlock(x, y, z);
      if (heightMapIncludes(heightMap, block)) return getBlockAt(x, y, z);
    }
    return getBlockAt(x, minHeight(), z);
  }

  private static boolean heightMapIncludes(
      HeightMap heightMap, net.minestom.server.instance.block.Block block) {
    if (block.air()) return false;
    boolean leaves = block.key().value().endsWith("_leaves");
    return switch (heightMap) {
      case WORLD_SURFACE, WORLD_SURFACE_WG -> true;
      case OCEAN_FLOOR, OCEAN_FLOOR_WG, MOTION_BLOCKING -> block.blocksMotion();
      case MOTION_BLOCKING_NO_LEAVES -> block.blocksMotion() && !leaves;
    };
  }

  @Override
  public @org.jetbrains.annotations.Nullable org.aincraft.api.domain.world.RayTraceResult
      rayTraceBlocks(
          @NotNull Location start,
          @NotNull Position direction,
          double maxDistance,
          @NotNull org.aincraft.api.domain.world.FluidCollisionMode fluidCollisionMode,
          boolean ignorePassableBlocks) {
    if (maxDistance < 0.0) return null;
    double length =
        Math.sqrt(
            direction.x() * direction.x()
                + direction.y() * direction.y()
                + direction.z() * direction.z());
    if (length == 0.0) return null;
    double dx = direction.x() / length;
    double dy = direction.y() / length;
    double dz = direction.z() / length;
    int blockX = start.blockX();
    int blockY = start.blockY();
    int blockZ = start.blockZ();
    int stepX = Integer.signum(Double.compare(dx, 0.0));
    int stepY = Integer.signum(Double.compare(dy, 0.0));
    int stepZ = Integer.signum(Double.compare(dz, 0.0));
    double nextX = nextBoundary(start.x(), blockX, stepX);
    double nextY = nextBoundary(start.y(), blockY, stepY);
    double nextZ = nextBoundary(start.z(), blockZ, stepZ);
    double tMaxX = stepX == 0 ? Double.POSITIVE_INFINITY : (nextX - start.x()) / dx;
    double tMaxY = stepY == 0 ? Double.POSITIVE_INFINITY : (nextY - start.y()) / dy;
    double tMaxZ = stepZ == 0 ? Double.POSITIVE_INFINITY : (nextZ - start.z()) / dz;
    double tDeltaX = stepX == 0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / dx);
    double tDeltaY = stepY == 0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / dy);
    double tDeltaZ = stepZ == 0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / dz);
    double t = 0.0;
    org.aincraft.api.domain.block.BlockFace enteredFace = null;
    for (int step = 0; t <= maxDistance && step < 1_000_000; step++) {
      net.minestom.server.instance.block.Block block = instance.getBlock(blockX, blockY, blockZ);
      if (rayBlockMatches(block, fluidCollisionMode, ignorePassableBlocks)) {
        Position hit =
            new MinestomPositionWrapper(
                new net.minestom.server.coordinate.Vec(
                    start.x() + dx * t, start.y() + dy * t, start.z() + dz * t));
        return new MinestomRayTraceResultWrapper(
            hit, MinestomAdapters.adapt(instance, blockX, blockY, blockZ), enteredFace, null);
      }
      if (tMaxX <= tMaxY && tMaxX <= tMaxZ) {
        blockX += stepX;
        t = tMaxX;
        tMaxX += tDeltaX;
        enteredFace =
            stepX > 0
                ? org.aincraft.api.domain.block.BlockFace.WEST
                : org.aincraft.api.domain.block.BlockFace.EAST;
      } else if (tMaxY <= tMaxZ) {
        blockY += stepY;
        t = tMaxY;
        tMaxY += tDeltaY;
        enteredFace =
            stepY > 0
                ? org.aincraft.api.domain.block.BlockFace.DOWN
                : org.aincraft.api.domain.block.BlockFace.UP;
      } else {
        blockZ += stepZ;
        t = tMaxZ;
        tMaxZ += tDeltaZ;
        enteredFace =
            stepZ > 0
                ? org.aincraft.api.domain.block.BlockFace.NORTH
                : org.aincraft.api.domain.block.BlockFace.SOUTH;
      }
    }
    return null;
  }

  private static double nextBoundary(double coordinate, int block, int step) {
    return step > 0 ? block + 1.0 : block;
  }

  private static boolean rayBlockMatches(
      net.minestom.server.instance.block.Block block,
      org.aincraft.api.domain.world.FluidCollisionMode mode,
      boolean ignorePassable) {
    if (block.air()) return false;
    if (block.liquid()) {
      if (mode == org.aincraft.api.domain.world.FluidCollisionMode.NEVER) return false;
      if (mode == org.aincraft.api.domain.world.FluidCollisionMode.SOURCE_ONLY
          && "0".equals(block.getProperty("level")) == false) return false;
    } else if (ignorePassable && !block.blocksMotion()) {
      return false;
    }
    return true;
  }

  @Override
  public @org.jetbrains.annotations.Nullable org.aincraft.api.domain.world.RayTraceResult rayTrace(
      @NotNull Location start,
      @NotNull Position direction,
      double maxDistance,
      @NotNull org.aincraft.api.domain.world.FluidCollisionMode fluidCollisionMode,
      boolean ignorePassableBlocks,
      double raySize) {
    org.aincraft.api.domain.world.RayTraceResult closest =
        rayTraceBlocks(start, direction, maxDistance, fluidCollisionMode, ignorePassableBlocks);
    double closestDistance =
        closest == null
            ? Double.POSITIVE_INFINITY
            : start.position().distance(closest.hitPosition());
    double length =
        Math.sqrt(
            direction.x() * direction.x()
                + direction.y() * direction.y()
                + direction.z() * direction.z());
    if (length == 0.0) return closest;
    org.joml.Vector3d normalizedDirection =
        new org.joml.Vector3d(
            direction.x() / length, direction.y() / length, direction.z() / length);
    for (net.minestom.server.entity.Entity candidate :
        instance.getNearbyEntities(MinestomAdapters.toMinestomPos(start), maxDistance)) {
      org.aincraft.api.domain.entity.Entity domain = MinestomAdapters.adapt(candidate);
      org.aincraft.api.domain.location.BoundingBox box =
          domain.boundingBox().expand(raySize, raySize, raySize);
      org.aincraft.api.domain.world.RayTraceResult hit =
          box.rayTrace(
              new org.joml.Vector3d(start.x(), start.y(), start.z()),
              normalizedDirection,
              maxDistance);
      if (hit == null) continue;
      double distance = start.position().distance(hit.hitPosition());
      if (distance < closestDistance) {
        closestDistance = distance;
        closest = new MinestomRayTraceResultWrapper(hit.hitPosition(), null, null, domain);
      }
    }
    return closest;
  }

  @Override
  public @NotNull Collection<? extends Entity> nearbyEntities(
      @NotNull Location center, double xRadius, double yRadius, double zRadius) {
    double x = Math.abs(xRadius);
    double y = Math.abs(yRadius);
    double z = Math.abs(zRadius);
    double radius = Math.max(x, Math.max(y, z));
    return instance.getNearbyEntities(MinestomAdapters.toMinestomPos(center), radius).stream()
        .filter(
            entity ->
                Math.abs(entity.getPosition().x() - center.x()) <= x
                    && Math.abs(entity.getPosition().y() - center.y()) <= y
                    && Math.abs(entity.getPosition().z() - center.z()) <= z)
        .map(MinestomAdapters::adapt)
        .toList();
  }

  @Override
  public @NotNull Collection<? extends Entity> nearbyEntities(
      @NotNull org.aincraft.api.domain.location.BoundingBox box) {
    return entities().stream().filter(entity -> entity.boundingBox().intersects(box)).toList();
  }

  @Override
  public @org.jetbrains.annotations.Nullable Entity entity(@NotNull UUID uniqueId) {
    net.minestom.server.entity.Entity entity = instance.getEntityByUuid(uniqueId);
    return entity == null ? null : MinestomAdapters.adapt(entity);
  }

  @Override
  public boolean hasStorm() {
    return instance.getWeather().isRaining();
  }

  @Override
  public void setStorm(boolean storm) {
    net.minestom.server.instance.Weather weather = instance.getWeather();
    instance.setWeather(
        new net.minestom.server.instance.Weather(
            storm ? Math.max(1.0f, weather.rainLevel()) : 0.0f,
            storm ? weather.thunderLevel() : 0.0f),
        weatherDuration);
  }

  @Override
  public boolean isThundering() {
    return instance.getWeather().thunderLevel() > 0.0f;
  }

  @Override
  public void setThundering(boolean thundering) {
    net.minestom.server.instance.Weather weather = instance.getWeather();
    instance.setWeather(
        new net.minestom.server.instance.Weather(
            thundering ? Math.max(1.0f, weather.rainLevel()) : weather.rainLevel(),
            thundering ? 1.0f : 0.0f),
        weatherDuration);
  }

  @Override
  public int weatherDuration() {
    return weatherDuration;
  }

  @Override
  public void setWeatherDuration(int ticks) {
    weatherDuration = ticks;
    instance.setWeather(instance.getWeather(), ticks);
  }

  @Override
  public void setTime(long time) {
    instance.setTime(time);
  }

  @Override
  public void setFullTime(long time) {
    instance.setWorldAge(time);
  }

  @Override
  public boolean isDayTime() {
    return Math.floorMod(time(), 24000L) < 12000L;
  }

  @Override
  public long gameTime() {
    return instance.getWorldAge();
  }

  @Override
  public @NotNull Location spawnLocation() {
    return spawnLocation;
  }

  @Override
  public boolean setSpawnLocation(@NotNull Location location) {
    spawnLocation = Objects.requireNonNull(location, "location cannot be null");
    return true;
  }

  @Override
  public void setDifficulty(@NotNull Difficulty difficulty) {
    this.difficulty = Objects.requireNonNull(difficulty, "difficulty cannot be null");
  }

  @Override
  public boolean createExplosion(
      @NotNull Location location, float power, boolean setFire, boolean breakBlocks) {
    if (setFire || breakBlocks) {
      throw new org.aincraft.api.UnsupportedCapabilityException(
          org.aincraft.api.Capability.EXPLOSION,
          "Minestom's public explosion API cannot independently control fire or block damage.");
    }
    instance.explode((float) location.x(), (float) location.y(), (float) location.z(), power);
    return true;
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
    return "MinestomWorldWrapper{name=" + name() + ", uid=" + uid() + "}";
  }
}
