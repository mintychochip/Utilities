package org.aincraft.minestom.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.attribute.Attribute;
import org.aincraft.api.domain.attribute.AttributeModifier;
import org.aincraft.api.domain.effect.PotionEffect;
import org.aincraft.api.domain.effect.PotionEffectType;
import org.aincraft.api.domain.effect.PotionEffectTypeCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public final class MinestomPotionEffectTypeWrapper implements PotionEffectType {

  private final net.minestom.server.potion.PotionEffect effect;

  public MinestomPotionEffectTypeWrapper(@NotNull net.minestom.server.potion.PotionEffect effect) {
    this.effect = Objects.requireNonNull(effect, "effect cannot be null");
  }

  public @NotNull net.minestom.server.potion.PotionEffect getMinestomPotionEffect() {
    return effect;
  }

  @Override
  public @NotNull Key key() {
    return effect.key();
  }

  @Override
  public @NotNull String name() {
    return effect.key().value().toUpperCase(java.util.Locale.ROOT);
  }

  @Override
  public boolean isInstant() {
    return effect.instantaneous();
  }

  @Override
  public @NotNull PotionEffectTypeCategory category() {
    return PotionEffectTypeCategory.NEUTRAL;
  }

  @Override
  public @NotNull PotionEffect createEffect(int duration, int amplifier) {
    return new MinestomPotionEffectWrapper(
        new net.minestom.server.potion.Potion(effect, duration, amplifier));
  }

  @Override
  public @NotNull Map<Attribute, AttributeModifier> effectAttributes() {
    return Map.of();
  }

  @Override
  public double attributeModifierAmount(@NotNull Attribute attribute, int amplifier) {
    return 0.0;
  }

  @Override
  public boolean equals(Object other) {
    return this == other || (other instanceof PotionEffectType type && key().equals(type.key()));
  }

  @Override
  public int hashCode() {
    return key().hashCode();
  }

  @Override
  public String toString() {
    return "MinestomPotionEffectTypeWrapper{" + key() + "}";
  }
}
