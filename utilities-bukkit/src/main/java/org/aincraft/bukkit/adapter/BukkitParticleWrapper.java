package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.common.effect.Particle;
import org.jetbrains.annotations.NotNull;

public class BukkitParticleWrapper implements Particle {

  private final org.bukkit.Particle particle;

  public BukkitParticleWrapper(@NotNull org.bukkit.Particle particle) {
    this.particle = particle;
  }

  public @NotNull org.bukkit.Particle getBukkitParticle() {
    return particle;
  }

  @Override
  public @NotNull String asString() {
    return Key.key(particle.getKey().toString()).asString();
  }

  @Override
  public @NotNull String namespace() {
    return particle.getKey().getNamespace();
  }

  @Override
  public @NotNull String value() {
    return particle.getKey().getKey();
  }

  @Override
  public @NotNull Class<?> dataType() {
    return particle.getDataType();
  }
}
