package org.aincraft.paper.adapter;

import org.aincraft.api.domain.scoreboard.Scoreboard;
import org.aincraft.bukkit.adapter.BukkitScoreboardManagerWrapper;
import org.jetbrains.annotations.NotNull;

/** Paper-backed scoreboard manager preserving Paper scoreboard wrappers. */
public class PaperScoreboardManagerWrapper extends BukkitScoreboardManagerWrapper {

  public PaperScoreboardManagerWrapper(@NotNull org.bukkit.scoreboard.ScoreboardManager manager) {
    super(manager);
  }

  @Override
  protected @NotNull Scoreboard adaptScoreboard(
      @NotNull org.bukkit.scoreboard.Scoreboard scoreboard) {
    return new PaperScoreboardWrapper(scoreboard);
  }
}
