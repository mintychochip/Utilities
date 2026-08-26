package org.aincraft.bukkit.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.aincraft.common.entity.Entity;
import org.aincraft.common.location.BoundingBox;
import org.aincraft.common.location.Location;
import org.aincraft.common.location.Position;
import org.aincraft.common.location.Vector3d;
import org.aincraft.common.world.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
  public float yaw() {
    return entity.getLocation().getYaw();
  }

  @Override
  public float pitch() {
    return entity.getLocation().getPitch();
  }

  @Override
  public void setRotation(float yaw, float pitch) {
    entity.setRotation(yaw, pitch);
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
  public void setVelocity(@NotNull Vector3d velocity) {
    entity.setVelocity(new Vector(velocity.x(), velocity.y(), velocity.z()));
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
  public @NotNull Collection<? extends Entity> nearbyEntities(double x, double y, double z) {
    List<Entity> result = new ArrayList<>();
    for (org.bukkit.entity.Entity e : entity.getNearbyEntities(x, y, z)) {
      result.add(BukkitAdapters.adapt(e));
    }
    return result;
  }

  @Override
  public @NotNull List<? extends Entity> passengers() {
    List<Entity> result = new ArrayList<>();
    for (org.bukkit.entity.Entity e : entity.getPassengers()) {
      result.add(BukkitAdapters.adapt(e));
    }
    return result;
  }

  @Override
  public boolean addPassenger(@NotNull Entity passenger) {
    return entity.addPassenger(BukkitAdapters.toBukkit(passenger));
  }

  @Override
  public boolean removePassenger(@NotNull Entity passenger) {
    return entity.removePassenger(BukkitAdapters.toBukkit(passenger));
  }

  @Override
  public boolean eject() {
    return entity.eject();
  }

  @Override
  public boolean isInsideVehicle() {
    return entity.isInsideVehicle();
  }

  @Override
  public boolean leaveVehicle() {
    return entity.leaveVehicle();
  }

  @Override
  public @Nullable Entity vehicle() {
    org.bukkit.entity.Entity vehicle = entity.getVehicle();
    return vehicle != null ? BukkitAdapters.adapt(vehicle) : null;
  }

  @Override
  public boolean isGlowing() {
    return entity.isGlowing();
  }

  @Override
  public void setGlowing(boolean glowing) {
    entity.setGlowing(glowing);
  }

  @Override
  public boolean isInvulnerable() {
    return entity.isInvulnerable();
  }

  @Override
  public void setInvulnerable(boolean invulnerable) {
    entity.setInvulnerable(invulnerable);
  }

  @Override
  public boolean isCustomNameVisible() {
    return entity.isCustomNameVisible();
  }

  @Override
  public void setCustomNameVisible(boolean visible) {
    entity.setCustomNameVisible(visible);
  }

  @Override
  public @Nullable Component customName() {
    String name = entity.getCustomName();
    return name != null ? LegacyComponentSerializer.legacySection().deserialize(name) : null;
  }

  @Override
  public void customName(@Nullable Component name) {
    entity.setCustomName(name != null ? LegacyComponentSerializer.legacySection().serialize(name) : null);
  }

  @Override
  public void remove() {
    entity.remove();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BukkitEntityWrapper that)) return false;
    return uniqueId().equals(that.uniqueId());
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
