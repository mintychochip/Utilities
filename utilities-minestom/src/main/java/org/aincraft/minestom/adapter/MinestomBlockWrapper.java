package org.aincraft.minestom.adapter;

import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import org.aincraft.api.domain.block.BlockState;
import org.aincraft.api.domain.block.BlockType;
import org.aincraft.api.domain.location.BoundingBox;
import org.aincraft.api.domain.location.Location;
import org.aincraft.api.domain.location.Position;
import org.aincraft.api.domain.world.Block;
import org.aincraft.api.domain.world.Chunk;
import org.aincraft.api.domain.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

public class MinestomBlockWrapper implements Block {

  private final Instance instance;
  private final int x;
  private final int y;
  private final int z;

  public MinestomBlockWrapper(@NotNull Instance instance, int x, int y, int z) {
    this.instance = Objects.requireNonNull(instance, "instance cannot be null");
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public @NotNull Instance getMinestomInstance() {
    return instance;
  }

  public @NotNull net.minestom.server.instance.block.Block getMinestomBlock() {
    return instance.getBlock(x, y, z);
  }

  @Override
  public int x() {
    return x;
  }

  @Override
  public int y() {
    return y;
  }

  @Override
  public int z() {
    return z;
  }

  @Override
  public @NotNull World world() {
    return MinestomAdapters.adapt(instance);
  }

  @Override
  public @NotNull Location location() {
    return new MinestomLocationWrapper(world(), new Pos(x, y, z));
  }

  @Override
  public @NotNull Position position() {
    return MinestomAdapters.adapt(new Pos(x, y, z));
  }

  @Override
  public @NotNull Chunk chunk() {
    return world().getChunkAt(x >> 4, z >> 4);
  }

  @Override
  public @NotNull BlockType type() {
    return MinestomAdapters.adapt(getMinestomBlock());
  }

  @Override
  public void setType(@NotNull BlockType type) {
    net.minestom.server.instance.block.Block mBlock = MinestomAdapters.toMinestom(type);
    instance.setBlock(x, y, z, mBlock);
  }

  @Override
  public void setType(@NotNull BlockType type, boolean applyPhysics) {
    instance.setBlock(x, y, z, MinestomAdapters.toMinestom(type), applyPhysics);
  }

  @Override
  public void setState(@NotNull BlockState state) {
    setState(state, true);
  }

  @Override
  public void setState(@NotNull BlockState state, boolean applyPhysics) {
    instance.setBlock(x, y, z, MinestomAdapters.toMinestom(state), applyPhysics);
  }

  @Override
  public @NotNull BlockState state() {
    return MinestomAdapters.adaptState(getMinestomBlock());
  }

  @Override
  public boolean isEmpty() {
    return getMinestomBlock().air();
  }

  @Override
  public boolean isLiquid() {
    return getMinestomBlock().liquid();
  }

  @Override
  public boolean isSolid() {
    return getMinestomBlock().solid();
  }

  @Override
  public boolean isAir() {
    return getMinestomBlock().air();
  }

  @Override
  public boolean isPassable() {
    return !getMinestomBlock().solid();
  }

  @Override
  public @NotNull BoundingBox boundingBox() {
    net.minestom.server.collision.Shape shape = getMinestomBlock().collisionShape();
    net.minestom.server.coordinate.Point start = shape.relativeStart();
    net.minestom.server.coordinate.Point end = shape.relativeEnd();
    return MinestomAdapters.adapt(
        new net.minestom.server.collision.BoundingBox(
            end.x() - start.x(),
            end.y() - start.y(),
            end.z() - start.z(),
            new net.minestom.server.coordinate.Vec(x + start.x(), y + start.y(), z + start.z())));
  }

  @Override
  public @NotNull org.aincraft.api.domain.block.VoxelShape collisionShape() {
    return new MinestomVoxelShapeWrapper(getMinestomBlock().collisionShape());
  }

  @Override
  public @NotNull org.aincraft.api.domain.block.TileBlockState tileState() {
    if (getMinestomBlock().blockEntityType() == null) {
      throw new org.aincraft.api.UnsupportedCapabilityException(
          org.aincraft.api.Capability.TILE_BLOCK_STATE,
          "Block " + getMinestomBlock().key() + " has no Minestom block entity state.");
    }
    return new MinestomTileBlockStateWrapper(instance, x, y, z);
  }

  @Override
  public @NotNull Key biome() {
    net.minestom.server.registry.RegistryKey<net.minestom.server.world.biome.Biome> biome =
        instance.getBiome(x, y, z);
    if (biome == null) return Key.key("minecraft", "plains");
    return biome.key();
  }

  @Override
  public void setBiome(@NotNull Key biome) {
    instance.setBiome(x, y, z, net.minestom.server.registry.RegistryKey.unsafeOf(biome));
  }

  // -- Light queries --

  @Override
  public int lightLevel() {
    return Math.max(lightFromSky(), lightFromBlocks());
  }

  @Override
  public int lightFromSky() {
    return Math.max(0, Math.min(15, instance.getSkyLight(x, y, z)));
  }

  @Override
  public int lightFromBlocks() {
    return Math.max(0, Math.min(15, instance.getBlockLight(x, y, z)));
  }

  @Override
  public boolean isReplaceable() {
    return getMinestomBlock().replaceable();
  }

  @Override
  public boolean isCollidable() {
    net.minestom.server.collision.Shape shape = getMinestomBlock().collisionShape();
    return shape.relativeStart().x() < shape.relativeEnd().x()
        && shape.relativeStart().y() < shape.relativeEnd().y()
        && shape.relativeStart().z() < shape.relativeEnd().z();
  }

  @Override
  public boolean isBuildable() {
    return getMinestomBlock().blocksMotion();
  }

  @Override
  public boolean isBurnable() {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.BLOCK_QUERY,
        "Minestom does not expose block flammability on its public block API.");
  }

  @Override
  public boolean isSuffocating() {
    return getMinestomBlock().occludes();
  }

  @Override
  public boolean isPowered() {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.REDSTONE,
        "Minestom does not expose block power levels on its public block API.");
  }

  @Override
  public boolean isIndirectlyPowered() {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.REDSTONE,
        "Minestom does not expose block power levels on its public block API.");
  }

  @Override
  public boolean isFacePowered(@NotNull org.aincraft.api.domain.block.BlockFace face) {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.REDSTONE,
        "Minestom does not expose block power levels on its public block API.");
  }

  @Override
  public boolean isFaceIndirectlyPowered(@NotNull org.aincraft.api.domain.block.BlockFace face) {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.REDSTONE,
        "Minestom does not expose block power levels on its public block API.");
  }

  @Override
  public int blockPower() {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.REDSTONE,
        "Minestom does not expose block power levels on its public block API.");
  }

  @Override
  public int blockPower(@NotNull org.aincraft.api.domain.block.BlockFace face) {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.REDSTONE,
        "Minestom does not expose block power levels on its public block API.");
  }

  @Override
  public @NotNull Collection<? extends org.aincraft.api.domain.inventory.ItemStack> drops() {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.BLOCK_QUERY,
        "Minestom does not expose block loot tables on its public block API.");
  }

  @Override
  public @NotNull Collection<? extends org.aincraft.api.domain.inventory.ItemStack> drops(
      @Nullable org.aincraft.api.domain.inventory.ItemStack tool) {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.BLOCK_QUERY,
        "Minestom does not expose block loot tables on its public block API.");
  }

  @Override
  public @NotNull Collection<? extends org.aincraft.api.domain.inventory.ItemStack> drops(
      @Nullable org.aincraft.api.domain.inventory.ItemStack tool,
      @Nullable org.aincraft.api.domain.entity.Entity breaker) {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.BLOCK_QUERY,
        "Minestom does not expose block loot tables on its public block API.");
  }

  @Override
  public boolean breakNaturally() {
    if (isAir()) return false;
    instance.setBlock(x, y, z, net.minestom.server.instance.block.Block.AIR);
    return true;
  }

  @Override
  public boolean breakNaturally(@Nullable org.aincraft.api.domain.inventory.ItemStack tool) {
    return breakNaturally();
  }

  @Override
  public boolean canPlace(@NotNull BlockState state) {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.BLOCK_QUERY,
        "Minestom does not expose block placement checks on its public block API.");
  }

  @Override
  public long blockKey() {
    long key = ((long) x & 0x3FFFFFFL) << 38;
    key |= ((long) z & 0x3FFFFFFL) << 12;
    return key | ((long) y & 0xFFFL);
  }

  @Override
  public float breakSpeed(@NotNull org.aincraft.api.domain.entity.Player player) {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.BLOCK_QUERY,
        "Minestom does not expose player-specific block break speed on its public block API.");
  }

  // -- Object identity --

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Block that)) return false;
    return x == that.x() && y == that.y() && z == that.z() && Objects.equals(world(), that.world());
  }

  @Override
  public int hashCode() {
    return Objects.hash(world(), x, y, z);
  }

  @Override
  public String toString() {
    return "MinestomBlockWrapper{world="
        + world().name()
        + ", x="
        + x
        + ", y="
        + y
        + ", z="
        + z
        + "}";
  }
}
