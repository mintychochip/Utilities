package org.aincraft.common.attribute;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Attributable {

  @Nullable AttributeInstance getAttribute(@NotNull Key attribute);

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
