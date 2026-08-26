package org.aincraft.minestom.adapter;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.damage.Damage;
import org.aincraft.common.attribute.AttributeInstance;
import org.aincraft.common.attribute.AttributeModifier;
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
  public @Nullable AttributeInstance getAttribute(@NotNull org.aincraft.common.attribute.Attribute attribute) {
    Objects.requireNonNull(attribute, "attribute cannot be null");
    Attribute mAttr = Attribute.fromKey(attribute.key());
    if (mAttr == null) {
      return null;
    }
    net.minestom.server.entity.attribute.AttributeInstance inst = livingEntity.getAttribute(mAttr);
    if (inst == null) {
      return null;
    }
    return new AttributeInstance() {
      @Override
      public @NotNull org.aincraft.common.attribute.Attribute attribute() {
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
      public void addModifier(@NotNull AttributeModifier modifier) {
      }

      @Override
      public void removeModifier(@NotNull AttributeModifier modifier) {
      }

      @Override
      public void removeModifier(@NotNull UUID id) {
      }

      @Override
      public @Nullable AttributeModifier getModifier(@NotNull UUID id) {
        return null;
      }
    };
  }

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
