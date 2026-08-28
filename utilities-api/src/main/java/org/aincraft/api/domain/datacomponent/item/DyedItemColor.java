package org.aincraft.api.domain.datacomponent.item;

import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

/**
 * Common contract for the color of a dyed item, mirroring Paper's {@code DyedItemColor}.
 *
 * <p>The color is represented by a Kyori Adventure text color.
 */
public interface DyedItemColor {

  @NotNull
  TextColor color();
}
