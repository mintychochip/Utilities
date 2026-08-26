package org.aincraft.minestom.adapter;

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

public class MinestomEntityWrapper implements Entity {

  private final net.minestom.server.entity.Entity entity;
  private final Key typeKey;

  public MinestomEntityWrapper(@NotNull net.minestom.server.entity.Entity entity) {
    this.entity = Objects.requireNonNull(entity, "entity cannot be null");
    this.typeKey = Key.key(entity.getEntityType().name());
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
  public @NotNull Vector3d velocity() {
    net.minestom.server.coordinate.Vec vel = entity.getVelocity();
    return new Vector3d() {
      @Override public double x() { return vel.x(); }
      @Override public double y() { return vel.y(); }
      @Override public double z() { return vel.z(); }
    };
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
      entity.setInstance(wrapper.getMinestomInstance(), targetPos);
    } else {
      entity.teleport(targetPos);
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
}
