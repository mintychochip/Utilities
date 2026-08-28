package org.aincraft.minestom.adapter;

import net.kyori.adventure.key.Key;
import net.minestom.server.component.DataComponents;
import net.minestom.server.item.Material;
import org.aincraft.api.domain.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class MinestomItemTypeWrapper implements ItemType {

  private final Material material;

  public MinestomItemTypeWrapper(@NotNull Material material) {
    this.material = Objects.requireNonNull(material, "material cannot be null");
  }

  public @NotNull Material getMinestomMaterial() {
    return material;
  }

  @Override
  public @NotNull Key key() {
    return material.key();
  }

  @Override
  public int maxStackSize() {
    return material.maxStackSize();
  }

  @Override
  public int maxDurability() {
    return material.prototype().get(DataComponents.MAX_DAMAGE, 0);
  }

  @Override
  public boolean isBlock() {
    return material.block() != null;
  }

  @Override
  public boolean isAir() {
    return "minecraft:air".equals(key().asString());
  }

  @Override
  public boolean isItem() {
    return true;
  }

  @Override
  public boolean isEdible() {
    return material.prototype().has(DataComponents.FOOD);
  }

  @Override
  public boolean equals(Object other) {
    return this == other || (other instanceof ItemType itemType && key().equals(itemType.key()));
  }

  @Override
  public int hashCode() {
    return key().hashCode();
  }

  @Override
  public String toString() {
    return "MinestomItemTypeWrapper{key=" + key() + "}";
  }
}
