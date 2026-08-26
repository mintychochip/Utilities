package org.aincraft.common.datacomponent.item;

import java.util.Set;
import org.aincraft.common.inventory.DataComponentType;

/**
 * Common contract for tooltip display properties, mirroring Paper's {@code TooltipDisplay}.
 */
public interface TooltipDisplay {

  boolean hideTooltip();

  Set<DataComponentType<?>> hiddenComponents();
}
