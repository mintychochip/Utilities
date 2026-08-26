package org.aincraft.common.entity;

import org.jetbrains.annotations.Nullable;

public interface Damageable extends Entity {

  double health();

  void setHealth(double health);

  double maxHealth();

  void damage(double amount);

  void damage(double amount, @Nullable Entity source);
}
