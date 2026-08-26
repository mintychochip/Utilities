package org.aincraft.common.datacomponent.item;

import java.util.List;
import org.aincraft.common.inventory.ItemStack;

/**
 * Common contract for container contents, mirroring Paper's {@code ItemContainerContents}.
 */
public interface ItemContainerContents {

  List<ItemStack> contents();
}
