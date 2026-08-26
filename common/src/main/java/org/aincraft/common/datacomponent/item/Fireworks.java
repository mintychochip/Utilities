package org.aincraft.common.datacomponent.item;

import java.util.List;

/**
 * Common contract for firework properties, mirroring Paper's {@code Fireworks}.
 */
public interface Fireworks {

  List<FireworkEffect> effects();

  Integer flightDuration();
}
