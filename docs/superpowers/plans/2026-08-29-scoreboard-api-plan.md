# Scoreboard API and Sidebar UI Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a portable Paper-shaped scoreboard API, Bukkit/Paper adapters, and a per-player Adventure sidebar controller.

**Architecture:** Extend the existing `utilities-api` contract with scoreboard interfaces and capability-backed server/player accessors. Implement strict Bukkit wrappers with legacy fallback conversion and Paper subclasses with native Adventure delegation. Add immutable layouts and a synchronous per-viewer controller under `utilities-common` so callers can render different sidebars without touching native scoreboard objects.

**Tech Stack:** Java 25, Gradle Kotlin DSL, Paper API `26.2.build.119-stable`, Spigot API `1.21.4-R0.1-SNAPSHOT`, Kyori Adventure, JUnit 5, Mockito.

**Spec:** `docs/superpowers/specs/2026-08-29-scoreboard-api-design.md`

## Global Constraints

- Keep all public scoreboard contracts under `org.aincraft.api.domain.scoreboard` free of `org.bukkit` and `io.papermc` imports.
- Target Bukkit/Paper adapters only; do not add a Minestom scoreboard adapter in this change.
- Use Adventure `Component` for public text and `NamedTextColor` for team colors.
- Preserve existing wrapper isolation and throw `IllegalArgumentException` when unwrapping foreign implementations.
- Use legacy section serialization only in `utilities-bukkit`; Paper wrappers must call native Adventure methods.
- Keep the sidebar controller synchronous and caller-scheduled; do not add scheduler/event/lifecycle code.
- Reject null values, blank identifiers, duplicate line IDs, and layouts larger than 15 rows before native mutation.
- Controller operations after `close()` throw `IllegalStateException`; `hide()` and repeated `close()` are idempotent.
- Every implementation task uses focused tests or compile checks and skips formatters, linters, and project-wide verification until the final verification task.
- Do not add a new Gradle module; `utilities-common` already participates in the published BOM.

## File Map

- Create portable contracts in `utilities-api/src/main/java/org/aincraft/api/domain/scoreboard/`.
- Modify `utilities-api/src/main/java/org/aincraft/api/Capability.java`, `.../domain/server/Server.java`, and `.../domain/entity/Player.java`.
- Add API validation/capability tests under `utilities-api/src/test/java/org/aincraft/api/domain/scoreboard/`.
- Create Bukkit wrappers in `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/` and modify `BukkitAdapters.java`, `BukkitServerWrapper.java`, and `BukkitPlayerWrapper.java`.
- Add focused Bukkit tests under `utilities-bukkit/src/test/java/org/aincraft/bukkit/adapter/`.
- Create Paper wrappers in `utilities-paper/src/main/java/org/aincraft/paper/adapter/` and modify `PaperAdapters.java`, `PaperServerWrapper.java`, and `PaperPlayerWrapper.java`.
- Add focused Paper tests under `utilities-paper/src/test/java/org/aincraft/paper/adapter/`.
- Modify `utilities-common/build.gradle.kts` to allow `org/aincraft/ui/` and expose Mockito for controller tests.
- Create `ScoreboardLine.java`, `ScoreboardLayout.java`, and `ScoreboardController.java` under `utilities-common/src/main/java/org/aincraft/ui/scoreboard/`.
- Add common tests under `utilities-common/src/test/java/org/aincraft/ui/scoreboard/`.
- Modify `README.md` with the API/controller usage; verify `utilities-bom/build.gradle.kts` needs no new constraint because `utilities-common` is already constrained.

---

### Task 1: Add portable scoreboard contracts and capability defaults

**Files:**
- Create: `utilities-api/src/main/java/org/aincraft/api/domain/scoreboard/Criteria.java`
- Create: `utilities-api/src/main/java/org/aincraft/api/domain/scoreboard/DisplaySlot.java`
- Create: `utilities-api/src/main/java/org/aincraft/api/domain/scoreboard/RenderType.java`
- Create: `utilities-api/src/main/java/org/aincraft/api/domain/scoreboard/TeamOption.java`
- Create: `utilities-api/src/main/java/org/aincraft/api/domain/scoreboard/TeamOptionStatus.java`
- Create: `utilities-api/src/main/java/org/aincraft/api/domain/scoreboard/ScoreboardManager.java`
- Create: `utilities-api/src/main/java/org/aincraft/api/domain/scoreboard/Scoreboard.java`
- Create: `utilities-api/src/main/java/org/aincraft/api/domain/scoreboard/Objective.java`
- Create: `utilities-api/src/main/java/org/aincraft/api/domain/scoreboard/Score.java`
- Create: `utilities-api/src/main/java/org/aincraft/api/domain/scoreboard/Team.java`
- Modify: `utilities-api/src/main/java/org/aincraft/api/Capability.java`
- Modify: `utilities-api/src/main/java/org/aincraft/api/domain/server/Server.java`
- Modify: `utilities-api/src/main/java/org/aincraft/api/domain/entity/Player.java`
- Test: `utilities-api/src/test/java/org/aincraft/api/domain/scoreboard/ScoreboardContractsTest.java`

**Interfaces:**
- Consumes: existing `Capability`, `UnsupportedCapabilityException`, `Entity`, `Player`, `Server`, Adventure `Audience`, `Component`, and `NamedTextColor` conventions.
- Produces: the exact portable API consumed by all adapter and UI tasks:

```java
public interface ScoreboardManager {
  @NotNull Scoreboard mainScoreboard();
  @NotNull Scoreboard newScoreboard();
}

public interface Scoreboard {
  @NotNull Set<? extends Objective> objectives();
  @NotNull Set<? extends Objective> objectivesByCriteria(@NotNull Criteria criteria);
  @NotNull Set<? extends Team> teams();
  @NotNull Set<String> entries();
  @Nullable Objective objective(@NotNull String name);
  @Nullable Objective objective(@NotNull DisplaySlot slot);
  @Nullable Team team(@NotNull String name);
  @Nullable Team entryTeam(@NotNull String entry);
  @NotNull Set<? extends Score> scores(@NotNull String entry);
  @NotNull Objective registerObjective(
      @NotNull String name, @NotNull Criteria criteria, @NotNull Component displayName);
  @NotNull Objective registerObjective(
      @NotNull String name,
      @NotNull Criteria criteria,
      @NotNull Component displayName,
      @NotNull RenderType renderType);
  @NotNull Team registerTeam(@NotNull String name);
  void clearSlot(@NotNull DisplaySlot slot);
  void resetScores(@NotNull String entry);

  default @NotNull Set<? extends Score> scoresFor(@NotNull Entity entity) {
    throw new UnsupportedCapabilityException(Capability.SCOREBOARD);
  }

  default void resetScoresFor(@NotNull Entity entity) {
    throw new UnsupportedCapabilityException(Capability.SCOREBOARD);
  }
}

public interface Objective {
  @NotNull String name();
  @NotNull Criteria trackedCriteria();
  boolean isModifiable();
  @NotNull Component displayName();
  void displayName(@NotNull Component displayName);
  @Nullable Scoreboard scoreboard();
  void unregister();
  void displaySlot(@Nullable DisplaySlot slot);
  @Nullable DisplaySlot displaySlot();
  void renderType(@NotNull RenderType renderType);
  @NotNull RenderType renderType();
  @NotNull Score score(@NotNull String entry);

  default boolean willAutoUpdateDisplay() {
    throw new UnsupportedCapabilityException(Capability.SCOREBOARD);
  }

  default void willAutoUpdateDisplay(boolean enabled) {
    throw new UnsupportedCapabilityException(Capability.SCOREBOARD);
  }
}

public interface Score {
  @NotNull String entry();
  @NotNull Objective objective();
  @Nullable Scoreboard scoreboard();
  int score();
  void score(int score);
  boolean isScoreSet();
  void reset();

  default @Nullable Component customName() {
    throw new UnsupportedCapabilityException(Capability.SCOREBOARD);
  }

  default void customName(@Nullable Component customName) {
    throw new UnsupportedCapabilityException(Capability.SCOREBOARD);
  }

  default boolean isTriggerable() {
    throw new UnsupportedCapabilityException(Capability.SCOREBOARD);
  }

  default void isTriggerable(boolean triggerable) {
    throw new UnsupportedCapabilityException(Capability.SCOREBOARD);
  }
}
```

Define `Team` as an Adventure `Audience` with component display name/prefix/suffix, nullable named color, entries, size, add/remove/has entry, friendly-fire/invisibility flags, `option(TeamOption)`, `option(TeamOption, TeamOptionStatus)`, nullable owning scoreboard, and `unregister()`.

Define `Criteria` as an interface with `name()`, `isReadOnly()`, and `defaultRenderType()`. Add `Criteria.DUMMY` and `Criteria.TRIGGER` plus `Criteria.of(String)`, backed by a nested immutable implementation whose default is writable integer criteria. Reject null/blank names in `of`.

Add enum values `DisplaySlot.SIDEBAR/BELOW_NAME/PLAYER_LIST`, `RenderType.INTEGER/HEARTS`, `TeamOption.NAME_TAG_VISIBILITY/DEATH_MESSAGE_VISIBILITY/COLLISION_RULE`, and `TeamOptionStatus.ALWAYS/NEVER/FOR_OTHER_TEAMS/FOR_OWN_TEAM`.

Add `@NotNull Capability SCOREBOARD = of("scoreboard")`. Add these default methods, each throwing `UnsupportedCapabilityException(Capability.SCOREBOARD)`, so existing Minestom implementations remain source-compatible:

```java
// Server
@NotNull default ScoreboardManager scoreboardManager() { ... }
@NotNull default Criteria scoreboardCriteria(@NotNull String name) { ... }

// Player
@NotNull default Scoreboard scoreboard() { ... }
default void scoreboard(@NotNull Scoreboard scoreboard) { ... }
```

- [ ] **Step 1: Write failing contract tests**

```java
@Test
void criteriaFactoryRejectsBlankNames() {
  assertThrows(IllegalArgumentException.class, () -> Criteria.of("  "));
}

@Test
void serverAndPlayerDefaultsReportUnsupportedScoreboardCapability() {
  Server server = new MinimalServer();
  Player player = new MinimalPlayer();
  assertThrows(UnsupportedCapabilityException.class, server::scoreboardManager);
  assertThrows(UnsupportedCapabilityException.class, player::scoreboard);
}
```

Use the repository's existing minimal test doubles or anonymous implementations; keep the test focused on exception capability identity rather than implementation details.

- [ ] **Step 2: Run the focused API test to verify it fails**

Run: `./gradlew :utilities-api:test --tests org.aincraft.api.domain.scoreboard.ScoreboardContractsTest`

Expected: FAIL because the scoreboard package and capability methods do not exist.

- [ ] **Step 3: Implement the portable contracts**

Add the interfaces/enums exactly as specified. Keep optional Paper-only methods as default capability failures. Do not import Bukkit, Paper, or legacy serializers.

- [ ] **Step 4: Run the focused API test**

Run: `./gradlew :utilities-api:test --tests org.aincraft.api.domain.scoreboard.ScoreboardContractsTest`

Expected: PASS.

- [ ] **Step 5: Compile the API module**

Run: `./gradlew :utilities-api:compileJava`

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 6: Commit the portable contract**

```bash
git add utilities-api/src/main/java/org/aincraft/api/Capability.java \
  utilities-api/src/main/java/org/aincraft/api/domain/server/Server.java \
  utilities-api/src/main/java/org/aincraft/api/domain/entity/Player.java \
  utilities-api/src/main/java/org/aincraft/api/domain/scoreboard \
  utilities-api/src/test/java/org/aincraft/api/domain/scoreboard
git commit -m "feat: add portable scoreboard contracts"
```

---

### Task 2: Implement Bukkit scoreboard wrappers and conversion factory

**Files:**
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitScoreboardManagerWrapper.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitScoreboardWrapper.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitObjectiveWrapper.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitScoreWrapper.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitTeamWrapper.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitCriteriaWrapper.java`
- Modify: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitAdapters.java`
- Test: `utilities-bukkit/src/test/java/org/aincraft/bukkit/adapter/BukkitScoreboardAdapterTest.java`

**Interfaces:**
- Consumes: Task 1 portable contracts and existing Bukkit adapter factories.
- Produces: strict `adapt`/`toBukkit` methods and overridable wrapper factories used by Paper subclasses.

Implement each wrapper as a thin delegate. Store the native object in a final field and expose package adapter getters (`getBukkitScoreboard`, `getBukkitObjective`, `getBukkitScore`, `getBukkitTeam`, `getBukkitCriteria`) for strict unwrapping.

`BukkitScoreboardManagerWrapper` delegates `getMainScoreboard()` and `getNewScoreboard()`. Its protected `adaptScoreboard(org.bukkit.scoreboard.Scoreboard)` returns `new BukkitScoreboardWrapper(...)`.

`BukkitScoreboardWrapper` delegates these native operations: `getObjectives`, `getObjectivesByCriteria`, `getTeams`, `getEntries`, `getObjective(String)`, `getObjective(DisplaySlot)`, `getTeam`, `getEntryTeam`, `getScores(String)`, `registerNewObjective`, `registerNewTeam`, `clearSlot`, and `resetScores`. Map sets with `Collectors.toUnmodifiableSet()`. Add protected `adaptObjective`, `adaptScore`, and `adaptTeam` methods so Paper can override nested wrapper types. Component registration and all Spigot text mutation use `LegacyComponentSerializer.legacySection()`.

`BukkitObjectiveWrapper` delegates name, tracked criteria, modifiability, display name, scoreboard, unregister, display slot, render type, and string score. Use protected `adaptScore`. `BukkitScoreWrapper` delegates entry, objective, scoreboard, score get/set, score-set state, and reset. `BukkitTeamWrapper` delegates modern entry and option methods plus display name/prefix/suffix through legacy serialization; convert `ChatColor` to/from `NamedTextColor` with an explicit switch for the 16 named colors and return null for non-named/reset values. The wrapper should extend `Audience` through the API without adding native audience leakage.

Map API enums to native enums with exhaustive switches. Map `Criteria` wrappers by native object; map value criteria from `Criteria.of` with `org.bukkit.scoreboard.Criteria.create(criteria.name())`. If the target Spigot API lacks that static factory, use the existing compile-time API equivalent rather than reflection in the wrapper.

Extend `BukkitAdapters` with overloads:

```java
public static ScoreboardManager adapt(org.bukkit.scoreboard.ScoreboardManager manager);
public static Scoreboard adapt(org.bukkit.scoreboard.Scoreboard scoreboard);
public static Objective adapt(org.bukkit.scoreboard.Objective objective);
public static Score adapt(org.bukkit.scoreboard.Score score);
public static Team adapt(org.bukkit.scoreboard.Team team);
public static Criteria adapt(org.bukkit.scoreboard.Criteria criteria);

public static org.bukkit.scoreboard.ScoreboardManager toBukkit(ScoreboardManager manager);
public static org.bukkit.scoreboard.Scoreboard toBukkit(Scoreboard scoreboard);
public static org.bukkit.scoreboard.Objective toBukkit(Objective objective);
public static org.bukkit.scoreboard.Score toBukkit(Score score);
public static org.bukkit.scoreboard.Team toBukkit(Team team);
public static org.bukkit.scoreboard.Criteria toBukkit(Criteria criteria);
```

Every `toBukkit` must accept only the corresponding Bukkit wrapper (with Paper subclasses accepted through inheritance) and otherwise throw `IllegalArgumentException` naming the foreign class.

- [ ] **Step 1: Write failing Bukkit adapter tests**

Use Mockito native scoreboard interfaces. Cover at least:

```java
@Test
void objectiveComponentUsesLegacySerializer() {
  org.bukkit.scoreboard.Objective nativeObjective = mock(org.bukkit.scoreboard.Objective.class);
  when(nativeObjective.getDisplayName()).thenReturn("§aTitle");
  Objective objective = new BukkitObjectiveWrapper(nativeObjective);
  assertEquals(Component.text("Title").color(NamedTextColor.GREEN), objective.displayName());
  objective.displayName(Component.text("Next").color(NamedTextColor.RED));
  verify(nativeObjective).setDisplayName("§cNext");
}

@Test
void foreignScoreboardCannotBeUnwrapped() {
  Scoreboard foreign = mock(Scoreboard.class);
  assertThrows(IllegalArgumentException.class, () -> BukkitAdapters.toBukkit(foreign));
}
```

Also verify registration maps `RenderType.HEARTS`, `DisplaySlot.SIDEBAR`, and `TeamOption`/status pairs to their native enums.

- [ ] **Step 2: Run the focused Bukkit test to verify it fails**

Run: `./gradlew :utilities-bukkit:test --tests org.aincraft.bukkit.adapter.BukkitScoreboardAdapterTest`

Expected: FAIL because the wrapper classes and factory overloads do not exist.

- [ ] **Step 3: Implement wrappers and conversion methods**

Keep delegation boring and one-to-one. Use existing imports/order/style. Do not add Paper imports to the Bukkit module.

- [ ] **Step 4: Run the focused Bukkit test**

Run: `./gradlew :utilities-bukkit:test --tests org.aincraft.bukkit.adapter.BukkitScoreboardAdapterTest`

Expected: PASS.

- [ ] **Step 5: Compile the Bukkit module**

Run: `./gradlew :utilities-bukkit:compileJava`

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 6: Commit the Bukkit adapter**

```bash
git add utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter \
  utilities-bukkit/src/test/java/org/aincraft/bukkit/adapter/BukkitScoreboardAdapterTest.java
git commit -m "feat: adapt scoreboard through Bukkit"
```

---

### Task 3: Wire Bukkit server and player scoreboard access

**Files:**
- Modify: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitServerWrapper.java`
- Modify: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitPlayerWrapper.java`
- Test: `utilities-bukkit/src/test/java/org/aincraft/bukkit/adapter/BukkitScoreboardAccessTest.java`

**Interfaces:**
- Consumes: Task 1 `Server.scoreboardManager`, `Server.scoreboardCriteria`, `Player.scoreboard`, and `Player.scoreboard(Scoreboard)`; Task 2 adapter factories.
- Produces: live Bukkit server/player access used by the common controller.

Add these overrides:

```java
@Override
public @NotNull ScoreboardManager scoreboardManager() {
  return BukkitAdapters.adapt(server.getScoreboardManager());
}

@Override
public @NotNull Criteria scoreboardCriteria(@NotNull String name) {
  return BukkitAdapters.adapt(server.getScoreboardCriteria(name));
}

@Override
public @NotNull Scoreboard scoreboard() {
  return BukkitAdapters.adapt(player.getScoreboard());
}

@Override
public void scoreboard(@NotNull Scoreboard scoreboard) {
  player.setScoreboard(BukkitAdapters.toBukkit(scoreboard));
}
```

Use the existing private native fields/getters and import conventions. Do not alter unrelated server/player behavior.

- [ ] **Step 1: Write failing access tests**

Mock native `org.bukkit.Server` and `org.bukkit.entity.Player`, stub scoreboard manager/scoreboard/criteria, adapt the wrappers, and assert the returned portable wrappers plus native setter invocation.

- [ ] **Step 2: Run the focused access tests**

Run: `./gradlew :utilities-bukkit:test --tests org.aincraft.bukkit.adapter.BukkitScoreboardAccessTest`

Expected: FAIL until the overrides exist.

- [ ] **Step 3: Implement the four overrides**

Delegate exactly once through `BukkitAdapters`.

- [ ] **Step 4: Run the focused access tests and compile**

Run: `./gradlew :utilities-bukkit:test --tests org.aincraft.bukkit.adapter.BukkitScoreboardAccessTest :utilities-bukkit:compileJava`

Expected: PASS and `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit the access wiring**

```bash
git add utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitServerWrapper.java \
  utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitPlayerWrapper.java \
  utilities-bukkit/src/test/java/org/aincraft/bukkit/adapter/BukkitScoreboardAccessTest.java
git commit -m "feat: expose Bukkit scoreboard access"
```

---

### Task 4: Preserve native Paper scoreboard components

**Files:**
- Create: `utilities-paper/src/main/java/org/aincraft/paper/adapter/PaperScoreboardManagerWrapper.java`
- Create: `utilities-paper/src/main/java/org/aincraft/paper/adapter/PaperScoreboardWrapper.java`
- Create: `utilities-paper/src/main/java/org/aincraft/paper/adapter/PaperObjectiveWrapper.java`
- Create: `utilities-paper/src/main/java/org/aincraft/paper/adapter/PaperScoreWrapper.java`
- Create: `utilities-paper/src/main/java/org/aincraft/paper/adapter/PaperTeamWrapper.java`
- Modify: `utilities-paper/src/main/java/org/aincraft/paper/adapter/PaperAdapters.java`
- Modify: `utilities-paper/src/main/java/org/aincraft/paper/adapter/PaperServerWrapper.java`
- Modify: `utilities-paper/src/main/java/org/aincraft/paper/adapter/PaperPlayerWrapper.java`
- Test: `utilities-paper/src/test/java/org/aincraft/paper/adapter/PaperScoreboardAdapterTest.java`

**Interfaces:**
- Consumes: Task 2 protected Bukkit wrapper factories and Task 3 accessors.
- Produces: Paper wrappers whose component methods call native Paper APIs and whose nested results remain Paper wrappers.

`PaperScoreboardManagerWrapper` extends `BukkitScoreboardManagerWrapper` and overrides the protected scoreboard factory with `PaperScoreboardWrapper`. `PaperScoreboardWrapper` overrides protected objective/score/team factories. `PaperObjectiveWrapper` overrides `displayName()`/setter and `willAutoUpdateDisplay()`/setter with native Paper methods. `PaperScoreWrapper` overrides `customName()`/setter and `isTriggerable()`/setter. `PaperTeamWrapper` overrides component display name/prefix/suffix and native color methods. Preserve base behavior for core methods that Paper does not specialize.

Extend `PaperAdapters` with the same `adapt` overloads as `BukkitAdapters` for native scoreboard types, returning Paper wrappers; delegate `toBukkit` to `BukkitAdapters` so strict wrapper checks remain centralized.

Override `PaperServerWrapper.scoreboardManager()` and `PaperServerWrapper.scoreboardCriteria(String)` to return Paper wrappers. Override `PaperPlayerWrapper.scoreboard()` and setter to return/consume Paper wrappers. This prevents a Paper call chain from falling back to a Bukkit wrapper after the first scoreboard access.

- [ ] **Step 1: Write failing Paper adapter tests**

Use Mockito native Paper API interfaces. Verify that:

```java
objective.displayName(Component.text("Native"));
verify(nativeObjective).displayName(Component.text("Native"));
when(nativeObjective.displayName()).thenReturn(Component.text("Paper"));
assertEquals(Component.text("Paper"), objective.displayName());
```

Also verify `PaperAdapters.adapt(nativeScoreboard)` returns `PaperScoreboardWrapper` and that `PaperScoreboardWrapper.objectives()` returns `PaperObjectiveWrapper`.

- [ ] **Step 2: Run the focused Paper test to verify it fails**

Run: `./gradlew :utilities-paper:test --tests org.aincraft.paper.adapter.PaperScoreboardAdapterTest`

Expected: FAIL because Paper scoreboard wrappers do not exist.

- [ ] **Step 3: Implement Paper wrappers and access overrides**

Call Paper methods directly; do not serialize components through legacy strings in these classes.

- [ ] **Step 4: Run the focused Paper test and compile**

Run: `./gradlew :utilities-paper:test --tests org.aincraft.paper.adapter.PaperScoreboardAdapterTest :utilities-paper:compileJava`

Expected: PASS and `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit the Paper adapter**

```bash
git add utilities-paper/src/main/java/org/aincraft/paper/adapter \
  utilities-paper/src/test/java/org/aincraft/paper/adapter/PaperScoreboardAdapterTest.java
git commit -m "feat: preserve Paper scoreboard components"
```

---

### Task 5: Add immutable sidebar layout models

**Files:**
- Modify: `utilities-common/build.gradle.kts`
- Create: `utilities-common/src/main/java/org/aincraft/ui/scoreboard/ScoreboardLine.java`
- Create: `utilities-common/src/main/java/org/aincraft/ui/scoreboard/ScoreboardLayout.java`
- Test: `utilities-common/src/test/java/org/aincraft/ui/scoreboard/ScoreboardLayoutTest.java`

**Interfaces:**
- Consumes: Task 1 portable `Component` dependency and existing common module conventions.
- Produces: immutable layout values consumed by `ScoreboardController`:

```java
public record ScoreboardLine(@NotNull String id, @NotNull Component content) { }

public record ScoreboardLayout(
    @NotNull Component title, @NotNull List<ScoreboardLine> lines) {
  public static final int MAX_LINES = 15;
  public static Builder builder(@NotNull Component title) { ... }
  public static ScoreboardLayout of(
      @NotNull Component title, @NotNull ScoreboardLine... lines) { ... }

  public static final class Builder {
    public Builder line(@NotNull String id, @NotNull Component content) { ... }
    public ScoreboardLayout build() { ... }
  }
}
```

Validate title/line/content non-null, IDs nonblank, IDs unique, and line count at most `MAX_LINES`. Use `List.copyOf` and preserve caller order. Builder `line` returns `this`; `of` delegates to the same constructor. Add `testImplementation(libs.mockito.core)` to `utilities-common` for the controller task and add `org/aincraft/ui/` to `allowedAincraftPrefixes`.

- [ ] **Step 1: Write failing layout tests**

```java
@Test
void layoutCopiesLinesAndRejectsDuplicates() {
  List<ScoreboardLine> source = new ArrayList<>();
  source.add(new ScoreboardLine("one", Component.text("One")));
  ScoreboardLayout layout = new ScoreboardLayout(Component.text("Title"), source);
  source.clear();
  assertEquals(1, layout.lines().size());
  assertThrows(
      IllegalArgumentException.class,
      () -> new ScoreboardLayout(
          Component.text("Title"),
          List.of(
              new ScoreboardLine("same", Component.empty()),
              new ScoreboardLine("same", Component.empty()))));
}

@Test
void layoutRejectsMoreThanFifteenRows() {
  List<ScoreboardLine> lines = IntStream.range(0, 16)
      .mapToObj(i -> new ScoreboardLine("line-" + i, Component.text(i)))
      .toList();
  assertThrows(IllegalArgumentException.class,
      () -> new ScoreboardLayout(Component.empty(), lines));
}
```

- [ ] **Step 2: Run the focused layout test to verify it fails**

Run: `./gradlew :utilities-common:test --tests org.aincraft.ui.scoreboard.ScoreboardLayoutTest`

Expected: FAIL because the package and classes do not exist.

- [ ] **Step 3: Implement the immutable models and module isolation entry**

Use records, `Objects.requireNonNull`, `String.isBlank`, `Set` ID tracking, and `List.copyOf`. Do not add controller behavior yet.

- [ ] **Step 4: Run the focused layout test**

Run: `./gradlew :utilities-common:test --tests org.aincraft.ui.scoreboard.ScoreboardLayoutTest`

Expected: PASS.

- [ ] **Step 5: Compile the common module**

Run: `./gradlew :utilities-common:compileJava`

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 6: Commit the layout models**

```bash
git add utilities-common/build.gradle.kts \
  utilities-common/src/main/java/org/aincraft/ui/scoreboard \
  utilities-common/src/test/java/org/aincraft/ui/scoreboard/ScoreboardLayoutTest.java
git commit -m "feat: add immutable scoreboard layouts"
```

---

### Task 6: Implement per-player sidebar reconciliation

**Files:**
- Create: `utilities-common/src/main/java/org/aincraft/ui/scoreboard/ScoreboardController.java`
- Test: `utilities-common/src/test/java/org/aincraft/ui/scoreboard/ScoreboardControllerTest.java`

**Interfaces:**
- Consumes: Task 1 scoreboard contracts and Task 5 `ScoreboardLayout`/`ScoreboardLine`.
- Produces: `ScoreboardController` with this public surface:

```java
public final class ScoreboardController implements AutoCloseable {
  public ScoreboardController(
      @NotNull ScoreboardManager manager, @NotNull String objectiveName);
  public void show(@NotNull Player player, @NotNull ScoreboardLayout layout);
  public void update(@NotNull Player player, @NotNull ScoreboardLayout layout);
  public void refresh(
      @NotNull Player player,
      @NotNull Function<? super Player, ? extends ScoreboardLayout> renderer);
  public void hide(@NotNull Player player);
  public boolean isShown(@NotNull Player player);
  @Override public void close();
}
```

Use a `Map<UUID, ViewerState>`; each state stores the player, the previous scoreboard, a newly created private scoreboard, objective, and `Map<String, LineState>`. `LineState` stores a managed team, invisible entry string, and slot number. `show` and `update` both ensure a state exists, assign its private scoreboard to the player, and reconcile the layout. `refresh` applies the renderer and delegates to `show`.

Creation sequence:

```java
Scoreboard previous = player.scoreboard();
Scoreboard scoreboard = manager.newScoreboard();
Objective objective = scoreboard.registerObjective(
    objectiveName, Criteria.DUMMY, Component.empty(), RenderType.INTEGER);
objective.displaySlot(DisplaySlot.SIDEBAR);
```

Reconciliation invariants:

- Remove state IDs absent from the new layout by `scoreboard.resetScores(entry)`, `team.removeEntry(entry)`, and `team.unregister()`.
- Allocate the first unused slot from `0..14`; use team name `aincraft_ui_<hex-slot>` and invisible entry `"§" + Integer.toHexString(slot)`.
- For every line in order, set `team.prefix(line.content())`, `team.suffix(Component.empty())`, ensure the entry is a team member, and set `objective.score(entry).score(layout.lines().size() - index)`.
- Reuse an existing ID's slot/team when it moves or changes content.
- Set `objective.displayName(layout.title())` on every render.

`hide` removes the state, restores its captured scoreboard, and unregisters all teams/objective. `close` hides every state, then marks the controller closed. `show`, `update`, and `refresh` after close throw `IllegalStateException`; `hide` for an unknown player and repeated `close` do nothing. Require non-null arguments and reject blank objective names in the constructor.

Do not synchronize the controller or schedule tasks. Native API exceptions must propagate.

- [ ] **Step 1: Write failing controller tests**

Use Mockito portable contracts. Stub each player's UUID and previous scoreboard, each manager's `newScoreboard`, each scoreboard's objective/team registration, each objective's `score(entry)`, and each player's scoreboard setter. Cover:

```java
@Test
void showCreatesPrivateSidebarWithDescendingScores() {
  controller.show(player, new ScoreboardLayout(
      Component.text("Title"),
      List.of(
          new ScoreboardLine("first", Component.text("First")),
          new ScoreboardLine("second", Component.text("Second")))));

  verify(manager).newScoreboard();
  verify(player).scoreboard(privateScoreboard);
  verify(objective).displayName(Component.text("Title"));
  verify(scoreForFirst).score(2);
  verify(scoreForSecond).score(1);
}

@Test
void updateRemovesStaleLinesAndHideRestoresPreviousScoreboard() { ... }

@Test
void separatePlayersReceiveSeparateScoreboards() { ... }

@Test
void operationsAfterCloseFailFast() { ... }
```

- [ ] **Step 2: Run the focused controller test to verify it fails**

Run: `./gradlew :utilities-common:test --tests org.aincraft.ui.scoreboard.ScoreboardControllerTest`

Expected: FAIL because `ScoreboardController` does not exist.

- [ ] **Step 3: Implement state creation and rendering reconciliation**

Keep line allocation deterministic and bounded to 15 slots. Ensure no native mutation occurs before `ScoreboardLayout` validation has completed. Use `UUID` keys rather than `Player` object identity.

- [ ] **Step 4: Run the focused controller test**

Run: `./gradlew :utilities-common:test --tests org.aincraft.ui.scoreboard.ScoreboardControllerTest`

Expected: PASS.

- [ ] **Step 5: Compile the common module**

Run: `./gradlew :utilities-common:compileJava`

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 6: Commit the controller**

```bash
git add utilities-common/src/main/java/org/aincraft/ui/scoreboard/ScoreboardController.java \
  utilities-common/src/test/java/org/aincraft/ui/scoreboard/ScoreboardControllerTest.java
git commit -m "feat: add per-player scoreboard controller"
```

---

### Task 7: Document the public API and verify publication surface

**Files:**
- Modify: `README.md`
- Inspect: `utilities-bom/build.gradle.kts`
- Inspect: module isolation configuration from `utilities-common/build.gradle.kts`

**Interfaces:**
- Consumes: completed API, adapters, and UI controller.
- Produces: consumer-facing usage documentation and confirmed BOM coverage.

Add a concise README section after the cross-platform lifecycle section describing:

```java
ScoreboardController sidebar =
    new ScoreboardController(server.scoreboardManager(), "lobby_sidebar");

sidebar.show(player, ScoreboardLayout.builder(Component.text("Lobby"))
    .line("online", Component.text("Online: ").append(Component.text(online)))
    .line("mode", Component.text("Mode: Survival"))
    .build());

sidebar.refresh(player, viewer -> layoutFor(viewer));
sidebar.hide(player);
sidebar.close();
```

Explain that the controller is synchronous, creates one scoreboard per viewer, supports at most 15 rows, and must be called on the platform's valid server thread. Mention that `utilities-api` contracts are portable and Paper preserves native Adventure components, while Bukkit fallback serializes components for Spigot.

Confirm `utilities-bom` already constrains `utilities-common`; do not change it unless the file has diverged. Confirm `utilities-common` isolation allows only `org/aincraft/ui/` in addition to existing prefixes.

- [ ] **Step 1: Update README with the supported usage**

Keep the existing module table and runtime sections accurate; do not claim Minestom scoreboard support.

- [ ] **Step 2: Check the publication/isolation configuration**

Run: `./gradlew :utilities-common:jar :utilities-bom:dependencies --configuration runtimeElements`

Expected: the common jar contains `org/aincraft/ui/scoreboard` classes, the BOM still includes `utilities-common`, and no platform dependency is introduced into the common runtime artifact.

- [ ] **Step 3: Commit documentation**

```bash
git add README.md
git commit -m "docs: document scoreboard api and sidebar controller"
```

---

### Task 8: Run focused regression checks and final verification

**Files:**
- Test: all changed module tests; no source changes expected.

**Interfaces:**
- Consumes: all completed tasks.
- Produces: evidence that contracts, adapters, controller, isolation, and existing modules remain healthy.

- [ ] **Step 1: Run focused API, common, Bukkit, and Paper tests**

Run:

```bash
./gradlew \
  :utilities-api:test \
  :utilities-common:test \
  :utilities-bukkit:test \
  :utilities-paper:test
```

Expected: all tests pass.

- [ ] **Step 2: Run the full repository verification**

Run: `./gradlew check`

Expected: `BUILD SUCCESSFUL`, including Spotless, jar isolation, no-Bukkit/no-Paper checks, compilation, and all tests.

- [ ] **Step 3: Inspect changed behavior evidence**

Confirm the focused test output covers:

- portable unsupported capability behavior;
- legacy Bukkit component conversion and strict unwrapping;
- Paper native component delegation and wrapper preservation;
- immutable layout validation and 15-row limit;
- per-player scoreboard isolation, score ordering, line removal/reuse, restoration, and close lifecycle.

- [ ] **Step 4: Commit any only-if-needed verification fixes**

If the final checks reveal a source defect, fix it with a focused regression test and rerun the affected module plus `./gradlew check`. Do not suppress failures or add compatibility shims.
