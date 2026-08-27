package org.aincraft.common.effect;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public interface Particle extends Key {

  @NotNull
  Class<?> dataType();
}
