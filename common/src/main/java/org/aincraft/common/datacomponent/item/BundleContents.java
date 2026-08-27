package org.aincraft.common.datacomponent.item;

import org.aincraft.common.inventory.ItemStack;

import java.util.List;

/**
 * Common contract for bundle contents, mirroring Paper's {@code BundleContents} without depending
 * on Bukkit.
 */
public interface BundleContents {

  List<ItemStack> contents();
}
