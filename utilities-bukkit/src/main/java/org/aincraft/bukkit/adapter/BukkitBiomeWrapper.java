package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.common.effect.Biome;
import org.jetbrains.annotations.NotNull;

public class BukkitBiomeWrapper implements Biome {

  private final org.bukkit.block.Biome biome;

  public BukkitBiomeWrapper(@NotNull org.bukkit.block.Biome biome) {
    this.biome = biome;
  }

  public @NotNull org.bukkit.block.Biome getBukkitBiome() {
    return biome;
  }

  @Override
  public @NotNull Key key() {
    return Key.key(biome.getKey().getNamespace(), biome.getKey().getKey());
  }
}
