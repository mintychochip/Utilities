package org.aincraft.api.domain.datacomponent.item;

import org.aincraft.api.domain.datacomponent.text.Filtered;

import java.util.List;

/**
 * Common contract for writable book content, mirroring Paper's {@code WritableBookContent} without
 * depending on Bukkit.
 */
public interface WritableBookContent {

  List<Filtered<String>> pages();
}
