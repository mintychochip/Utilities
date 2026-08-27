package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.common.block.BlockType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BukkitBlockTypeWrapper implements BlockType {

  private final Material material;
  private final Key key;

  public BukkitBlockTypeWrapper(@NotNull Material material) {
    this.material = Objects.requireNonNull(material, "material cannot be null");
    this.key = Key.key(material.getKey().getNamespace(), material.getKey().getKey());
  }

  public @NotNull Material getBukkitMaterial() {
    return material;
  }

  @Override
  public @NotNull Key key() {
    return key;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BlockType that)) return false;
    return Objects.equals(key, that.key());
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
