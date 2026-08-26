package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.common.effect.Sound;
import org.jetbrains.annotations.NotNull;

public class BukkitSoundWrapper implements Sound {

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
