package org.aincraft.bukkit.adapter;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class BukkitBiomeWrapper implements Key {

  private final org.bukkit.block.Biome biome;
  private final Key key;

  public BukkitBiomeWrapper(@NotNull org.bukkit.block.Biome biome) {
    this.biome = Objects.requireNonNull(biome, "biome cannot be null");
    this.key = Key.key(biome.getKey().getNamespace(), biome.getKey().getKey());
  }

  public @NotNull org.bukkit.block.Biome getBukkitBiome() {
    return biome;
  }

  @Override
  public @NotNull String asString() {
    return key.asString();
  }

  @Override
  public @NotNull String namespace() {
    return key.namespace();
  }

  @Override
  public @NotNull String value() {
    return key.value();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Key that)) return false;
    return Objects.equals(key, that);
  }

  @Override
  public int hashCode() {
    return key.hashCode();
  }

  @Override
  public String toString() {
    return key.asString();
  }
}
