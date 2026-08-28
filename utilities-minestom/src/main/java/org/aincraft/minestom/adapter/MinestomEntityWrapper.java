package org.aincraft.minestom.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.entity.Entity;
import org.aincraft.api.domain.location.BoundingBox;
import org.aincraft.api.domain.location.Location;
import org.aincraft.api.domain.location.Position;
import org.aincraft.api.domain.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.Objects;
import java.util.UUID;

public class MinestomEntityWrapper implements Entity {

  private final net.minestom.server.entity.Entity entity;
  private final Key typeKey;

  public MinestomEntityWrapper(@NotNull net.minestom.server.entity.Entity entity) {
    this.entity = Objects.requireNonNull(entity, "entity cannot be null");
    this.typeKey = entity.getEntityType().key();
  }

  public @NotNull net.minestom.server.entity.Entity getMinestomEntity() {
    return entity;
  }

  @Override
  public @NotNull UUID uniqueId() {
    return entity.getUuid();
  }

  @Override
  public @NotNull World world() {
    if (entity.getInstance() == null) {
      throw new IllegalStateException("Entity is not attached to an Instance");
    }
    return MinestomAdapters.adapt(entity.getInstance());
  }

  @Override
  public @NotNull Location location() {
    return MinestomAdapters.adapt(entity.getInstance(), entity.getPosition());
  }

  @Override
  public @NotNull Position position() {
    return MinestomAdapters.adapt(entity.getPosition());
  }

  @Override
  public @NotNull Key type() {
    return typeKey;
  }

  @Override
  public double height() {
    return entity.getBoundingBox().height();
  }

  @Override
  public double width() {
    return entity.getBoundingBox().width();
  }

  @Override
  public int entityId() {
    return entity.getEntityId();
  }

  @Override
  public boolean isValid() {
    return !entity.isRemoved();
  }

  @Override
  public boolean isDead() {
    return entity.isRemoved();
  }

  @Override
  public @NotNull BoundingBox boundingBox() {
    return MinestomAdapters.adapt(entity.getBoundingBox());
  }

  @Override
  public @NotNull Vector3dc velocity() {
    net.minestom.server.coordinate.Vec vel = entity.getVelocity();
    return new Vector3d(vel.x(), vel.y(), vel.z());
  }

  @Override
  public boolean isOnGround() {
    return entity.isOnGround();
  }

  @Override
  public void teleport(@NotNull Location targetLocation) {
    Objects.requireNonNull(targetLocation, "targetLocation cannot be null");
    net.minestom.server.coordinate.Pos targetPos = MinestomAdapters.toMinestomPos(targetLocation);
    if (targetLocation.world() instanceof MinestomWorldWrapper wrapper
        && wrapper.getMinestomInstance() != entity.getInstance()) {
      entity.setInstance(wrapper.getMinestomInstance(), targetPos).join();
    } else {
      entity.teleport(targetPos).join();
    }
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
    return "MinestomEntityWrapper{type=" + type() + ", uuid=" + uniqueId() + "}";
  }

  @Override
  public float yaw() {
    return entity.getPosition().yaw();
  }

  @Override
  public float pitch() {
    return entity.getPosition().pitch();
  }

  @Override
  public void setRotation(float yaw, float pitch) {
    entity.setView(yaw, pitch);
  }

  @Override
  public void setVelocity(@NotNull Vector3dc velocity) {
    Objects.requireNonNull(velocity, "velocity cannot be null");
    entity.setVelocity(
        new net.minestom.server.coordinate.Vec(velocity.x(), velocity.y(), velocity.z()));
  }

  @Override
  public @NotNull java.util.Collection<? extends org.aincraft.api.domain.entity.Entity>
      nearbyEntities(double x, double y, double z) {
    if (entity.getInstance() == null) return java.util.List.of();
    double radius = Math.max(Math.abs(x), Math.max(Math.abs(y), Math.abs(z)));
    return entity.getInstance().getNearbyEntities(entity.getPosition(), radius).stream()
        .filter(
            other ->
                Math.abs(other.getPosition().x() - entity.getPosition().x()) <= Math.abs(x)
                    && Math.abs(other.getPosition().y() - entity.getPosition().y()) <= Math.abs(y)
                    && Math.abs(other.getPosition().z() - entity.getPosition().z()) <= Math.abs(z))
        .map(MinestomAdapters::adapt)
        .toList();
  }

  @Override
  public @NotNull java.util.List<? extends org.aincraft.api.domain.entity.Entity> passengers() {
    return entity.getPassengers().stream().map(MinestomAdapters::adapt).toList();
  }

  @Override
  public boolean addPassenger(@NotNull org.aincraft.api.domain.entity.Entity passenger) {
    net.minestom.server.entity.Entity minestomPassenger = MinestomAdapters.toMinestom(passenger);
    entity.addPassenger(minestomPassenger);
    return entity.getPassengers().contains(minestomPassenger);
  }

  @Override
  public boolean removePassenger(@NotNull org.aincraft.api.domain.entity.Entity passenger) {
    net.minestom.server.entity.Entity minestomPassenger = MinestomAdapters.toMinestom(passenger);
    boolean wasPassenger = entity.getPassengers().contains(minestomPassenger);
    entity.removePassenger(minestomPassenger);
    return wasPassenger && !entity.getPassengers().contains(minestomPassenger);
  }

  @Override
  public boolean eject() {
    java.util.List<net.minestom.server.entity.Entity> current =
        java.util.List.copyOf(entity.getPassengers());
    current.forEach(entity::removePassenger);
    return !current.isEmpty();
  }

  @Override
  public boolean isInsideVehicle() {
    return entity.getVehicle() != null;
  }

  @Override
  public boolean leaveVehicle() {
    net.minestom.server.entity.Entity vehicle = entity.getVehicle();
    if (vehicle == null) return false;
    vehicle.removePassenger(entity);
    return entity.getVehicle() == null;
  }

  @Override
  public @org.jetbrains.annotations.Nullable org.aincraft.api.domain.entity.Entity vehicle() {
    net.minestom.server.entity.Entity vehicle = entity.getVehicle();
    return vehicle == null ? null : MinestomAdapters.adapt(vehicle);
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
    return entity instanceof net.minestom.server.entity.LivingEntity living
        ? living.isInvulnerable()
        : false;
  }

  @Override
  public void setInvulnerable(boolean invulnerable) {
    if (entity instanceof net.minestom.server.entity.LivingEntity living) {
      living.setInvulnerable(invulnerable);
    }
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
  public @org.jetbrains.annotations.Nullable net.kyori.adventure.text.Component customName() {
    return entity.getCustomName();
  }

  @Override
  public void customName(
      @org.jetbrains.annotations.Nullable net.kyori.adventure.text.Component name) {
    entity.setCustomName(name);
  }
}
