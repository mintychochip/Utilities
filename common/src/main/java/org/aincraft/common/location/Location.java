package org.aincraft.common.location;

import org.aincraft.common.world.Block;
import org.aincraft.common.world.Chunk;
import org.aincraft.common.world.World;
import org.jetbrains.annotations.NotNull;

public interface Location<W extends World> {

  @NotNull W world();

  @NotNull Position position();

  float yaw();

  float pitch();

  default double x() {
    return position().x();
  }

  default double y() {
    return position().y();
  }

  default double z() {
    return position().z();
  }

  default int blockX() {
    return position().blockX();
  }

  default int blockY() {
    return position().blockY();
  }

  default int blockZ() {
    return position().blockZ();
  }

  default double distanceSquared(@NotNull Location<?> other) {
    return position().distanceSquared(other.position());
  }

  default double distance(@NotNull Location<?> other) {
    return position().distance(other.position());
  }

  default @NotNull Block block() {
    return world().getBlockAt(blockX(), blockY(), blockZ());
  }

  default @NotNull Chunk chunk() {
    return world().getChunkAt(blockX() >> 4, blockZ() >> 4);
  }
}
