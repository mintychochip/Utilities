package org.aincraft.api.domain.datacomponent.item;

import java.util.Set;
import java.util.UUID;

/**
 * Common contract for a player profile, mirroring {@code
 * com.destroystokyo.paper.profile.PlayerProfile}.
 */
public interface PlayerProfile {

  UUID uuid();

  String name();

  Set<Property> properties();

  /**
   * Common contract for a profile property, mirroring {@code
   * com.destroystokyo.paper.profile.ProfileProperty}.
   */
  interface Property {

    String name();

    String value();

    String signature();
  }
}
