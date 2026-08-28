package org.aincraft.api.domain.datacomponent.item;

import org.aincraft.api.domain.inventory.ItemStack;

import java.util.List;

/** Common contract for container contents, mirroring Paper's {@code ItemContainerContents}. */
public interface ItemContainerContents {

  List<ItemStack> contents();
}
