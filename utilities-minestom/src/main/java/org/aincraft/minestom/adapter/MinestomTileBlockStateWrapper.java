package org.aincraft.minestom.adapter;

import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.aincraft.api.domain.block.BlockState;
import org.aincraft.api.domain.block.TileBlockState;
import org.aincraft.api.domain.persistence.PersistentDataContainer;
import org.aincraft.api.domain.world.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class MinestomTileBlockStateWrapper implements TileBlockState {

  private final net.minestom.server.instance.Instance instance;
  private final int x;
  private final int y;
  private final int z;
  private final net.minestom.server.instance.block.Block state;

  public MinestomTileBlockStateWrapper(
      @NotNull net.minestom.server.instance.Instance instance, int x, int y, int z) {
    this(instance, x, y, z, instance.getBlock(x, y, z));
  }

  private MinestomTileBlockStateWrapper(
      net.minestom.server.instance.Instance instance,
      int x,
      int y,
      int z,
      net.minestom.server.instance.block.Block state) {
    this.instance = Objects.requireNonNull(instance, "instance cannot be null");
    this.x = x;
    this.y = y;
    this.z = z;
    this.state = Objects.requireNonNull(state, "state cannot be null");
  }

  @Override
  public @NotNull Block block() {
    return MinestomAdapters.adapt(instance, x, y, z);
  }

  @Override
  public @NotNull BlockState blockData() {
    return MinestomAdapters.adaptState(state);
  }

  @Override
  public boolean update() {
    instance.setBlock(x, y, z, state);
    return true;
  }

  @Override
  public boolean update(boolean force) {
    instance.setBlock(x, y, z, state, true);
    return true;
  }

  @Override
  public boolean update(boolean force, boolean applyPhysics) {
    instance.setBlock(x, y, z, state, applyPhysics);
    return true;
  }

  @Override
  public @NotNull TileBlockState copy() {
    return new MinestomTileBlockStateWrapper(instance, x, y, z, state);
  }

  @Override
  public boolean isPlaced() {
    return instance.getBlock(x, y, z) != null;
  }

  @Override
  public @NotNull PersistentDataContainer persistentData() {
    throw new UnsupportedCapabilityException(Capability.PERSISTENT_DATA);
  }
}
