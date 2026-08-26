package org.aincraft.common.effect;

import org.aincraft.common.inventory.ItemStack;
import net.kyori.adventure.key.Keyed;

public interface Enchantment extends Keyed {

  int maxLevel();

  int startLevel();

  boolean isCursed();

  boolean isTreasure();

  boolean conflictsWith(Enchantment other);

  boolean canEnchant(ItemStack item);
}
