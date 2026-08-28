package org.aincraft.minestom.adapter;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.aincraft.api.domain.entity.Entity;
import org.aincraft.api.domain.entity.Player;
import org.aincraft.api.domain.inventory.Inventory;
import org.aincraft.api.domain.inventory.InventoryHolder;
import org.aincraft.api.domain.inventory.InventoryType;
import org.aincraft.api.domain.inventory.ItemFactory;
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

public final class MinestomServerWrapper implements Server {

  private volatile GameMode defaultGameMode = GameMode.SURVIVAL;
  private volatile Component motd = Component.empty();

  public @NotNull Audience audience() {
    return net.minestom.server.adventure.audience.PacketGroupingAudience.of(
        net.minestom.server.MinecraftServer.getConnectionManager().getOnlinePlayers());
  }

  @Override
  public void sendMessage(@NotNull Component message) {
    audience().sendMessage(message);
  }

  @Override
  public void sendActionBar(@NotNull Component message) {
    audience().sendActionBar(message);
  }

  @Override
  public void showTitle(@NotNull net.kyori.adventure.title.Title title) {
    audience().showTitle(title);
  }

  @Override
  public void clearTitle() {
    audience().clearTitle();
  }

  @Override
  public void resetTitle() {
    audience().resetTitle();
  }

  private @NotNull net.minestom.server.instance.InstanceManager instanceManager() {
    return net.minestom.server.MinecraftServer.getInstanceManager();
  }

  @Override
  public @NotNull String version() {
    return net.minestom.server.MinecraftServer.getBrandName();
  }

  @Override
  public @NotNull String name() {
    return "Minestom";
  }

  @Override
  public int port() {
    return net.minestom.server.MinecraftServer.getServer().getPort();
  }

  @Override
  public @NotNull String ip() {
    return net.minestom.server.MinecraftServer.getServer().getAddress();
  }

  @Override
  public int maxPlayers() {
    return Integer.MAX_VALUE;
  }

  @Override
  public @NotNull Collection<? extends Player> onlinePlayers() {
    return net.minestom.server.MinecraftServer.getConnectionManager().getOnlinePlayers().stream()
        .map(MinestomAdapters::adapt)
        .toList();
  }

  @Override
  public @NotNull Collection<? extends World> worlds() {
    return instanceManager().getInstances().stream().map(MinestomAdapters::adapt).toList();
  }

  @Override
  public @Nullable World world(@NotNull net.kyori.adventure.key.Key key) {
    for (World world : worlds()) if (world.key().equals(key)) return world;
    return null;
  }

  @Override
  public @Nullable World world(@NotNull String name) {
    for (World world : worlds()) if (world.name().equals(name)) return world;
    return null;
  }

  @Override
  public @Nullable World world(@NotNull UUID uid) {
    net.minestom.server.instance.Instance instance = instanceManager().getInstance(uid);
    return instance == null ? null : MinestomAdapters.adapt(instance);
  }

  @Override
  public @Nullable Player player(@NotNull UUID uid) {
    net.minestom.server.entity.Player player =
        net.minestom.server.MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(uid);
    return player == null ? null : MinestomAdapters.adapt(player);
  }

  @Override
  public @Nullable Player player(@NotNull String name) {
    net.minestom.server.entity.Player player =
        net.minestom.server.MinecraftServer.getConnectionManager().getOnlinePlayerByUsername(name);
    return player == null ? null : MinestomAdapters.adapt(player);
  }

  @Override
  public void broadcast(@NotNull Component message) {
    onlinePlayers().forEach(player -> player.sendMessage(message));
  }

  @Override
  public @NotNull ConsoleCommandSender consoleSender() {
    return new MinestomConsoleCommandSenderWrapper(
        net.minestom.server.MinecraftServer.getCommandManager().getConsoleSender(), this);
  }

  @Override
  public void shutdown() {
    net.minestom.server.MinecraftServer.stopCleanly();
  }

  @Override
  public void reload() {
    throw new UnsupportedOperationException("Minestom has no server reload operation");
  }

  @Override
  public @NotNull String minecraftVersion() {
    return version();
  }

  @Override
  public boolean dispatchCommand(
      @NotNull org.aincraft.api.domain.server.CommandSender sender, @NotNull String commandLine) {
    net.minestom.server.command.CommandSender minestomSender = toMinestom(sender);
    return net.minestom.server.MinecraftServer.getCommandManager()
            .execute(minestomSender, commandLine)
            .getType()
        == net.minestom.server.command.builder.CommandResult.Type.SUCCESS;
  }

  @Override
  public @Nullable Entity entity(@NotNull UUID uniqueId) {
    for (net.minestom.server.instance.Instance instance : instanceManager().getInstances()) {
      net.minestom.server.entity.Entity entity = instance.getEntityByUuid(uniqueId);
      if (entity != null) return MinestomAdapters.adapt(entity);
    }
    return null;
  }

  @Override
  public boolean isPrimaryThread() {
    return net.minestom.server.MinecraftServer.process().dispatcher().threads().stream()
        .anyMatch(thread -> thread == Thread.currentThread());
  }

  @Override
  public int currentTick() {
    long tick =
        net.minestom.server.MinecraftServer.process().dispatcher().threads().stream()
            .mapToLong(net.minestom.server.thread.TickThread::getTick)
            .max()
            .orElse(0L);
    return Math.toIntExact(tick);
  }

  @Override
  public @NotNull OfflinePlayer offlinePlayer(@NotNull UUID uniqueId) {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.OFFLINE_PLAYER, "Minestom only tracks online players.");
  }

  @Override
  public @NotNull OfflinePlayer offlinePlayer(@NotNull String name) {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.OFFLINE_PLAYER, "Minestom only tracks online players.");
  }

  @Override
  public @Nullable Player playerExact(@NotNull String name) {
    return player(name);
  }

  @Override
  public @NotNull Collection<? extends Player> matchPlayers(@NotNull String partialName) {
    String query = partialName.toLowerCase(java.util.Locale.ROOT);
    return onlinePlayers().stream()
        .filter(player -> player.username().toLowerCase(java.util.Locale.ROOT).contains(query))
        .toList();
  }

  @Override
  public @NotNull GameMode defaultGameMode() {
    return defaultGameMode;
  }

  @Override
  public void setDefaultGameMode(@NotNull GameMode gameMode) {
    defaultGameMode = Objects.requireNonNull(gameMode, "gameMode cannot be null");
  }

  @Override
  public @NotNull Component motd() {
    return motd;
  }

  @Override
  public void motd(@NotNull Component motd) {
    this.motd = Objects.requireNonNull(motd, "motd cannot be null");
  }

  @Override
  public int broadcast(@NotNull Component message, @NotNull String permission) {
    int count = 0;
    for (net.minestom.server.entity.Player player :
        net.minestom.server.MinecraftServer.getConnectionManager().getOnlinePlayers()) {
      if (player.getPermissionLevel() >= 4) {
        player.sendMessage(message);
        count++;
      }
    }
    return count;
  }

  @Override
  public @NotNull Inventory createInventory(
      @Nullable InventoryHolder holder, int size, @NotNull Component title) {
    if (size <= 0 || size > 54 || size % 9 != 0) {
      throw new IllegalArgumentException("Chest inventory size must be a positive multiple of 9");
    }
    net.minestom.server.inventory.InventoryType type =
        switch (size) {
          case 9 -> net.minestom.server.inventory.InventoryType.CHEST_1_ROW;
          case 18 -> net.minestom.server.inventory.InventoryType.CHEST_2_ROW;
          case 27 -> net.minestom.server.inventory.InventoryType.CHEST_3_ROW;
          case 36 -> net.minestom.server.inventory.InventoryType.CHEST_4_ROW;
          case 45 -> net.minestom.server.inventory.InventoryType.CHEST_5_ROW;
          case 54 -> net.minestom.server.inventory.InventoryType.CHEST_6_ROW;
          default ->
              throw new IllegalArgumentException("Unsupported chest inventory size: " + size);
        };
    return new MinestomInventoryWrapper(
        new net.minestom.server.inventory.Inventory(type, title), holder);
  }

  @Override
  public @NotNull Inventory createInventory(
      @Nullable InventoryHolder holder, @NotNull InventoryType type) {
    net.minestom.server.inventory.InventoryType minestomType = toMinestomType(type);
    return new MinestomInventoryWrapper(
        new net.minestom.server.inventory.Inventory(minestomType, Component.empty()), holder);
  }

  @Override
  public void savePlayers() {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.SERVER_PERSISTENCE,
        "Minestom does not provide a player-save operation.");
  }

  @Override
  public boolean onlineMode() {
    return false;
  }

  @Override
  public @NotNull ItemFactory itemFactory() {
    return new MinestomItemFactoryWrapper();
  }

  private static net.minestom.server.inventory.InventoryType toMinestomType(InventoryType type) {
    return switch (type) {
      case CHEST, BARREL, ENDER_CHEST, CHISELED_BOOKSHELF, COMPOSTER ->
          net.minestom.server.inventory.InventoryType.CHEST_3_ROW;
      case DISPENSER, DROPPER -> net.minestom.server.inventory.InventoryType.WINDOW_3X3;
      case FURNACE -> net.minestom.server.inventory.InventoryType.FURNACE;
      case WORKBENCH, CRAFTING, PLAYER, CREATIVE ->
          net.minestom.server.inventory.InventoryType.CRAFTING;
      case ENCHANTING -> net.minestom.server.inventory.InventoryType.ENCHANTMENT;
      case BREWING -> net.minestom.server.inventory.InventoryType.BREWING_STAND;
      case MERCHANT -> net.minestom.server.inventory.InventoryType.MERCHANT;
      case ANVIL -> net.minestom.server.inventory.InventoryType.ANVIL;
      case SMITHING -> net.minestom.server.inventory.InventoryType.SMITHING;
      case BEACON -> net.minestom.server.inventory.InventoryType.BEACON;
      case HOPPER -> net.minestom.server.inventory.InventoryType.HOPPER;
      case SHULKER_BOX -> net.minestom.server.inventory.InventoryType.SHULKER_BOX;
      case SMOKER -> net.minestom.server.inventory.InventoryType.SMOKER;
      case BLAST_FURNACE -> net.minestom.server.inventory.InventoryType.BLAST_FURNACE;
      case LECTERN -> net.minestom.server.inventory.InventoryType.LECTERN;
      case LOOM -> net.minestom.server.inventory.InventoryType.LOOM;
      case CARTOGRAPHY -> net.minestom.server.inventory.InventoryType.CARTOGRAPHY;
      case GRINDSTONE -> net.minestom.server.inventory.InventoryType.GRINDSTONE;
      case STONECUTTER -> net.minestom.server.inventory.InventoryType.STONE_CUTTER;
      case CRAFTER -> net.minestom.server.inventory.InventoryType.CRAFTER_3X3;
    };
  }

  private static net.minestom.server.command.CommandSender toMinestom(
      org.aincraft.api.domain.server.CommandSender sender) {
    if (sender instanceof MinestomConsoleCommandSenderWrapper wrapper) {
      return wrapper.getMinestomSender();
    }
    if (sender instanceof MinestomPlayerWrapper player) {
      return player.getMinestomPlayer();
    }
    throw new IllegalArgumentException("CommandSender is not backed by Minestom: " + sender);
  }
}
