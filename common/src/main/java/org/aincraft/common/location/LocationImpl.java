package org.aincraft.common.location;

import java.util.Objects;
import org.aincraft.common.world.World;
import org.jetbrains.annotations.NotNull;

final class LocationImpl<W extends World> implements Location<W> {

  private final W world;
  private final Position position;
  private final float yaw;
  private final float pitch;

  LocationImpl(@NotNull W world, @NotNull Position position, float yaw, float pitch) {
    this.world = Objects.requireNonNull(world, "world cannot be null");
    this.position = Objects.requireNonNull(position, "position cannot be null");
    this.yaw = yaw;
    this.pitch = pitch;
  }

  @Override
  public @NotNull W world() {
    return world;
  }

  @Override
  public @NotNull Position position() {
    return position;
  }

  @Override
  public float yaw() {
    return yaw;
  }

  @Override
  public float pitch() {
    return pitch;
  }

  @Override
  public @NotNull Location<W> withPosition(@NotNull Position newPosition) {
    return new LocationImpl<>(world, newPosition, yaw, pitch);
  }

  @Override
  public @NotNull Location<W> withOrientation(float newYaw, float newPitch) {
    return new LocationImpl<>(world, position, newYaw, newPitch);
  }

  @Override
  public <T extends World> @NotNull Location<T> withWorld(@NotNull T newWorld) {
    return new LocationImpl<>(newWorld, position, yaw, pitch);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Location<?> location)) return false;
    return Float.compare(location.yaw(), yaw) == 0
        && Float.compare(location.pitch(), pitch) == 0
        && Objects.equals(world, location.world())
        && Objects.equals(position, location.position());
  }

  @Override
  public int hashCode() {
    return Objects.hash(world, position, yaw, pitch);
  }

  @Override
  public String toString() {
    return "Location{world=" + world + ", position=" + position + ", yaw=" + yaw + ", pitch=" + pitch + "}";
  }
}
