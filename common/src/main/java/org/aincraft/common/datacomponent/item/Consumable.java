package org.aincraft.common.datacomponent.item;

import java.util.List;
import net.kyori.adventure.key.Key;

/**
 * Common contract for consumable properties, mirroring Paper's {@code Consumable}.
 */
public interface Consumable {

  float consumeSeconds();

  ItemUseAnimation animation();

  Key sound();

  boolean hasConsumeParticles();

  List<ConsumeEffect> consumeEffects();

  /**
   * Common contract for item use animations, mirroring Paper's {@code ItemUseAnimation}.
   */
  enum ItemUseAnimation {

    NONE,
    EAT,
    DRINK,
    BLOCK,
    BOW,
    TRIDENT,
    CROSSBOW,
    SPYGLASS,
    TOOT_HORN,
    BRUSH,
    BUNDLE,
    SPEAR
  }
}
