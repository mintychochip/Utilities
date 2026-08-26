package org.aincraft.common.location;

import org.aincraft.common.world.World;
import org.jetbrains.annotations.NotNull;

public interface Location<W extends World> {

  @NotNull W world();

  @NotNull Position position();

  float yaw();

  float pitch();

  default double x() {
    return position().x();
  }

  default double y() {
    return position().y();
  }

  default double z() {
    return position().z();
  }

  default int blockX() {
    return position().blockX();
  }

  default int blockY() {
    return position().blockY();
  }

  default int blockZ() {
    return position().blockZ();
  }

  @NotNull Location<W> withPosition(@NotNull Position position);

  default @NotNull Location<W> withPosition(double x, double y, double z) {
    return withPosition(Position.of(x, y, z));
  }

  @NotNull Location<W> withOrientation(float yaw, float pitch);

  <T extends World> @NotNull Location<T> withWorld(@NotNull T world);

  static <W extends World> @NotNull Location<W> of(
      @NotNull W world,
      @NotNull Position position,
      float yaw,
      float pitch
  ) {
    return new LocationImpl<>(world, position, yaw, pitch);
  }

  static <W extends World> @NotNull Location<W> of(
      @NotNull W world,
      @NotNull Position position
  ) {
    return of(world, position, 0.0f, 0.0f);
  }

  static <W extends World> @NotNull Location<W> of(
      @NotNull W world,
      double x,
      double y,
      double z,
      float yaw,
      float pitch
  ) {
    return of(world, Position.of(x, y, z), yaw, pitch);
  }

  static <W extends World> @NotNull Location<W> of(
      @NotNull W world,
      double x,
      double y,
      double z
  ) {
    return of(world, Position.of(x, y, z), 0.0f, 0.0f);
  }
}
