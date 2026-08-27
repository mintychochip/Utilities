package org.aincraft.common.datacomponent.item;

import org.aincraft.common.block.BlockState;
import org.aincraft.common.block.BlockType;

/**
 * Common contract for block item data properties, mirroring Paper's {@code
 * BlockItemDataProperties}.
 */
public interface BlockItemDataProperties {

  BlockState createBlockState(BlockType type);

  BlockState applyTo(BlockState state);
}
