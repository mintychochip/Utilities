package org.aincraft.bukkit.adapter;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import net.kyori.adventure.key.Key;
import org.aincraft.common.attribute.AttributeInstance;
import org.aincraft.common.attribute.AttributeModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
  public @NotNull Collection<? extends AttributeModifier> modifiers() {
    return instance.getModifiers().stream()
        .map(BukkitAdapters::adapt)
        .toList();
  }

  @Override
  public void addModifier(@NotNull AttributeModifier modifier) {
    instance.addModifier(BukkitAdapters.toBukkit(modifier));
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
