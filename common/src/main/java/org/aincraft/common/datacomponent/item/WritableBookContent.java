package org.aincraft.common.datacomponent.item;

import org.aincraft.common.datacomponent.text.Filtered;

import java.util.List;

/**
 * Common contract for writable book content, mirroring Paper's {@code WritableBookContent} without
 * depending on Bukkit.
 */
public interface WritableBookContent {

  List<Filtered<String>> pages();
}
