package org.aincraft.api.domain.entity;

import org.jetbrains.annotations.Nullable;

public interface Damageable extends Entity {

  double health();

  void setHealth(double health);

  double maxHealth();

  double absorptionAmount();

  void setAbsorptionAmount(double amount);

  void damage(double amount);

  void damage(double amount, @Nullable Entity source);

  void kill();
}
