package org.aincraft.paper.adapter;

import net.kyori.adventure.text.Component;
import org.aincraft.bukkit.adapter.BukkitCommandSenderWrapper;
import org.jetbrains.annotations.NotNull;

public class PaperCommandSenderWrapper extends BukkitCommandSenderWrapper {

  public PaperCommandSenderWrapper(@NotNull org.bukkit.command.CommandSender sender) {
    super(sender);
  }

  @Override
  public void sendMessage(@NotNull Component message) {
    getBukkitCommandSender().sendMessage(message);
  }
}
