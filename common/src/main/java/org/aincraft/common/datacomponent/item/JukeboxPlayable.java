package org.aincraft.common.datacomponent.item;

import net.kyori.adventure.key.Key;

/** Common contract for jukebox playable, mirroring Paper's {@code JukeboxPlayable}. */
public interface JukeboxPlayable {

  Key song();

  Boolean showInTooltip();
}
