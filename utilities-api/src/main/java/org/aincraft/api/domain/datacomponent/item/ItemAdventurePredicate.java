package org.aincraft.api.domain.datacomponent.item;

import org.aincraft.api.domain.datacomponent.block.BlockPredicate;

import java.util.List;

/**
 * Common contract for an item adventure predicate, mirroring Paper's {@code
 * ItemAdventurePredicate}.
 */
public interface ItemAdventurePredicate {

  List<BlockPredicate> predicates();
}
