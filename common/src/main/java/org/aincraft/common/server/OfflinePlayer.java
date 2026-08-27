package org.aincraft.common.server;

import net.kyori.adventure.identity.Identified;
import org.aincraft.common.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface OfflinePlayer extends Identified {

  @NotNull
  UUID uniqueId();

  @Nullable
  String name();

  boolean hasPlayedBefore();

  boolean isOnline();

  @Nullable
  Player player();

  long lastPlayed();

  boolean isWhitelisted();

  void setWhitelisted(boolean whitelisted);

  boolean isBanned();

  boolean isOp();

  void setOp(boolean op);
}
