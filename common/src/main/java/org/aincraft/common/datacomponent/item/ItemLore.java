package org.aincraft.common.datacomponent.item;

import net.kyori.adventure.text.Component;

import java.util.List;

/** Common contract for item lore, mirroring Paper's {@code ItemLore}. */
public interface ItemLore {

  List<Component> lines();

  List<Component> styledLines();
}
