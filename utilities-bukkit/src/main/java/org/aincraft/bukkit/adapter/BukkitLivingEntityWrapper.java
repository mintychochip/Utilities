package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.attribute.AttributeInstance;
import org.aincraft.api.domain.effect.PotionEffect;
import org.aincraft.api.domain.effect.PotionEffectType;
import org.aincraft.api.domain.entity.Entity;
import org.aincraft.api.domain.entity.LivingEntity;
import org.aincraft.api.domain.inventory.EntityEquipment;
import org.aincraft.api.domain.location.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    org.bukkit.attribute.AttributeInstance attr =
        livingEntity.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH);
    return attr != null ? attr.getValue() : 20.0;
  }

  @Override
  public double absorptionAmount() {
    return livingEntity.getAbsorptionAmount();
  }

  @Override
  public void setAbsorptionAmount(double amount) {
    livingEntity.setAbsorptionAmount(amount);
  }

  @Override
  public void kill() {
    setHealth(0);
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
      mob.setTarget(
          target != null ? (org.bukkit.entity.LivingEntity) BukkitAdapters.toBukkit(target) : null);
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
  public boolean isInvisible() {
    return livingEntity.isInvisible();
  }

  @Override
  public void setInvisible(boolean invisible) {
    livingEntity.setInvisible(invisible);
  }

  @Override
  public @NotNull EntityEquipment equipment() {
    return new BukkitEntityEquipmentWrapper(livingEntity.getEquipment());
  }

  @Override
  public void attack(@NotNull Entity target) {
    livingEntity.attack(BukkitAdapters.toBukkit(target));
  }

  @Override
  public void swingMainHand() {
    livingEntity.swingMainHand();
  }

  @Override
  public void swingOffHand() {
    livingEntity.swingOffHand();
  }

  @Override
  public @Nullable PotionEffect potionEffect(@NotNull PotionEffectType type) {
    org.bukkit.potion.PotionEffect bEffect =
        livingEntity.getPotionEffect(BukkitAdapters.toBukkit(type));
    return bEffect != null ? BukkitAdapters.adapt(bEffect) : null;
  }

  @Override
  public @NotNull Collection<? extends PotionEffect> activePotionEffects() {
    List<PotionEffect> result = new ArrayList<>();
    for (org.bukkit.potion.PotionEffect bEffect : livingEntity.getActivePotionEffects()) {
      result.add(BukkitAdapters.adapt(bEffect));
    }
    return result;
  }

  @Override
  public void addPotionEffect(@NotNull PotionEffect effect) {
    livingEntity.addPotionEffect(BukkitAdapters.toBukkit(effect));
  }

  @Override
  public boolean addPotionEffect(@NotNull PotionEffect effect, boolean force) {
    return livingEntity.addPotionEffect(BukkitAdapters.toBukkit(effect), force);
  }

  @Override
  public boolean clearActivePotionEffects() {
    boolean changed = false;
    for (org.bukkit.potion.PotionEffectType type :
        livingEntity.getActivePotionEffects().stream()
            .map(org.bukkit.potion.PotionEffect::getType)
            .toList()) {
      livingEntity.removePotionEffect(type);
      changed = true;
    }
    return changed;
  }

  @Override
  public void removePotionEffect(@NotNull PotionEffectType type) {
    org.bukkit.potion.PotionEffectType bType = BukkitAdapters.toBukkit(type);
    livingEntity.removePotionEffect(bType);
  }

  @Override
  public boolean hasPotionEffect(@NotNull PotionEffectType type) {
    org.bukkit.potion.PotionEffectType bType = BukkitAdapters.toBukkit(type);
    return livingEntity.hasPotionEffect(bType);
  }

  @Override
  public int remainingAir() {
    return livingEntity.getRemainingAir();
  }

  @Override
  public void setRemainingAir(int ticks) {
    livingEntity.setRemainingAir(ticks);
  }

  @Override
  public boolean hasAI() {
    return livingEntity.hasAI();
  }

  @Override
  public void setAI(boolean hasAi) {
    livingEntity.setAI(hasAi);
  }

  @Override
  public @Nullable AttributeInstance getAttribute(@NotNull Key attribute) {
    org.bukkit.attribute.Attribute bAttr = BukkitAdapters.toBukkit(attribute);
    org.bukkit.attribute.AttributeInstance inst = livingEntity.getAttribute(bAttr);
    return inst != null ? new BukkitAttributeInstanceWrapper(inst) : null;
  }

  @Override
  public void registerAttribute(@NotNull org.aincraft.api.domain.attribute.Attribute attribute) {
    try {
      livingEntity
          .getClass()
          .getMethod("registerAttribute", org.bukkit.attribute.Attribute.class)
          .invoke(livingEntity, BukkitAdapters.toBukkit(attribute.key()));
    } catch (NoSuchMethodException e) {
      throw new org.aincraft.api.UnsupportedCapabilityException(
          org.aincraft.api.Capability.ATTRIBUTE_MODIFIER,
          "Spigot Attributable does not expose custom attribute registration.");
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Unable to register attribute", e);
    }
  }
}
