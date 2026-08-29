package org.aincraft.api.domain.server;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
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
import org.aincraft.api.domain.world.GameMode;
import org.aincraft.api.domain.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface Server extends Audience {

  @NotNull
  String version();

  @NotNull
  String name();

  int port();

  @NotNull
  String ip();

  int maxPlayers();

  @NotNull
  Collection<? extends Player> onlinePlayers();

  @NotNull
  Collection<? extends World> worlds();

  @Nullable
  World world(@NotNull Key key);

  @Nullable
  World world(@NotNull String name);

  @Nullable
  World world(@NotNull UUID uid);

  @Nullable
  Player player(@NotNull UUID uid);

  @Nullable
  Player player(@NotNull String name);

  void broadcast(@NotNull Component message);

  @NotNull
  ConsoleCommandSender consoleSender();

  void shutdown();

  void reload();

  // -- Additional Paper/Bukkit server contracts --

  default @NotNull String minecraftVersion() {
    throw new UnsupportedCapabilityException(Capability.SERVER_INFO);
  }

  default boolean dispatchCommand(@NotNull CommandSender sender, @NotNull String commandLine) {
    throw new UnsupportedCapabilityException(Capability.DISPATCH_COMMAND);
  }

  default @Nullable Entity entity(@NotNull UUID uniqueId) {
    throw new UnsupportedCapabilityException(Capability.ENTITY_LOOKUP);
  }

  default boolean isPrimaryThread() {
    throw new UnsupportedCapabilityException(Capability.PRIMARY_THREAD);
  }

  default int currentTick() {
    throw new UnsupportedCapabilityException(Capability.SERVER_TICK);
  }

  default @NotNull OfflinePlayer offlinePlayer(@NotNull UUID uniqueId) {
    throw new UnsupportedCapabilityException(Capability.OFFLINE_PLAYER);
  }

  default @NotNull OfflinePlayer offlinePlayer(@NotNull String name) {
    throw new UnsupportedCapabilityException(Capability.OFFLINE_PLAYER);
  }

  default @Nullable Player playerExact(@NotNull String name) {
    throw new UnsupportedCapabilityException(Capability.PLAYER_LOOKUP);
  }

  default @NotNull Collection<? extends Player> matchPlayers(@NotNull String partialName) {
    throw new UnsupportedCapabilityException(Capability.PLAYER_LOOKUP);
  }

  default @NotNull GameMode defaultGameMode() {
    throw new UnsupportedCapabilityException(Capability.WORLD_CONFIGURATION);
  }

  default void setDefaultGameMode(@NotNull GameMode gameMode) {
    throw new UnsupportedCapabilityException(Capability.WORLD_CONFIGURATION);
  }

  default @NotNull Component motd() {
    throw new UnsupportedCapabilityException(Capability.MOTD);
  }

  default void motd(@NotNull Component motd) {
    throw new UnsupportedCapabilityException(Capability.MOTD);
  }

  default int broadcast(@NotNull Component message, @NotNull String permission) {
    throw new UnsupportedCapabilityException(Capability.PERMISSION_BROADCAST);
  }

  default @NotNull Inventory createInventory(@Nullable InventoryHolder holder, int size) {
    return createInventory(holder, size, Component.empty());
  }

  default @NotNull Inventory createInventory(
      @Nullable InventoryHolder holder, int size, @NotNull Component title) {
    throw new UnsupportedCapabilityException(Capability.CREATE_INVENTORY);
  }

  default @NotNull Inventory createInventory(
      @Nullable InventoryHolder holder, @NotNull InventoryType type) {
    throw new UnsupportedCapabilityException(Capability.CREATE_INVENTORY);
  }

  default void savePlayers() {
    throw new UnsupportedCapabilityException(Capability.SERVER_PERSISTENCE);
  }

  default boolean onlineMode() {
    throw new UnsupportedCapabilityException(Capability.SERVER_INFO);
  }

  default @NotNull ItemFactory itemFactory() {
    throw new UnsupportedCapabilityException(Capability.CREATE_INVENTORY);
  }

  default @NotNull ScoreboardManager scoreboardManager() {
    throw new UnsupportedCapabilityException(Capability.SCOREBOARD);
  }

  default @NotNull Criteria scoreboardCriteria(@NotNull String name) {
    throw new UnsupportedCapabilityException(Capability.SCOREBOARD);
  }
}
