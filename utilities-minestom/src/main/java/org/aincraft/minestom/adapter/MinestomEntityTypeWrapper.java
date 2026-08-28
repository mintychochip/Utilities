package org.aincraft.minestom.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/** Registry-backed entity type adapter, including 26.2 mob types. */
public final class MinestomEntityTypeWrapper implements EntityType {

  private final net.minestom.server.entity.EntityType entityType;

  public MinestomEntityTypeWrapper(@NotNull net.minestom.server.entity.EntityType entityType) {
    this.entityType = Objects.requireNonNull(entityType, "entityType cannot be null");
  }

  public @NotNull net.minestom.server.entity.EntityType getMinestomEntityType() {
    return entityType;
  }

  @Override
  public @NotNull Key key() {
    return entityType.key();
  }

  @Override
  public boolean isAlive() {
    return !entityType.defaultAttributes().isEmpty();
  }

  @Override
  public boolean isSpawnable() {
    return !"player".equals(key().value());
  }

  @Override
  public boolean equals(Object other) {
    return this == other || (other instanceof EntityType type && key().equals(type.key()));
  }

  @Override
  public int hashCode() {
    return key().hashCode();
  }

  @Override
  public String toString() {
    return "MinestomEntityTypeWrapper{" + key() + "}";
  }
}
