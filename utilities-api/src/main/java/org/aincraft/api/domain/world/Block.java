package org.aincraft.api.domain.world;

import net.kyori.adventure.key.Key;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface Block {

  int x();

  int y();

  int z();

  @NotNull
  World world();

  @NotNull
  Chunk chunk();

  @NotNull
  Location location();

  @NotNull
  Position position();

  @NotNull
  BlockType type();

  default void setType(@NotNull BlockType type) {
    throw new UnsupportedOperationException();
  }

  default void setType(@NotNull Key typeKey) {
    setType(BlockType.of(typeKey));
  }

  default void setType(@NotNull BlockType type, boolean applyPhysics) {
    setType(type);
  }

  default void setState(@NotNull BlockState state) {
    throw new UnsupportedOperationException();
  }

  default void setState(@NotNull BlockState state, boolean applyPhysics) {
    setState(state);
  }

  @NotNull
  BlockState state();

  default @NotNull Key key() {
    return type().key();
  }

  default @NotNull Block relative(int modX, int modY, int modZ) {
    return world().getBlockAt(x() + modX, y() + modY, z() + modZ);
  }

  default @NotNull Block relative(@NotNull BlockFace face) {
    return relative(face.modX(), face.modY(), face.modZ());
  }

  default @NotNull Block relative(@NotNull BlockFace face, int distance) {
    return relative(face.modX() * distance, face.modY() * distance, face.modZ() * distance);
  }

  boolean isEmpty();

  boolean isLiquid();

  boolean isSolid();

  boolean isAir();

  boolean isPassable();

  @NotNull
  BoundingBox boundingBox();

  default @NotNull VoxelShape collisionShape() {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.VOXEL_SHAPE);
  }

  default @NotNull TileBlockState tileState() {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.TILE_BLOCK_STATE);
  }

  @NotNull
  Key biome();

  void setBiome(@NotNull Key biome);

  // -- Light queries --

  /** Combined sky + block light level (0..15). */
  int lightLevel();

  /** Sky light (0..15). */
  int lightFromSky();

  /** Block-emitted light (0..15). */
  int lightFromBlocks();

  // -- Material predicates --

  default boolean isReplaceable() {
    throw new UnsupportedOperationException();
  }

  default boolean isCollidable() {
    throw new UnsupportedOperationException();
  }

  default boolean isBuildable() {
    throw new UnsupportedOperationException();
  }

  default boolean isBurnable() {
    throw new UnsupportedOperationException();
  }

  default boolean isSuffocating() {
    throw new UnsupportedOperationException();
  }

  // -- Redstone / power queries --

  default boolean isPowered() {
    throw new UnsupportedOperationException();
  }

  default boolean isIndirectlyPowered() {
    throw new UnsupportedOperationException();
  }

  default boolean isFacePowered(@NotNull BlockFace face) {
    throw new UnsupportedOperationException();
  }

  default boolean isFaceIndirectlyPowered(@NotNull BlockFace face) {
    throw new UnsupportedOperationException();
  }

  default int blockPower() {
    throw new UnsupportedOperationException();
  }

  default int blockPower(@NotNull BlockFace face) {
    throw new UnsupportedOperationException();
  }

  // -- Drops / breakNaturally --

  /** Items dropped by this block when mined without a tool. */
  @NotNull
  Collection<? extends ItemStack> drops();

  /** Items dropped by this block when mined with the given tool. */
  @NotNull
  Collection<? extends ItemStack> drops(@Nullable ItemStack tool);

  /** Items dropped by this block when mined with the given tool by the given entity. */
  @NotNull
  Collection<? extends ItemStack> drops(@Nullable ItemStack tool, @Nullable Entity breaker);

  default boolean breakNaturally() {
    throw new UnsupportedOperationException();
  }

  default boolean breakNaturally(@Nullable ItemStack tool) {
    throw new UnsupportedOperationException();
  }

  default boolean canPlace(@NotNull BlockState state) {
    throw new UnsupportedOperationException();
  }

  default long blockKey() {
    throw new UnsupportedOperationException();
  }

  default float breakSpeed(@NotNull Player player) {
    throw new UnsupportedOperationException();
  }
}
