package org.aincraft.common.effect;

import org.jetbrains.annotations.NotNull;

public interface PotionEffect {

  @NotNull
  PotionEffectType type();

  int duration();

  int amplifier();

  boolean isAmbient();

  boolean hasParticles();

  boolean hasIcon();

  boolean isInfinite();

  @NotNull
  PotionEffect withDuration(int duration);

  @NotNull
  PotionEffect withAmplifier(int amplifier);
}
