package org.aincraft.common.datacomponent.item;

import java.util.List;
import org.aincraft.common.inventory.ItemStack;

/**
 * Common contract for bundle contents, mirroring Paper's {@code BundleContents}
 * without depending on Bukkit.
 */
public interface BundleContents {

  List<ItemStack> contents();
}
