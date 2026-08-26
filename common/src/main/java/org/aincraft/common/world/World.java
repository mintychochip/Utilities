package org.aincraft.common.world;

import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.key.Keyed;
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

  default @NotNull Block getBlockAt(@NotNull Location<?> location) {
    return getBlockAt(location.position());
  }

  @NotNull Chunk getChunkAt(int chunkX, int chunkZ);

  boolean isChunkLoaded(int chunkX, int chunkZ);

  int minHeight();

  int maxHeight();
}
