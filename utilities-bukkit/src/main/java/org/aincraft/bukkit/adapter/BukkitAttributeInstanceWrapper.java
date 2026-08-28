package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.attribute.AttributeInstance;
import org.aincraft.api.domain.attribute.AttributeModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class BukkitAttributeInstanceWrapper implements AttributeInstance {

  private final org.bukkit.attribute.AttributeInstance instance;

  public BukkitAttributeInstanceWrapper(@NotNull org.bukkit.attribute.AttributeInstance instance) {
    this.instance = Objects.requireNonNull(instance, "instance cannot be null");
  }

  public @NotNull org.bukkit.attribute.AttributeInstance getBukkitAttributeInstance() {
    return instance;
  }

  @Override
  public @NotNull Key attribute() {
    return BukkitAdapters.adapt(instance.getAttribute());
  }

  @Override
  public double baseValue() {
    return instance.getBaseValue();
  }

  @Override
  public void setBaseValue(double value) {
    instance.setBaseValue(value);
  }

  @Override
  public double value() {
    return instance.getValue();
  }

  @Override
  public double defaultValue() {
    return instance.getDefaultValue();
  }

  @Override
  public @NotNull Collection<? extends AttributeModifier> modifiers() {
    return instance.getModifiers().stream().map(BukkitAdapters::adapt).toList();
  }

  @Override
  public void addModifier(@NotNull AttributeModifier modifier) {
    instance.addModifier(BukkitAdapters.toBukkit(modifier));
  }

  @Override
  public void addTransientModifier(@NotNull AttributeModifier modifier) {
    try {
      instance
          .getClass()
          .getMethod("addTransientModifier", org.bukkit.attribute.AttributeModifier.class)
          .invoke(instance, BukkitAdapters.toBukkit(modifier));
    } catch (NoSuchMethodException e) {
      throw new org.aincraft.api.UnsupportedCapabilityException(
          org.aincraft.api.Capability.ATTRIBUTE_MODIFIER,
          "Spigot AttributeInstance has no addTransientModifier method.");
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Unable to invoke addTransientModifier", e);
    }
  }

  @Override
  public void removeModifier(@NotNull AttributeModifier modifier) {
    instance.removeModifier(BukkitAdapters.toBukkit(modifier));
  }

  @Override
  public void removeModifier(@NotNull UUID id) {
    for (org.bukkit.attribute.AttributeModifier mod : instance.getModifiers()) {
      if (mod.getUniqueId().equals(id)) {
        instance.removeModifier(mod);
        break;
      }
    }
  }

  @Override
  public @Nullable AttributeModifier getModifier(@NotNull UUID id) {
    for (org.bukkit.attribute.AttributeModifier mod : instance.getModifiers()) {
      if (mod.getUniqueId().equals(id)) {
        return BukkitAdapters.adapt(mod);
      }
    }
    return null;
  }
}
