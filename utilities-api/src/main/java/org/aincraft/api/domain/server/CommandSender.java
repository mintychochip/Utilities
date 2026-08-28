package org.aincraft.api.domain.server;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identified;
import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.jetbrains.annotations.NotNull;

public interface CommandSender extends Audience, Identified {

  @NotNull
  String name();

  boolean hasPermission(@NotNull String permission);

  boolean isOp();

  void setOp(boolean op);

  /** Returns the server associated with this sender. */
  default @NotNull Server server() {
    throw new UnsupportedCapabilityException(Capability.SERVER_INFO);
  }
}
