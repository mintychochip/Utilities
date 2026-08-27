package org.aincraft.bukkit.adapter;

import org.aincraft.common.effect.PotionEffect;
import org.aincraft.common.effect.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class BukkitPotionEffectWrapper implements PotionEffect {

  private final org.bukkit.potion.PotionEffect effect;

  public BukkitPotionEffectWrapper(@NotNull org.bukkit.potion.PotionEffect effect) {
    this.effect = effect;
  }

  public @NotNull org.bukkit.potion.PotionEffect getBukkitPotionEffect() {
    return effect;
  }

  @Override
  public @NotNull PotionEffectType type() {
    return BukkitAdapters.adapt(effect.getType());
  }

  @Override
  public int duration() {
    return effect.getDuration();
  }

  @Override
  public int amplifier() {
    return effect.getAmplifier();
  }

  @Override
  public boolean isAmbient() {
    return effect.isAmbient();
  }

  @Override
  public boolean hasParticles() {
    return effect.hasParticles();
  }

  @Override
  public boolean hasIcon() {
    return effect.hasIcon();
  }

  @Override
  public boolean isInfinite() {
    return effect.isInfinite();
  }

  @Override
  public @NotNull PotionEffect withDuration(int duration) {
    org.bukkit.potion.PotionEffect bEffect =
        new org.bukkit.potion.PotionEffect(
            effect.getType(),
            duration,
            effect.getAmplifier(),
            effect.isAmbient(),
            effect.hasParticles(),
            effect.hasIcon());
    return new BukkitPotionEffectWrapper(bEffect);
  }

  @Override
  public @NotNull PotionEffect withAmplifier(int amplifier) {
    org.bukkit.potion.PotionEffect bEffect =
        new org.bukkit.potion.PotionEffect(
            effect.getType(),
            effect.getDuration(),
            amplifier,
            effect.isAmbient(),
            effect.hasParticles(),
            effect.hasIcon());
    return new BukkitPotionEffectWrapper(bEffect);
  }
}
