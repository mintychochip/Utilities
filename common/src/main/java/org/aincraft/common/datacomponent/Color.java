package org.aincraft.common.datacomponent;

/**
 * Common contract for an ARGB color, mirroring Bukkit's {@code org.bukkit.Color}
 * without depending on Bukkit or {@code java.awt.Color}.
 */
public interface Color {

  int alpha();

  int red();

  int green();

  int blue();

  int asRGB();

  int asARGB();

  int asBGR();
}
