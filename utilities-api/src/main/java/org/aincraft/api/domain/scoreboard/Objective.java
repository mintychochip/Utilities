package org.aincraft.api.domain.scoreboard;

import net.kyori.adventure.text.Component;
import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** An objective that tracks scores and may be displayed in one slot. */
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

  /** Returns whether Paper automatically updates displayed scores. */
  default boolean willAutoUpdateDisplay() {
    throw new UnsupportedCapabilityException(Capability.SCOREBOARD);
  }

  /** Configures whether Paper automatically updates displayed scores. */
  default void setAutoUpdateDisplay(boolean enabled) {
    throw new UnsupportedCapabilityException(Capability.SCOREBOARD);
  }
}
