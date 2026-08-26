package org.aincraft.common.datacomponent.item;

import java.util.Set;
import net.kyori.adventure.key.Key;

/**
 * Common contract for repairable item properties, mirroring Paper's {@code Repairable}.
 */
public interface Repairable {

  Set<Key> items();
}
