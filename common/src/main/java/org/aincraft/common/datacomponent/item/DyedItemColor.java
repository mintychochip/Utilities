package org.aincraft.common.datacomponent.item;

/**
 * Common contract for the color of a dyed item, mirroring Paper's {@code DyedItemColor}.
 *
 * <p>The color is represented as a packed 32-bit ARGB integer.
 */
public interface DyedItemColor {

  int color();
}
