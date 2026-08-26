package org.aincraft.paper.adapter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.aincraft.bukkit.adapter.BukkitWorldWrapper;
import org.jetbrains.annotations.NotNull;

public class PaperWorldWrapper extends BukkitWorldWrapper {

  public PaperWorldWrapper(@NotNull org.bukkit.World world) {
    super(world);
  }

  @Override
  public void sendMessage(@NotNull Component message) {
    getBukkitWorld().sendMessage(message);
  }

  @Override
  public void sendActionBar(@NotNull Component message) {
    getBukkitWorld().sendActionBar(message);
  }

  @Override
  public void showTitle(@NotNull Title title) {
    getBukkitWorld().showTitle(title);
  }

  @Override
  public void clearTitle() {
    getBukkitWorld().clearTitle();
  }

  @Override
  public void resetTitle() {
    getBukkitWorld().resetTitle();
  }

  @Override
  public String toString() {
    return "PaperWorldWrapper{name=" + name() + ", uid=" + uid() + "}";
  }
}
