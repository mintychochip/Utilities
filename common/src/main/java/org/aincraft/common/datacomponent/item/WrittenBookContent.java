package org.aincraft.common.datacomponent.item;

import java.util.List;
import net.kyori.adventure.text.Component;
import org.aincraft.common.datacomponent.text.Filtered;

/**
 * Common contract for written book content, mirroring Paper's
 * {@code WrittenBookContent} without depending on Bukkit.
 */
public interface WrittenBookContent {

  Filtered<String> title();

  String author();

  int generation();

  List<Filtered<Component>> pages();

  boolean resolved();
}
