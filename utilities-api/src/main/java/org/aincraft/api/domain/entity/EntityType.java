package org.aincraft.api.domain.entity;

import net.kyori.adventure.key.Keyed;

public interface EntityType extends Keyed {

  boolean isAlive();

  boolean isSpawnable();
}
