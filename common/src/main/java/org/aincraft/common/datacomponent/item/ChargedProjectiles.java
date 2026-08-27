package org.aincraft.common.datacomponent.item;

import org.aincraft.common.inventory.ItemStack;

import java.util.List;

/** Common contract for charged projectiles, mirroring Paper's {@code ChargedProjectiles}. */
public interface ChargedProjectiles {

  List<ItemStack> projectiles();
}
