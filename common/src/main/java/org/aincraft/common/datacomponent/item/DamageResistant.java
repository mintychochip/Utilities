package org.aincraft.common.datacomponent.item;

import java.util.Set;
import net.kyori.adventure.key.Key;

/**
 * Common contract for damage resistance, mirroring Paper's {@code DamageResistant}.
 */
public interface DamageResistant {

  Set<Key> types();
}
