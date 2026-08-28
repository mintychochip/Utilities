package org.aincraft.api.domain.datacomponent.item;

import org.aincraft.api.domain.inventory.ItemStack;

/**
 * Common contract for a sulfur cube's absorbed item, mirroring Paper's {@code SulfurCubeContent}.
 */
public interface SulfurCubeContent {

  ItemStack absorbedItem();
}
