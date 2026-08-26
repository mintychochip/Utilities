package org.aincraft.common.entity;

import java.util.Collection;
import org.aincraft.common.attribute.Attributable;
import org.aincraft.common.effect.PotionEffect;
import org.aincraft.common.effect.PotionEffectType;
import org.aincraft.common.inventory.EntityEquipment;
import org.aincraft.common.location.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LivingEntity extends Damageable, Attributable {

  double eyeHeight();

  @NotNull Location eyeLocation();

  boolean hasLineOfSight(@NotNull Entity other);

  @Nullable LivingEntity target();

  void setTarget(@Nullable LivingEntity target);

  boolean isGliding();

  boolean isSwimming();

  boolean isSleeping();

  boolean isInvisible();

  void setInvisible(boolean invisible);

  @NotNull EntityEquipment equipment();

  void attack(@NotNull Entity target);

  void swingMainHand();

  void swingOffHand();

  @Nullable PotionEffect potionEffect(@NotNull PotionEffectType type);

  @NotNull Collection<? extends PotionEffect> activePotionEffects();

  void addPotionEffect(@NotNull PotionEffect effect);

  boolean addPotionEffect(@NotNull PotionEffect effect, boolean force);

  boolean clearActivePotionEffects();

  void removePotionEffect(@NotNull PotionEffectType type);

  boolean hasPotionEffect(@NotNull PotionEffectType type);
}
