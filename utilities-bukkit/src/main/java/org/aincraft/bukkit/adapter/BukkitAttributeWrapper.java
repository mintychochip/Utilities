package org.aincraft.bukkit.adapter;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.aincraft.common.attribute.Attribute;
import org.jetbrains.annotations.NotNull;

public class BukkitAttributeWrapper implements Attribute {

  private final org.bukkit.attribute.Attribute attribute;
  private final Key key;

  public BukkitAttributeWrapper(@NotNull org.bukkit.attribute.Attribute attribute) {
    this.attribute = Objects.requireNonNull(attribute, "attribute cannot be null");
    this.key = Key.key(attribute.getKey().getNamespace(), attribute.getKey().getKey());
  }

  public @NotNull org.bukkit.attribute.Attribute getBukkitAttribute() {
    return attribute;
  }

  @Override
  public @NotNull Key key() {
    return key;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Attribute that)) return false;
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
