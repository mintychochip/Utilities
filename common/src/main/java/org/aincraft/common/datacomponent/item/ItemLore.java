package org.aincraft.common.datacomponent.item;

import java.util.List;
import net.kyori.adventure.text.Component;

/**
 * Common contract for item lore, mirroring Paper's {@code ItemLore}.
 */
public interface ItemLore {

  List<Component> lines();

  List<Component> styledLines();
}
