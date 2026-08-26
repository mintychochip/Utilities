package org.aincraft.common.datacomponent.item;

/**
 * Common contract for weapon properties, mirroring Paper's {@code Weapon}.
 */
public interface Weapon {

  int itemDamagePerAttack();

  float disableBlockingForSeconds();
}
