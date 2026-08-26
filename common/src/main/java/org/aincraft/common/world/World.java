package org.aincraft.common.world;

import java.util.Collection;
import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.aincraft.common.entity.Entity;
import org.aincraft.common.entity.Player;
import org.aincraft.common.location.Location;
import org.aincraft.common.location.Position;
import org.jetbrains.annotations.NotNull;

public interface World extends Keyed, Identified, Audience {

  @NotNull UUID uid();

  @NotNull String name();

  @Override
  default @NotNull Identity identity() {
    return Identity.identity(uid());
  }

  @NotNull Block getBlockAt(int x, int y, int z);

  default @NotNull Block getBlockAt(@NotNull Position position) {
    return getBlockAt(position.blockX(), position.blockY(), position.blockZ());
  }
  default @NotNull Block getBlockAt(@NotNull Location location) {
    return getBlockAt(location.position());
  }
  @NotNull Chunk getChunkAt(int chunkX, int chunkZ);

  default @NotNull Chunk getChunkAt(@NotNull Block block) {
    return getChunkAt(block.x() >> 4, block.z() >> 4);
  }

  default @NotNull Chunk getChunkAt(@NotNull Position position) {
    return getChunkAt(position.blockX() >> 4, position.blockZ() >> 4);
  }
  default @NotNull Chunk getChunkAt(@NotNull Location location) {
    return getChunkAt(location.blockX() >> 4, location.blockZ() >> 4);
  }
  boolean isChunkLoaded(int chunkX, int chunkZ);

  default boolean isChunkLoaded(@NotNull Chunk chunk) {
    return isChunkLoaded(chunk.x(), chunk.z());
  }

  int minHeight();

  int maxHeight();

  @NotNull WorldBorder worldBorder();

  @NotNull Environment environment();

  @NotNull Difficulty difficulty();

  long time();

  long fullTime();
  @NotNull Collection<? extends Player> players();

  @NotNull Collection<? extends Entity> entities();

  @NotNull Collection<? extends Chunk> loadedChunks();
}
