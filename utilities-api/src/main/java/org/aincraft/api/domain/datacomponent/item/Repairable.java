package org.aincraft.api.domain.datacomponent.item;

import net.kyori.adventure.key.Key;

import java.util.Set;

/** Common contract for repairable item properties, mirroring Paper's {@code Repairable}. */
public interface Repairable {

  Set<Key> items();
}
