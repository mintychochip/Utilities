package org.aincraft.ui.scoreboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.aincraft.api.domain.entity.Player;
import org.aincraft.api.domain.scoreboard.Criteria;
import org.aincraft.api.domain.scoreboard.DisplaySlot;
import org.aincraft.api.domain.scoreboard.Objective;
import org.aincraft.api.domain.scoreboard.RenderType;
import org.aincraft.api.domain.scoreboard.Score;
import org.aincraft.api.domain.scoreboard.Scoreboard;
import org.aincraft.api.domain.scoreboard.ScoreboardManager;
import org.aincraft.api.domain.scoreboard.Team;
import org.aincraft.api.domain.scoreboard.TeamOption;
import org.aincraft.api.domain.scoreboard.TeamOptionStatus;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

class ScoreboardControllerTest {

  @Test
  void showCreatesPrivateSidebarWithDescendingScores() {
    FakeScoreboard previous = new FakeScoreboard();
    Player player = player(UUID.randomUUID(), previous);
    ScoreboardController controller =
        new ScoreboardController(new FakeScoreboardManager(), "sidebar");

    controller.show(
        player,
        ScoreboardLayout.of(
            Component.text("Title"),
            new ScoreboardLine("first", Component.text("First")),
            new ScoreboardLine("second", Component.text("Second"))));

    FakeScoreboard shown = (FakeScoreboard) currentScoreboard(player);
    assertNotSame(previous, shown);
    FakeObjective objective = shown.objectiveByName("sidebar");
    assertEquals(Component.text("Title"), objective.displayName());
    assertEquals(2, shown.scoreForContent(objective, Component.text("First")));
    assertEquals(1, shown.scoreForContent(objective, Component.text("Second")));
    assertEquals(DisplaySlot.SIDEBAR, objective.displaySlot());
    assertTrue(controller.isShown(player));
  }

  @Test
  void updateRemovesStaleLinesAndReusesExistingLineState() {
    Player player = player(UUID.randomUUID(), new FakeScoreboard());
    ScoreboardController controller =
        new ScoreboardController(new FakeScoreboardManager(), "sidebar");
    controller.show(
        player,
        ScoreboardLayout.of(
            Component.empty(),
            new ScoreboardLine("first", Component.text("First")),
            new ScoreboardLine("second", Component.text("Second"))));

    FakeScoreboard shown = (FakeScoreboard) currentScoreboard(player);
    FakeTeam secondTeam = shown.teamWithPrefix(Component.text("Second"));
    FakeTeam firstTeam = shown.teamWithPrefix(Component.text("First"));

    controller.update(
        player,
        ScoreboardLayout.of(
            Component.empty(),
            new ScoreboardLine("second", Component.text("Second")),
            new ScoreboardLine("third", Component.text("Third"))));

    assertFalse(firstTeam.registered);
    assertTrue(secondTeam.registered);
    assertEquals(2, shown.registeredTeams().size());
    FakeObjective objective = shown.objectiveByName("sidebar");
    assertEquals(2, shown.scoreForContent(objective, Component.text("Second")));
    assertEquals(1, shown.scoreForContent(objective, Component.text("Third")));
    assertEquals(2, shown.entries().size());
  }

  @Test
  void separatePlayersReceiveSeparateScoreboards() {
    FakeScoreboardManager manager = new FakeScoreboardManager();
    ScoreboardController controller = new ScoreboardController(manager, "sidebar");
    Player first = player(UUID.randomUUID(), new FakeScoreboard());
    Player second = player(UUID.randomUUID(), new FakeScoreboard());
    ScoreboardLayout layout = ScoreboardLayout.of(Component.text("Title"));

    controller.show(first, layout);
    controller.show(second, layout);

    assertNotSame(currentScoreboard(first), currentScoreboard(second));
    assertEquals(2, manager.created.size());
  }

  @Test
  void hideRestoresPreviousScoreboardAndCloseStopsFurtherUpdates() {
    FakeScoreboard previous = new FakeScoreboard();
    Player player = player(UUID.randomUUID(), previous);
    ScoreboardController controller =
        new ScoreboardController(new FakeScoreboardManager(), "sidebar");
    controller.show(player, ScoreboardLayout.of(Component.empty()));
    FakeScoreboard shown = (FakeScoreboard) currentScoreboard(player);
    FakeObjective objective = shown.objectiveByName("sidebar");

    controller.hide(player);

    assertSame(previous, currentScoreboard(player));
    assertFalse(controller.isShown(player));
    assertTrue(objective.unregistered);
    assertTrue(shown.registeredTeams().isEmpty());

    controller.close();
    assertThrows(
        IllegalStateException.class,
        () -> controller.update(player, ScoreboardLayout.of(Component.empty())));
    assertThrows(IllegalStateException.class, () -> controller.isShown(player));
  }

  @Test
  void hideCleansUpWhenPreviousScoreboardRestorationFails() {
    FakeScoreboard previous = new FakeScoreboard();
    AtomicBoolean restorationAttempted = new AtomicBoolean();
    Player player = failingRestorePlayer(UUID.randomUUID(), previous, restorationAttempted);
    ScoreboardController controller =
        new ScoreboardController(new FakeScoreboardManager(), "sidebar");

    controller.show(player, ScoreboardLayout.of(Component.empty()));
    FakeScoreboard shown = (FakeScoreboard) currentScoreboard(player);
    FakeObjective objective = shown.objectiveByName("sidebar");

    assertThrows(IllegalStateException.class, () -> controller.hide(player));

    assertTrue(restorationAttempted.get());
    assertTrue(objective.unregistered);
    assertFalse(controller.isShown(player));
  }

  @Test
  void closeCleansEveryViewerAndClosesWhenRestorationFails() {
    FakeScoreboard firstPrevious = new FakeScoreboard();
    FakeScoreboard secondPrevious = new FakeScoreboard();
    AtomicBoolean firstRestorationAttempted = new AtomicBoolean();
    AtomicBoolean secondRestorationAttempted = new AtomicBoolean();
    Player first =
        failingRestorePlayer(UUID.randomUUID(), firstPrevious, firstRestorationAttempted);
    Player second =
        failingRestorePlayer(UUID.randomUUID(), secondPrevious, secondRestorationAttempted);
    ScoreboardController controller =
        new ScoreboardController(new FakeScoreboardManager(), "sidebar");

    controller.show(first, ScoreboardLayout.of(Component.empty()));
    controller.show(second, ScoreboardLayout.of(Component.empty()));
    FakeObjective firstObjective =
        ((FakeScoreboard) currentScoreboard(first)).objectiveByName("sidebar");
    FakeObjective secondObjective =
        ((FakeScoreboard) currentScoreboard(second)).objectiveByName("sidebar");

    assertThrows(IllegalStateException.class, controller::close);

    assertTrue(firstRestorationAttempted.get());
    assertTrue(secondRestorationAttempted.get());
    assertTrue(firstObjective.unregistered);
    assertTrue(secondObjective.unregistered);
    assertThrows(IllegalStateException.class, () -> controller.isShown(first));
  }

  @Test
  void refreshBuildsLayoutFromViewer() {
    Player player = player(UUID.randomUUID(), new FakeScoreboard());
    ScoreboardController controller =
        new ScoreboardController(new FakeScoreboardManager(), "sidebar");

    controller.refresh(
        player, viewer -> ScoreboardLayout.of(Component.text(viewer.uniqueId().toString())));

    FakeScoreboard shown = (FakeScoreboard) currentScoreboard(player);
    assertEquals(
        Component.text(player.uniqueId().toString()),
        shown.objectiveByName("sidebar").displayName());
  }

  private static Scoreboard currentScoreboard(Player player) {
    return player.scoreboard();
  }

  private static Player player(UUID id, Scoreboard initial) {
    AtomicReference<Scoreboard> current = new AtomicReference<>(initial);
    return (Player)
        Proxy.newProxyInstance(
            Player.class.getClassLoader(),
            new Class<?>[] {Player.class},
            (proxy, method, args) -> {
              return switch (method.getName()) {
                case "uniqueId" -> id;
                case "scoreboard" -> {
                  if (args == null) {
                    yield current.get();
                  }
                  current.set((Scoreboard) args[0]);
                  yield null;
                }
                case "equals" -> proxy == args[0];
                case "hashCode" -> System.identityHashCode(proxy);
                case "toString" -> "TestPlayer{" + id + "}";
                default -> defaultValue(method.getReturnType());
              };
            });
  }

  private static Player failingRestorePlayer(
      UUID id, Scoreboard initial, AtomicBoolean restorationAttempted) {
    AtomicReference<Scoreboard> current = new AtomicReference<>(initial);
    return (Player)
        Proxy.newProxyInstance(
            Player.class.getClassLoader(),
            new Class<?>[] {Player.class},
            (proxy, method, args) -> {
              return switch (method.getName()) {
                case "uniqueId" -> id;
                case "scoreboard" -> {
                  if (args == null) {
                    yield current.get();
                  }
                  Scoreboard next = (Scoreboard) args[0];
                  if (next == initial) {
                    restorationAttempted.set(true);
                    throw new IllegalStateException("restore failed");
                  }
                  current.set(next);
                  yield null;
                }
                case "equals" -> proxy == args[0];
                case "hashCode" -> System.identityHashCode(proxy);
                case "toString" -> "FailingTestPlayer{" + id + "}";
                default -> defaultValue(method.getReturnType());
              };
            });
  }

  private static Object defaultValue(Class<?> type) {
    if (!type.isPrimitive()) {
      return null;
    }
    if (type == boolean.class) {
      return false;
    }
    if (type == byte.class) {
      return (byte) 0;
    }
    if (type == short.class) {
      return (short) 0;
    }
    if (type == int.class) {
      return 0;
    }
    if (type == long.class) {
      return 0L;
    }
    if (type == float.class) {
      return 0F;
    }
    if (type == double.class) {
      return 0D;
    }
    if (type == char.class) {
      return '\0';
    }
    return null;
  }

  private static final class FakeScoreboardManager implements ScoreboardManager {

    private final FakeScoreboard main = new FakeScoreboard();
    private final List<FakeScoreboard> created = new ArrayList<>();

    @Override
    public Scoreboard mainScoreboard() {
      return main;
    }

    @Override
    public Scoreboard newScoreboard() {
      FakeScoreboard scoreboard = new FakeScoreboard();
      created.add(scoreboard);
      return scoreboard;
    }
  }

  private static final class FakeScoreboard implements Scoreboard {

    private final Map<String, FakeObjective> objectives = new LinkedHashMap<>();
    private final Map<String, FakeTeam> teams = new LinkedHashMap<>();

    @Override
    public Set<? extends Objective> objectives() {
      return new HashSet<>(objectives.values());
    }

    @Override
    public Set<? extends Objective> objectivesByCriteria(Criteria criteria) {
      return objectives();
    }

    @Override
    public Set<? extends Team> teams() {
      return new HashSet<>(teams.values());
    }

    @Override
    public Set<String> entries() {
      Set<String> entries = new HashSet<>();
      teams.values().stream()
          .filter(team -> team.registered)
          .forEach(team -> entries.addAll(team.entries));
      return entries;
    }

    @Override
    public Objective objective(String name) {
      return objectives.get(name);
    }

    @Override
    public Objective objective(DisplaySlot slot) {
      return objectives.values().stream()
          .filter(objective -> objective.displaySlot == slot)
          .findFirst()
          .orElse(null);
    }

    @Override
    public Team team(String name) {
      return teams.get(name);
    }

    @Override
    public Team entryTeam(String entry) {
      return teams.values().stream()
          .filter(team -> team.registered && team.entries.contains(entry))
          .findFirst()
          .orElse(null);
    }

    @Override
    public Set<? extends Score> scores(String entry) {
      Set<Score> scores = new HashSet<>();
      objectives.values().forEach(objective -> scores.add(objective.score(entry)));
      return scores;
    }

    @Override
    public Objective registerObjective(String name, Criteria criteria, Component displayName) {
      return registerObjective(name, criteria, displayName, criteria.defaultRenderType());
    }

    @Override
    public Objective registerObjective(
        String name, Criteria criteria, Component displayName, RenderType renderType) {
      FakeObjective objective = new FakeObjective(this, name, criteria, displayName, renderType);
      objectives.put(name, objective);
      return objective;
    }

    @Override
    public Team registerTeam(String name) {
      FakeTeam team = new FakeTeam(this, name);
      teams.put(name, team);
      return team;
    }

    @Override
    public void clearSlot(DisplaySlot slot) {
      objectives.values().stream()
          .filter(objective -> objective.displaySlot == slot)
          .forEach(objective -> objective.displaySlot = null);
    }

    @Override
    public void resetScores(String entry) {
      objectives.values().forEach(objective -> objective.scores.remove(entry));
    }

    private FakeObjective objectiveByName(String name) {
      return objectives.get(name);
    }

    private List<FakeTeam> registeredTeams() {
      return teams.values().stream().filter(team -> team.registered).toList();
    }

    private FakeTeam teamWithPrefix(Component prefix) {
      return teams.values().stream()
          .filter(team -> team.registered && team.prefix.equals(prefix))
          .findFirst()
          .orElseThrow();
    }

    private int scoreForContent(FakeObjective objective, Component prefix) {
      FakeTeam team = teamWithPrefix(prefix);
      String entry = team.entries.iterator().next();
      return objective.score(entry).score();
    }
  }

  private static final class FakeObjective implements Objective {

    private final FakeScoreboard scoreboard;
    private final String name;
    private final Criteria criteria;
    private final Map<String, FakeScore> scores = new HashMap<>();
    private Component displayName;
    private DisplaySlot displaySlot;
    private final RenderType renderType;
    private boolean unregistered;

    private FakeObjective(
        FakeScoreboard scoreboard,
        String name,
        Criteria criteria,
        Component displayName,
        RenderType renderType) {
      this.scoreboard = scoreboard;
      this.name = name;
      this.criteria = criteria;
      this.displayName = displayName;
      this.renderType = renderType;
    }

    @Override
    public String name() {
      return name;
    }

    @Override
    public Criteria trackedCriteria() {
      return criteria;
    }

    @Override
    public boolean isModifiable() {
      return !criteria.isReadOnly();
    }

    @Override
    public Component displayName() {
      return displayName;
    }

    @Override
    public void displayName(Component displayName) {
      this.displayName = displayName;
    }

    @Override
    public Scoreboard scoreboard() {
      return unregistered ? null : scoreboard;
    }

    @Override
    public void unregister() {
      unregistered = true;
      scoreboard.objectives.remove(name);
    }

    @Override
    public void displaySlot(DisplaySlot slot) {
      this.displaySlot = slot;
    }

    @Override
    public DisplaySlot displaySlot() {
      return displaySlot;
    }

    @Override
    public void renderType(RenderType renderType) {
      throw new UnsupportedOperationException("Fake objective render type is immutable");
    }

    @Override
    public RenderType renderType() {
      return renderType;
    }

    @Override
    public Score score(String entry) {
      return scores.computeIfAbsent(entry, key -> new FakeScore(this, key));
    }
  }

  private static final class FakeScore implements Score {

    private final FakeObjective objective;
    private final String entry;
    private int value;
    private boolean set;

    private FakeScore(FakeObjective objective, String entry) {
      this.objective = objective;
      this.entry = entry;
    }

    @Override
    public String entry() {
      return entry;
    }

    @Override
    public Objective objective() {
      return objective;
    }

    @Override
    public Scoreboard scoreboard() {
      return objective.scoreboard();
    }

    @Override
    public int score() {
      return value;
    }

    @Override
    public void score(int score) {
      value = score;
      set = true;
    }

    @Override
    public boolean isScoreSet() {
      return set;
    }

    @Override
    public void reset() {
      objective.scores.remove(entry);
      set = false;
    }
  }

  private static final class FakeTeam implements Team {

    private final FakeScoreboard scoreboard;
    private final String name;
    private final Set<String> entries = new HashSet<>();
    private Component prefix = Component.empty();
    private boolean registered = true;

    private FakeTeam(FakeScoreboard scoreboard, String name) {
      this.scoreboard = scoreboard;
      this.name = name;
    }

    @Override
    public String name() {
      return name;
    }

    @Override
    public Component displayName() {
      return Component.empty();
    }

    @Override
    public void displayName(Component displayName) {}

    @Override
    public Component prefix() {
      return prefix;
    }

    @Override
    public void prefix(Component prefix) {
      this.prefix = prefix;
    }

    @Override
    public Component suffix() {
      return Component.empty();
    }

    @Override
    public void suffix(Component suffix) {}

    @Override
    public NamedTextColor color() {
      return null;
    }

    @Override
    public void color(NamedTextColor color) {}

    @Override
    public Set<String> entries() {
      return Collections.unmodifiableSet(entries);
    }

    @Override
    public int size() {
      return entries.size();
    }

    @Override
    public boolean addEntry(String entry) {
      return entries.add(entry);
    }

    @Override
    public boolean removeEntry(String entry) {
      return entries.remove(entry);
    }

    @Override
    public boolean hasEntry(String entry) {
      return entries.contains(entry);
    }

    @Override
    public boolean allowFriendlyFire() {
      return false;
    }

    @Override
    public void setAllowFriendlyFire(boolean enabled) {}

    @Override
    public boolean canSeeFriendlyInvisibles() {
      return false;
    }

    @Override
    public void setCanSeeFriendlyInvisibles(boolean enabled) {}

    @Override
    public TeamOptionStatus option(TeamOption option) {
      return TeamOptionStatus.ALWAYS;
    }

    @Override
    public void setOption(TeamOption option, TeamOptionStatus status) {}

    @Override
    public Scoreboard scoreboard() {
      return registered ? scoreboard : null;
    }

    @Override
    public void unregister() {
      registered = false;
      entries.clear();
      scoreboard.teams.remove(name);
    }
  }
}
