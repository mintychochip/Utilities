package org.aincraft.common.effect;

import net.kyori.adventure.key.Keyed;
import org.aincraft.common.inventory.ItemStack;

public interface Enchantment extends Keyed {

  int maxLevel();

  int startLevel();

  boolean isCursed();

  boolean isTreasure();

  boolean conflictsWith(Enchantment other);

  boolean canEnchant(ItemStack item);
}
