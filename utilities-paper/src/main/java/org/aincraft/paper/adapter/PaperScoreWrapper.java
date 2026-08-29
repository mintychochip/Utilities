package org.aincraft.paper.adapter;

import net.kyori.adventure.text.Component;
import org.aincraft.api.domain.scoreboard.Objective;
import org.aincraft.api.domain.scoreboard.Score;
import org.aincraft.api.domain.scoreboard.Scoreboard;
import org.aincraft.bukkit.adapter.BukkitScoreWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Paper-backed score entry exposing Paper's optional display operations. */
public class PaperScoreWrapper extends BukkitScoreWrapper {

  public PaperScoreWrapper(@NotNull org.bukkit.scoreboard.Score score) {
    super(score);
  }

  @Override
  public @NotNull Objective objective() {
    return new PaperObjectiveWrapper(getBukkitScore().getObjective());
  }

  @Override
  public @Nullable Scoreboard scoreboard() {
    org.bukkit.scoreboard.Scoreboard scoreboard = getBukkitScore().getScoreboard();
    return scoreboard == null ? null : PaperAdapters.adapt(scoreboard);
  }

  @Override
  public @Nullable Component customName() {
    return getBukkitScore().customName();
  }

  @Override
  public void customName(@Nullable Component customName) {
    getBukkitScore().customName(customName);
  }

  @Override
  public boolean isTriggerable() {
    return getBukkitScore().isTriggerable();
  }

  @Override
  public void setTriggerable(boolean triggerable) {
    getBukkitScore().setTriggerable(triggerable);
  }
}
