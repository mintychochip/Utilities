package org.aincraft.common.effect;

import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;
public interface Particle extends Keyed {

  @NotNull Class<?> dataType();
}
