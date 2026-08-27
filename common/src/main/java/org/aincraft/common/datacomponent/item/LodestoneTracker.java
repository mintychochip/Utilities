package org.aincraft.common.datacomponent.item;

import org.aincraft.common.location.Location;

/**
 * Common contract for a lodestone tracker, mirroring Paper's {@code
 * io.papermc.paper.datacomponent.item.LodestoneTracker}.
 */
public interface LodestoneTracker {

  Location location();

  Boolean tracked();
}
