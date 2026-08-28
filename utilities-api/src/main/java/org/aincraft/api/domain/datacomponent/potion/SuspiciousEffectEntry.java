package org.aincraft.api.domain.datacomponent.potion;

import net.kyori.adventure.key.Key;

/**
 * Common contract for a suspicious stew effect entry, mirroring Paper's {@code
 * SuspiciousEffectEntry} without depending on Bukkit.
 */
public interface SuspiciousEffectEntry {

  Key effect();

  int duration();
}
