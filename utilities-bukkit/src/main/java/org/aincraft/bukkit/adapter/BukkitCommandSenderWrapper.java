package org.aincraft.bukkit.adapter;

import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.aincraft.api.domain.server.CommandSender;
import org.aincraft.api.domain.server.Server;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BukkitCommandSenderWrapper implements CommandSender {

  private final org.bukkit.command.CommandSender sender;

  public BukkitCommandSenderWrapper(@NotNull org.bukkit.command.CommandSender sender) {
    this.sender = Objects.requireNonNull(sender, "sender cannot be null");
  }

  public @NotNull org.bukkit.command.CommandSender getBukkitCommandSender() {
    return sender;
  }

  @Override
  public @NotNull String name() {
    return sender.getName();
  }

  @Override
  public boolean hasPermission(@NotNull String permission) {
    return sender.hasPermission(permission);
  }

  @Override
  public boolean isOp() {
    return sender.isOp();
  }

  @Override
  public void setOp(boolean op) {
    sender.setOp(op);
  }

  @Override
  public @NotNull Server server() {
    return BukkitAdapters.adapt(sender.getServer());
  }

  @Override
  public void sendMessage(@NotNull Component message) {
    sender.sendMessage(LegacyComponentSerializer.legacySection().serialize(message));
  }

  @Override
  public @NotNull Identity identity() {
    if (sender instanceof org.bukkit.entity.Player player) {
      return Identity.identity(player.getUniqueId());
    }
    return Identity.nil();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CommandSender that)) return false;
    return Objects.equals(name(), that.name());
  }

  @Override
  public int hashCode() {
    return name().hashCode();
  }

  @Override
  public String toString() {
    return "BukkitCommandSenderWrapper{name=" + name() + "}";
  }
}
