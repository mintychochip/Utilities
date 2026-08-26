package org.aincraft.common.entity;

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

public interface Player extends Entity, Audience {

  @NotNull String username();

  boolean isOnline();
}
