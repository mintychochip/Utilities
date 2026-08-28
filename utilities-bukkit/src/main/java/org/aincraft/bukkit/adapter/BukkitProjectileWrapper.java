package org.aincraft.bukkit.adapter;

import org.aincraft.api.domain.entity.Projectile;
import org.aincraft.api.domain.entity.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitProjectileWrapper extends BukkitEntityWrapper implements Projectile {

  private final org.bukkit.entity.Projectile projectile;

  public BukkitProjectileWrapper(@NotNull org.bukkit.entity.Projectile projectile) {
    super(projectile);
    this.projectile = projectile;
  }

  public @NotNull org.bukkit.entity.Projectile getBukkitProjectile() {
    return projectile;
  }

  @Override
  public @Nullable ProjectileSource shooter() {
    org.bukkit.projectiles.ProjectileSource shooter = projectile.getShooter();
    return shooter != null ? BukkitAdapters.adapt(shooter) : null;
  }

  @Override
  public void setShooter(@Nullable ProjectileSource shooter) {
    projectile.setShooter(shooter != null ? BukkitAdapters.toBukkit(shooter) : null);
  }

  @Override
  public boolean doesBounce() {
    return projectile.doesBounce();
  }

  @Override
  public void setBounce(boolean bounce) {
    projectile.setBounce(bounce);
  }
}
