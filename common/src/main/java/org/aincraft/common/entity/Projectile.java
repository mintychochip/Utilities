package org.aincraft.common.entity;

import org.jetbrains.annotations.Nullable;

public interface Projectile extends Entity {

  @Nullable ProjectileSource shooter();

  void setShooter(@Nullable ProjectileSource shooter);

  boolean doesBounce();

  void setBounce(boolean bounce);
}
