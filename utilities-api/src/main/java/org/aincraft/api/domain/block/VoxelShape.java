package org.aincraft.api.domain.block;

import org.aincraft.api.domain.location.BoundingBox;
import org.jetbrains.annotations.NotNull;

/**
 * A collision shape for block interaction queries, backed by an AABB union. Mirrors Bukkit's {@code
 * VoxelShape} / Minestom's equivalent.
 */
public interface VoxelShape {

  @NotNull
  BoundingBox boundingBox();

  boolean isEmpty();

  boolean contains(double x, double y, double z);
}
