package org.aincraft.common.datacomponent.potion;

import java.util.List;

/**
 * Common contract for suspicious stew effects, mirroring Paper's {@code SuspiciousStewEffects}
 * without depending on Bukkit.
 */
public interface SuspiciousStewEffects {

  List<SuspiciousEffectEntry> effects();
}
