package org.aincraft.common.entity;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Nameable {

  @Nullable Component customName();

  void customName(@Nullable Component name);

  default boolean hasCustomName() {
    return customName() != null;
  }
}
