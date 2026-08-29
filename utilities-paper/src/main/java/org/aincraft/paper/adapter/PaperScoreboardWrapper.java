package org.aincraft.paper.adapter;

import net.kyori.adventure.text.Component;
import org.aincraft.api.domain.entity.Entity;
import org.aincraft.api.domain.scoreboard.Criteria;
import org.aincraft.api.domain.scoreboard.Objective;
import org.aincraft.api.domain.scoreboard.RenderType;
import org.aincraft.api.domain.scoreboard.Score;
import org.aincraft.api.domain.scoreboard.Team;
import org.aincraft.bukkit.adapter.BukkitScoreboardWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

/** Paper-backed scoreboard that keeps nested values on the Paper adapter path. */
public class PaperScoreboardWrapper extends BukkitScoreboardWrapper {

  public PaperScoreboardWrapper(@NotNull org.bukkit.scoreboard.Scoreboard scoreboard) {
    super(scoreboard);
  }

  @Override
  public @NotNull Set<? extends Score> scoresFor(@NotNull Entity entity) {
    return getBukkitScoreboard()
        .getScoresFor(org.aincraft.bukkit.adapter.BukkitAdapters.toBukkit(entity))
        .stream()
        .map(this::adaptScore)
        .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public void resetScoresFor(@NotNull Entity entity) {
    getBukkitScoreboard()
        .resetScoresFor(org.aincraft.bukkit.adapter.BukkitAdapters.toBukkit(entity));
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
        getBukkitScoreboard()
            .registerNewObjective(
                name, PaperAdapters.toBukkit(criteria), displayName, toBukkit(renderType));
    return adaptObjective(objective);
  }

  private static org.bukkit.scoreboard.RenderType toBukkit(@NotNull RenderType renderType) {
    return switch (renderType) {
      case INTEGER -> org.bukkit.scoreboard.RenderType.INTEGER;
      case HEARTS -> org.bukkit.scoreboard.RenderType.HEARTS;
    };
  }

  @Override
  protected @NotNull Objective adaptObjective(@NotNull org.bukkit.scoreboard.Objective objective) {
    return new PaperObjectiveWrapper(objective);
  }

  @Override
  protected @NotNull Score adaptScore(@NotNull org.bukkit.scoreboard.Score score) {
    return new PaperScoreWrapper(score);
  }

  @Override
  protected @NotNull Team adaptTeam(@NotNull org.bukkit.scoreboard.Team team) {
    return new PaperTeamWrapper(team);
  }
}
