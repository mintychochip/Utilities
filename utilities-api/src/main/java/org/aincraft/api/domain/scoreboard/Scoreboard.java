package org.aincraft.api.domain.scoreboard;

import java.util.Set;
import net.kyori.adventure.text.Component;
import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.aincraft.api.domain.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A collection of objectives, scores, and teams displayed by a server. */
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

  /** Returns entity scores where the platform exposes the Paper entity overload. */
  default @NotNull Set<? extends Score> scoresFor(@NotNull Entity entity) {
    throw new UnsupportedCapabilityException(Capability.SCOREBOARD);
  }

  /** Resets entity scores where the platform exposes the Paper entity overload. */
  default void resetScoresFor(@NotNull Entity entity) {
    throw new UnsupportedCapabilityException(Capability.SCOREBOARD);
  }
}
