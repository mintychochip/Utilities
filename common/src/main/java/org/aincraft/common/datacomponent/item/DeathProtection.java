package org.aincraft.common.datacomponent.item;

import java.util.List;

/**
 * Common contract for death protection item properties, mirroring Paper's {@code DeathProtection}.
 */
public interface DeathProtection {

  List<ConsumeEffect> deathEffects();
}
