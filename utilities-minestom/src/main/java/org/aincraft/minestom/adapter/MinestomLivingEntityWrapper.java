package org.aincraft.minestom.adapter;

import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.attribute.Attribute;
import org.aincraft.api.domain.attribute.AttributeInstance;
import org.aincraft.api.domain.effect.PotionEffect;
import org.aincraft.api.domain.effect.PotionEffectType;
import org.aincraft.api.domain.entity.Entity;
import org.aincraft.api.domain.location.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

public class MinestomLivingEntityWrapper extends MinestomEntityWrapper
    implements org.aincraft.api.domain.entity.LivingEntity {

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
  public boolean isDead() {
    return livingEntity.isDead();
  }

  @Override
  public void damage(double amount) {
    livingEntity.damage(
        net.minestom.server.entity.damage.Damage.fromPosition(
            net.minestom.server.registry.RegistryKey.unsafeOf(
                net.kyori.adventure.key.Key.key("minecraft", "generic")),
            livingEntity.getPosition(),
            (float) amount));
  }

  @Override
  public void damage(double amount, @Nullable Entity source) {
    if (source == null) {
      damage(amount);
    } else {
      livingEntity.damage(
          net.minestom.server.entity.damage.Damage.fromEntity(
              MinestomAdapters.toMinestom(source), (float) amount));
    }
  }

  @Override
  public @Nullable AttributeInstance getAttribute(@NotNull Key attribute) {
    Objects.requireNonNull(attribute, "attribute cannot be null");
    Attribute minestomAttribute = Attribute.fromKey(attribute);
    if (minestomAttribute == null) return null;
    net.minestom.server.entity.attribute.AttributeInstance instance =
        livingEntity.getAttribute(minestomAttribute);
    return instance == null ? null : MinestomAdapters.adapt(instance);
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
    return livingEntity.hasLineOfSight(MinestomAdapters.toMinestom(other));
  }

  @Override
  public @Nullable org.aincraft.api.domain.entity.LivingEntity target() {
    if (!(livingEntity instanceof net.minestom.server.entity.EntityCreature creature)) return null;
    net.minestom.server.entity.Entity target = creature.getTarget();
    return target instanceof net.minestom.server.entity.LivingEntity living
        ? (org.aincraft.api.domain.entity.LivingEntity) MinestomAdapters.adapt(living)
        : null;
  }

  @Override
  public void setTarget(@Nullable org.aincraft.api.domain.entity.LivingEntity target) {
    if (livingEntity instanceof net.minestom.server.entity.EntityCreature creature) {
      creature.setTarget(target == null ? null : MinestomAdapters.toMinestom(target));
    }
  }

  @Override
  public boolean isGliding() {
    return livingEntity.isFlyingWithElytra();
  }

  @Override
  public boolean isSwimming() {
    return livingEntity.getPose() == net.minestom.server.entity.EntityPose.SWIMMING;
  }

  @Override
  public boolean isSleeping() {
    return livingEntity.getPose() == net.minestom.server.entity.EntityPose.SLEEPING;
  }

  @Override
  public @NotNull org.aincraft.api.domain.inventory.EntityEquipment equipment() {
    return new MinestomEntityEquipmentWrapper(livingEntity);
  }

  @Override
  public void attack(@NotNull org.aincraft.api.domain.entity.Entity target) {
    net.minestom.server.entity.Entity minestomTarget = MinestomAdapters.toMinestom(target);
    if (livingEntity instanceof net.minestom.server.entity.EntityCreature creature) {
      creature.attack(minestomTarget);
      return;
    }
    if (minestomTarget instanceof net.minestom.server.entity.LivingEntity targetLiving) {
      targetLiving.damage(
          net.minestom.server.entity.damage.Damage.fromEntity(
              livingEntity, (float) livingEntity.getAttributeValue(Attribute.ATTACK_DAMAGE)));
    } else {
      throw new org.aincraft.api.UnsupportedCapabilityException(
          org.aincraft.api.Capability.COMBAT,
          "Minestom cannot apply living-entity attack damage to a non-living target.");
    }
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
  public @Nullable org.aincraft.api.domain.effect.PotionEffect potionEffect(
      @NotNull org.aincraft.api.domain.effect.PotionEffectType type) {
    for (net.minestom.server.potion.TimedPotion active : livingEntity.getActiveEffects()) {
      if (active.potion().effect().key().equals(type.key())) {
        return MinestomAdapters.adapt(active.potion());
      }
    }
    return null;
  }

  @Override
  public @NotNull Collection<? extends PotionEffect> activePotionEffects() {
    return livingEntity.getActiveEffects().stream()
        .map(active -> MinestomAdapters.adapt(active.potion()))
        .toList();
  }

  @Override
  public void addPotionEffect(@NotNull PotionEffect effect) {
    livingEntity.addEffect(MinestomAdapters.toMinestom(effect));
  }

  @Override
  public boolean addPotionEffect(@NotNull PotionEffect effect, boolean force) {
    net.minestom.server.potion.Potion potion = MinestomAdapters.toMinestom(effect);
    if (force) livingEntity.removeEffect(potion.effect());
    livingEntity.addEffect(potion);
    return hasPotionEffect(effect.type());
  }

  @Override
  public boolean clearActivePotionEffects() {
    boolean hadEffects = !livingEntity.getActiveEffects().isEmpty();
    livingEntity.clearEffects();
    return hadEffects;
  }

  @Override
  public void removePotionEffect(@NotNull PotionEffectType type) {
    livingEntity.removeEffect(MinestomAdapters.toMinestom(type));
  }

  @Override
  public boolean hasPotionEffect(@NotNull PotionEffectType type) {
    return potionEffect(type) != null;
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
  public double absorptionAmount() {
    if (livingEntity instanceof net.minestom.server.entity.Player player) {
      return player.getAdditionalHearts();
    }
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.DAMAGEABLE_ABSORPTION,
        "Minestom living entities do not expose current absorption hearts.");
  }

  @Override
  public void setAbsorptionAmount(double amount) {
    if (livingEntity instanceof net.minestom.server.entity.Player player) {
      player.setAdditionalHearts((float) Math.max(0.0, amount));
      return;
    }
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.DAMAGEABLE_ABSORPTION,
        "Minestom living entities do not expose current absorption hearts.");
  }

  @Override
  public void kill() {
    livingEntity.kill();
  }

  @Override
  public int remainingAir() {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.LIVING_AIR,
        "Minestom does not expose Bukkit-style remaining-air state.");
  }

  @Override
  public void setRemainingAir(int ticks) {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.LIVING_AIR,
        "Minestom does not expose Bukkit-style remaining-air state.");
  }

  @Override
  public boolean hasAI() {
    return livingEntity instanceof net.minestom.server.entity.EntityCreature creature
        && !creature.getAIGroups().isEmpty();
  }

  @Override
  public void setAI(boolean hasAi) {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.LIVING_AI,
        "Minestom AI groups cannot be enabled or disabled through its public API.");
  }
}
