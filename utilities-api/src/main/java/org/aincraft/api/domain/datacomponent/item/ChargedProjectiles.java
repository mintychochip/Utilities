package org.aincraft.api.domain.datacomponent.item;

import org.aincraft.api.domain.inventory.ItemStack;

import java.util.List;

/** Common contract for charged projectiles, mirroring Paper's {@code ChargedProjectiles}. */
public interface ChargedProjectiles {

  List<ItemStack> projectiles();
}
