package org.aincraft.api.domain.entity;

import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.aincraft.api.domain.attribute.Attributable;
import org.aincraft.api.domain.effect.PotionEffect;
import org.aincraft.api.domain.effect.PotionEffectType;
import org.aincraft.api.domain.inventory.EntityEquipment;
import org.aincraft.api.domain.location.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface LivingEntity extends Damageable, Attributable {

  double eyeHeight();

  @NotNull
  Location eyeLocation();

  boolean hasLineOfSight(@NotNull Entity other);

  @Nullable
  LivingEntity target();

  void setTarget(@Nullable LivingEntity target);

  boolean isGliding();

  boolean isSwimming();

  boolean isSleeping();

  boolean isInvisible();

  void setInvisible(boolean invisible);

  @NotNull
  EntityEquipment equipment();

  void attack(@NotNull Entity target);

  void swingMainHand();

  void swingOffHand();

  @Nullable
  PotionEffect potionEffect(@NotNull PotionEffectType type);

  @NotNull
  Collection<? extends PotionEffect> activePotionEffects();

  void addPotionEffect(@NotNull PotionEffect effect);

  boolean addPotionEffect(@NotNull PotionEffect effect, boolean force);

  boolean clearActivePotionEffects();

  void removePotionEffect(@NotNull PotionEffectType type);

  boolean hasPotionEffect(@NotNull PotionEffectType type);

  default int remainingAir() {
    throw new UnsupportedCapabilityException(Capability.LIVING_AIR);
  }

  default void setRemainingAir(int ticks) {
    throw new UnsupportedCapabilityException(Capability.LIVING_AIR);
  }

  default boolean hasAI() {
    throw new UnsupportedCapabilityException(Capability.LIVING_AI);
  }

  default void setAI(boolean hasAi) {
    throw new UnsupportedCapabilityException(Capability.LIVING_AI);
  }
}
