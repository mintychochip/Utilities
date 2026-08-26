package org.aincraft.common.datacomponent.item;

import java.util.List;
import org.aincraft.common.datacomponent.block.BlockPredicate;

/**
 * Common contract for an item adventure predicate, mirroring Paper's {@code ItemAdventurePredicate}.
 */
public interface ItemAdventurePredicate {

  List<BlockPredicate> predicates();
}
