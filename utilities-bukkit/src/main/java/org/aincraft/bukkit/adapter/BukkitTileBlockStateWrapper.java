package org.aincraft.bukkit.adapter;

import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.aincraft.api.domain.block.BlockState;
import org.aincraft.api.domain.block.TileBlockState;
import org.aincraft.api.domain.persistence.PersistentDataContainer;
import org.aincraft.api.domain.world.Block;
import org.aincraft.bukkit.persistence.BukkitPersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class BukkitTileBlockStateWrapper implements TileBlockState {

  private final org.bukkit.block.BlockState state;

  public BukkitTileBlockStateWrapper(@NotNull org.bukkit.block.BlockState state) {
    this.state = Objects.requireNonNull(state, "state cannot be null");
  }

  public @NotNull org.bukkit.block.BlockState getBukkitBlockState() {
    return state;
  }

  @Override
  public @NotNull Block block() {
    return BukkitAdapters.adapt(state.getBlock());
  }

  @Override
  public @NotNull BlockState blockData() {
    return BukkitAdapters.adapt(state.getBlockData());
  }

  @Override
  public boolean update() {
    return state.update();
  }

  @Override
  public boolean update(boolean force) {
    return state.update(force);
  }

  @Override
  public boolean update(boolean force, boolean applyPhysics) {
    return state.update(force, applyPhysics);
  }

  @Override
  public @NotNull TileBlockState copy() {
    return new BukkitTileBlockStateWrapper(state.copy());
  }

  @Override
  public boolean isPlaced() {
    return state.isPlaced();
  }

  @Override
  public @NotNull PersistentDataContainer persistentData() {
    if (!(state instanceof org.bukkit.block.TileState tileState)) {
      throw new UnsupportedCapabilityException(Capability.PERSISTENT_DATA);
    }
    return new BukkitPersistentDataContainer(tileState.getPersistentDataContainer());
  }
}
