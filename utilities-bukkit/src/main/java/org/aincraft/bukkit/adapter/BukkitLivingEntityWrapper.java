package org.aincraft.bukkit.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.key.Key;
import org.aincraft.common.effect.PotionEffect;
import org.aincraft.common.effect.PotionEffectType;
import org.aincraft.common.entity.Entity;
import org.aincraft.common.entity.LivingEntity;
import org.aincraft.common.location.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitLivingEntityWrapper extends BukkitEntityWrapper implements LivingEntity {

  private final org.bukkit.entity.LivingEntity livingEntity;

  public BukkitLivingEntityWrapper(@NotNull org.bukkit.entity.LivingEntity livingEntity) {
    super(livingEntity);
    this.livingEntity = livingEntity;
  }

  public @NotNull org.bukkit.entity.LivingEntity getBukkitLivingEntity() {
    return livingEntity;
  }

  @Override
  public double health() {
    return livingEntity.getHealth();
  }

  @Override
  public void setHealth(double health) {
    livingEntity.setHealth(Math.max(0.0, health));
  }

  @Override
  public double maxHealth() {
    org.bukkit.attribute.AttributeInstance attr = livingEntity.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH);
    return attr != null ? attr.getValue() : 20.0;
  }

  @Override
  public void damage(double amount) {
    livingEntity.damage(amount);
  }

  @Override
  public void damage(double amount, @Nullable Entity source) {
    if (source != null) {
      livingEntity.damage(amount, BukkitAdapters.toBukkit(source));
    } else {
      livingEntity.damage(amount);
    }
  }

  @Override
  public double eyeHeight() {
    return livingEntity.getEyeHeight();
  }

  @Override
  public @NotNull Location eyeLocation() {
    return BukkitAdapters.adapt(livingEntity.getEyeLocation());
  }

  @Override
  public boolean hasLineOfSight(@NotNull Entity other) {
    return livingEntity.hasLineOfSight(BukkitAdapters.toBukkit(other));
  }

  @Override
  public @Nullable LivingEntity target() {
    if (livingEntity instanceof org.bukkit.entity.Mob mob) {
      org.bukkit.entity.LivingEntity bTarget = mob.getTarget();
      return bTarget != null ? (LivingEntity) BukkitAdapters.adapt(bTarget) : null;
    }
    return null;
  }

  @Override
  public void setTarget(@Nullable LivingEntity target) {
    if (livingEntity instanceof org.bukkit.entity.Mob mob) {
      mob.setTarget(target != null ? (org.bukkit.entity.LivingEntity) BukkitAdapters.toBukkit(target) : null);
    }
  }

  @Override
  public boolean isGliding() {
    return livingEntity.isGliding();
  }

  @Override
  public boolean isSwimming() {
    return livingEntity.isSwimming();
  }

  @Override
  public boolean isSleeping() {
    return livingEntity.isSleeping();
  }

  @Override
  public @NotNull Collection<? extends PotionEffect> activePotionEffects() {
    List<PotionEffect> result = new ArrayList<>();
    for (org.bukkit.potion.PotionEffect bEffect : livingEntity.getActivePotionEffects()) {
      org.bukkit.potion.PotionEffectType bType = bEffect.getType();
      Key typeKey = Key.key(bType.getKey().getNamespace(), bType.getKey().getKey());
      PotionEffectType cType = new PotionEffectType() {
        @Override public @NotNull Key key() { return typeKey; }
        @Override public @NotNull String name() { return bType.getName(); }
        @Override public boolean isInstant() { return bType.isInstant(); }
      };
      result.add(new PotionEffect() {
        @Override public @NotNull PotionEffectType type() { return cType; }
        @Override public int duration() { return bEffect.getDuration(); }
        @Override public int amplifier() { return bEffect.getAmplifier(); }
        @Override public boolean isAmbient() { return bEffect.isAmbient(); }
        @Override public boolean hasParticles() { return bEffect.hasParticles(); }
        @Override public boolean hasIcon() { return bEffect.hasIcon(); }
      });
    }
    return result;
  }

  @Override
  public void addPotionEffect(@NotNull PotionEffect effect) {
    org.bukkit.NamespacedKey nKey = new org.bukkit.NamespacedKey(effect.type().key().namespace(), effect.type().key().value());
    org.bukkit.potion.PotionEffectType bType = org.bukkit.potion.PotionEffectType.getByKey(nKey);
    if (bType != null) {
      livingEntity.addPotionEffect(new org.bukkit.potion.PotionEffect(
          bType, effect.duration(), effect.amplifier(), effect.isAmbient(), effect.hasParticles(), effect.hasIcon()
      ));
    }
  }

  @Override
  public void removePotionEffect(@NotNull PotionEffectType type) {
    org.bukkit.NamespacedKey nKey = new org.bukkit.NamespacedKey(type.key().namespace(), type.key().value());
    org.bukkit.potion.PotionEffectType bType = org.bukkit.potion.PotionEffectType.getByKey(nKey);
    if (bType != null) {
      livingEntity.removePotionEffect(bType);
    }
  }

  @Override
  public boolean hasPotionEffect(@NotNull PotionEffectType type) {
    org.bukkit.NamespacedKey nKey = new org.bukkit.NamespacedKey(type.key().namespace(), type.key().value());
    org.bukkit.potion.PotionEffectType bType = org.bukkit.potion.PotionEffectType.getByKey(nKey);
    return bType != null && livingEntity.hasPotionEffect(bType);
  }
}
