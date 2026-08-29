package org.aincraft.paper.adapter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.aincraft.api.domain.scoreboard.Scoreboard;
import org.aincraft.bukkit.adapter.BukkitTeamWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Paper-backed team using native Adventure component methods. */
public class PaperTeamWrapper extends BukkitTeamWrapper {

  public PaperTeamWrapper(@NotNull org.bukkit.scoreboard.Team team) {
    super(team);
  }

  @Override
  public @NotNull Component displayName() {
    return getBukkitTeam().displayName();
  }

  @Override
  public void displayName(@NotNull Component displayName) {
    getBukkitTeam().displayName(displayName);
  }

  @Override
  public @NotNull Component prefix() {
    return getBukkitTeam().prefix();
  }

  @Override
  public void prefix(@NotNull Component prefix) {
    getBukkitTeam().prefix(prefix);
  }

  @Override
  public @NotNull Component suffix() {
    return getBukkitTeam().suffix();
  }

  @Override
  public void suffix(@NotNull Component suffix) {
    getBukkitTeam().suffix(suffix);
  }

  @Override
  public @Nullable NamedTextColor color() {
    TextColor color = getBukkitTeam().color();
    return color instanceof NamedTextColor named ? named : null;
  }

  @Override
  public void color(@Nullable NamedTextColor color) {
    getBukkitTeam().color(color);
  }

  @Override
  public @Nullable Scoreboard scoreboard() {
    org.bukkit.scoreboard.Scoreboard scoreboard = getBukkitTeam().getScoreboard();
    return scoreboard == null ? null : PaperAdapters.adapt(scoreboard);
  }
}
