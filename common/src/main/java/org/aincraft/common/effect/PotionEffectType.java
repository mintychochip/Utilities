package org.aincraft.common.effect;

import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

public interface PotionEffectType extends Keyed {

  @NotNull String name();

  boolean isInstant();
}
