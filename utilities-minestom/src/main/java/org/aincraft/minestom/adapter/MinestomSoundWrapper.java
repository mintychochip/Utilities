package org.aincraft.minestom.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.effect.Sound;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class MinestomSoundWrapper implements Sound {

  private final net.minestom.server.sound.SoundEvent sound;

  public MinestomSoundWrapper(@NotNull net.minestom.server.sound.SoundEvent sound) {
    this.sound = Objects.requireNonNull(sound, "sound cannot be null");
  }

  public @NotNull net.minestom.server.sound.SoundEvent getMinestomSound() {
    return sound;
  }

  @Override
  public @NotNull Key key() {
    return sound.key();
  }

  @Override
  public boolean equals(Object other) {
    return this == other || (other instanceof Sound value && key().equals(value.key()));
  }

  @Override
  public int hashCode() {
    return key().hashCode();
  }

  @Override
  public String toString() {
    return "MinestomSoundWrapper{" + key() + "}";
  }
}
