package org.aincraft.common.datacomponent.item;

import net.kyori.adventure.key.Key;

import java.util.Set;

/** Common contract for damage resistance, mirroring Paper's {@code DamageResistant}. */
public interface DamageResistant {

  Set<Key> types();
}
