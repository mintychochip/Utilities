package org.aincraft.common.datacomponent.block;

import net.kyori.adventure.key.Key;

import java.util.Set;

/** Common contract for a block predicate, mirroring Paper's {@code BlockPredicate}. */
public interface BlockPredicate {

  Set<Key> blocks();
}
