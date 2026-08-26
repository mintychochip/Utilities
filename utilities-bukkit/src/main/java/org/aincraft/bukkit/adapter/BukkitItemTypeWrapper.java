package org.aincraft.bukkit.adapter;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.aincraft.common.inventory.ItemType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class BukkitItemTypeWrapper implements ItemType {

  private final Material material;
  private final Key key;

  public BukkitItemTypeWrapper(@NotNull Material material) {
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
  public int maxStackSize() {
    return material.getMaxStackSize();
  }

  @Override
  public int maxDurability() {
    return material.getMaxDurability();
  }

  @Override
  public boolean isBlock() {
    return material.isBlock();
  }

  @Override
  public boolean isAir() {
    return material.isAir();
  }

  @Override
  public boolean isItem() {
    return material.isItem();
  }

  @Override
  public boolean isEdible() {
    return material.isEdible();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ItemType that)) return false;
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
