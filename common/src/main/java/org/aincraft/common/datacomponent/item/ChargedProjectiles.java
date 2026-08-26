package org.aincraft.common.datacomponent.item;

import java.util.List;
import org.aincraft.common.inventory.ItemStack;

/**
 * Common contract for charged projectiles, mirroring Paper's {@code ChargedProjectiles}.
 */
public interface ChargedProjectiles {

  List<ItemStack> projectiles();
}
