package org.aincraft.bukkit.adapter;

import java.util.Objects;
import java.util.UUID;
import net.kyori.adventure.identity.Identity;
import org.aincraft.common.entity.Player;
import org.aincraft.common.server.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitOfflinePlayerWrapper implements OfflinePlayer {

  private final org.bukkit.OfflinePlayer offlinePlayer;

  public BukkitOfflinePlayerWrapper(@NotNull org.bukkit.OfflinePlayer offlinePlayer) {
    this.offlinePlayer = Objects.requireNonNull(offlinePlayer, "offlinePlayer cannot be null");
  }

  public @NotNull org.bukkit.OfflinePlayer getBukkitOfflinePlayer() {
    return offlinePlayer;
  }

  @Override
  public @NotNull UUID uniqueId() {
    return offlinePlayer.getUniqueId();
  }

  @Override
  public @Nullable String name() {
    return offlinePlayer.getName();
  }

  @Override
  public boolean hasPlayedBefore() {
    return offlinePlayer.hasPlayedBefore();
  }

  @Override
  public boolean isOnline() {
    return offlinePlayer.isOnline();
  }

  @Override
  public @Nullable Player player() {
    org.bukkit.entity.Player player = offlinePlayer.getPlayer();
    return player != null ? BukkitAdapters.adapt(player) : null;
  }

  @Override
  public long lastPlayed() {
    return offlinePlayer.getLastPlayed();
  }

  @Override
  public boolean isWhitelisted() {
    return offlinePlayer.isWhitelisted();
  }

  @Override
  public void setWhitelisted(boolean whitelisted) {
    offlinePlayer.setWhitelisted(whitelisted);
  }

  @Override
  public boolean isBanned() {
    return offlinePlayer.isBanned();
  }

  @Override
  public boolean isOp() {
    return offlinePlayer.isOp();
  }

  @Override
  public void setOp(boolean op) {
    offlinePlayer.setOp(op);
  }

  @Override
  public @NotNull Identity identity() {
    return Identity.identity(uniqueId());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof OfflinePlayer that)) return false;
    return uniqueId().equals(that.uniqueId());
  }

  @Override
  public int hashCode() {
    return uniqueId().hashCode();
  }

  @Override
  public String toString() {
    return "BukkitOfflinePlayerWrapper{uuid=" + uniqueId() + ", name=" + name() + "}";
  }
}
