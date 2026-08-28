package org.aincraft.paper.adapter;

import org.aincraft.api.domain.effect.PotionEffect;
import org.aincraft.api.domain.effect.PotionEffectType;
import org.aincraft.bukkit.adapter.BukkitPotionEffectWrapper;
import org.jetbrains.annotations.NotNull;

public final class PaperPotionEffectWrapper extends BukkitPotionEffectWrapper {

  public PaperPotionEffectWrapper(@NotNull org.bukkit.potion.PotionEffect effect) {
    super(effect);
  }

  @Override
  public @NotNull PotionEffectType type() {
    return PaperAdapters.adapt(getBukkitPotionEffect().getType());
  }

  @Override
  public @NotNull PotionEffect withDuration(int duration) {
    org.bukkit.potion.PotionEffect effect = getBukkitPotionEffect();
    return new PaperPotionEffectWrapper(
        new org.bukkit.potion.PotionEffect(
            effect.getType(),
            duration,
            effect.getAmplifier(),
            effect.isAmbient(),
            effect.hasParticles(),
            effect.hasIcon()));
  }

  @Override
  public @NotNull PotionEffect withAmplifier(int amplifier) {
    org.bukkit.potion.PotionEffect effect = getBukkitPotionEffect();
    return new PaperPotionEffectWrapper(
        new org.bukkit.potion.PotionEffect(
            effect.getType(),
            effect.getDuration(),
            amplifier,
            effect.isAmbient(),
            effect.hasParticles(),
            effect.hasIcon()));
  }
}
