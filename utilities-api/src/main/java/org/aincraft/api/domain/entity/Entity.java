package org.aincraft.api.domain.entity;

import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.aincraft.api.domain.location.BoundingBox;
import org.aincraft.api.domain.location.Location;
import org.aincraft.api.domain.location.Position;
import org.aincraft.api.domain.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3dc;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface Entity extends Keyed, Identified, Nameable {

  @NotNull
  UUID uniqueId();

  @NotNull
  World world();

  @NotNull
  Location location();

  @NotNull
  Position position();

  default double x() {
    return position().x();
  }

  default double y() {
    return position().y();
  }

  default double z() {
    return position().z();
  }

  float yaw();

  float pitch();

  void setRotation(float yaw, float pitch);

  @NotNull
  Key type();

  double height();

  double width();

  int entityId();

  boolean isValid();

  boolean isDead();

  @NotNull
  BoundingBox boundingBox();

  @NotNull
  Vector3dc velocity();

  void setVelocity(@NotNull Vector3dc velocity);

  boolean isOnGround();

  void teleport(@NotNull Location targetLocation);

  @NotNull
  Collection<? extends Entity> nearbyEntities(double x, double y, double z);

  @NotNull
  List<? extends Entity> passengers();

  boolean addPassenger(@NotNull Entity passenger);

  boolean removePassenger(@NotNull Entity passenger);

  boolean eject();

  boolean isInsideVehicle();

  boolean leaveVehicle();

  @Nullable
  Entity vehicle();

  boolean isGlowing();

  void setGlowing(boolean glowing);

  boolean isInvulnerable();

  void setInvulnerable(boolean invulnerable);

  boolean isCustomNameVisible();

  void setCustomNameVisible(boolean visible);

  void remove();

  @Override
  default @NotNull Identity identity() {
    return Identity.identity(uniqueId());
  }

  @Override
  default @NotNull Key key() {
    return type();
  }
}
