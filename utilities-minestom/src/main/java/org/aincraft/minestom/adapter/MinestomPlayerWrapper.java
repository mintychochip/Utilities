package org.aincraft.minestom.adapter;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.aincraft.common.entity.Player;
import org.aincraft.common.inventory.PlayerInventory;
import org.aincraft.common.world.GameMode;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MinestomPlayerWrapper extends MinestomLivingEntityWrapper
    implements Player, ForwardingAudience.Single {

  private final net.minestom.server.entity.Player player;
  private final PlayerInventory inventory;

  public MinestomPlayerWrapper(@NotNull net.minestom.server.entity.Player player) {
    super(player);
    this.player = player;
    this.inventory = new MinestomPlayerInventoryWrapper(player.getInventory(), this);
  }

  public @NotNull net.minestom.server.entity.Player getMinestomPlayer() {
    return player;
  }

  @Override
  public @NotNull Audience audience() {
    return player;
  }

  @Override
  public @NotNull String username() {
    return player.getUsername();
  }

  @Override
  public boolean isOnline() {
    return player.isOnline();
  }

  @Override
  public int ping() {
    return player.getLatency();
  }

  @Override
  public int foodLevel() {
    return player.getFood();
  }

  @Override
  public void setFoodLevel(int foodLevel) {
    player.setFood(foodLevel);
  }

  @Override
  public float saturation() {
    return player.getFoodSaturation();
  }

  @Override
  public void setSaturation(float saturation) {
    player.setFoodSaturation(saturation);
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
    Objects.requireNonNull(gameMode, "gameMode cannot be null");
    net.minestom.server.entity.GameMode mMode =
        switch (gameMode) {
          case SURVIVAL -> net.minestom.server.entity.GameMode.SURVIVAL;
          case CREATIVE -> net.minestom.server.entity.GameMode.CREATIVE;
          case ADVENTURE -> net.minestom.server.entity.GameMode.ADVENTURE;
          case SPECTATOR -> net.minestom.server.entity.GameMode.SPECTATOR;
        };
    player.setGameMode(mMode);
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
    return inventory;
  }

  @Override
  public boolean hasPermission(@NotNull String permission) {
    return isOp();
  }

  @Override
  public boolean isOp() {
    return player.getPermissionLevel() >= 4;
  }

  @Override
  public void setOp(boolean op) {
    player.setPermissionLevel(op ? 4 : 0);
  }

  @Override
  public void kick(@NotNull Component reason) {
    player.kick(reason);
  }

  @Override
  public void sendMessage(@NotNull Component message) {
    player.sendMessage(message);
  }

  @Override
  public void sendActionBar(@NotNull Component message) {
    player.sendActionBar(message);
  }

  @Override
  public void showTitle(@NotNull Title title) {
    player.showTitle(title);
  }

  @Override
  public void clearTitle() {
    player.clearTitle();
  }

  @Override
  public void resetTitle() {
    player.resetTitle();
  }

  @Override
  public void playSound(@NotNull Sound sound) {
    player.playSound(sound);
  }

  @Override
  public void stopSound(@NotNull SoundStop stop) {
    player.stopSound(stop);
  }

  @Override
  public String toString() {
    return "MinestomPlayerWrapper{username=" + username() + ", uuid=" + uniqueId() + "}";
  }

  @Override
  public boolean allowFlight() {
    return false;
  }

  @Override
  public void setAllowFlight(boolean allow) {
    throw new UnsupportedOperationException();
  }

  @Override
  public @NotNull net.kyori.adventure.text.Component displayName() {
    return net.kyori.adventure.text.Component.text(username());
  }

  @Override
  public void displayName(@NotNull net.kyori.adventure.text.Component displayName) {
    throw new UnsupportedOperationException();
  }
}
