package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.common.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class BukkitEntityTypeWrapper implements EntityType {

  private final org.bukkit.entity.EntityType entityType;
  private final Key key;

  public BukkitEntityTypeWrapper(@NotNull org.bukkit.entity.EntityType entityType) {
    this.entityType = entityType;
    this.key = Key.key(entityType.getKey().getNamespace(), entityType.getKey().getKey());
  }

  public @NotNull org.bukkit.entity.EntityType getBukkitEntityType() {
    return entityType;
  }

  @Override
  public @NotNull Key key() {
    return key;
  }

  @Override
  public boolean isAlive() {
    return entityType.isAlive();
  }

  @Override
  public boolean isSpawnable() {
    return entityType.isSpawnable();
  }
}
