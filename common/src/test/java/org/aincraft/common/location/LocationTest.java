package org.aincraft.common.location;

import java.util.UUID;
import net.kyori.adventure.key.Key;
import org.aincraft.common.world.Block;
import org.aincraft.common.world.Chunk;
import org.aincraft.common.world.World;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocationTest {

  private static World createTestWorld(String name) {
    UUID uid = UUID.nameUUIDFromBytes(name.getBytes());
    Key key = Key.key("test", name);
    return new World() {
      @Override public UUID uid() { return uid; }
      @Override public String name() { return name; }
      @Override public Key key() { return key; }
      @Override public Block getBlockAt(int x, int y, int z) { throw new UnsupportedOperationException(); }
      @Override public Chunk getChunkAt(int chunkX, int chunkZ) { throw new UnsupportedOperationException(); }
      @Override public boolean isChunkLoaded(int chunkX, int chunkZ) { return false; }
      @Override public int minHeight() { return 0; }
      @Override public int maxHeight() { return 256; }
    };
  }

  @Test
  void testLocationCoordinatesAndOrientation() {
    World world = createTestWorld("overworld");
    Location<World> loc = Location.of(world, 10.5, 64.0, -12.5, 90.0f, 45.0f);

    assertSame(world, loc.world());
    assertEquals(10.5, loc.x(), 1e-6);
    assertEquals(64.0, loc.y(), 1e-6);
    assertEquals(-12.5, loc.z(), 1e-6);
    assertEquals(10, loc.blockX());
    assertEquals(64, loc.blockY());
    assertEquals(-13, loc.blockZ());
    assertEquals(90.0f, loc.yaw(), 1e-6f);
    assertEquals(45.0f, loc.pitch(), 1e-6f);
  }

  @Test
  void testLocationWithModifications() {
    World world1 = createTestWorld("world1");
    World world2 = createTestWorld("world2");

    Location<World> loc = Location.of(world1, 0, 0, 0);
    Location<World> loc2 = loc.withPosition(Position.of(5, 10, 15));
    assertEquals(5, loc2.x(), 1e-6);

    Location<World> loc3 = loc.withOrientation(180f, -90f);
    assertEquals(180f, loc3.yaw(), 1e-6f);
    assertEquals(-90f, loc3.pitch(), 1e-6f);

    Location<World> loc4 = loc.withWorld(world2);
    assertSame(world2, loc4.world());
  }
}
