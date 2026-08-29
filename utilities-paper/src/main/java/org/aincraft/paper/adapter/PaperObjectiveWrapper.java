package org.aincraft.paper.adapter;

import net.kyori.adventure.text.Component;
import org.aincraft.api.domain.scoreboard.Objective;
import org.aincraft.api.domain.scoreboard.Score;
import org.aincraft.api.domain.scoreboard.Scoreboard;
import org.aincraft.bukkit.adapter.BukkitObjectiveWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Paper-backed objective using native Adventure component methods. */
public class PaperObjectiveWrapper extends BukkitObjectiveWrapper {

  public PaperObjectiveWrapper(@NotNull org.bukkit.scoreboard.Objective objective) {
    super(objective);
  }

  @Override
  public @NotNull Component displayName() {
    return getBukkitObjective().displayName();
  }

  @Override
  public void displayName(@NotNull Component displayName) {
    getBukkitObjective().displayName(displayName);
  }

  @Override
  public @Nullable Scoreboard scoreboard() {
    org.bukkit.scoreboard.Scoreboard scoreboard = getBukkitObjective().getScoreboard();
    return scoreboard == null ? null : PaperAdapters.adapt(scoreboard);
  }

  @Override
  public @NotNull Score score(@NotNull String entry) {
    return new PaperScoreWrapper(getBukkitObjective().getScore(entry));
  }

  @Override
  public boolean willAutoUpdateDisplay() {
    return getBukkitObjective().willAutoUpdateDisplay();
  }

  @Override
  public void setAutoUpdateDisplay(boolean enabled) {
    getBukkitObjective().setAutoUpdateDisplay(enabled);
  }
}
