package org.aincraft.minestom.adapter;

import java.util.Objects;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
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
import org.jetbrains.annotations.NotNull;

public final class MinestomAdapters {

  private MinestomAdapters() {}

  public static @NotNull Location adapt(@NotNull Instance instance, @NotNull Pos pos) {
    Objects.requireNonNull(instance, "instance cannot be null");
    Objects.requireNonNull(pos, "pos cannot be null");
    return new MinestomLocationWrapper(adapt(instance), pos);
  }

  public static @NotNull Pos toMinestomPos(@NotNull Location location) {
    Objects.requireNonNull(location, "location cannot be null");
    if (location instanceof MinestomLocationWrapper wrapper) {
      return wrapper.getMinestomPos();
    }
    return new Pos(location.x(), location.y(), location.z(), location.yaw(), location.pitch());
  }

  public static @NotNull Position adapt(@NotNull Point point) {
    Objects.requireNonNull(point, "point cannot be null");
    return new MinestomPositionWrapper(point);
  }

  public static @NotNull Vec toMinestomVec(@NotNull Position position) {
    Objects.requireNonNull(position, "position cannot be null");
    if (position instanceof MinestomPositionWrapper wrapper && wrapper.getMinestomPoint() instanceof Vec vec) {
      return vec;
    }
    return new Vec(position.x(), position.y(), position.z());
  }

  public static @NotNull BoundingBox adapt(@NotNull net.minestom.server.collision.BoundingBox box) {
    Objects.requireNonNull(box, "box cannot be null");
    return new MinestomBoundingBoxWrapper(box);
  }

  public static @NotNull net.minestom.server.collision.BoundingBox toMinestom(@NotNull BoundingBox box) {
    Objects.requireNonNull(box, "box cannot be null");
    if (box instanceof MinestomBoundingBoxWrapper wrapper) {
      return wrapper.getMinestomBoundingBox();
    }
    return new net.minestom.server.collision.BoundingBox(
        box.maxX() - box.minX(),
        box.maxY() - box.minY(),
        box.maxZ() - box.minZ()
    );
  }

  public static @NotNull World adapt(@NotNull Instance instance) {
    Objects.requireNonNull(instance, "instance cannot be null");
    return new MinestomWorldWrapper(instance);
  }

  public static @NotNull Instance toMinestom(@NotNull World world) {
    Objects.requireNonNull(world, "world cannot be null");
    if (world instanceof MinestomWorldWrapper wrapper) {
      return wrapper.getMinestomInstance();
    }
    throw new IllegalArgumentException("World is not a MinestomWorldWrapper: " + world);
  }

  public static @NotNull Block adapt(@NotNull Instance instance, int x, int y, int z) {
    Objects.requireNonNull(instance, "instance cannot be null");
    return new MinestomBlockWrapper(instance, x, y, z);
  }

  public static @NotNull Chunk adapt(@NotNull net.minestom.server.instance.Chunk chunk) {
    Objects.requireNonNull(chunk, "chunk cannot be null");
    return new MinestomChunkWrapper(chunk);
  }

  public static @NotNull net.minestom.server.instance.Chunk toMinestom(@NotNull Chunk chunk) {
    Objects.requireNonNull(chunk, "chunk cannot be null");
    if (chunk instanceof MinestomChunkWrapper wrapper) {
      return wrapper.getMinestomChunk();
    }
    throw new IllegalArgumentException("Chunk is not a MinestomChunkWrapper: " + chunk);
  }

  public static @NotNull Entity adapt(@NotNull net.minestom.server.entity.Entity entity) {
    Objects.requireNonNull(entity, "entity cannot be null");
    return new MinestomEntityWrapper(entity);
  }

  public static @NotNull net.minestom.server.entity.Entity toMinestom(@NotNull Entity entity) {
    Objects.requireNonNull(entity, "entity cannot be null");
    if (entity instanceof MinestomEntityWrapper wrapper) {
      return wrapper.getMinestomEntity();
    }
    throw new IllegalArgumentException("Entity is not a MinestomEntityWrapper: " + entity);
  }

  public static @NotNull Player adapt(@NotNull net.minestom.server.entity.Player player) {
    Objects.requireNonNull(player, "player cannot be null");
    return new MinestomPlayerWrapper(player);
  }

  public static @NotNull net.minestom.server.entity.Player toMinestom(@NotNull Player player) {
    Objects.requireNonNull(player, "player cannot be null");
    if (player instanceof MinestomPlayerWrapper wrapper) {
      return wrapper.getMinestomPlayer();
    }
    throw new IllegalArgumentException("Player is not a MinestomPlayerWrapper: " + player);
  }

  public static @NotNull BlockType adapt(@NotNull net.minestom.server.instance.block.Block block) {
    Objects.requireNonNull(block, "block cannot be null");
    return new MinestomBlockTypeWrapper(block);
  }

  public static @NotNull net.minestom.server.instance.block.Block toMinestom(@NotNull BlockType blockType) {
    Objects.requireNonNull(blockType, "blockType cannot be null");
    if (blockType instanceof MinestomBlockTypeWrapper wrapper) {
      return wrapper.getMinestomBlock();
    }
    net.minestom.server.instance.block.Block block =
        net.minestom.server.instance.block.Block.fromKey(blockType.key());
    if (block == null) {
      throw new IllegalArgumentException("Unknown block type: " + blockType.key());
    }
    return block;
  }

  public static @NotNull BlockState adaptState(@NotNull net.minestom.server.instance.block.Block block) {
    Objects.requireNonNull(block, "block cannot be null");
    return new MinestomBlockStateWrapper(block);
  }

  public static @NotNull net.minestom.server.instance.block.Block toMinestom(@NotNull BlockState blockState) {
    Objects.requireNonNull(blockState, "blockState cannot be null");
    if (blockState instanceof MinestomBlockStateWrapper wrapper) {
      return wrapper.getMinestomBlock();
    }
    net.minestom.server.instance.block.Block block =
        net.minestom.server.instance.block.Block.fromState(blockState.asString());
    if (block == null) {
      throw new IllegalArgumentException("Unknown block state: " + blockState.asString());
    }
    return block;
  }

  public static @NotNull BlockFace adapt(@NotNull net.minestom.server.instance.block.BlockFace face) {
    Objects.requireNonNull(face, "face cannot be null");
    return switch (face) {
      case BOTTOM -> BlockFace.DOWN;
      case TOP -> BlockFace.UP;
      case NORTH -> BlockFace.NORTH;
      case SOUTH -> BlockFace.SOUTH;
      case WEST -> BlockFace.WEST;
      case EAST -> BlockFace.EAST;
    };
  }

  public static @NotNull net.minestom.server.instance.block.BlockFace toMinestom(@NotNull BlockFace face) {
    Objects.requireNonNull(face, "face cannot be null");
    return switch (face) {
      case DOWN -> net.minestom.server.instance.block.BlockFace.BOTTOM;
      case UP -> net.minestom.server.instance.block.BlockFace.TOP;
      case NORTH -> net.minestom.server.instance.block.BlockFace.NORTH;
      case SOUTH -> net.minestom.server.instance.block.BlockFace.SOUTH;
      case WEST -> net.minestom.server.instance.block.BlockFace.WEST;
      case EAST -> net.minestom.server.instance.block.BlockFace.EAST;
      default -> throw new IllegalArgumentException("Minestom does not support 2D/compound block faces: " + face);
    };
  }
}
