package org.aincraft.bukkit.adapter;

import org.aincraft.api.domain.scoreboard.Scoreboard;
import org.aincraft.api.domain.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

/** Bukkit-backed scoreboard manager. */
public class BukkitScoreboardManagerWrapper implements ScoreboardManager {

  private final org.bukkit.scoreboard.ScoreboardManager manager;

  public BukkitScoreboardManagerWrapper(@NotNull org.bukkit.scoreboard.ScoreboardManager manager) {
    this.manager = manager;
  }

  public @NotNull org.bukkit.scoreboard.ScoreboardManager getBukkitScoreboardManager() {
    return manager;
  }

  @Override
  public @NotNull Scoreboard mainScoreboard() {
    return adaptScoreboard(manager.getMainScoreboard());
  }

  @Override
  public @NotNull Scoreboard newScoreboard() {
    return adaptScoreboard(manager.getNewScoreboard());
  }

  protected @NotNull Scoreboard adaptScoreboard(
      @NotNull org.bukkit.scoreboard.Scoreboard scoreboard) {
    return new BukkitScoreboardWrapper(scoreboard);
  }
}
