package org.aincraft.paper.adapter;

import org.aincraft.api.domain.attribute.Attribute;
import org.aincraft.api.domain.attribute.AttributeModifier;
import org.aincraft.bukkit.adapter.BukkitAdapters;
import org.aincraft.bukkit.adapter.BukkitPotionEffectTypeWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class PaperPotionEffectTypeWrapper extends BukkitPotionEffectTypeWrapper {

  public PaperPotionEffectTypeWrapper(@NotNull org.bukkit.potion.PotionEffectType type) {
    super(type);
  }

  @Override
  public @NotNull Map<Attribute, AttributeModifier> effectAttributes() {
    return getBukkitPotionEffectType().getEffectAttributes().entrySet().stream()
        .collect(
            java.util.stream.Collectors.toUnmodifiableMap(
                entry -> new PaperAttributeKey(entry.getKey()),
                entry -> BukkitAdapters.adapt(entry.getValue())));
  }

  @Override
  public double attributeModifierAmount(@NotNull Attribute attribute, int amplifier) {
    return getBukkitPotionEffectType()
        .getAttributeModifierAmount(BukkitAdapters.toBukkit(attribute.key()), amplifier);
  }

  private record PaperAttributeKey(org.bukkit.attribute.Attribute attribute) implements Attribute {
    @Override
    public net.kyori.adventure.key.Key key() {
      return BukkitAdapters.adapt(attribute);
    }

    @Override
    public double getDefaultValue() {
      return attribute.getDefaultValue();
    }

    @Override
    public @NotNull Sentiment getSentiment() {
      return switch (attribute.getSentiment()) {
        case POSITIVE -> Sentiment.POSITIVE;
        case NEUTRAL -> Sentiment.NEUTRAL;
        case NEGATIVE -> Sentiment.NEGATIVE;
      };
    }
  }
}
