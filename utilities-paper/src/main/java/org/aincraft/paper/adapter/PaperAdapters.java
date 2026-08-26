package org.aincraft.paper.adapter;

import org.aincraft.bukkit.adapter.BukkitAdapters;
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
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public final class PaperAdapters {

  private PaperAdapters() {}

  public static @NotNull Player adapt(@NotNull org.bukkit.entity.Player player) {
    return new PaperPlayerWrapper(player);
  }

  public static @NotNull World adapt(@NotNull org.bukkit.World world) {
    return new PaperWorldWrapper(world);
  }

  public static @NotNull Location<World> adapt(@NotNull org.bukkit.Location location) {
    return BukkitAdapters.adapt(location);
  }

  public static @NotNull org.bukkit.Location toBukkit(@NotNull Location<?> location) {
    return BukkitAdapters.toBukkit(location);
  }

  public static @NotNull Position adapt(@NotNull Vector vector) {
    return BukkitAdapters.adapt(vector);
  }

  public static @NotNull Vector toBukkit(@NotNull Position position) {
    return BukkitAdapters.toBukkit(position);
  }

  public static @NotNull BoundingBox adapt(@NotNull org.bukkit.util.BoundingBox box) {
    return BukkitAdapters.adapt(box);
  }

  public static @NotNull org.bukkit.util.BoundingBox toBukkit(@NotNull BoundingBox box) {
    return BukkitAdapters.toBukkit(box);
  }

  public static @NotNull Block adapt(@NotNull org.bukkit.block.Block block) {
    return BukkitAdapters.adapt(block);
  }

  public static @NotNull org.bukkit.block.Block toBukkit(@NotNull Block block) {
    return BukkitAdapters.toBukkit(block);
  }

  public static @NotNull Chunk adapt(@NotNull org.bukkit.Chunk chunk) {
    return BukkitAdapters.adapt(chunk);
  }

  public static @NotNull org.bukkit.Chunk toBukkit(@NotNull Chunk chunk) {
    return BukkitAdapters.toBukkit(chunk);
  }

  public static @NotNull org.bukkit.World toBukkit(@NotNull World world) {
    return BukkitAdapters.toBukkit(world);
  }

  public static @NotNull Entity adapt(@NotNull org.bukkit.entity.Entity entity) {
    if (entity instanceof org.bukkit.entity.Player player) {
      return adapt(player);
    }
    return BukkitAdapters.adapt(entity);
  }

  public static @NotNull org.bukkit.entity.Entity toBukkit(@NotNull Entity entity) {
    return BukkitAdapters.toBukkit(entity);
  }

  public static @NotNull org.bukkit.entity.Player toBukkit(@NotNull Player player) {
    return BukkitAdapters.toBukkit(player);
  }

  public static @NotNull BlockFace adapt(@NotNull org.bukkit.block.BlockFace face) {
    return BukkitAdapters.adapt(face);
  }

  public static @NotNull org.bukkit.block.BlockFace toBukkit(@NotNull BlockFace face) {
    return BukkitAdapters.toBukkit(face);
  }

  public static @NotNull BlockType adapt(@NotNull Material material) {
    return BukkitAdapters.adapt(material);
  }

  public static @NotNull Material toBukkit(@NotNull BlockType blockType) {
    return BukkitAdapters.toBukkit(blockType);
  }

  public static @NotNull BlockState adapt(@NotNull BlockData blockData) {
    return BukkitAdapters.adapt(blockData);
  }

  public static @NotNull BlockData toBukkit(@NotNull BlockState blockState) {
    return BukkitAdapters.toBukkit(blockState);
  }
}
