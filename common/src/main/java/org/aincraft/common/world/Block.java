package org.aincraft.common.world;

import net.kyori.adventure.key.Key;
import org.aincraft.common.block.BlockFace;
import org.aincraft.common.block.BlockState;
import org.aincraft.common.block.BlockType;
import org.aincraft.common.location.BoundingBox;
import org.aincraft.common.location.Location;
import org.aincraft.common.location.Position;
import org.jetbrains.annotations.NotNull;
public interface Block {

  int x();

  int y();

  int z();

  @NotNull World world();

  @NotNull Chunk chunk();
  @NotNull Location location();
  @NotNull Position position();

  @NotNull BlockType type();

  @NotNull BlockState state();

  default @NotNull Key key() {
    return type().key();
  }

  default @NotNull Block relative(int modX, int modY, int modZ) {
    return world().getBlockAt(x() + modX, y() + modY, z() + modZ);
  }

  default @NotNull Block relative(@NotNull BlockFace face) {
    return relative(face.modX(), face.modY(), face.modZ());
  }

  default @NotNull Block relative(@NotNull BlockFace face, int distance) {
    return relative(face.modX() * distance, face.modY() * distance, face.modZ() * distance);
  }

  boolean isEmpty();

  boolean isLiquid();

  boolean isSolid();

  boolean isAir();

  boolean isPassable();

  @NotNull BoundingBox boundingBox();

  @NotNull Key biome();

  void setBiome(@NotNull Key biome);
}
