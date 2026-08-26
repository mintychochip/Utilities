package org.aincraft.minestom.adapter;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.damage.Damage;
import org.aincraft.common.effect.PotionEffect;
import org.aincraft.common.effect.PotionEffectType;
import org.aincraft.common.entity.Entity;
import org.aincraft.common.location.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MinestomLivingEntityWrapper extends MinestomEntityWrapper implements org.aincraft.common.entity.LivingEntity {

  private final LivingEntity livingEntity;

  public MinestomLivingEntityWrapper(@NotNull LivingEntity livingEntity) {
    super(livingEntity);
    this.livingEntity = Objects.requireNonNull(livingEntity, "livingEntity cannot be null");
  }

  public @NotNull LivingEntity getMinestomLivingEntity() {
    return livingEntity;
  }

  @Override
  public double health() {
    return livingEntity.getHealth();
  }

  @Override
  public void setHealth(double health) {
    livingEntity.setHealth((float) Math.max(0.0, health));
  }

  @Override
  public double maxHealth() {
    return livingEntity.getAttributeValue(Attribute.MAX_HEALTH);
  }

  @Override
  public void damage(double amount) {
    livingEntity.damage(Damage.fromEntity(livingEntity, (float) amount));
  }

  @Override
  public void damage(double amount, @Nullable Entity source) {
    if (source instanceof MinestomEntityWrapper wrapper) {
      livingEntity.damage(Damage.fromEntity(wrapper.getMinestomEntity(), (float) amount));
    } else {
      damage(amount);
    }
  }

  @Override
  public double eyeHeight() {
    return livingEntity.getEyeHeight();
  }

  @Override
  public @NotNull Location eyeLocation() {
    Pos pos = livingEntity.getPosition().add(0, livingEntity.getEyeHeight(), 0);
    return MinestomAdapters.adapt(livingEntity.getInstance(), pos);
  }

  @Override
  public boolean hasLineOfSight(@NotNull Entity other) {
    return true;
  }

  @Override
  public @Nullable org.aincraft.common.entity.LivingEntity target() {
    return null;
  }

  @Override
  public void setTarget(@Nullable org.aincraft.common.entity.LivingEntity target) {
  }

  @Override
  public boolean isGliding() {
    return false;
  }

  @Override
  public boolean isSwimming() {
    return false;
  }

  @Override
  public boolean isSleeping() {
    return false;
  }

  @Override
  public @NotNull Collection<? extends PotionEffect> activePotionEffects() {
    return Collections.emptyList();
  }

  @Override
  public void addPotionEffect(@NotNull PotionEffect effect) {
  }

  @Override
  public void removePotionEffect(@NotNull PotionEffectType type) {
  }

  @Override
  public boolean hasPotionEffect(@NotNull PotionEffectType type) {
    return false;
  }
}
