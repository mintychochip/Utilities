package org.aincraft.minestom.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.attribute.AttributeInstance;
import org.aincraft.api.domain.attribute.AttributeModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public final class MinestomAttributeInstanceWrapper implements AttributeInstance {

  private final net.minestom.server.entity.attribute.AttributeInstance instance;

  public MinestomAttributeInstanceWrapper(
      @NotNull net.minestom.server.entity.attribute.AttributeInstance instance) {
    this.instance = Objects.requireNonNull(instance, "instance cannot be null");
  }

  public @NotNull net.minestom.server.entity.attribute.AttributeInstance getMinestomInstance() {
    return instance;
  }

  @Override
  public @NotNull Key attribute() {
    return instance.attribute().key();
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
    return instance.modifiers().stream().map(MinestomAdapters::adapt).toList();
  }

  @Override
  public void addModifier(@NotNull AttributeModifier modifier) {
    instance.addModifier(MinestomAdapters.toMinestom(modifier));
  }

  @Override
  public void addTransientModifier(@NotNull AttributeModifier modifier) {
    addModifier(modifier);
  }

  @Override
  public double defaultValue() {
    return instance.attribute().defaultValue();
  }

  @Override
  public void removeModifier(@NotNull AttributeModifier modifier) {
    instance.removeModifier(MinestomAdapters.toMinestom(modifier));
  }

  @Override
  public void removeModifier(@NotNull UUID id) {
    AttributeModifier modifier = getModifier(id);
    if (modifier != null) removeModifier(modifier);
  }

  @Override
  public @Nullable AttributeModifier getModifier(@NotNull UUID id) {
    for (AttributeModifier modifier : modifiers()) {
      if (id.equals(modifier.id())) return modifier;
    }
    return null;
  }
}
