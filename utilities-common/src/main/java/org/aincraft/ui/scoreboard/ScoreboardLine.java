package org.aincraft.ui.scoreboard;

import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/** One stable, ordered line in a sidebar layout. */
public record ScoreboardLine(@NotNull String id, @NotNull Component content) {

  public ScoreboardLine {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(content, "content");
    if (id.isBlank()) {
      throw new IllegalArgumentException("Line id must not be blank");
    }
  }
}
