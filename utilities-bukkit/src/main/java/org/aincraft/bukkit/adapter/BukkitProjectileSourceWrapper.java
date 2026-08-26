package org.aincraft.bukkit.adapter;

import org.aincraft.common.entity.Projectile;
import org.aincraft.common.entity.ProjectileSource;
import org.jetbrains.annotations.NotNull;

public class BukkitProjectileSourceWrapper implements ProjectileSource {

  private final org.bukkit.projectiles.ProjectileSource source;

  public BukkitProjectileSourceWrapper(@NotNull org.bukkit.projectiles.ProjectileSource source) {
    this.source = source;
  }

  public @NotNull org.bukkit.projectiles.ProjectileSource getBukkitProjectileSource() {
    return source;
  }

  @Override
  public <T extends Projectile> @NotNull T launchProjectile(@NotNull Class<? extends T> projectileClass) {
    throw new UnsupportedOperationException();
  }
}
