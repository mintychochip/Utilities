package org.aincraft.common.datacomponent.item;

import org.aincraft.common.inventory.ItemStack;

import java.util.List;

/**
 * Common contract for decorated pot contents, mirroring Paper's {@code
 * io.papermc.paper.datacomponent.item.PotDecorations}.
 */
public interface PotDecorations {

  List<ItemStack> decorations();
}
