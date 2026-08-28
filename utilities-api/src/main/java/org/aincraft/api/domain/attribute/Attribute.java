package org.aincraft.api.domain.attribute;

import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

/**
 * An attribute type exposed by the server's attribute registry. Corresponds to {@code
 * org.bukkit.attribute.Attribute} in Paper 26.2. Implementations are obtained from {@link
 * Attributes} or {@link AttributeRegistry}.
 *
 * @see Attributes
 * @see AttributeRegistry
 */
public interface Attribute extends Keyed {

  /** Returns the default value for this attribute. */
  double getDefaultValue();

  default double defaultValue() {
    return getDefaultValue();
  }

  default @NotNull Sentiment sentiment() {
    return getSentiment();
  }

  /** Returns the sentiment classification used for UI display. */
  @NotNull
  Sentiment getSentiment();

  /** Sentiment classification for attribute values in UI contexts. */
  enum Sentiment {
    POSITIVE,
    NEUTRAL,
    NEGATIVE
  }
}
