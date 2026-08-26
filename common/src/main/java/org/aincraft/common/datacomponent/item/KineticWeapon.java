package org.aincraft.common.datacomponent.item;

import net.kyori.adventure.key.Key;

/**
 * Common contract for the {@code KineticWeapon} data component, mirroring Paper's {@code KineticWeapon}.
 */
public interface KineticWeapon {

  int contactCooldownTicks();

  int delayTicks();

  Condition dismountConditions();

  Condition knockbackConditions();

  Condition damageConditions();

  float forwardMovement();

  float damageMultiplier();

  Key sound();

  Key hitSound();

  /**
   * Common contract for a kinetic-weapon condition, mirroring Paper's {@code KineticWeapon.Condition}.
   */
  interface Condition {

    int maxDurationTicks();

    float minSpeed();

    float minRelativeSpeed();
  }
}
