package org.aincraft.common.datacomponent.item.blocksattacks;

import java.util.Set;
import net.kyori.adventure.key.Key;

/**
 * Common contract for a damage reduction, mirroring Paper's {@code DamageReduction}.
 */
public interface DamageReduction {

  Set<Key> type();

  float horizontalBlockingAngle();

  float base();

  float factor();
}
