package org.aincraft.api.domain.block;

import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.aincraft.api.domain.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface BlockState {

  @NotNull
  BlockType type();

  @NotNull
  String asString();

  /** Returns an independent copy of this block data. */
  default @NotNull BlockState copy() {
    throw new UnsupportedCapabilityException(Capability.BLOCK_QUERY);
  }

  /** Merges compatible properties from another block data instance. */
  default @NotNull BlockState merge(@NotNull BlockState other) {
    throw new UnsupportedCapabilityException(Capability.BLOCK_QUERY);
  }

  /** Tests whether another state matches this state. */
  default boolean matches(@NotNull BlockState other) {
    throw new UnsupportedCapabilityException(Capability.BLOCK_QUERY);
  }

  default int lightEmission() {
    throw new UnsupportedCapabilityException(Capability.BLOCK_QUERY);
  }

  default boolean isOccluding() {
    throw new UnsupportedCapabilityException(Capability.BLOCK_QUERY);
  }

  default boolean requiresCorrectToolForDrops() {
    throw new UnsupportedCapabilityException(Capability.BLOCK_QUERY);
  }

  default boolean isReplaceable() {
    throw new UnsupportedCapabilityException(Capability.BLOCK_QUERY);
  }

  default boolean isRandomlyTicked() {
    throw new UnsupportedCapabilityException(Capability.BLOCK_QUERY);
  }

  default float destroySpeed(@NotNull ItemStack tool) {
    throw new UnsupportedCapabilityException(Capability.BLOCK_QUERY);
  }

  default boolean isFaceSturdy(@NotNull BlockFace face, @NotNull BlockSupport support) {
    throw new UnsupportedCapabilityException(Capability.BLOCK_SUPPORT);
  }

  default @NotNull PistonMoveReaction pistonMoveReaction() {
    throw new UnsupportedCapabilityException(Capability.PISTON_REACTION);
  }
}
