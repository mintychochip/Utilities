package org.aincraft.bukkit.adapter;

import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.aincraft.api.domain.block.BlockFace;
import org.aincraft.api.domain.block.BlockState;
import org.aincraft.api.domain.block.BlockSupport;
import org.aincraft.api.domain.block.BlockType;
import org.aincraft.api.domain.block.PistonMoveReaction;
import org.aincraft.api.domain.inventory.ItemStack;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BukkitBlockStateWrapper implements BlockState {

  private final BlockData blockData;
  private final BlockType type;

  public BukkitBlockStateWrapper(@NotNull BlockData blockData) {
    this.blockData = Objects.requireNonNull(blockData, "blockData cannot be null");
    this.type = new BukkitBlockTypeWrapper(blockData.getMaterial());
  }

  public @NotNull BlockData getBukkitBlockData() {
    return blockData;
  }

  @Override
  public @NotNull BlockType type() {
    return type;
  }

  @Override
  public @NotNull String asString() {
    return blockData.getAsString();
  }

  @Override
  public @NotNull BlockState copy() {
    return new BukkitBlockStateWrapper(blockData.clone());
  }

  @Override
  public @NotNull BlockState merge(@NotNull BlockState other) {
    return new BukkitBlockStateWrapper(blockData.merge(BukkitAdapters.toBukkit(other)));
  }

  @Override
  public boolean matches(@NotNull BlockState other) {
    return blockData.matches(BukkitAdapters.toBukkit(other));
  }

  @Override
  public int lightEmission() {
    return blockData.getLightEmission();
  }

  @Override
  public boolean isOccluding() {
    return blockData.isOccluding();
  }

  @Override
  public boolean requiresCorrectToolForDrops() {
    return blockData.requiresCorrectToolForDrops();
  }

  @Override
  public boolean isReplaceable() {
    throw new UnsupportedCapabilityException(
        Capability.BLOCK_QUERY, "Spigot BlockData does not expose replaceability metadata.");
  }

  @Override
  public boolean isRandomlyTicked() {
    throw new UnsupportedCapabilityException(
        Capability.BLOCK_QUERY, "Spigot BlockData does not expose random-tick metadata.");
  }

  @Override
  public float destroySpeed(@NotNull ItemStack tool) {
    throw new UnsupportedCapabilityException(
        Capability.BLOCK_QUERY, "Spigot BlockData does not expose destroy speed for a tool.");
  }

  @Override
  public boolean isFaceSturdy(@NotNull BlockFace face, @NotNull BlockSupport support) {
    return blockData.isFaceSturdy(
        BukkitAdapters.toBukkit(face), org.bukkit.block.BlockSupport.valueOf(support.name()));
  }

  @Override
  public @NotNull PistonMoveReaction pistonMoveReaction() {
    return PistonMoveReaction.valueOf(blockData.getPistonMoveReaction().name());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BlockState that)) return false;
    return Objects.equals(asString(), that.asString());
  }

  @Override
  public int hashCode() {
    return blockData.hashCode();
  }

  @Override
  public String toString() {
    return asString();
  }
}
