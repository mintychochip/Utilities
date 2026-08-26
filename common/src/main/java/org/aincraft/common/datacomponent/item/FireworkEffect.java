package org.aincraft.common.datacomponent.item;

import java.util.List;
import org.aincraft.common.datacomponent.Color;

/**
 * Common contract for a single firework explosion, mirroring Bukkit's
 * {@code org.bukkit.FireworkEffect}.
 */
public interface FireworkEffect {

  Type type();

  List<Color> colors();

  List<Color> fadeColors();

  boolean trail();

  boolean flicker();

  enum Type {
    BALL,
    BALL_LARGE,
    STAR,
    BURST,
    CREEPER
  }
}
