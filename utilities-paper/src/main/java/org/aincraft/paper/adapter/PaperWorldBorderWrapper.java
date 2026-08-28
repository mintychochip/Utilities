package org.aincraft.paper.adapter;

import org.aincraft.api.domain.world.WorldBorder;
import org.aincraft.bukkit.adapter.BukkitWorldBorderWrapper;
import org.jetbrains.annotations.NotNull;

/**
 * Paper-specific {@link WorldBorder} that overrides {@link WorldBorder#reset()} with the native
 * Paper implementation. Spigot's {@code org.bukkit.WorldBorder} has no {@code reset()} method; this
 * adapter bridges the gap for Paper servers.
 */
public class PaperWorldBorderWrapper extends BukkitWorldBorderWrapper {

  public PaperWorldBorderWrapper(@NotNull org.bukkit.WorldBorder bukkitWorldBorder) {
    super(bukkitWorldBorder);
  }

  /**
   * Restores the border to its world default.
   *
   * <p>Paper exposes {@code WorldBorder#reset()} natively. Spigot has no equivalent.
   */
  @Override
  public void reset() {
    getBukkitWorldBorder().reset();
  }
}
