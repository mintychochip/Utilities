package org.aincraft.api.domain.datacomponent.item;

import org.aincraft.api.domain.inventory.ItemStack;

import java.util.List;

/**
 * Common contract for decorated pot contents, mirroring Paper's {@code
 * io.papermc.paper.datacomponent.item.PotDecorations}.
 */
public interface PotDecorations {

  List<ItemStack> decorations();
}
