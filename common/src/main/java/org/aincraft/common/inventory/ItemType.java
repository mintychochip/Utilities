package org.aincraft.common.inventory;

import net.kyori.adventure.key.Keyed;

public interface ItemType extends Keyed {

  int maxStackSize();

  int maxDurability();

  boolean isBlock();

  boolean isAir();

  boolean isItem();

  boolean isEdible();
}
