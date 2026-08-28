package org.aincraft.api.domain.datacomponent.item;

import org.aincraft.api.domain.inventory.DataComponentType;

import java.util.Set;

/** Common contract for tooltip display properties, mirroring Paper's {@code TooltipDisplay}. */
public interface TooltipDisplay {

  boolean hideTooltip();

  Set<DataComponentType<?>> hiddenComponents();
}
