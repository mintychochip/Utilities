package org.aincraft.api.domain.attribute;

import net.kyori.adventure.key.Key;
import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface AttributeInstance {

  @NotNull
  Key attribute();

  double baseValue();

  void setBaseValue(double value);

  double value();

  @NotNull
  Collection<? extends AttributeModifier> modifiers();

  void addModifier(@NotNull AttributeModifier modifier);

  /** Adds a modifier that is not persisted by the platform, when supported. */
  default void addTransientModifier(@NotNull AttributeModifier modifier) {
    throw new UnsupportedCapabilityException(
        Capability.ATTRIBUTE_MODIFIER,
        "Transient attribute modifiers are not supported by this adapter.");
  }

  /** Returns the registry default for this attribute, when exposed by the platform. */
  default double defaultValue() {
    throw new UnsupportedCapabilityException(
        Capability.ATTRIBUTE_MODIFIER,
        "Attribute default values are not supported by this adapter.");
  }

  void removeModifier(@NotNull AttributeModifier modifier);

  void removeModifier(@NotNull UUID id);

  /** Removes a modifier by its stable keyed identity. */
  default void removeModifier(@NotNull Key key) {
    AttributeModifier modifier = getModifier(key);
    if (modifier != null) removeModifier(modifier);
  }

  @Nullable
  AttributeModifier getModifier(@NotNull UUID id);

  /** Finds a modifier by its stable keyed identity. */
  default @Nullable AttributeModifier getModifier(@NotNull Key key) {
    for (AttributeModifier modifier : modifiers()) {
      if (key.equals(modifier.key())) return modifier;
    }
    return null;
  }
}
