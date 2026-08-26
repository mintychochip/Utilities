package org.aincraft.bukkit.adapter;

import java.util.Objects;
import org.aincraft.common.block.BlockFace;
import org.aincraft.common.block.BlockState;
import org.aincraft.common.block.BlockType;
import org.aincraft.common.entity.Entity;
import org.aincraft.common.entity.Player;
import org.aincraft.common.location.BoundingBox;
import org.aincraft.common.location.Location;
import org.aincraft.common.location.Position;
import org.aincraft.common.world.Block;
import org.aincraft.common.world.Chunk;
import org.aincraft.common.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public final class BukkitAdapters {

  private BukkitAdapters() {}

  public static @NotNull Location<World> adapt(@NotNull org.bukkit.Location location) {
    return new BukkitLocationWrapper<>(location);
  }

  public static @NotNull org.bukkit.Location toBukkit(@NotNull Location<?> location) {
    if (location instanceof BukkitLocationWrapper<?> wrapper) {
      return wrapper.getBukkitLocation();
    }
    org.bukkit.World bWorld = toBukkit(location.world());
    return new org.bukkit.Location(bWorld, location.x(), location.y(), location.z(), location.yaw(), location.pitch());
  }

  public static @NotNull Position adapt(@NotNull Vector vector) {
    return new BukkitPositionWrapper(vector);
  }

  public static @NotNull Vector toBukkit(@NotNull Position position) {
    if (position instanceof BukkitPositionWrapper wrapper) {
      return wrapper.getBukkitVector();
    }
    return new Vector(position.x(), position.y(), position.z());
  }

  public static @NotNull BoundingBox adapt(@NotNull org.bukkit.util.BoundingBox box) {
    return new BukkitBoundingBoxWrapper(box);
  }

  public static @NotNull org.bukkit.util.BoundingBox toBukkit(@NotNull BoundingBox box) {
    if (box instanceof BukkitBoundingBoxWrapper wrapper) {
      return wrapper.getBukkitBoundingBox();
    }
    return new org.bukkit.util.BoundingBox(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
  }

  public static @NotNull Block adapt(@NotNull org.bukkit.block.Block block) {
    return new BukkitBlockWrapper(block);
  }

  public static @NotNull org.bukkit.block.Block toBukkit(@NotNull Block block) {
    if (block instanceof BukkitBlockWrapper wrapper) {
      return wrapper.getBukkitBlock();
    }
    org.bukkit.World bWorld = toBukkit(block.world());
    return bWorld.getBlockAt(block.x(), block.y(), block.z());
  }

  public static @NotNull Chunk adapt(@NotNull org.bukkit.Chunk chunk) {
    return new BukkitChunkWrapper(chunk);
  }

  public static @NotNull org.bukkit.Chunk toBukkit(@NotNull Chunk chunk) {
    if (chunk instanceof BukkitChunkWrapper wrapper) {
      return wrapper.getBukkitChunk();
    }
    org.bukkit.World bWorld = toBukkit(chunk.world());
    return bWorld.getChunkAt(chunk.x(), chunk.z());
  }

  public static @NotNull World adapt(@NotNull org.bukkit.World world) {
    return new BukkitWorldWrapper(world);
  }

  public static @NotNull org.bukkit.World toBukkit(@NotNull World world) {
    if (world instanceof BukkitWorldWrapper wrapper) {
      return wrapper.getBukkitWorld();
    }
    org.bukkit.World bWorld = Bukkit.getWorld(world.uid());
    if (bWorld == null) {
      bWorld = Bukkit.getWorld(world.name());
    }
    if (bWorld == null) {
      throw new IllegalArgumentException("Cannot find Bukkit World for: " + world.name() + " (" + world.uid() + ")");
    }
    return bWorld;
  }

  public static @NotNull Entity adapt(@NotNull org.bukkit.entity.Entity entity) {
    if (entity instanceof org.bukkit.entity.Player player) {
      return adapt(player);
    }
    return new BukkitEntityWrapper(entity);
  }

  public static @NotNull org.bukkit.entity.Entity toBukkit(@NotNull Entity entity) {
    if (entity instanceof BukkitEntityWrapper wrapper) {
      return wrapper.getBukkitEntity();
    }
    org.bukkit.entity.Entity bEntity = Bukkit.getEntity(entity.uniqueId());
    if (bEntity == null) {
      throw new IllegalArgumentException("Cannot find Bukkit Entity with UUID: " + entity.uniqueId());
    }
    return bEntity;
  }

  public static @NotNull Player adapt(@NotNull org.bukkit.entity.Player player) {
    return new BukkitPlayerWrapper(player);
  }

  public static @NotNull org.bukkit.entity.Player toBukkit(@NotNull Player player) {
    if (player instanceof BukkitPlayerWrapper wrapper) {
      return wrapper.getBukkitPlayer();
    }
    org.bukkit.entity.Player bPlayer = Bukkit.getPlayer(player.uniqueId());
    if (bPlayer == null) {
      throw new IllegalArgumentException("Cannot find Bukkit Player with UUID: " + player.uniqueId());
    }
    return bPlayer;
  }

  public static @NotNull BlockFace adapt(@NotNull org.bukkit.block.BlockFace face) {
    return switch (face) {
      case NORTH -> BlockFace.NORTH;
      case EAST -> BlockFace.EAST;
      case SOUTH -> BlockFace.SOUTH;
      case WEST -> BlockFace.WEST;
      case UP -> BlockFace.UP;
      case DOWN -> BlockFace.DOWN;
      case NORTH_EAST -> BlockFace.NORTH_EAST;
      case NORTH_WEST -> BlockFace.NORTH_WEST;
      case SOUTH_EAST -> BlockFace.SOUTH_EAST;
      case SOUTH_WEST -> BlockFace.SOUTH_WEST;
      case WEST_NORTH_WEST -> BlockFace.WEST_NORTH_WEST;
      case NORTH_NORTH_WEST -> BlockFace.NORTH_NORTH_WEST;
      case NORTH_NORTH_EAST -> BlockFace.NORTH_NORTH_EAST;
      case EAST_NORTH_EAST -> BlockFace.EAST_NORTH_EAST;
      case EAST_SOUTH_EAST -> BlockFace.EAST_SOUTH_EAST;
      case SOUTH_SOUTH_EAST -> BlockFace.SOUTH_SOUTH_EAST;
      case SOUTH_SOUTH_WEST -> BlockFace.SOUTH_SOUTH_WEST;
      case WEST_SOUTH_WEST -> BlockFace.WEST_SOUTH_WEST;
      case SELF -> BlockFace.SELF;
    };
  }

  public static @NotNull org.bukkit.block.BlockFace toBukkit(@NotNull BlockFace face) {
    return switch (face) {
      case NORTH -> org.bukkit.block.BlockFace.NORTH;
      case EAST -> org.bukkit.block.BlockFace.EAST;
      case SOUTH -> org.bukkit.block.BlockFace.SOUTH;
      case WEST -> org.bukkit.block.BlockFace.WEST;
      case UP -> org.bukkit.block.BlockFace.UP;
      case DOWN -> org.bukkit.block.BlockFace.DOWN;
      case NORTH_EAST -> org.bukkit.block.BlockFace.NORTH_EAST;
      case NORTH_WEST -> org.bukkit.block.BlockFace.NORTH_WEST;
      case SOUTH_EAST -> org.bukkit.block.BlockFace.SOUTH_EAST;
      case SOUTH_WEST -> org.bukkit.block.BlockFace.SOUTH_WEST;
      case WEST_NORTH_WEST -> org.bukkit.block.BlockFace.WEST_NORTH_WEST;
      case NORTH_NORTH_WEST -> org.bukkit.block.BlockFace.NORTH_NORTH_WEST;
      case NORTH_NORTH_EAST -> org.bukkit.block.BlockFace.NORTH_NORTH_EAST;
      case EAST_NORTH_EAST -> org.bukkit.block.BlockFace.EAST_NORTH_EAST;
      case EAST_SOUTH_EAST -> org.bukkit.block.BlockFace.EAST_SOUTH_EAST;
      case SOUTH_SOUTH_EAST -> org.bukkit.block.BlockFace.SOUTH_SOUTH_EAST;
      case SOUTH_SOUTH_WEST -> org.bukkit.block.BlockFace.SOUTH_SOUTH_WEST;
      case WEST_SOUTH_WEST -> org.bukkit.block.BlockFace.WEST_SOUTH_WEST;
      case SELF -> org.bukkit.block.BlockFace.SELF;
    };
  }

  public static @NotNull BlockType adapt(@NotNull Material material) {
    return new BukkitBlockTypeWrapper(material);
  }

  public static @NotNull Material toBukkit(@NotNull BlockType blockType) {
    if (blockType instanceof BukkitBlockTypeWrapper wrapper) {
      return wrapper.getBukkitMaterial();
    }
    Material material = Material.matchMaterial(blockType.key().asString());
    if (material == null) {
      throw new IllegalArgumentException("Cannot match Bukkit Material for: " + blockType.key());
    }
    return material;
  }

  public static @NotNull BlockState adapt(@NotNull BlockData blockData) {
    return new BukkitBlockStateWrapper(blockData);
  }

  public static @NotNull BlockData toBukkit(@NotNull BlockState blockState) {
    if (blockState instanceof BukkitBlockStateWrapper wrapper) {
      return wrapper.getBukkitBlockData();
    }
    return Bukkit.createBlockData(blockState.asString());
  }
}
