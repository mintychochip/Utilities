package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.attribute.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BukkitAttributeWrapper implements Key, Attribute {

  private final org.bukkit.attribute.Attribute attribute;
  private final Key key;

  public BukkitAttributeWrapper(@NotNull org.bukkit.attribute.Attribute attribute) {
    this.attribute = Objects.requireNonNull(attribute, "attribute cannot be null");
    this.key = Key.key(attribute.getKey().getNamespace(), attribute.getKey().getKey());
  }

  public @NotNull org.bukkit.attribute.Attribute getBukkitAttribute() {
    return attribute;
  }

  @Override
  public @NotNull String asString() {
    return key.asString();
  }

  @Override
  public @NotNull String namespace() {
    return key.namespace();
  }

  @Override
  public @NotNull String value() {
    return key.value();
  }

  @Override
  public @NotNull Key key() {
    return key;
  }

  @Override
  public double getDefaultValue() {
    try {
      return ((Number) attribute.getClass().getMethod("getDefaultValue").invoke(attribute))
          .doubleValue();
    } catch (ReflectiveOperationException | ClassCastException ignored) {
      return 0.0;
    }
  }

  @Override
  public @NotNull Sentiment getSentiment() {
    try {
      Object sentiment = attribute.getClass().getMethod("getSentiment").invoke(attribute);
      return Sentiment.valueOf(String.valueOf(sentiment));
    } catch (ReflectiveOperationException | IllegalArgumentException ignored) {
      return Sentiment.NEUTRAL;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Key that)) return false;
    return Objects.equals(key, that);
  }

  @Override
  public int hashCode() {
    return key.hashCode();
  }

  @Override
  public String toString() {
    return key.asString();
  }
}
