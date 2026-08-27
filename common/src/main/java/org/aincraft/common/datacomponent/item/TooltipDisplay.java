package org.aincraft.common.datacomponent.item;

import org.aincraft.common.inventory.DataComponentType;

import java.util.Set;

/** Common contract for tooltip display properties, mirroring Paper's {@code TooltipDisplay}. */
public interface TooltipDisplay {

  boolean hideTooltip();

  Set<DataComponentType<?>> hiddenComponents();
}
