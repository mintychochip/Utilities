package org.aincraft.paper.adapter;

import org.aincraft.api.domain.inventory.ItemStack;
import org.aincraft.bukkit.adapter.BukkitAdapters;
import org.aincraft.bukkit.adapter.BukkitBlockStateWrapper;
import org.jetbrains.annotations.NotNull;

/** Paper block-data metadata bridge. */
public final class PaperBlockStateWrapper extends BukkitBlockStateWrapper {

  public PaperBlockStateWrapper(@NotNull org.bukkit.block.data.BlockData blockData) {
    super(blockData);
  }

  @Override
  public @NotNull org.aincraft.api.domain.block.BlockState copy() {
    return new PaperBlockStateWrapper(getBukkitBlockData().clone());
  }

  @Override
  public @NotNull org.aincraft.api.domain.block.BlockState merge(
      @NotNull org.aincraft.api.domain.block.BlockState other) {
    return new PaperBlockStateWrapper(
        getBukkitBlockData().merge(org.aincraft.bukkit.adapter.BukkitAdapters.toBukkit(other)));
  }

  @Override
  public boolean isReplaceable() {
    return getBukkitBlockData().isReplaceable();
  }

  @Override
  public boolean isRandomlyTicked() {
    return getBukkitBlockData().isRandomlyTicked();
  }

  @Override
  public float destroySpeed(@NotNull ItemStack tool) {
    return getBukkitBlockData().getDestroySpeed(BukkitAdapters.toBukkit(tool));
  }
}
