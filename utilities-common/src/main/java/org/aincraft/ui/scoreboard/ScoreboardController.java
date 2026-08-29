package org.aincraft.ui.scoreboard;

import net.kyori.adventure.text.Component;
import org.aincraft.api.domain.entity.Player;
import org.aincraft.api.domain.scoreboard.Criteria;
import org.aincraft.api.domain.scoreboard.DisplaySlot;
import org.aincraft.api.domain.scoreboard.Objective;
import org.aincraft.api.domain.scoreboard.RenderType;
import org.aincraft.api.domain.scoreboard.Scoreboard;
import org.aincraft.api.domain.scoreboard.ScoreboardManager;
import org.aincraft.api.domain.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

/** Renders immutable layouts as private per-player sidebars. */
public final class ScoreboardController implements AutoCloseable {

  private static final String TEAM_PREFIX = "aincraft_ui_";

  private final ScoreboardManager manager;
  private final String objectiveName;
  private final Map<UUID, ViewerState> viewers = new HashMap<>();
  private boolean closed;

  public ScoreboardController(@NotNull ScoreboardManager manager, @NotNull String objectiveName) {
    this.manager = Objects.requireNonNull(manager, "manager");
    this.objectiveName = Objects.requireNonNull(objectiveName, "objectiveName");
    if (objectiveName.isBlank()) {
      throw new IllegalArgumentException("Objective name must not be blank");
    }
  }

  public void show(@NotNull Player player, @NotNull ScoreboardLayout layout) {
    ensureOpen();
    Objects.requireNonNull(player, "player");
    Objects.requireNonNull(layout, "layout");
    UUID uniqueId = Objects.requireNonNull(player.uniqueId(), "player uniqueId");
    ViewerState state = viewers.get(uniqueId);
    if (state == null) {
      state = createState(player);
      viewers.put(uniqueId, state);
    } else {
      state.player = player;
    }
    player.scoreboard(state.scoreboard);
    render(state, layout);
  }

  public void update(@NotNull Player player, @NotNull ScoreboardLayout layout) {
    show(player, layout);
  }

  public void refresh(
      @NotNull Player player,
      @NotNull Function<? super Player, ? extends ScoreboardLayout> renderer) {
    ensureOpen();
    Objects.requireNonNull(player, "player");
    Objects.requireNonNull(renderer, "renderer");
    show(player, Objects.requireNonNull(renderer.apply(player), "renderer result"));
  }

  public void hide(@NotNull Player player) {
    Objects.requireNonNull(player, "player");
    UUID uniqueId = Objects.requireNonNull(player.uniqueId(), "player uniqueId");
    ViewerState state = viewers.remove(uniqueId);
    if (state == null) {
      return;
    }
    player.scoreboard(state.previousScoreboard);
    cleanup(state);
  }

  public boolean isShown(@NotNull Player player) {
    ensureOpen();
    Objects.requireNonNull(player, "player");
    UUID uniqueId = Objects.requireNonNull(player.uniqueId(), "player uniqueId");
    return viewers.containsKey(uniqueId);
  }

  @Override
  public void close() {
    if (closed) {
      return;
    }
    for (ViewerState state : List.copyOf(viewers.values())) {
      state.player.scoreboard(state.previousScoreboard);
      cleanup(state);
    }
    viewers.clear();
    closed = true;
  }

  private ViewerState createState(Player player) {
    Scoreboard previousScoreboard =
        Objects.requireNonNull(player.scoreboard(), "player scoreboard");
    Scoreboard scoreboard = manager.newScoreboard();
    Objective objective =
        scoreboard.registerObjective(
            objectiveName, Criteria.DUMMY, Component.empty(), RenderType.INTEGER);
    objective.displaySlot(DisplaySlot.SIDEBAR);
    return new ViewerState(player, previousScoreboard, scoreboard, objective);
  }

  private void render(ViewerState state, ScoreboardLayout layout) {
    state.objective.displayName(layout.title());

    Set<String> activeIds = new HashSet<>();
    for (ScoreboardLine line : layout.lines()) {
      activeIds.add(line.id());
    }
    state.lines.entrySet().removeIf(entry -> removeStaleLine(state, entry, activeIds));

    Set<Integer> usedSlots = new HashSet<>();
    for (LineState lineState : state.lines.values()) {
      usedSlots.add(lineState.slot);
    }
    for (int index = 0; index < layout.lines().size(); index++) {
      ScoreboardLine line = layout.lines().get(index);
      LineState lineState = state.lines.get(line.id());
      if (lineState == null) {
        int slot = firstUnusedSlot(usedSlots);
        usedSlots.add(slot);
        String entry = "\u00a7" + Integer.toHexString(slot);
        Team team = state.scoreboard.registerTeam(TEAM_PREFIX + Integer.toHexString(slot));
        lineState = new LineState(team, entry, slot);
        state.lines.put(line.id(), lineState);
      }
      lineState.team.prefix(line.content());
      lineState.team.suffix(Component.empty());
      if (!lineState.team.hasEntry(lineState.entry)) {
        lineState.team.addEntry(lineState.entry);
      }
      state.objective.score(lineState.entry).score(layout.lines().size() - index);
    }
  }

  private boolean removeStaleLine(
      ViewerState state, Map.Entry<String, LineState> entry, Set<String> activeIds) {
    if (activeIds.contains(entry.getKey())) {
      return false;
    }
    LineState lineState = entry.getValue();
    state.scoreboard.resetScores(lineState.entry);
    lineState.team.removeEntry(lineState.entry);
    lineState.team.unregister();
    return true;
  }

  private static int firstUnusedSlot(Set<Integer> usedSlots) {
    for (int slot = 0; slot < ScoreboardLayout.MAX_LINES; slot++) {
      if (!usedSlots.contains(slot)) {
        return slot;
      }
    }
    throw new IllegalStateException("No sidebar line slots available");
  }

  private static void cleanup(ViewerState state) {
    for (LineState lineState : state.lines.values()) {
      state.scoreboard.resetScores(lineState.entry);
      lineState.team.removeEntry(lineState.entry);
      lineState.team.unregister();
    }
    state.lines.clear();
    state.objective.unregister();
  }

  private void ensureOpen() {
    if (closed) {
      throw new IllegalStateException("Scoreboard controller is closed");
    }
  }

  private static final class ViewerState {

    private Player player;
    private final Scoreboard previousScoreboard;
    private final Scoreboard scoreboard;
    private final Objective objective;
    private final Map<String, LineState> lines = new HashMap<>();

    private ViewerState(
        Player player, Scoreboard previousScoreboard, Scoreboard scoreboard, Objective objective) {
      this.player = player;
      this.previousScoreboard = previousScoreboard;
      this.scoreboard = scoreboard;
      this.objective = objective;
    }
  }

  private record LineState(Team team, String entry, int slot) {}
}
