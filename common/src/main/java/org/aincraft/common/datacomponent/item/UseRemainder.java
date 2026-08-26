package org.aincraft.common.datacomponent.item;

import org.aincraft.common.inventory.ItemStack;

/**
 * Common contract for use-remainder, mirroring Paper's {@code UseRemainder}.
 */
public interface UseRemainder {

  ItemStack transformInto();
}
