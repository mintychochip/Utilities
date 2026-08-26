package org.aincraft.paper.adapter;

import net.kyori.adventure.text.Component;
import org.aincraft.bukkit.adapter.BukkitPlayerWrapper;
import org.jetbrains.annotations.NotNull;

public class PaperPlayerWrapper extends BukkitPlayerWrapper {

  public PaperPlayerWrapper(@NotNull org.bukkit.entity.Player player) {
    super(player);
  }

  @Override
  public void sendMessage(@NotNull Component message) {
    getBukkitPlayer().sendMessage(message);
  }

  @Override
  public void sendActionBar(@NotNull Component message) {
    getBukkitPlayer().sendActionBar(message);
  }

  @Override
  public void kick(@NotNull Component reason) {
    getBukkitPlayer().kick(reason);
  }

  @Override
  public String toString() {
    return "PaperPlayerWrapper{name=" + username() + ", uuid=" + uniqueId() + "}";
  }
}
