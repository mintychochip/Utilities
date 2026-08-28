package org.aincraft.minestom.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.attribute.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class MinestomAttributeWrapper implements Attribute {

  private final net.minestom.server.entity.attribute.Attribute attribute;

  public MinestomAttributeWrapper(
      @NotNull net.minestom.server.entity.attribute.Attribute attribute) {
    this.attribute = Objects.requireNonNull(attribute, "attribute cannot be null");
  }

  public @NotNull net.minestom.server.entity.attribute.Attribute getMinestomAttribute() {
    return attribute;
  }

  @Override
  public @NotNull Key key() {
    return attribute.key();
  }

  @Override
  public double getDefaultValue() {
    return attribute.defaultValue();
  }

  @Override
  public @NotNull Sentiment getSentiment() {
    return Sentiment.NEUTRAL;
  }

  @Override
  public boolean equals(Object other) {
    return this == other || (other instanceof Attribute value && key().equals(value.key()));
  }

  @Override
  public int hashCode() {
    return key().hashCode();
  }

  @Override
  public String toString() {
    return "MinestomAttributeWrapper{" + key() + "}";
  }
}
