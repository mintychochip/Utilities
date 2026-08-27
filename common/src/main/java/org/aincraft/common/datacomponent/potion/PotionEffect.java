package org.aincraft.common.datacomponent.potion;

import net.kyori.adventure.key.Key;

/**
 * Common contract for a potion effect, mirroring {@code org.bukkit.potion.PotionEffect} without
 * depending on Bukkit.
 */
public interface PotionEffect {

  Key type();

  int duration();

  int amplifier();

  boolean ambient();

  boolean particles();

  boolean icon();
}
