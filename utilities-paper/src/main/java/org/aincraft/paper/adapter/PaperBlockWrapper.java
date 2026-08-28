package org.aincraft.paper.adapter;

import org.aincraft.api.domain.block.BlockState;
import org.aincraft.api.domain.inventory.ItemStack;
import org.aincraft.api.domain.world.World;
import org.aincraft.bukkit.adapter.BukkitAdapters;
import org.aincraft.bukkit.adapter.BukkitBlockWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Paper block wrapper exposing Paper-only block predicates. */
public class PaperBlockWrapper extends BukkitBlockWrapper {

  public PaperBlockWrapper(@NotNull org.bukkit.block.Block block) {
    super(block);
  }

  @Override
  public boolean breakNaturally() {
    return getBukkitBlock().breakNaturally();
  }

  @Override
  public boolean breakNaturally(@Nullable ItemStack tool) {
    return tool == null
        ? getBukkitBlock().breakNaturally()
        : getBukkitBlock().breakNaturally(BukkitAdapters.toBukkit(tool));
  }

  @Override
  public boolean canPlace(@NotNull BlockState state) {
    return getBukkitBlock().canPlace(BukkitAdapters.toBukkit(state));
  }

  public @NotNull World world() {
    return PaperAdapters.adapt(getBukkitBlock().getWorld());
  }

  @Override
  public @NotNull BlockState state() {
    return new PaperBlockStateWrapper(getBukkitBlock().getBlockData());
  }

  @Override
  public boolean isReplaceable() {
    return getBukkitBlock().isReplaceable();
  }

  @Override
  public boolean isCollidable() {
    return getBukkitBlock().isCollidable();
  }

  @Override
  public boolean isBuildable() {
    return getBukkitBlock().isBuildable();
  }

  @Override
  public boolean isBurnable() {
    return getBukkitBlock().isBurnable();
  }

  @Override
  public boolean isSuffocating() {
    return getBukkitBlock().isSuffocating();
  }
}
