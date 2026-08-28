package org.aincraft.minestom.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.effect.Particle;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class MinestomParticleWrapper implements Particle {

  private final net.minestom.server.particle.Particle particle;

  public MinestomParticleWrapper(@NotNull net.minestom.server.particle.Particle particle) {
    this.particle = Objects.requireNonNull(particle, "particle cannot be null");
  }

  public @NotNull net.minestom.server.particle.Particle getMinestomParticle() {
    return particle;
  }

  @Override
  public @NotNull String asString() {
    return particle.key().asString();
  }

  @Override
  public @NotNull String namespace() {
    return particle.key().namespace();
  }

  @Override
  public @NotNull String value() {
    return particle.key().value();
  }

  @Override
  public @NotNull Class<?> dataType() {
    return particle instanceof net.minestom.server.particle.Particle.Simple
        ? Void.class
        : particle.getClass();
  }

  @Override
  public boolean equals(Object other) {
    return this == other
        || (other instanceof Particle value && asString().equals(value.asString()));
  }

  @Override
  public int hashCode() {
    return Key.key(asString()).hashCode();
  }

  @Override
  public String toString() {
    return "MinestomParticleWrapper{" + asString() + "}";
  }
}
