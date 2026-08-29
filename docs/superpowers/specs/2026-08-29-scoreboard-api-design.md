# Scoreboard API and Sidebar UI — Design Spec

**Date:** 2026-08-29  
**Status:** Approved by implementation decision  
**Target modules:** `:utilities-api`, `:utilities-bukkit`, `:utilities-paper`, `:utilities-common`, `:utilities-bom`

## Context

The library currently exposes platform-neutral server, player, and Adventure contracts, with Bukkit and Paper adapters in separate runtime modules. It has no scoreboard abstraction. Consumers that want a sidebar must currently depend directly on `org.bukkit.scoreboard`, making their UI code platform-bound and difficult to update without leaking native objects through shared code.

The requested feature has two layers:

1. A low-level API that models the useful, current Paper/Bukkit scoreboard behavior without importing Bukkit types.
2. A higher-level sidebar controller in a separate package that manages per-player scoreboards and renders immutable layouts.

The first implementation targets Bukkit/Paper adapters. The API is intentionally portable so a future adapter can map it after the semantics are verified; no Minestom implementation is added in this change.

## Goals

- Expose scoreboard managers, scoreboards, objectives, scores, teams, criteria, display slots, render types, and team options through `utilities-api`.
- Add scoreboard access to the portable `Server` and `Player` contracts without forcing unsupported platforms to implement it immediately.
- Adapt the contract to Spigot/Bukkit and Paper while keeping all native types inside adapter modules.
- Preserve Paper Adventure component behavior in the Paper adapter instead of converting components to legacy strings.
- Provide a synchronous, caller-scheduled sidebar controller under `org.aincraft.ui.scoreboard`.
- Give every viewer an isolated scoreboard so per-player layouts can differ.
- Support stable line IDs, reordering, updates, removal, title changes, and restoration of each viewer's previous scoreboard.
- Keep the public module graph unchanged apart from exposing the new package and BOM alignment.

## Non-goals

- A scheduler, tick task, event listener, or lifecycle plugin integration.
- A Minestom adapter before scoreboard semantics are verified there.
- Direct NMS packet manipulation, fake entries, or reflection in the public API.
- Paper number-format implementations, below-name custom score rendering, or persistence. The core score operations are modeled first; these can be added as capability-backed extensions later.
- Compatibility aliases for deprecated Bukkit getter/setter names.

## Architecture

### Portable API

Add `org.aincraft.api.domain.scoreboard` to `utilities-api`:

- `ScoreboardManager`: returns the main scoreboard and creates new scoreboards.
- `Scoreboard`: queries objectives, teams, entries, scores, and display slots; registers objectives and teams; resets entries. Entity-specific score operations are optional defaults because they are Paper-specific in the target dependency set.
- `Objective`: exposes its name, tracked `Criteria`, Adventure display name, display slot, render type, modifiability, owning scoreboard, and entry scores. Paper auto-update operations are optional defaults.
- `Score`: exposes its entry, objective, score value, set state, reset operation, and owning scoreboard. Paper custom-name and triggerable operations are optional defaults.
- `Team`: extends Adventure `Audience` and exposes component display name, prefix, suffix, named color, membership operations, friendly-fire/invisibility options, team options, size, owning scoreboard, and unregister.
- `Criteria`: exposes name, read-only state, and default render type. It has a small `of(String)` value factory and standard `DUMMY`/`TRIGGER` values needed by UI code; adapters resolve value criteria through the native criteria registry.
- `DisplaySlot`: `SIDEBAR`, `BELOW_NAME`, and `PLAYER_LIST`.
- `RenderType`: `INTEGER` and `HEARTS`.
- `TeamOption` and `TeamOptionStatus`: modern Paper team option equivalents.

Add `Capability.SCOREBOARD`. Add default `scoreboardManager()` and `scoreboardCriteria(String)` methods to `Server`, plus default `scoreboard()` and `scoreboard(Scoreboard)` methods to `Player`. Defaults throw `UnsupportedCapabilityException(Capability.SCOREBOARD)`, preserving existing non-Bukkit implementations and making unsupported behavior explicit.

All public API signatures use `Component`, `NamedTextColor`, portable entity/player interfaces, collections, and annotations. No `org.bukkit` or `io.papermc` type appears in `utilities-api`.

### Bukkit adapter

Add strict wrappers in `org.aincraft.bukkit.adapter`:

- `BukkitScoreboardManagerWrapper`
- `BukkitScoreboardWrapper`
- `BukkitObjectiveWrapper`
- `BukkitScoreWrapper`
- `BukkitTeamWrapper`
- `BukkitCriteriaWrapper`

Extend `BukkitAdapters` with adapt/unwrap overloads for every scoreboard contract. Unwrapping foreign implementations throws `IllegalArgumentException`, matching the existing adapter policy. Spigot component gaps use `LegacyComponentSerializer.legacySection()`; team colors and modern enum values are converted explicitly. The wrapper factory methods are overridable so Paper subclasses can preserve native return types.

`BukkitServerWrapper.scoreboardManager()` delegates to `Server#getScoreboardManager`, and `BukkitServerWrapper.scoreboardCriteria(String)` delegates to `Server#getScoreboardCriteria`. `BukkitPlayerWrapper` delegates scoreboard get/set to the native player.

### Paper adapter

Add Paper-specialized wrappers in `org.aincraft.paper.adapter`:

- `PaperScoreboardManagerWrapper`
- `PaperScoreboardWrapper`
- `PaperObjectiveWrapper`
- `PaperScoreWrapper`
- `PaperTeamWrapper`

`PaperAdapters` gains scoreboard adapt/unwrap overloads. Paper server/player wrappers override scoreboard accessors so all nested results remain Paper wrappers. Objective/team component accessors and mutators use Paper's native Adventure methods. Paper score optional methods use their native Paper methods. The Paper layer therefore avoids the legacy conversion used only by the Spigot fallback.

### Sidebar UI package

Add to `utilities-common` under `org.aincraft.ui.scoreboard`:

- `ScoreboardLine`: immutable `(id, Component content)` value with non-null and non-blank ID validation.
- `ScoreboardLayout`: immutable title plus ordered lines, defensive copies, duplicate-ID validation, and a hard maximum of 15 sidebar lines. A builder and varargs factory provide ergonomic construction.
- `ScoreboardController`: owns a `ScoreboardManager`, objective name, and viewer states. `show`/`update` render a layout for a player; `refresh` computes a layout from a player; `hide` restores the player's scoreboard captured before showing; `isShown` reports ownership; `close` hides all viewers and unregisters managed teams/objectives.

Each viewer state owns a newly created scoreboard and objective. Lines use one managed team and one invisible color-code entry per active row. Team prefixes carry the Adventure content and objective scores determine row order. Line IDs retain their allocated slot across updates, so reordering changes scores rather than creating unnecessary native entries. Stale lines reset their scores, remove their entries, and unregister their teams. The controller does not schedule updates and requires callers to invoke it on the platform's valid server thread.

## Data flow

1. Consumer obtains `server.scoreboardManager()` from the portable API.
2. Consumer creates `ScoreboardController(manager, objectiveName)`.
3. `show(player, layout)` captures `player.scoreboard()`, creates a private scoreboard, registers a dummy integer objective in `SIDEBAR`, assigns it to the player, and renders the layout.
4. Rendering updates the title, allocates/reuses managed entries, writes team prefixes, and sets descending scores.
5. `update`/`refresh` applies the same reconciliation to the existing private scoreboard.
6. `hide` restores the captured scoreboard and unregisters managed native objects. `close` repeats this for every viewer.

## Error handling and invariants

- Null arguments fail with `NullPointerException` at API boundaries.
- Blank objective names and line IDs fail with `IllegalArgumentException`.
- Layouts with duplicate IDs or more than 15 lines fail before native mutation.
- Controller operations after `close` fail fast with `IllegalStateException`; `hide` and repeated `close` are idempotent.
- Native API validation and lifecycle exceptions propagate unchanged.
- Unsupported platform calls throw the explicit scoreboard capability exception rather than returning fabricated state.
- Controller state is not synchronized; scoreboard operations are expected on the platform's server thread.

## Testing and verification

- API tests validate layout immutability, ID validation, line-count limits, and unsupported capability defaults.
- Common tests use mocked portable contracts to verify per-viewer scoreboard isolation, descending scores, line reconciliation, reordering, restoration, and cleanup.
- Bukkit tests verify legacy component delegation, enum/color conversion, adapt/unwrap strictness, and player/server accessors.
- Paper tests verify native Adventure delegation and Paper wrapper preservation.
- Focused Gradle module tests run first, followed by `./gradlew check` for formatting, isolation, compilation, and all tests.
- A final smoke check exercises the controller through the adapter contracts; no live server is required because the repository's adapter tests use platform API doubles.

## File map

- Modify `utilities-api/build.gradle.kts`, `utilities-api/src/main/java/org/aincraft/api/Capability.java`, `utilities-api/src/main/java/org/aincraft/api/domain/server/Server.java`, and `utilities-api/src/main/java/org/aincraft/api/domain/entity/Player.java`.
- Add the portable scoreboard types under `utilities-api/src/main/java/org/aincraft/api/domain/scoreboard/` and focused API tests.
- Modify `utilities-bukkit/build.gradle.kts`; add the six Bukkit scoreboard wrappers; extend `BukkitAdapters`, `BukkitServerWrapper`, and `BukkitPlayerWrapper`; add adapter tests.
- Modify `utilities-paper/src/main/java/org/aincraft/paper/adapter/PaperAdapters.java`, `PaperServerWrapper.java`, and `PaperPlayerWrapper.java`; add the five Paper scoreboard wrappers and tests.
- Modify `utilities-common/build.gradle.kts`; add the three UI package classes and tests.
- Modify `utilities-bom/build.gradle.kts` and `README.md` to publish and document the new capability.
