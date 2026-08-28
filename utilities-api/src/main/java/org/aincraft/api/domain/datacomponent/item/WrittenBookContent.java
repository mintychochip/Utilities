package org.aincraft.api.domain.datacomponent.item;

import net.kyori.adventure.text.Component;
import org.aincraft.api.domain.datacomponent.text.Filtered;

import java.util.List;

/**
 * Common contract for written book content, mirroring Paper's {@code WrittenBookContent} without
 * depending on Bukkit.
 */
public interface WrittenBookContent {

  Filtered<String> title();

  String author();

  int generation();

  List<Filtered<Component>> pages();

  boolean resolved();
}
