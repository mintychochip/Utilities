package org.aincraft.bukkit.adapter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.aincraft.common.entity.Player;
import org.aincraft.common.inventory.PlayerInventory;
import org.aincraft.common.world.GameMode;
import org.jetbrains.annotations.NotNull;

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
    org.bukkit.GameMode bMode = switch (gameMode) {
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
  public @NotNull PlayerInventory inventory() {
    return new BukkitPlayerInventoryWrapper(player.getInventory());
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
  public String toString() {
    return "BukkitPlayerWrapper{name=" + username() + ", uuid=" + uniqueId() + "}";
  }
}
