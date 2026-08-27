package org.aincraft.common.datacomponent.item;

import org.aincraft.common.inventory.ItemStack;

import java.util.List;

/** Common contract for container contents, mirroring Paper's {@code ItemContainerContents}. */
public interface ItemContainerContents {

  List<ItemStack> contents();
}
