package org.aincraft.common.datacomponent.block;

import java.util.Set;
import net.kyori.adventure.key.Key;

/**
 * Common contract for a block predicate, mirroring Paper's {@code BlockPredicate}.
 */
public interface BlockPredicate {

  Set<Key> blocks();
}
