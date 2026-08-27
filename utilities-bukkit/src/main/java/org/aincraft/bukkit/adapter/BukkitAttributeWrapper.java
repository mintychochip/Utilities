package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BukkitAttributeWrapper implements Key {

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
