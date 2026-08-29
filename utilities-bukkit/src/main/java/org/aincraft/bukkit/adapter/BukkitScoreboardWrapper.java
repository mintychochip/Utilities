package org.aincraft.bukkit.adapter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.aincraft.api.domain.scoreboard.Criteria;
import org.aincraft.api.domain.scoreboard.DisplaySlot;
import org.aincraft.api.domain.scoreboard.Objective;
import org.aincraft.api.domain.scoreboard.RenderType;
import org.aincraft.api.domain.scoreboard.Score;
import org.aincraft.api.domain.scoreboard.Scoreboard;
import org.aincraft.api.domain.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Collectors;

/** Spigot-backed scoreboard. */
public class BukkitScoreboardWrapper implements Scoreboard {

  private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

  private final org.bukkit.scoreboard.Scoreboard scoreboard;

  public BukkitScoreboardWrapper(@NotNull org.bukkit.scoreboard.Scoreboard scoreboard) {
    this.scoreboard = scoreboard;
  }

  public @NotNull org.bukkit.scoreboard.Scoreboard getBukkitScoreboard() {
    return scoreboard;
  }

  @Override
  public @NotNull Set<? extends Objective> objectives() {
    return scoreboard.getObjectives().stream()
        .map(this::adaptObjective)
        .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public @NotNull Set<? extends Objective> objectivesByCriteria(@NotNull Criteria criteria) {
    return scoreboard.getObjectivesByCriteria(BukkitAdapters.toBukkit(criteria)).stream()
        .map(this::adaptObjective)
        .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public @NotNull Set<? extends Team> teams() {
    return scoreboard.getTeams().stream()
        .map(this::adaptTeam)
        .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public @NotNull Set<String> entries() {
    return Set.copyOf(scoreboard.getEntries());
  }

  @Override
  public @Nullable Objective objective(@NotNull String name) {
    org.bukkit.scoreboard.Objective objective = scoreboard.getObjective(name);
    return objective == null ? null : adaptObjective(objective);
  }

  @Override
  public @Nullable Objective objective(@NotNull DisplaySlot slot) {
    org.bukkit.scoreboard.Objective objective = scoreboard.getObjective(toBukkit(slot));
    return objective == null ? null : adaptObjective(objective);
  }

  @Override
  public @Nullable Team team(@NotNull String name) {
    org.bukkit.scoreboard.Team team = scoreboard.getTeam(name);
    return team == null ? null : adaptTeam(team);
  }

  @Override
  public @Nullable Team entryTeam(@NotNull String entry) {
    org.bukkit.scoreboard.Team team = scoreboard.getEntryTeam(entry);
    return team == null ? null : adaptTeam(team);
  }

  @Override
  public @NotNull Set<? extends Score> scores(@NotNull String entry) {
    return scoreboard.getScores(entry).stream()
        .map(this::adaptScore)
        .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public @NotNull Objective registerObjective(
      @NotNull String name, @NotNull Criteria criteria, @NotNull Component displayName) {
    return registerObjective(name, criteria, displayName, criteria.defaultRenderType());
  }

  @Override
  public @NotNull Objective registerObjective(
      @NotNull String name,
      @NotNull Criteria criteria,
      @NotNull Component displayName,
      @NotNull RenderType renderType) {
    org.bukkit.scoreboard.Objective objective =
        scoreboard.registerNewObjective(
            name,
            BukkitAdapters.toBukkit(criteria),
            LEGACY.serialize(displayName),
            toBukkit(renderType));
    return adaptObjective(objective);
  }

  @Override
  public @NotNull Team registerTeam(@NotNull String name) {
    return adaptTeam(scoreboard.registerNewTeam(name));
  }

  @Override
  public void clearSlot(@NotNull DisplaySlot slot) {
    scoreboard.clearSlot(toBukkit(slot));
  }

  @Override
  public void resetScores(@NotNull String entry) {
    scoreboard.resetScores(entry);
  }

  protected @NotNull Objective adaptObjective(@NotNull org.bukkit.scoreboard.Objective objective) {
    return new BukkitObjectiveWrapper(objective);
  }

  protected @NotNull Score adaptScore(@NotNull org.bukkit.scoreboard.Score score) {
    return new BukkitScoreWrapper(score);
  }

  protected @NotNull Team adaptTeam(@NotNull org.bukkit.scoreboard.Team team) {
    return new BukkitTeamWrapper(team);
  }

  private static org.bukkit.scoreboard.DisplaySlot toBukkit(@NotNull DisplaySlot slot) {
    return switch (slot) {
      case SIDEBAR -> org.bukkit.scoreboard.DisplaySlot.SIDEBAR;
      case BELOW_NAME -> org.bukkit.scoreboard.DisplaySlot.BELOW_NAME;
      case PLAYER_LIST -> org.bukkit.scoreboard.DisplaySlot.PLAYER_LIST;
    };
  }

  private static org.bukkit.scoreboard.RenderType toBukkit(@NotNull RenderType renderType) {
    return switch (renderType) {
      case INTEGER -> org.bukkit.scoreboard.RenderType.INTEGER;
      case HEARTS -> org.bukkit.scoreboard.RenderType.HEARTS;
    };
  }
}
