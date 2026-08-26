package org.aincraft.common.effect;

import net.kyori.adventure.key.Keyed;

public interface Enchantment extends Keyed {

  int maxLevel();

  int startLevel();

  boolean isCursed();

  boolean isTreasure();
}
