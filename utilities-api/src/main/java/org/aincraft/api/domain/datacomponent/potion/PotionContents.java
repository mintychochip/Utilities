package org.aincraft.api.domain.datacomponent.potion;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.TextColor;

import java.util.List;

/**
 * Common contract for potion contents, mirroring Paper's {@code PotionContents} without depending
 * on Bukkit.
 */
public interface PotionContents {

  Key potion();

  TextColor customColor();

  List<PotionEffect> customEffects();

  String customName();

  List<PotionEffect> allEffects();

  TextColor computeEffectiveColor();
}
