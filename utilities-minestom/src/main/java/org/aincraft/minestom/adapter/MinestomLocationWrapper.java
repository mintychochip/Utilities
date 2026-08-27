package org.aincraft.minestom.adapter;

import java.util.Objects;
import net.minestom.server.coordinate.Pos;
import org.aincraft.common.location.Location;
import org.aincraft.common.location.Position;
import org.aincraft.common.world.HeightMap;
import org.aincraft.common.world.World;
import org.jetbrains.annotations.NotNull;
public class MinestomLocationWrapper implements Location {

  private final Pos pos;
  private final World world;
  private final Position position;

  public MinestomLocationWrapper(@NotNull World world, @NotNull Pos pos) {
    this.world = Objects.requireNonNull(world, "world cannot be null");
    this.pos = Objects.requireNonNull(pos, "pos cannot be null");
    this.position = new MinestomPositionWrapper(pos);
  }

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
    return pos.yaw();
  }

  @Override
  public float pitch() {
    return pos.pitch();
  }

  @Override
  public double x() {
    return pos.x();
  }

  @Override
  public double y() {
    return pos.y();
  }

  @Override
  public double z() {
    return pos.z();
  }

  @Override
  public int blockX() {
    return pos.blockX();
  }

  @Override
  public int blockY() {
    return pos.blockY();
  }

  @Override
  public int blockZ() {
    return pos.blockZ();
  }
  @Override
  public @NotNull Location toHighestLocation() {
    return toHighestLocation(HeightMap.WORLD_SURFACE);
  }

  @Override
  public @NotNull Location toHighestLocation(@NotNull HeightMap heightMap) {
    org.aincraft.common.world.Block highest = world.getHighestBlockAt(blockX(), blockZ(), heightMap);
    // Return location at highest block with original yaw/pitch
    return new MinestomLocationWrapper(world, new Pos(highest.x(), highest.y(), highest.z(), yaw(), pitch()));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Location that)) return false;
    return Float.compare(that.yaw(), yaw()) == 0
        && Float.compare(that.pitch(), pitch()) == 0
        && Double.compare(that.x(), x()) == 0
        && Double.compare(that.y(), y()) == 0
        && Double.compare(that.z(), z()) == 0
        && Objects.equals(that.world(), world());
  }

  @Override
  public int hashCode() {
    return Objects.hash(world, x(), y(), z(), yaw(), pitch());
  }

  @Override
  public String toString() {
    return "MinestomLocationWrapper{world=" + world + ", x=" + x() + ", y=" + y() + ", z=" + z() + ", yaw=" + yaw() + ", pitch=" + pitch() + "}";
  }
}
