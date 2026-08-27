package org.aincraft.minestom.adapter;

import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
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
    return MinestomAdapters.adapt(new net.minestom.server.collision.BoundingBox(1.0, 1.0, 1.0));
  }

  @Override
  public @NotNull Key biome() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setBiome(@NotNull Key biome) {
    throw new UnsupportedOperationException();
  }

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
        + ", block="
        + getMinestomBlock().name()
        + "}";
  }
}
