package org.aincraft.api.domain.datacomponent.item;

/** Common contract for food properties, mirroring Paper's {@code FoodProperties}. */
public interface FoodProperties {

  int nutrition();

  float saturation();

  boolean canAlwaysEat();
}
