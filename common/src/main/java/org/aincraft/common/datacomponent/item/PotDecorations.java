package org.aincraft.common.datacomponent.item;

import java.util.List;
import org.aincraft.common.inventory.ItemStack;

/**
 * Common contract for decorated pot contents, mirroring Paper's
 * {@code io.papermc.paper.datacomponent.item.PotDecorations}.
 */
public interface PotDecorations {

  List<ItemStack> decorations();
}
