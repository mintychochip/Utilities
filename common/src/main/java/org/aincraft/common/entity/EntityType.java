package org.aincraft.common.entity;

import net.kyori.adventure.key.Keyed;

public interface EntityType extends Keyed {

  boolean isAlive();

  boolean isSpawnable();
}
