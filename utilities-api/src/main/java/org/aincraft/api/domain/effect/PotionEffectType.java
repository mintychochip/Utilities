package org.aincraft.api.domain.effect;

import net.kyori.adventure.key.Keyed;
import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.aincraft.api.domain.attribute.Attribute;
import org.aincraft.api.domain.attribute.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface PotionEffectType extends Keyed {

  @NotNull
  String name();

  boolean isInstant();

  @NotNull
  PotionEffectTypeCategory category();

  @NotNull
  PotionEffect createEffect(int duration, int amplifier);

  /** Returns the attribute modifiers contributed by this effect type. */
  default @NotNull Map<Attribute, AttributeModifier> effectAttributes() {
    throw new UnsupportedCapabilityException(Capability.POTION_EFFECT_ATTRIBUTES);
  }

  /** Returns the modifier amount for an attribute at the requested amplifier. */
  default double attributeModifierAmount(@NotNull Attribute attribute, int amplifier) {
    throw new UnsupportedCapabilityException(Capability.POTION_EFFECT_ATTRIBUTES);
  }
}
