package org.aincraft.common.effect;

import org.jetbrains.annotations.NotNull;

public interface PotionEffect {

  @NotNull PotionEffectType type();

  int duration();

  int amplifier();

  boolean isAmbient();

  boolean hasParticles();

  boolean hasIcon();
}
