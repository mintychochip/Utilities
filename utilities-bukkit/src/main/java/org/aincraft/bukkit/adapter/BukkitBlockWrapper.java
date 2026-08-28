package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.aincraft.api.domain.block.BlockFace;
import org.aincraft.api.domain.block.BlockState;
import org.aincraft.api.domain.block.BlockType;
import org.aincraft.api.domain.block.TileBlockState;
import org.aincraft.api.domain.block.VoxelShape;
import org.aincraft.api.domain.entity.Entity;
import org.aincraft.api.domain.entity.Player;
import org.aincraft.api.domain.inventory.ItemStack;
import org.aincraft.api.domain.location.BoundingBox;
import org.aincraft.api.domain.location.Location;
import org.aincraft.api.domain.location.Position;
import org.aincraft.api.domain.world.Block;
import org.aincraft.api.domain.world.Chunk;
import org.aincraft.api.domain.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
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
  public void setType(@NotNull BlockType type) {
    block.setType(BukkitAdapters.toBukkitBlockMaterial(type));
  }

  @Override
  public void setType(@NotNull BlockType type, boolean applyPhysics) {
    block.setType(BukkitAdapters.toBukkitBlockMaterial(type), applyPhysics);
  }

  @Override
  public void setState(@NotNull BlockState state) {
    block.setBlockData(BukkitAdapters.toBukkit(state));
  }

  @Override
  public void setState(@NotNull BlockState state, boolean applyPhysics) {
    block.setBlockData(BukkitAdapters.toBukkit(state), applyPhysics);
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
  public @NotNull VoxelShape collisionShape() {
    return BukkitAdapters.adapt(block.getCollisionShape());
  }

  @Override
  public @NotNull TileBlockState tileState() {
    return new BukkitTileBlockStateWrapper(block.getState());
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
  public int lightLevel() {
    return block.getLightLevel();
  }

  @Override
  public int lightFromSky() {
    return block.getLightFromSky();
  }

  @Override
  public int lightFromBlocks() {
    return block.getLightFromBlocks();
  }

  @Override
  public boolean isReplaceable() {
    throw new UnsupportedCapabilityException(
        Capability.BLOCK_QUERY,
        "Spigot Block has no replaceability predicate on the compile surface.");
  }

  @Override
  public boolean isCollidable() {
    throw new UnsupportedCapabilityException(
        Capability.BLOCK_QUERY,
        "Spigot Block has no collidability predicate on the compile surface.");
  }

  @Override
  public boolean isBuildable() {
    throw new UnsupportedCapabilityException(
        Capability.BLOCK_QUERY,
        "Spigot Block has no buildability predicate on the compile surface.");
  }

  @Override
  public boolean isBurnable() {
    return block.getType().isBurnable();
  }

  @Override
  public boolean isSuffocating() {
    throw new UnsupportedCapabilityException(
        Capability.BLOCK_QUERY,
        "Spigot Block has no suffocation predicate on the compile surface.");
  }

  @Override
  public boolean isPowered() {
    return block.isBlockPowered();
  }

  @Override
  public boolean isIndirectlyPowered() {
    return block.isBlockIndirectlyPowered();
  }

  @Override
  public boolean isFacePowered(@NotNull BlockFace face) {
    return block.isBlockFacePowered(BukkitAdapters.toBukkit(face));
  }

  @Override
  public boolean isFaceIndirectlyPowered(@NotNull BlockFace face) {
    return block.isBlockFaceIndirectlyPowered(BukkitAdapters.toBukkit(face));
  }

  @Override
  public int blockPower() {
    return block.getBlockPower();
  }

  @Override
  public int blockPower(@NotNull BlockFace face) {
    return block.getBlockPower(BukkitAdapters.toBukkit(face));
  }

  @Override
  public @NotNull Collection<? extends ItemStack> drops() {
    return drops(null, null);
  }

  @Override
  public @NotNull Collection<? extends ItemStack> drops(@Nullable ItemStack tool) {
    return drops(tool, null);
  }

  @Override
  public @NotNull Collection<? extends ItemStack> drops(
      @Nullable ItemStack tool, @Nullable Entity breaker) {
    org.bukkit.inventory.ItemStack bukkitTool = tool == null ? null : BukkitAdapters.toBukkit(tool);
    org.bukkit.entity.Entity bukkitBreaker =
        breaker instanceof BukkitEntityWrapper wrapper ? wrapper.getBukkitEntity() : null;
    Collection<org.bukkit.inventory.ItemStack> bukkitDrops =
        bukkitBreaker == null
            ? block.getDrops(bukkitTool)
            : block.getDrops(bukkitTool, bukkitBreaker);
    ArrayList<ItemStack> out = new ArrayList<>(bukkitDrops.size());
    for (org.bukkit.inventory.ItemStack item : bukkitDrops) out.add(BukkitAdapters.adapt(item));
    return out;
  }

  @Override
  public boolean breakNaturally() {
    return block.breakNaturally();
  }

  @Override
  public boolean breakNaturally(@Nullable ItemStack tool) {
    return tool == null
        ? block.breakNaturally()
        : block.breakNaturally(BukkitAdapters.toBukkit(tool));
  }

  @Override
  public boolean canPlace(@NotNull BlockState state) {
    return block.canPlace(BukkitAdapters.toBukkit(state));
  }

  @Override
  public long blockKey() {
    long key = ((long) x() & 0x3FFFFFFL) << 38;
    key |= ((long) z() & 0x3FFFFFFL) << 12;
    key |= ((long) y() & 0xFFFL);
    return key;
  }

  @Override
  public float breakSpeed(@NotNull Player player) {
    return block.getBreakSpeed(BukkitAdapters.toBukkit(player));
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
        + "}";
  }
}
