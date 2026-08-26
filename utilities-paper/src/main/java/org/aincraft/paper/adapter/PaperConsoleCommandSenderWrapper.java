package org.aincraft.paper.adapter;

import net.kyori.adventure.text.Component;
import org.aincraft.bukkit.adapter.BukkitConsoleCommandSenderWrapper;
import org.jetbrains.annotations.NotNull;

public class PaperConsoleCommandSenderWrapper extends BukkitConsoleCommandSenderWrapper {

  public PaperConsoleCommandSenderWrapper(@NotNull org.bukkit.command.ConsoleCommandSender sender) {
    super(sender);
  }

  @Override
  public void sendMessage(@NotNull Component message) {
    getBukkitCommandSender().sendMessage(message);
  }
}
