package org.aincraft.bukkit.adapter;

import org.aincraft.api.domain.scoreboard.Objective;
import org.aincraft.api.domain.scoreboard.Score;
import org.aincraft.api.domain.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Spigot-backed score entry. */
public class BukkitScoreWrapper implements Score {

  private final org.bukkit.scoreboard.Score score;

  public BukkitScoreWrapper(@NotNull org.bukkit.scoreboard.Score score) {
    this.score = score;
  }

  public @NotNull org.bukkit.scoreboard.Score getBukkitScore() {
    return score;
  }

  @Override
  public @NotNull String entry() {
    return score.getEntry();
  }

  @Override
  public @NotNull Objective objective() {
    return BukkitAdapters.adapt(score.getObjective());
  }

  @Override
  public @Nullable Scoreboard scoreboard() {
    org.bukkit.scoreboard.Scoreboard scoreboard = score.getScoreboard();
    return scoreboard == null ? null : BukkitAdapters.adapt(scoreboard);
  }

  @Override
  public int score() {
    return score.getScore();
  }

  @Override
  public void score(int value) {
    score.setScore(value);
  }

  @Override
  public boolean isScoreSet() {
    return score.isScoreSet();
  }

  @Override
  public void reset() {
    org.bukkit.scoreboard.Objective objective = score.getObjective();
    org.bukkit.scoreboard.Scoreboard scoreboard = objective.getScoreboard();
    if (scoreboard == null) {
      throw new IllegalStateException("Cannot reset a score on an unregistered objective");
    }
    scoreboard.resetScores(score.getEntry());
  }
}
