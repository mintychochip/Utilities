package org.aincraft.minestom.adapter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.world.DimensionType;
import org.aincraft.common.location.BoundingBox;
import org.aincraft.common.location.Location;
import org.aincraft.common.location.Position;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MinestomAdaptersCoordinateTest {

  @BeforeAll
  static void setup() {
    MinecraftServer.init();
  }

  @Test
  void testPositionConversion() {
    Vec vec = new Vec(1.5, 2.5, 3.5);
    Position pos = MinestomAdapters.adapt(vec);

    assertEquals(1.5, pos.x());
    assertEquals(2.5, pos.y());
    assertEquals(3.5, pos.z());
    assertEquals(1, pos.blockX());
    assertEquals(2, pos.blockY());
    assertEquals(3, pos.blockZ());

    Vec back = MinestomAdapters.toMinestomVec(pos);
    assertEquals(vec, back);
  }

  @Test
  void testLocationConversion() {
    Instance instance = new InstanceContainer(UUID.randomUUID(), DimensionType.OVERWORLD);
    Pos pos = new Pos(10.0, 64.0, -20.0, 90.0f, 45.0f);

    Location loc = MinestomAdapters.adapt(instance, pos);
    assertEquals(10.0, loc.x());
    assertEquals(64.0, loc.y());
    assertEquals(-20.0, loc.z());
    assertEquals(90.0f, loc.yaw());
    assertEquals(45.0f, loc.pitch());
    assertNotNull(loc.world());

    Pos back = MinestomAdapters.toMinestomPos(loc);
    assertEquals(10.0, back.x());
    assertEquals(64.0, back.y());
    assertEquals(-20.0, back.z());
    assertEquals(90.0f, back.yaw());
    assertEquals(45.0f, back.pitch());
  }

  @Test
  void testBoundingBoxConversion() {
    net.minestom.server.collision.BoundingBox minestomBox =
        new net.minestom.server.collision.BoundingBox(0.6, 1.8, 0.6);
    BoundingBox box = MinestomAdapters.adapt(minestomBox);

    assertEquals(minestomBox.minX(), box.minX(), 1e-6);
    assertEquals(minestomBox.minY(), box.minY(), 1e-6);
    assertEquals(minestomBox.minZ(), box.minZ(), 1e-6);
    assertEquals(minestomBox.maxX(), box.maxX(), 1e-6);
    assertEquals(minestomBox.maxY(), box.maxY(), 1e-6);
    assertEquals(minestomBox.maxZ(), box.maxZ(), 1e-6);

    net.minestom.server.collision.BoundingBox back = MinestomAdapters.toMinestom(box);
    assertEquals(minestomBox.minX(), back.minX(), 1e-6);
    assertEquals(minestomBox.maxX(), back.maxX(), 1e-6);
  }
}
