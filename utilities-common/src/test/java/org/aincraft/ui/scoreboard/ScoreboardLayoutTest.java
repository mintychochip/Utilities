package org.aincraft.ui.scoreboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

class ScoreboardLayoutTest {

  @Test
  void layoutCopiesLinesAndRejectsDuplicates() {
    List<ScoreboardLine> source = new ArrayList<>();
    source.add(new ScoreboardLine("one", Component.text("One")));

    ScoreboardLayout layout = new ScoreboardLayout(Component.text("Title"), source);
    source.clear();

    assertEquals(1, layout.lines().size());
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new ScoreboardLayout(
                Component.text("Title"),
                List.of(
                    new ScoreboardLine("same", Component.empty()),
                    new ScoreboardLine("same", Component.empty()))));
  }

  @Test
  void layoutRejectsMoreThanFifteenRows() {
    List<ScoreboardLine> lines =
        IntStream.range(0, 16)
            .mapToObj(i -> new ScoreboardLine("line-" + i, Component.text(i)))
            .toList();

    assertThrows(
        IllegalArgumentException.class, () -> new ScoreboardLayout(Component.empty(), lines));
  }
}
