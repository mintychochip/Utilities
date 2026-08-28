package org.aincraft.api.domain.location;

import org.jetbrains.annotations.NotNull;

/**
 * A yaw/pitch pair representing orientation independently of position. Mirrors {@code
 * io.papermc.paper.math.Rotation} for cross-platform use.
 */
public interface Rotation {

  float yaw();

  float pitch();

  @NotNull
  Rotation add(float yawDelta, float pitchDelta);

  @NotNull
  Rotation subtract(float yawDelta, float pitchDelta);
}
