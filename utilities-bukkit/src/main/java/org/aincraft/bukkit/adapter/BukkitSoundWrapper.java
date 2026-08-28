package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;

public class BukkitSoundWrapper implements Sound.Type, org.aincraft.api.domain.effect.Sound {

  private final org.bukkit.Sound sound;

  public BukkitSoundWrapper(@NotNull org.bukkit.Sound sound) {
    this.sound = sound;
  }

  public @NotNull org.bukkit.Sound getBukkitSound() {
    return sound;
  }

  @Override
  public @NotNull Key key() {
    return Key.key(sound.getKey().getNamespace(), sound.getKey().getKey());
  }
}
