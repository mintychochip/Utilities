package org.aincraft.common.datacomponent.item;

/**
 * Common contract for the color of a map item, mirroring Paper's {@code MapItemColor}.
 *
 * <p>The color is represented as a packed 32-bit ARGB integer.
 */
public interface MapItemColor {

  int color();
}
