package org.aincraft.api.domain.datacomponent.item;

import net.kyori.adventure.text.format.TextColor;

import java.util.List;

/**
 * Common contract for a single firework explosion, mirroring Bukkit's {@code
 * org.bukkit.FireworkEffect}.
 */
public interface FireworkEffect {

  Type type();

  List<TextColor> colors();

  List<TextColor> fadeColors();

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
