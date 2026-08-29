package org.aincraft.bukkit.adapter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.aincraft.api.domain.entity.Player;
import org.aincraft.api.domain.inventory.Inventory;
import org.aincraft.api.domain.inventory.ItemStack;
import org.aincraft.api.domain.inventory.PlayerInventory;
import org.aincraft.api.domain.scoreboard.Scoreboard;
import org.aincraft.api.domain.location.Location;
import org.aincraft.api.domain.world.GameMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitPlayerWrapper extends BukkitLivingEntityWrapper implements Player {

  private final org.bukkit.entity.Player player;

  public BukkitPlayerWrapper(@NotNull org.bukkit.entity.Player player) {
    super(player);
    this.player = player;
  }

  public @NotNull org.bukkit.entity.Player getBukkitPlayer() {
    return player;
  }

  @Override
  public @NotNull String username() {
    return player.getName();
  }

  @Override
  public @NotNull Component displayName() {
    String name = player.getDisplayName();
    return name != null
        ? LegacyComponentSerializer.legacySection().deserialize(name)
        : Component.text(username());
  }

  @Override
  public void displayName(@NotNull Component displayName) {
    player.setDisplayName(LegacyComponentSerializer.legacySection().serialize(displayName));
  }

  @Override
  public boolean isOnline() {
    return player.isOnline();
  }

  @Override
  public int ping() {
    return player.getPing();
  }

  @Override
  public int foodLevel() {
    return player.getFoodLevel();
  }

  @Override
  public void setFoodLevel(int foodLevel) {
    player.setFoodLevel(foodLevel);
  }

  @Override
  public float saturation() {
    return player.getSaturation();
  }

  @Override
  public void setSaturation(float saturation) {
    player.setSaturation(saturation);
  }

  @Override
  public int level() {
    return player.getLevel();
  }

  @Override
  public void setLevel(int level) {
    player.setLevel(level);
  }

  @Override
  public float exp() {
    return player.getExp();
  }

  @Override
  public void setExp(float exp) {
    player.setExp(exp);
  }

  @Override
  public @NotNull GameMode gameMode() {
    return switch (player.getGameMode()) {
      case SURVIVAL -> GameMode.SURVIVAL;
      case CREATIVE -> GameMode.CREATIVE;
      case ADVENTURE -> GameMode.ADVENTURE;
      case SPECTATOR -> GameMode.SPECTATOR;
    };
  }

  @Override
  public void setGameMode(@NotNull GameMode gameMode) {
    org.bukkit.GameMode bMode =
        switch (gameMode) {
          case SURVIVAL -> org.bukkit.GameMode.SURVIVAL;
          case CREATIVE -> org.bukkit.GameMode.CREATIVE;
          case ADVENTURE -> org.bukkit.GameMode.ADVENTURE;
          case SPECTATOR -> org.bukkit.GameMode.SPECTATOR;
        };
    player.setGameMode(bMode);
  }

  @Override
  public boolean isSneaking() {
    return player.isSneaking();
  }

  @Override
  public void setSneaking(boolean sneaking) {
    player.setSneaking(sneaking);
  }

  @Override
  public boolean isSprinting() {
    return player.isSprinting();
  }

  @Override
  public void setSprinting(boolean sprinting) {
    player.setSprinting(sprinting);
  }

  @Override
  public boolean isFlying() {
    return player.isFlying();
  }

  @Override
  public void setFlying(boolean flying) {
    player.setFlying(flying);
  }

  @Override
  public boolean allowFlight() {
    return player.getAllowFlight();
  }

  @Override
  public void setAllowFlight(boolean allow) {
    player.setAllowFlight(allow);
  }

  @Override
  public @NotNull PlayerInventory inventory() {
    return new BukkitPlayerInventoryWrapper(player.getInventory());
  }

  @Override
  public @NotNull Scoreboard scoreboard() {
    return BukkitAdapters.adapt(player.getScoreboard());
  }

  @Override
  public void scoreboard(@NotNull Scoreboard scoreboard) {
    player.setScoreboard(BukkitAdapters.toBukkit(scoreboard));
  }

  @Override
  public @NotNull org.aincraft.api.domain.inventory.InventoryView openInventory() {
    return BukkitAdapters.adapt(player.getOpenInventory());
  }

  @Override
  public @NotNull org.aincraft.api.domain.inventory.InventoryView openInventory(
      @NotNull org.aincraft.api.domain.inventory.Inventory inventory) {
    player.openInventory(BukkitAdapters.toBukkit(inventory));
    return openInventory();
  }

  @Override
  public void closeInventory() {
    player.closeInventory();
  }

  @Override
  public boolean hasPermission(@NotNull String permission) {
    return player.hasPermission(permission);
  }

  @Override
  public boolean isOp() {
    return player.isOp();
  }

  @Override
  public @NotNull org.aincraft.api.domain.server.Server server() {
    return BukkitAdapters.adapt(player.getServer());
  }

  @Override
  public void setOp(boolean op) {
    player.setOp(op);
  }

  @Override
  public void kick(@NotNull Component reason) {
    player.kickPlayer(LegacyComponentSerializer.legacySection().serialize(reason));
  }

  @Override
  public void sendMessage(@NotNull Component message) {
    player.sendMessage(LegacyComponentSerializer.legacySection().serialize(message));
  }

  @Override
  public void sendActionBar(@NotNull Component message) {
    String legacy = LegacyComponentSerializer.legacySection().serialize(message);
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(legacy));
  }

  @Override
  public @NotNull Inventory enderChest() {
    return BukkitAdapters.adapt(player.getEnderChest());
  }

  @Override
  public @Nullable ItemStack itemOnCursor() {
    org.bukkit.inventory.ItemStack cursor = player.getItemOnCursor();
    return cursor == null || cursor.getType().isAir() ? null : BukkitAdapters.adapt(cursor);
  }

  @Override
  public void setItemOnCursor(@Nullable ItemStack item) {
    if (item == null) {
      player.setItemOnCursor(null);
    } else {
      org.bukkit.inventory.ItemStack bukkit = BukkitAdapters.toBukkit(item);
      player.setItemOnCursor(bukkit);
    }
  }

  @Override
  public void sendEntityEffect(
      @NotNull org.aincraft.api.domain.effect.EntityEffect effect,
      @NotNull org.aincraft.api.domain.entity.Entity entity) {
    String name = effect.name();
    try {
      player
          .getClass()
          .getMethod(
              "sendEntityEffect", org.bukkit.EntityEffect.class, org.bukkit.entity.Entity.class)
          .invoke(player, org.bukkit.EntityEffect.valueOf(name), BukkitAdapters.toBukkit(entity));
    } catch (NoSuchMethodException e) {
      throw new org.aincraft.api.UnsupportedCapabilityException(
          org.aincraft.api.Capability.ENTITY_LOOKUP,
          "Spigot Player does not expose sendEntityEffect.");
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Unable to send entity effect", e);
    }
  }

  @Override
  public float exhaustion() {
    return player.getExhaustion();
  }

  @Override
  public void setExhaustion(float exhaustion) {
    player.setExhaustion(exhaustion);
  }

  @Override
  public @Nullable Location bedSpawnLocation() {
    org.bukkit.Location loc = player.getBedSpawnLocation();
    return loc == null ? null : new BukkitLocationWrapper(loc);
  }

  @Override
  public void setBedSpawnLocation(@Nullable Location location, boolean force) {
    if (location == null) {
      player.setBedSpawnLocation(null, force);
    } else {
      player.setBedSpawnLocation(BukkitAdapters.toBukkit(location), force);
    }
  }

  @Override
  public String toString() {
    return "BukkitPlayerWrapper{name=" + username() + ", uuid=" + uniqueId() + "}";
  }
}
