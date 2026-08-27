package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.aincraft.common.entity.Player;
import org.aincraft.common.server.ConsoleCommandSender;
import org.aincraft.common.server.Server;
import org.aincraft.common.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class BukkitServerWrapper implements Server {

  private final org.bukkit.Server server;

  public BukkitServerWrapper(@NotNull org.bukkit.Server server) {
    this.server = Objects.requireNonNull(server, "server cannot be null");
  }

  public @NotNull org.bukkit.Server getBukkitServer() {
    return server;
  }

  @Override
  public @NotNull String version() {
    return server.getVersion();
  }

  @Override
  public @NotNull String name() {
    return server.getName();
  }

  @Override
  public int port() {
    return server.getPort();
  }

  @Override
  public @NotNull String ip() {
    return server.getIp();
  }

  @Override
  public int maxPlayers() {
    return server.getMaxPlayers();
  }

  @Override
  public @NotNull Collection<? extends Player> onlinePlayers() {
    return server.getOnlinePlayers().stream().map(BukkitAdapters::adapt).toList();
  }

  @Override
  public @NotNull Collection<? extends World> worlds() {
    return server.getWorlds().stream().map(BukkitAdapters::adapt).toList();
  }

  @Override
  public @Nullable World world(@NotNull Key key) {
    for (org.bukkit.World bWorld : server.getWorlds()) {
      if (bWorld.getKey().getNamespace().equals(key.namespace())
          && bWorld.getKey().getKey().equals(key.value())) {
        return BukkitAdapters.adapt(bWorld);
      }
    }
    return null;
  }

  public @Nullable World world(@NotNull String name) {
    org.bukkit.World bWorld = server.getWorld(name);
    return bWorld != null ? BukkitAdapters.adapt(bWorld) : null;
  }

  @Override
  public @Nullable World world(@NotNull UUID uid) {
    org.bukkit.World bWorld = server.getWorld(uid);
    return bWorld != null ? BukkitAdapters.adapt(bWorld) : null;
  }

  @Override
  public @Nullable Player player(@NotNull UUID uid) {
    org.bukkit.entity.Player bPlayer = server.getPlayer(uid);
    return bPlayer != null ? BukkitAdapters.adapt(bPlayer) : null;
  }

  @Override
  public @Nullable Player player(@NotNull String name) {
    org.bukkit.entity.Player bPlayer = server.getPlayer(name);
    return bPlayer != null ? BukkitAdapters.adapt(bPlayer) : null;
  }

  @Override
  public void broadcast(@NotNull Component message) {
    server.broadcastMessage(LegacyComponentSerializer.legacySection().serialize(message));
  }

  @Override
  public void sendMessage(@NotNull Component message) {
    broadcast(message);
  }

  @Override
  public @NotNull ConsoleCommandSender consoleSender() {
    return new BukkitConsoleCommandSenderWrapper(server.getConsoleSender());
  }

  @Override
  public void shutdown() {
    server.shutdown();
  }

  @Override
  public void reload() {
    server.reload();
  }

  @Override
  public String toString() {
    return "BukkitServerWrapper{name=" + name() + ", version=" + version() + "}";
  }
}
