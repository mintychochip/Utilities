package org.aincraft.paper.adapter;

import org.aincraft.api.domain.scoreboard.Objective;
import org.aincraft.api.domain.scoreboard.Score;
import org.aincraft.api.domain.scoreboard.Team;
import org.aincraft.bukkit.adapter.BukkitScoreboardWrapper;
import org.jetbrains.annotations.NotNull;

/** Paper-backed scoreboard that keeps nested values on the Paper adapter path. */
public class PaperScoreboardWrapper extends BukkitScoreboardWrapper {

  public PaperScoreboardWrapper(@NotNull org.bukkit.scoreboard.Scoreboard scoreboard) {
    super(scoreboard);
  }

  @Override
  protected @NotNull Objective adaptObjective(@NotNull org.bukkit.scoreboard.Objective objective) {
    return new PaperObjectiveWrapper(objective);
  }

  @Override
  protected @NotNull Score adaptScore(@NotNull org.bukkit.scoreboard.Score score) {
    return new PaperScoreWrapper(score);
  }

  @Override
  protected @NotNull Team adaptTeam(@NotNull org.bukkit.scoreboard.Team team) {
    return new PaperTeamWrapper(team);
  }
}
