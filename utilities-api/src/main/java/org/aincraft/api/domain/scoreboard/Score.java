package org.aincraft.api.domain.scoreboard;

import net.kyori.adventure.text.Component;
import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A score value for one entry on one objective. */
public interface Score {

  @NotNull String entry();

  @NotNull Objective objective();

  @Nullable Scoreboard scoreboard();

  int score();

  void score(int score);

  boolean isScoreSet();

  void reset();

  /** Returns Paper's optional custom display name for this score entry. */
  default @Nullable Component customName() {
    throw new UnsupportedCapabilityException(Capability.SCOREBOARD);
  }

  /** Sets or clears Paper's optional custom display name for this score entry. */
  default void customName(@Nullable Component customName) {
    throw new UnsupportedCapabilityException(Capability.SCOREBOARD);
  }

  /** Returns whether a trigger objective's score can be changed with /trigger. */
  default boolean isTriggerable() {
    throw new UnsupportedCapabilityException(Capability.SCOREBOARD);
  }

  /** Sets whether a trigger objective's score can be changed with /trigger. */
  default void setTriggerable(boolean triggerable) {
    throw new UnsupportedCapabilityException(Capability.SCOREBOARD);
  }
}
