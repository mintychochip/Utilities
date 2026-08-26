package org.aincraft.common.datacomponent.item;

import org.aincraft.common.inventory.ItemStack;

/**
 * Common contract for a sulfur cube's absorbed item, mirroring Paper's {@code SulfurCubeContent}.
 */
public interface SulfurCubeContent {

  ItemStack absorbedItem();
}
