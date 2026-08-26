package org.aincraft.common.entity;

import java.util.UUID;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.aincraft.common.location.Location;
import org.aincraft.common.world.World;
import org.jetbrains.annotations.NotNull;

public interface Entity extends Keyed, Identified {

  @NotNull UUID uniqueId();

  @NotNull World world();

  @NotNull Location<World> location();

  @NotNull Key type();

  boolean isValid();

  @Override
  default @NotNull Identity identity() {
    return Identity.identity(uniqueId());
  }

  @Override
  default @NotNull Key key() {
    return type();
  }
}
