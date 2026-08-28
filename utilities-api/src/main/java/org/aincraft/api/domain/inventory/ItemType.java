package org.aincraft.api.domain.inventory;

import net.kyori.adventure.key.Keyed;

public interface ItemType extends Keyed {

  int maxStackSize();

  int maxDurability();

  boolean isBlock();

  boolean isAir();

  boolean isItem();

  boolean isEdible();
}
