package org.aincraft.api.domain.attribute;

import net.kyori.adventure.key.Key;
import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Attributable {

  @Nullable
  AttributeInstance getAttribute(@NotNull Key attribute);

  default @Nullable AttributeInstance getAttribute(@NotNull Attribute attribute) {
    return getAttribute(attribute.key());
  }

  /** Registers a custom attribute on this object, when supported by the platform. */
  default void registerAttribute(@NotNull Attribute attribute) {
    throw new UnsupportedCapabilityException(
        Capability.ATTRIBUTE_MODIFIER,
        "Custom attribute registration is not supported by this adapter.");
  }

  default double getAttributeValue(@NotNull Key attribute) {
    AttributeInstance inst = getAttribute(attribute);
    return inst != null ? inst.value() : 0.0;
  }

  default void setAttributeBaseValue(@NotNull Key attribute, double value) {
    AttributeInstance inst = getAttribute(attribute);
    if (inst != null) {
      inst.setBaseValue(value);
    }
  }
}
