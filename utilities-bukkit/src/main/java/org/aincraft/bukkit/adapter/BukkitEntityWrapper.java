package org.aincraft.bukkit.adapter;

import java.util.Objects;
import java.util.UUID;
import net.kyori.adventure.key.Key;
import org.aincraft.common.entity.Entity;
import org.aincraft.common.location.BoundingBox;
import org.aincraft.common.location.Location;
import org.aincraft.common.location.Position;
import org.aincraft.common.location.Vector3d;
import org.aincraft.common.world.World;
import org.jetbrains.annotations.NotNull;

public class BukkitEntityWrapper implements Entity {

  private final org.bukkit.entity.Entity entity;
  private final Key typeKey;

  public BukkitEntityWrapper(@NotNull org.bukkit.entity.Entity entity) {
    this.entity = Objects.requireNonNull(entity, "entity cannot be null");
    this.typeKey = Key.key(entity.getType().getKey().getNamespace(), entity.getType().getKey().getKey());
  }

  public @NotNull org.bukkit.entity.Entity getBukkitEntity() {
    return entity;
  }

  @Override
  public @NotNull UUID uniqueId() {
    return entity.getUniqueId();
  }

  @Override
  public @NotNull World world() {
    return BukkitAdapters.adapt(entity.getWorld());
  }

  @Override
  public @NotNull Location location() {
    return BukkitAdapters.adapt(entity.getLocation());
  }

  @Override
  public @NotNull Position position() {
    return new BukkitPositionWrapper(entity.getLocation().toVector());
  }

  @Override
  public @NotNull Key type() {
    return typeKey;
  }

  @Override
  public boolean isValid() {
    return entity.isValid();
  }

  @Override
  public boolean isDead() {
    return entity.isDead();
  }

  @Override
  public @NotNull BoundingBox boundingBox() {
    return BukkitAdapters.adapt(entity.getBoundingBox());
  }

  @Override
  public @NotNull Vector3d velocity() {
    org.bukkit.util.Vector vec = entity.getVelocity();
    return new Vector3d() {
      @Override public double x() { return vec.getX(); }
      @Override public double y() { return vec.getY(); }
      @Override public double z() { return vec.getZ(); }
    };
  }

  @Override
  public boolean isOnGround() {
    return entity.isOnGround();
  }

  @Override
  public void teleport(@NotNull Location targetLocation) {
    entity.teleport(BukkitAdapters.toBukkit(targetLocation));
  }

  @Override
  public void remove() {
    entity.remove();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Entity that)) return false;
    return Objects.equals(uniqueId(), that.uniqueId());
  }

  @Override
  public int hashCode() {
    return uniqueId().hashCode();
  }

  @Override
  public String toString() {
    return "BukkitEntityWrapper{type=" + type() + ", uuid=" + uniqueId() + "}";
  }
}
