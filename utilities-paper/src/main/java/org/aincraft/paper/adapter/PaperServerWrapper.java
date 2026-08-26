package org.aincraft.paper.adapter;

import net.kyori.adventure.text.Component;
import org.aincraft.bukkit.adapter.BukkitServerWrapper;
import org.jetbrains.annotations.NotNull;

public class PaperServerWrapper extends BukkitServerWrapper {

  public PaperServerWrapper(@NotNull org.bukkit.Server server) {
    super(server);
  }

  @Override
  public void broadcast(@NotNull Component message) {
    getBukkitServer().broadcast(message);
  }

  @Override
  public void sendMessage(@NotNull Component message) {
    getBukkitServer().sendMessage(message);
  }

  @Override
  public String toString() {
    return "PaperServerWrapper{name=" + name() + ", version=" + version() + "}";
  }
}
