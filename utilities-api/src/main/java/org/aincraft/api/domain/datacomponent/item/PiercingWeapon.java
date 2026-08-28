package org.aincraft.api.domain.datacomponent.item;

import net.kyori.adventure.key.Key;

/**
 * Common contract for the {@code PiercingWeapon} data component, mirroring Paper's {@code
 * PiercingWeapon}.
 */
public interface PiercingWeapon {

  boolean dealsKnockback();

  boolean dismounts();

  Key sound();

  Key hitSound();
}
