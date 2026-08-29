package org.aincraft.api.domain.scoreboard;

import org.jetbrains.annotations.NotNull;

/** Creates and accesses scoreboards tracked by a server. */
public interface ScoreboardManager {

  /** Returns the server's persistent main scoreboard. */
  @NotNull Scoreboard mainScoreboard();

  /** Creates a new scoreboard tracked while a reference is retained. */
  @NotNull Scoreboard newScoreboard();
}
