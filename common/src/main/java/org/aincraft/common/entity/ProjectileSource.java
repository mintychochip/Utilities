package org.aincraft.common.entity;

import org.jetbrains.annotations.NotNull;

public interface ProjectileSource {

  <T extends Projectile> @NotNull T launchProjectile(@NotNull Class<? extends T> projectileClass);
}
