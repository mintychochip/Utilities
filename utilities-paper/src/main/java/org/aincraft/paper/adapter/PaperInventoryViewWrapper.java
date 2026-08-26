package org.aincraft.paper.adapter;

import net.kyori.adventure.text.Component;
import org.aincraft.bukkit.adapter.BukkitInventoryViewWrapper;
import org.jetbrains.annotations.NotNull;

public class PaperInventoryViewWrapper extends BukkitInventoryViewWrapper {

  public PaperInventoryViewWrapper(@NotNull org.bukkit.inventory.InventoryView view) {
    super(view);
  }

  @Override
  public @NotNull Component title() {
    return getBukkitInventoryView().title();
  }
}
