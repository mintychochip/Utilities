package org.aincraft.common.datacomponent.item;

import java.util.List;
import net.kyori.adventure.key.Key;

/**
 * Common contract for banner pattern layers, mirroring Paper's
 * {@code io.papermc.paper.datacomponent.item.BannerPatternLayers}.
 */
public interface BannerPatternLayers {

  List<BannerPatternLayer> layers();

  /**
   * Common contract for a single banner pattern layer, mirroring
   * {@code org.bukkit.block.banner.Pattern}.
   */
  interface BannerPatternLayer {

    Key pattern();

    DyeColor color();
  }

  /**
   * Common dye color values, mirroring {@code org.bukkit.DyeColor}.
   */
  enum DyeColor {
    WHITE,
    ORANGE,
    MAGENTA,
    LIGHT_BLUE,
    YELLOW,
    LIME,
    PINK,
    GRAY,
    LIGHT_GRAY,
    CYAN,
    PURPLE,
    BLUE,
    BROWN,
    GREEN,
    RED,
    BLACK
  }
}
