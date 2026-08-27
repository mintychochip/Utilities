package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.common.block.BlockState;
import org.aincraft.common.block.BlockType;
import org.aincraft.common.location.BoundingBox;
import org.aincraft.common.location.Location;
import org.aincraft.common.location.Position;
import org.aincraft.common.world.Block;
import org.aincraft.common.world.Chunk;
import org.aincraft.common.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BukkitBlockWrapper implements Block {

  private final org.bukkit.block.Block block;

  public BukkitBlockWrapper(@NotNull org.bukkit.block.Block block) {
    this.block = Objects.requireNonNull(block, "block cannot be null");
  }

  public @NotNull org.bukkit.block.Block getBukkitBlock() {
    return block;
  }

  @Override
  public int x() {
    return block.getX();
  }

  @Override
  public int y() {
    return block.getY();
  }

  @Override
  public int z() {
    return block.getZ();
  }

  @Override
  public @NotNull World world() {
    return BukkitAdapters.adapt(block.getWorld());
  }

  @Override
  public @NotNull Chunk chunk() {
    return BukkitAdapters.adapt(block.getChunk());
  }

  @Override
  public @NotNull Location location() {
    return BukkitAdapters.adapt(block.getLocation());
  }

  @Override
  public @NotNull Position position() {
    return new BukkitPositionWrapper(block.getLocation().toVector());
  }

  @Override
  public @NotNull BlockType type() {
    return new BukkitBlockTypeWrapper(block.getType());
  }

  @Override
  public @NotNull BlockState state() {
    return new BukkitBlockStateWrapper(block.getBlockData());
  }

  @Override
  public boolean isEmpty() {
    return block.isEmpty();
  }

  @Override
  public boolean isLiquid() {
    return block.isLiquid();
  }

  @Override
  public boolean isSolid() {
    return block.getType().isSolid();
  }

  @Override
  public boolean isAir() {
    return block.getType().isAir();
  }

  @Override
  public boolean isPassable() {
    return block.isPassable();
  }

  @Override
  public @NotNull BoundingBox boundingBox() {
    return BukkitAdapters.adapt(block.getBoundingBox());
  }

  @Override
  public @NotNull Key biome() {
    return BukkitAdapters.adapt(block.getBiome());
  }

  @Override
  public void setBiome(@NotNull Key biome) {
    block.setBiome(BukkitAdapters.toBukkitBiome(biome));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Block that)) return false;
    return x() == that.x()
        && y() == that.y()
        && z() == that.z()
        && Objects.equals(world(), that.world());
  }

  @Override
  public int hashCode() {
    return Objects.hash(world(), x(), y(), z());
  }

  @Override
  public String toString() {
    return "BukkitBlockWrapper{world="
        + world().name()
        + ", x="
        + x()
        + ", y="
        + y()
        + ", z="
        + z()
        + ", type="
        + type()
        + "}";
  }
}
