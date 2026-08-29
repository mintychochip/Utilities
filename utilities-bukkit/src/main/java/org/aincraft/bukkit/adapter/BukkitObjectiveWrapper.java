package org.aincraft.bukkit.adapter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.aincraft.api.domain.scoreboard.Criteria;
import org.aincraft.api.domain.scoreboard.DisplaySlot;
import org.aincraft.api.domain.scoreboard.Objective;
import org.aincraft.api.domain.scoreboard.RenderType;
import org.aincraft.api.domain.scoreboard.Score;
import org.aincraft.api.domain.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Spigot-backed scoreboard objective. */
public class BukkitObjectiveWrapper implements Objective {

  private static final LegacyComponentSerializer LEGACY =
      LegacyComponentSerializer.legacySection();

  private final org.bukkit.scoreboard.Objective objective;

  public BukkitObjectiveWrapper(@NotNull org.bukkit.scoreboard.Objective objective) {
    this.objective = objective;
  }

  public @NotNull org.bukkit.scoreboard.Objective getBukkitObjective() {
    return objective;
  }

  @Override
  public @NotNull String name() {
    return objective.getName();
  }

  @Override
  public @NotNull Criteria trackedCriteria() {
    return BukkitAdapters.adapt(objective.getTrackedCriteria());
  }

  @Override
  public boolean isModifiable() {
    return objective.isModifiable();
  }

  @Override
  public @NotNull Component displayName() {
    return LEGACY.deserialize(objective.getDisplayName());
  }

  @Override
  public void displayName(@NotNull Component displayName) {
    objective.setDisplayName(LEGACY.serialize(displayName));
  }

  @Override
  public @Nullable Scoreboard scoreboard() {
    org.bukkit.scoreboard.Scoreboard scoreboard = objective.getScoreboard();
    return scoreboard == null ? null : BukkitAdapters.adapt(scoreboard);
  }

  @Override
  public void unregister() {
    objective.unregister();
  }

  @Override
  public void displaySlot(@Nullable DisplaySlot slot) {
    objective.setDisplaySlot(slot == null ? null : toBukkit(slot));
  }

  @Override
  public @Nullable DisplaySlot displaySlot() {
    org.bukkit.scoreboard.DisplaySlot slot = objective.getDisplaySlot();
    return slot == null ? null : fromBukkit(slot);
  }

  @Override
  public void renderType(@NotNull RenderType renderType) {
    objective.setRenderType(toBukkit(renderType));
  }

  @Override
  public @NotNull RenderType renderType() {
    return fromBukkit(objective.getRenderType());
  }

  @Override
  public @NotNull Score score(@NotNull String entry) {
    return adaptScore(objective.getScore(entry));
  }

  protected @NotNull Score adaptScore(@NotNull org.bukkit.scoreboard.Score score) {
    return new BukkitScoreWrapper(score);
  }

  private static org.bukkit.scoreboard.DisplaySlot toBukkit(@NotNull DisplaySlot slot) {
    return switch (slot) {
      case SIDEBAR -> org.bukkit.scoreboard.DisplaySlot.SIDEBAR;
      case BELOW_NAME -> org.bukkit.scoreboard.DisplaySlot.BELOW_NAME;
      case PLAYER_LIST -> org.bukkit.scoreboard.DisplaySlot.PLAYER_LIST;
    };
  }

  private static DisplaySlot fromBukkit(@NotNull org.bukkit.scoreboard.DisplaySlot slot) {
    return switch (slot) {
      case SIDEBAR -> DisplaySlot.SIDEBAR;
      case BELOW_NAME -> DisplaySlot.BELOW_NAME;
      case PLAYER_LIST -> DisplaySlot.PLAYER_LIST;
      default -> throw new IllegalArgumentException("Unsupported native display slot: " + slot);
    };
  }

  private static org.bukkit.scoreboard.RenderType toBukkit(@NotNull RenderType renderType) {
    return switch (renderType) {
      case INTEGER -> org.bukkit.scoreboard.RenderType.INTEGER;
      case HEARTS -> org.bukkit.scoreboard.RenderType.HEARTS;
    };
  }

  private static RenderType fromBukkit(@NotNull org.bukkit.scoreboard.RenderType renderType) {
    return switch (renderType) {
      case INTEGER -> RenderType.INTEGER;
      case HEARTS -> RenderType.HEARTS;
    };
  }
}
