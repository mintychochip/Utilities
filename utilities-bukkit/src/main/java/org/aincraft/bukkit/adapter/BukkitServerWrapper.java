package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.aincraft.api.domain.entity.Entity;
import org.aincraft.api.domain.entity.Player;
import org.aincraft.api.domain.inventory.Inventory;
import org.aincraft.api.domain.inventory.InventoryHolder;
import org.aincraft.api.domain.inventory.InventoryType;
import org.aincraft.api.domain.inventory.ItemFactory;
import org.aincraft.api.domain.scoreboard.Criteria;
import org.aincraft.api.domain.scoreboard.ScoreboardManager;
import org.aincraft.api.domain.server.ConsoleCommandSender;
import org.aincraft.api.domain.server.OfflinePlayer;
import org.aincraft.api.domain.server.Server;
import org.aincraft.api.domain.world.GameMode;
import org.aincraft.api.domain.world.World;
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

  @Override
  public @Nullable World world(@NotNull String name) {
    org.bukkit.World bWorld = server.getWorld(name);
    return bWorld == null ? null : BukkitAdapters.adapt(bWorld);
  }

  @Override
  public @Nullable World world(@NotNull UUID uid) {
    org.bukkit.World bWorld = server.getWorld(uid);
    return bWorld == null ? null : BukkitAdapters.adapt(bWorld);
  }

  @Override
  public @Nullable Player player(@NotNull UUID uid) {
    org.bukkit.entity.Player bPlayer = server.getPlayer(uid);
    return bPlayer == null ? null : BukkitAdapters.adapt(bPlayer);
  }

  @Override
  public @Nullable Player player(@NotNull String name) {
    org.bukkit.entity.Player bPlayer = server.getPlayer(name);
    return bPlayer == null ? null : BukkitAdapters.adapt(bPlayer);
  }

  @Override
  public void broadcast(@NotNull Component message) {
    server.broadcastMessage(LegacyComponentSerializer.legacySection().serialize(message));
  }

  @Override
  public int broadcast(@NotNull Component message, @NotNull String permission) {
    return server.broadcast(
        LegacyComponentSerializer.legacySection().serialize(message), permission);
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
  public @NotNull String minecraftVersion() {
    throw new UnsupportedCapabilityException(
        Capability.SERVER_INFO,
        "getMinecraftVersion() is Paper-only on the Spigot compile surface; use utilities-paper.");
  }

  @Override
  public boolean dispatchCommand(
      @NotNull org.aincraft.api.domain.server.CommandSender sender, @NotNull String commandLine) {
    return server.dispatchCommand(BukkitAdapters.toBukkit(sender), commandLine);
  }

  @Override
  public @Nullable Entity entity(@NotNull UUID uniqueId) {
    org.bukkit.entity.Entity entity = server.getEntity(uniqueId);
    return entity == null ? null : BukkitAdapters.adapt(entity);
  }

  @Override
  public boolean isPrimaryThread() {
    return server.isPrimaryThread();
  }

  @Override
  public int currentTick() {
    throw new UnsupportedCapabilityException(
        Capability.SERVER_TICK,
        "getCurrentTick() is Paper-only on the Spigot compile surface; use utilities-paper.");
  }

  @Override
  public @NotNull OfflinePlayer offlinePlayer(@NotNull UUID uniqueId) {
    return BukkitAdapters.adapt(server.getOfflinePlayer(uniqueId));
  }

  @Override
  public @NotNull OfflinePlayer offlinePlayer(@NotNull String name) {
    return BukkitAdapters.adapt(server.getOfflinePlayer(name));
  }

  @Override
  public @Nullable Player playerExact(@NotNull String name) {
    org.bukkit.entity.Player bPlayer = server.getPlayerExact(name);
    return bPlayer == null ? null : BukkitAdapters.adapt(bPlayer);
  }

  @Override
  public @NotNull Collection<? extends Player> matchPlayers(@NotNull String partialName) {
    return server.matchPlayer(partialName).stream().map(BukkitAdapters::adapt).toList();
  }

  @Override
  public @NotNull GameMode defaultGameMode() {
    return BukkitAdapters.adapt(server.getDefaultGameMode());
  }

  @Override
  public void setDefaultGameMode(@NotNull GameMode gameMode) {
    server.setDefaultGameMode(BukkitAdapters.toBukkit(gameMode));
  }

  @Override
  public @NotNull Component motd() {
    return LegacyComponentSerializer.legacySection().deserialize(server.getMotd());
  }

  @Override
  public void motd(@NotNull Component motd) {
    server.setMotd(LegacyComponentSerializer.legacySection().serialize(motd));
  }

  @Override
  public @NotNull Inventory createInventory(@Nullable InventoryHolder holder, int size) {
    org.bukkit.inventory.Inventory inventory =
        server.createInventory(holder == null ? null : BukkitAdapters.toBukkit(holder), size);
    return BukkitAdapters.adapt(inventory);
  }

  @Override
  public @NotNull Inventory createInventory(
      @Nullable InventoryHolder holder, int size, @NotNull Component title) {
    org.bukkit.inventory.Inventory inventory =
        server.createInventory(
            holder == null ? null : BukkitAdapters.toBukkit(holder),
            size,
            LegacyComponentSerializer.legacySection().serialize(title));
    return BukkitAdapters.adapt(inventory);
  }

  @Override
  public @NotNull Inventory createInventory(
      @Nullable InventoryHolder holder, @NotNull InventoryType type) {
    org.bukkit.inventory.Inventory inventory =
        server.createInventory(
            holder == null ? null : BukkitAdapters.toBukkit(holder), BukkitAdapters.toBukkit(type));
    return BukkitAdapters.adapt(inventory);
  }

  @Override
  public void savePlayers() {
    server.savePlayers();
  }

  @Override
  public boolean onlineMode() {
    return server.getOnlineMode();
  }

  @Override
  public @NotNull ItemFactory itemFactory() {
    return new BukkitItemFactoryWrapper(server.getItemFactory());
  }

  @Override
  public @NotNull ScoreboardManager scoreboardManager() {
    return BukkitAdapters.adapt(server.getScoreboardManager());
  }

  @Override
  public @NotNull Criteria scoreboardCriteria(@NotNull String name) {
    return BukkitAdapters.adapt(server.getScoreboardCriteria(name));
  }

  @Override
  public String toString() {
    return "BukkitServerWrapper{name=" + name() + ", version=" + version() + "}";
  }
}
