package org.aincraft.common.datacomponent.item;

import net.kyori.adventure.key.Key;

/** Common contract for seeded container loot, mirroring Paper's {@code SeededContainerLoot}. */
public interface SeededContainerLoot {

  Key lootTable();

  long seed();
}
