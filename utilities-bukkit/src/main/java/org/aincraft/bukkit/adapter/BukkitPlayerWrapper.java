package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.aincraft.common.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BukkitPlayerWrapper extends BukkitEntityWrapper implements Player {

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
  public double health() {
    return player.getHealth();
  }

  @Override
  public double maxHealth() {
    org.bukkit.attribute.AttributeInstance attr = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH);
    return attr != null ? attr.getValue() : 20.0;
  }

  @Override
  public int foodLevel() {
    return player.getFoodLevel();
  }

  @Override
  public float saturation() {
    return player.getSaturation();
  }

  @Override
  public int level() {
    return player.getLevel();
  }

  @Override
  public float exp() {
    return player.getExp();
  }

  @Override
  public @NotNull Key gameMode() {
    return Key.key("minecraft", player.getGameMode().name().toLowerCase(java.util.Locale.ROOT));
  }

  @Override
  public boolean isSneaking() {
    return player.isSneaking();
  }

  @Override
  public boolean isSprinting() {
    return player.isSprinting();
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
  public void setSneaking(boolean sneaking) {
    player.setSneaking(sneaking);
  }

  @Override
  public void setSprinting(boolean sprinting) {
    player.setSprinting(sprinting);
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
  public String toString() {
    return "BukkitPlayerWrapper{name=" + username() + ", uuid=" + uniqueId() + "}";
  }
}
