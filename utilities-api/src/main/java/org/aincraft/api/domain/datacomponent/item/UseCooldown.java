package org.aincraft.api.domain.datacomponent.item;

import net.kyori.adventure.key.Key;

/** Common contract for use cooldown, mirroring Paper's {@code UseCooldown}. */
public interface UseCooldown {

  float seconds();

  Key cooldownGroup();
}
