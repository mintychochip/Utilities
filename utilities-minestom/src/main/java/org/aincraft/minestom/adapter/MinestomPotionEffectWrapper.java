package org.aincraft.minestom.adapter;

import org.aincraft.api.domain.effect.PotionEffect;
import org.aincraft.api.domain.effect.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class MinestomPotionEffectWrapper implements PotionEffect {

  private final net.minestom.server.potion.Potion potion;

  public MinestomPotionEffectWrapper(@NotNull net.minestom.server.potion.Potion potion) {
    this.potion = Objects.requireNonNull(potion, "potion cannot be null");
  }

  public @NotNull net.minestom.server.potion.Potion getMinestomPotion() {
    return potion;
  }

  @Override
  public @NotNull PotionEffectType type() {
    return MinestomAdapters.adapt(potion.effect());
  }

  @Override
  public int duration() {
    return potion.duration();
  }

  @Override
  public int amplifier() {
    return potion.amplifier();
  }

  @Override
  public boolean isAmbient() {
    return potion.isAmbient();
  }

  @Override
  public boolean hasParticles() {
    return potion.hasParticles();
  }

  @Override
  public boolean hasIcon() {
    return potion.hasIcon();
  }

  @Override
  public boolean isInfinite() {
    return potion.duration() == net.minestom.server.potion.Potion.INFINITE_DURATION;
  }

  @Override
  public @NotNull PotionEffect withDuration(int duration) {
    return new MinestomPotionEffectWrapper(
        new net.minestom.server.potion.Potion(
            potion.effect(), duration, potion.amplifier(), potion.flags()));
  }

  @Override
  public @NotNull PotionEffect withAmplifier(int amplifier) {
    return new MinestomPotionEffectWrapper(
        new net.minestom.server.potion.Potion(
            potion.effect(), potion.duration(), amplifier, potion.flags()));
  }

  @Override
  public boolean equals(Object other) {
    return this == other
        || (other instanceof PotionEffect effect
            && type().equals(effect.type())
            && duration() == effect.duration()
            && amplifier() == effect.amplifier()
            && isAmbient() == effect.isAmbient()
            && hasParticles() == effect.hasParticles()
            && hasIcon() == effect.hasIcon());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), duration(), amplifier(), potion.flags());
  }

  @Override
  public String toString() {
    return "MinestomPotionEffectWrapper{" + type().key() + ", " + amplifier() + "}";
  }
}
