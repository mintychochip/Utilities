package org.aincraft.minestom.adapter;

import org.aincraft.api.domain.block.BlockState;
import org.aincraft.api.domain.block.BlockType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class MinestomBlockStateWrapper implements BlockState {

  private final net.minestom.server.instance.block.Block block;
  private final BlockType type;
  private final String stateString;

  public MinestomBlockStateWrapper(@NotNull net.minestom.server.instance.block.Block block) {
    this.block = Objects.requireNonNull(block, "block cannot be null");
    this.type = new MinestomBlockTypeWrapper(block);
    this.stateString = block.state() != null ? block.state() : block.name();
  }

  public @NotNull net.minestom.server.instance.block.Block getMinestomBlock() {
    return block;
  }

  @Override
  public @NotNull BlockType type() {
    return type;
  }

  @Override
  public @NotNull String asString() {
    return stateString;
  }

  private net.minestom.server.instance.block.Block parsedBlock() {
    net.minestom.server.instance.block.Block parsed =
        net.minestom.server.instance.block.Block.fromState(stateString);
    if (parsed == null) {
      throw new IllegalStateException("Cannot resolve block state: " + stateString);
    }
    return parsed;
  }

  @Override
  public @NotNull BlockState copy() {
    return new MinestomBlockStateWrapper(parsedBlock());
  }

  @Override
  public @NotNull BlockState merge(@NotNull BlockState other) {
    Objects.requireNonNull(other, "other cannot be null");
    net.minestom.server.instance.block.Block otherBlock = MinestomAdapters.toMinestom(other);
    return new MinestomBlockStateWrapper(parsedBlock().withProperties(otherBlock.properties()));
  }

  @Override
  public boolean matches(@NotNull BlockState other) {
    Objects.requireNonNull(other, "other cannot be null");
    return parsedBlock().compare(MinestomAdapters.toMinestom(other));
  }

  @Override
  public int lightEmission() {
    return block.lightEmission();
  }

  @Override
  public boolean isOccluding() {
    return block.occludes();
  }

  @Override
  public boolean requiresCorrectToolForDrops() {
    return block.requiresTool();
  }

  @Override
  public boolean isReplaceable() {
    return block.replaceable();
  }

  @Override
  public boolean isRandomlyTicked() {
    return block.handler() != null && block.handler().isTickable();
  }

  @Override
  public float destroySpeed(@NotNull org.aincraft.api.domain.inventory.ItemStack tool) {
    Objects.requireNonNull(tool, "tool cannot be null");
    return block.hardness();
  }

  @Override
  public boolean isFaceSturdy(
      @NotNull org.aincraft.api.domain.block.BlockFace face,
      @NotNull org.aincraft.api.domain.block.BlockSupport support) {
    return block.collisionShape().isFaceFull(MinestomAdapters.toMinestom(face));
  }

  @Override
  public @NotNull org.aincraft.api.domain.block.PistonMoveReaction pistonMoveReaction() {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.PISTON_REACTION,
        "Minestom does not expose piston move reactions on its public block API.");
  }

  @Override
  public boolean equals(Object o) {
    return this == o
        || (o instanceof BlockState that
            && Objects.equals(type(), that.type())
            && Objects.equals(asString(), that.asString()));
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), asString());
  }

  @Override
  public String toString() {
    return "MinestomBlockStateWrapper{" + asString() + "}";
  }
}
