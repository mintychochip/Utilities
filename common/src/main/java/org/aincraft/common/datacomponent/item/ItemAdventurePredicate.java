package org.aincraft.common.datacomponent.item;

import org.aincraft.common.datacomponent.block.BlockPredicate;

import java.util.List;

/**
 * Common contract for an item adventure predicate, mirroring Paper's {@code
 * ItemAdventurePredicate}.
 */
public interface ItemAdventurePredicate {

  List<BlockPredicate> predicates();
}
