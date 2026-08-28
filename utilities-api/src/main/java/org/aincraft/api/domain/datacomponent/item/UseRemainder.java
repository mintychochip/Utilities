package org.aincraft.api.domain.datacomponent.item;

import org.aincraft.api.domain.inventory.ItemStack;

/** Common contract for use-remainder, mirroring Paper's {@code UseRemainder}. */
public interface UseRemainder {

  ItemStack transformInto();
}
