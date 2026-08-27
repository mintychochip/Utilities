package org.aincraft.minestom.adapter;

import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.attribute.Attribute;
import org.aincraft.common.attribute.AttributeInstance;
import org.aincraft.common.attribute.AttributeModifier;
import org.aincraft.common.effect.PotionEffect;
import org.aincraft.common.effect.PotionEffectType;
import org.aincraft.common.entity.Entity;
import org.aincraft.common.location.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public class MinestomLivingEntityWrapper extends MinestomEntityWrapper
    implements org.aincraft.common.entity.LivingEntity {

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
    throw new UnsupportedOperationException();
  }

  @Override
  public void damage(double amount, @Nullable Entity source) {
    throw new UnsupportedOperationException();
  }

  @Override
  public @Nullable AttributeInstance getAttribute(@NotNull Key attribute) {
    Objects.requireNonNull(attribute, "attribute cannot be null");
    Attribute mAttr = Attribute.fromKey(attribute);
    if (mAttr == null) {
      return null;
    }
    net.minestom.server.entity.attribute.AttributeInstance inst = livingEntity.getAttribute(mAttr);
    if (inst == null) {
      return null;
    }
    return new AttributeInstance() {
      @Override
      public @NotNull Key attribute() {
        return attribute;
      }

      @Override
      public double baseValue() {
        return inst.getBaseValue();
      }

      @Override
      public void setBaseValue(double value) {
        inst.setBaseValue(value);
      }

      @Override
      public double value() {
        return inst.getValue();
      }

      @Override
      public @NotNull Collection<? extends AttributeModifier> modifiers() {
        return Collections.emptyList();
      }

      @Override
      public void addModifier(@NotNull AttributeModifier modifier) {}

      @Override
      public void removeModifier(@NotNull AttributeModifier modifier) {}

      @Override
      public void removeModifier(@NotNull UUID id) {}

      @Override
      public @Nullable AttributeModifier getModifier(@NotNull UUID id) {
        return null;
      }
    };
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
  public void setTarget(@Nullable org.aincraft.common.entity.LivingEntity target) {}

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
  public void addPotionEffect(@NotNull PotionEffect effect) {}

  @Override
  public void removePotionEffect(@NotNull PotionEffectType type) {}

  @Override
  public boolean hasPotionEffect(@NotNull PotionEffectType type) {
    return false;
  }

  @Override
  public boolean isInvisible() {
    return false;
  }

  @Override
  public void setInvisible(boolean invisible) {
    throw new UnsupportedOperationException();
  }

  @Override
  public @NotNull org.aincraft.common.inventory.EntityEquipment equipment() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void attack(@NotNull org.aincraft.common.entity.Entity target) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void swingMainHand() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void swingOffHand() {
    throw new UnsupportedOperationException();
  }

  @Override
  public @org.jetbrains.annotations.Nullable org.aincraft.common.effect.PotionEffect potionEffect(
      @NotNull org.aincraft.common.effect.PotionEffectType type) {
    return null;
  }

  @Override
  public boolean clearActivePotionEffects() {
    throw new UnsupportedOperationException();
  }

  @Override
  public double absorptionAmount() {
    return 0;
  }

  @Override
  public void setAbsorptionAmount(double amount) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void kill() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addPotionEffect(
      @NotNull org.aincraft.common.effect.PotionEffect effect, boolean force) {
    throw new UnsupportedOperationException();
  }
}
