package org.aincraft.api.domain.datacomponent.item;

import org.aincraft.api.domain.block.BlockState;
import org.aincraft.api.domain.block.BlockType;

/**
 * Common contract for block item data properties, mirroring Paper's {@code
 * BlockItemDataProperties}.
 */
public interface BlockItemDataProperties {

  BlockState createBlockState(BlockType type);

  BlockState applyTo(BlockState state);
}
