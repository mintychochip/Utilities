package org.aincraft.bukkit.adapter;

import org.aincraft.common.server.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class BukkitConsoleCommandSenderWrapper extends BukkitCommandSenderWrapper
    implements ConsoleCommandSender {

  public BukkitConsoleCommandSenderWrapper(
      @NotNull org.bukkit.command.ConsoleCommandSender sender) {
    super(sender);
  }
}
