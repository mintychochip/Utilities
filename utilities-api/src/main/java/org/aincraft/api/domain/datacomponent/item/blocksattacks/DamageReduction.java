package org.aincraft.api.domain.datacomponent.item.blocksattacks;

import net.kyori.adventure.key.Key;

import java.util.Set;

/** Common contract for a damage reduction, mirroring Paper's {@code DamageReduction}. */
public interface DamageReduction {

  Set<Key> type();

  float horizontalBlockingAngle();

  float base();

  float factor();
}
