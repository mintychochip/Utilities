package org.aincraft.common.entity;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.aincraft.common.location.BoundingBox;
import org.aincraft.common.location.Location;
import org.aincraft.common.location.Position;
import org.aincraft.common.location.Vector3d;
import org.aincraft.common.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Entity extends Keyed, Identified, Nameable {

  @NotNull UUID uniqueId();

  @NotNull World world();
  @NotNull Location location();
  @NotNull Position position();

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

  @NotNull Key type();

  boolean isValid();

  boolean isDead();

  @NotNull BoundingBox boundingBox();

  @NotNull Vector3d velocity();

  void setVelocity(@NotNull Vector3d velocity);

  boolean isOnGround();

  void teleport(@NotNull Location targetLocation);

  @NotNull Collection<? extends Entity> nearbyEntities(double x, double y, double z);

  @NotNull List<? extends Entity> passengers();

  boolean addPassenger(@NotNull Entity passenger);

  boolean removePassenger(@NotNull Entity passenger);

  boolean eject();

  boolean isInsideVehicle();

  boolean leaveVehicle();

  @Nullable Entity vehicle();

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
