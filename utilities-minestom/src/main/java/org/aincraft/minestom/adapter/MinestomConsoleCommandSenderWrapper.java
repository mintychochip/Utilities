package org.aincraft.minestom.adapter;

import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.aincraft.api.domain.server.ConsoleCommandSender;
import org.aincraft.api.domain.server.Server;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class MinestomConsoleCommandSenderWrapper implements ConsoleCommandSender {

  private final net.minestom.server.command.ConsoleSender sender;
  private final Server server;

  public MinestomConsoleCommandSenderWrapper(
      @NotNull net.minestom.server.command.ConsoleSender sender, @NotNull Server server) {
    this.sender = Objects.requireNonNull(sender, "sender cannot be null");
    this.server = Objects.requireNonNull(server, "server cannot be null");
  }

  public @NotNull net.minestom.server.command.ConsoleSender getMinestomSender() {
    return sender;
  }

  @Override
  public @NotNull String name() {
    return "CONSOLE";
  }

  @Override
  public boolean hasPermission(@NotNull String permission) {
    return true;
  }

  @Override
  public boolean isOp() {
    return true;
  }

  @Override
  public void setOp(boolean op) {
    if (!op) throw new UnsupportedOperationException("Minestom console is always operator-level");
  }

  @Override
  public @NotNull Server server() {
    return server;
  }

  @Override
  public void sendMessage(@NotNull Component message) {
    sender.sendMessage(message);
  }

  @Override
  public @NotNull Identity identity() {
    return sender.identity();
  }
}
