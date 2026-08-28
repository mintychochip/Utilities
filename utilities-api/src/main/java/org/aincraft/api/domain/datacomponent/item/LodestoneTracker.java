package org.aincraft.api.domain.datacomponent.item;

import org.aincraft.api.domain.location.Location;

/**
 * Common contract for a lodestone tracker, mirroring Paper's {@code
 * io.papermc.paper.datacomponent.item.LodestoneTracker}.
 */
public interface LodestoneTracker {

  Location location();

  Boolean tracked();
}
