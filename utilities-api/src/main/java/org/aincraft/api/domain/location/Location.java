package org.aincraft.api.domain.location;

import org.aincraft.api.domain.entity.Entity;
import org.aincraft.api.domain.entity.LivingEntity;
import org.aincraft.api.domain.entity.Player;
import org.aincraft.api.domain.world.Block;
import org.aincraft.api.domain.world.Chunk;
import org.aincraft.api.domain.world.HeightMap;
import org.aincraft.api.domain.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3dc;

import java.util.Collection;

public interface Location {

  @NotNull
  World world();

  @NotNull
  Position position();

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

  default double distanceSquared(@NotNull Location other) {
    return position().distanceSquared(other.position());
  }

  default double distance(@NotNull Location other) {
    return position().distance(other.position());
  }

  default @NotNull Block block() {
    return world().getBlockAt(blockX(), blockY(), blockZ());
  }

  default @NotNull Chunk chunk() {
    return world().getChunkAt(blockX() >> 4, blockZ() >> 4);
  }

  // -- Phase 2: transform helpers, block snap, nearby queries, key, chunk check --

  /** Unit-length direction vector from yaw/pitch. */
  @NotNull
  Vector3dc direction();

  /** New location offset by the given delta; yaw/pitch preserved. */
  @NotNull
  Location withOffset(double dx, double dy, double dz);

  /** New location offset by the given vector; yaw/pitch preserved. */
  @NotNull
  Location withOffset(@NotNull Vector3dc offset);

  /** New location with the same position but a different rotation. */
  @NotNull
  Location withRotation(float yaw, float pitch);

  /** New location aligned to block coordinates (floor of x,y,z). */
  @NotNull
  Location toBlockLocation();

  /** New location at the center of the block column. */
  @NotNull
  Location toCenterLocation();

  @NotNull
  Collection<? extends Entity> nearbyEntities(double radius);

  @NotNull
  Collection<? extends Entity> nearbyEntities(double xRadius, double yRadius, double zRadius);

  @NotNull
  Collection<? extends Player> nearbyPlayers(double radius);

  @NotNull
  Collection<? extends Player> nearbyPlayers(double xRadius, double yRadius, double zRadius);

  @NotNull
  Collection<? extends LivingEntity> nearbyLivingEntities(
      double xRadius, double yRadius, double zRadius);

  boolean isChunkLoaded();

  long toBlockKey();

  default @NotNull Location toHighestLocation() {
    throw new UnsupportedOperationException();
  }

  default @NotNull Location toHighestLocation(@NotNull HeightMap heightMap) {
    throw new UnsupportedOperationException();
  }

  static @NotNull Location of(
      @NotNull World world, @NotNull Position position, float yaw, float pitch) {
    return new Location() {
      @Override
      public @NotNull World world() {
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
      public @NotNull Vector3dc direction() {
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);
        double x = -Math.sin(yawRad) * Math.cos(pitchRad);
        double y = -Math.sin(pitchRad);
        double z = Math.cos(yawRad) * Math.cos(pitchRad);
        return new org.joml.Vector3d(x, y, z);
      }

      @Override
      public @NotNull Location withOffset(double dx, double dy, double dz) {
        Position np =
            new Position() {
              @Override
              public double x() {
                return position.x() + dx;
              }

              @Override
              public double y() {
                return position.y() + dy;
              }

              @Override
              public double z() {
                return position.z() + dz;
              }
            };
        return of(world, np, yaw, pitch);
      }

      @Override
      public @NotNull Location withOffset(@NotNull Vector3dc offset) {
        return withOffset(offset.x(), offset.y(), offset.z());
      }

      @Override
      public @NotNull Location withRotation(float ny, float np) {
        return of(world, position, ny, np);
      }

      @Override
      public @NotNull Location toBlockLocation() {
        Position np =
            new Position() {
              @Override
              public double x() {
                return Math.floor(position.x());
              }

              @Override
              public double y() {
                return Math.floor(position.y());
              }

              @Override
              public double z() {
                return Math.floor(position.z());
              }
            };
        return of(world, np, yaw, pitch);
      }

      @Override
      public @NotNull Location toCenterLocation() {
        Position np =
            new Position() {
              @Override
              public double x() {
                return Math.floor(position.x()) + 0.5;
              }

              @Override
              public double y() {
                return Math.floor(position.y());
              }

              @Override
              public double z() {
                return Math.floor(position.z()) + 0.5;
              }
            };
        return of(world, np, yaw, pitch);
      }

      @Override
      public @NotNull Collection<? extends Entity> nearbyEntities(double radius) {
        return nearbyEntities(radius, radius, radius);
      }

      @Override
      public @NotNull Collection<? extends Entity> nearbyEntities(
          double xRadius, double yRadius, double zRadius) {
        return world.nearbyEntities(this, xRadius, yRadius, zRadius);
      }

      @Override
      public @NotNull Collection<? extends Player> nearbyPlayers(double radius) {
        return nearbyPlayers(radius, radius, radius);
      }

      @Override
      public @NotNull Collection<? extends Player> nearbyPlayers(
          double xRadius, double yRadius, double zRadius) {
        return world.nearbyEntities(this, xRadius, yRadius, zRadius).stream()
            .filter(Player.class::isInstance)
            .map(Player.class::cast)
            .toList();
      }

      @Override
      public @NotNull Collection<? extends LivingEntity> nearbyLivingEntities(
          double xRadius, double yRadius, double zRadius) {
        return world.nearbyEntities(this, xRadius, yRadius, zRadius).stream()
            .filter(LivingEntity.class::isInstance)
            .map(LivingEntity.class::cast)
            .toList();
      }

      @Override
      public boolean isChunkLoaded() {
        return world().isChunkLoaded(blockX() >> 4, blockZ() >> 4);
      }

      @Override
      public long toBlockKey() {
        long key = ((long) blockX() & 0x3FFFFFFL) << 38;
        key |= ((long) blockZ() & 0x3FFFFFFL) << 12;
        key |= ((long) blockY() & 0xFFFL);
        return key;
      }

      @Override
      public String toString() {
        return "Location{world="
            + world.name()
            + ", x="
            + position.x()
            + ", y="
            + position.y()
            + ", z="
            + position.z()
            + "}";
      }
    };
  }

  static @NotNull Location of(@NotNull World world, @NotNull Position position) {
    return of(world, position, 0.0f, 0.0f);
  }

  static @NotNull Location of(@NotNull World world, double x, double y, double z) {
    return of(
        world,
        new Position() {
          @Override
          public double x() {
            return x;
          }

          @Override
          public double y() {
            return y;
          }

          @Override
          public double z() {
            return z;
          }
        },
        0.0f,
        0.0f);
  }
}
