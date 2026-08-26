package org.aincraft.common.attribute;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Attributable {

  @Nullable AttributeInstance getAttribute(@NotNull Attribute attribute);

  default double getAttributeValue(@NotNull Attribute attribute) {
    AttributeInstance inst = getAttribute(attribute);
    return inst != null ? inst.value() : 0.0;
  }

  default void setAttributeBaseValue(@NotNull Attribute attribute, double value) {
    AttributeInstance inst = getAttribute(attribute);
    if (inst != null) {
      inst.setBaseValue(value);
    }
  }
}
