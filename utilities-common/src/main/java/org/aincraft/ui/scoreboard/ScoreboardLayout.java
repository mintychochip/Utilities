package org.aincraft.ui.scoreboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/** An immutable title and ordered set of sidebar lines. */
public record ScoreboardLayout(@NotNull Component title, @NotNull List<ScoreboardLine> lines) {

  public static final int MAX_LINES = 15;

  public ScoreboardLayout {
    Objects.requireNonNull(title, "title");
    Objects.requireNonNull(lines, "lines");
    if (lines.size() > MAX_LINES) {
      throw new IllegalArgumentException("A sidebar cannot contain more than " + MAX_LINES + " lines");
    }
    List<ScoreboardLine> copy = List.copyOf(lines);
    Set<String> ids = new HashSet<>();
    for (ScoreboardLine line : copy) {
      if (!ids.add(line.id())) {
        throw new IllegalArgumentException("Duplicate sidebar line id: " + line.id());
      }
    }
    lines = copy;
  }

  public static @NotNull ScoreboardLayout of(
      @NotNull Component title, @NotNull ScoreboardLine... lines) {
    Objects.requireNonNull(lines, "lines");
    return new ScoreboardLayout(title, List.of(lines));
  }

  public static @NotNull Builder builder(@NotNull Component title) {
    return new Builder(title);
  }

  /** Mutable construction helper that produces immutable layouts. */
  public static final class Builder {

    private final Component title;
    private final List<ScoreboardLine> lines = new ArrayList<>();

    private Builder(@NotNull Component title) {
      this.title = Objects.requireNonNull(title, "title");
    }

    public @NotNull Builder line(@NotNull String id, @NotNull Component content) {
      lines.add(new ScoreboardLine(id, content));
      return this;
    }

    public @NotNull ScoreboardLayout build() {
      return new ScoreboardLayout(title, lines);
    }
  }
}
