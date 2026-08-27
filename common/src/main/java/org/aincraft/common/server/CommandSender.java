package org.aincraft.common.server;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identified;
import org.jetbrains.annotations.NotNull;

public interface CommandSender extends Audience, Identified {

  @NotNull
  String name();

  boolean hasPermission(@NotNull String permission);

  boolean isOp();

  void setOp(boolean op);
}
