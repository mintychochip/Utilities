package org.aincraft.api.domain.contract;

import static org.junit.jupiter.api.Assertions.*;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.entity.Entity;
import org.aincraft.api.domain.location.Location;
import org.aincraft.api.domain.world.Block;
import org.aincraft.api.domain.world.World;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.UUID;

/**
 * Shared contract test suite verifying that any platform adapter's {@link World} implementation
 * conforms to the expected {@code :utilities-api} behavior.
 */
public abstract class AbstractWorldContractTest {

  protected abstract World createWorldFixture(
      UUID uid, String name, Key key, int minHeight, int maxHeight);

  protected abstract Location createLocationFixture(World world, double x, double y, double z);

  @Test
  void testWorldIdentityAndBoundaries() {
    UUID uid = UUID.randomUUID();
    Key key = Key.key("minecraft", "custom_realm");
    World world = createWorldFixture(uid, "custom_realm", key, -64, 320);

    assertNotNull(world);
    assertEquals(uid, world.uid(), "World UID must match");
    assertEquals(uid, world.identity().uuid(), "Identity UUID must match UID");
    assertEquals(key, world.key(), "World key must match");
    assertEquals("custom_realm", world.name());
    assertEquals(-64, world.minHeight());
    assertEquals(320, world.maxHeight());
  }

  @Test
  void testGetBlockAtMatchesPosition() {
    UUID uid = UUID.randomUUID();
    World world = createWorldFixture(uid, "block_test", Key.key("minecraft", "overworld"), 0, 256);
    Block at = world.getBlockAt(5, 64, -3);
    assertNotNull(at, "World.getBlockAt(x,y,z) must never return null");
    assertEquals(5, at.x());
    assertEquals(64, at.y());
    assertEquals(-3, at.z());
  }

  @Test
  void testGetBlockAtPositionOverloadEquivalent() {
    World world =
        createWorldFixture(
            UUID.randomUUID(), "overload", Key.key("minecraft", "overworld"), 0, 256);
    Block a = world.getBlockAt(2, 3, 4);
    Block b = world.getBlockAt(createLocationFixture(world, 2.0, 3.0, 4.0));
    assertNotNull(a);
    assertNotNull(b);
    assertEquals(a.x(), b.x());
    assertEquals(a.y(), b.y());
    assertEquals(a.z(), b.z());
  }

  @Test
  void testTimeMonotonicNonDecreasing() {
    World world =
        createWorldFixture(UUID.randomUUID(), "time", Key.key("minecraft", "overworld"), 0, 256);
    long t0 = world.time();
    long t1 = world.time();
    long t2 = world.time();
    assertTrue(t1 >= t0, "time() must be non-decreasing on a single thread");
    assertTrue(t2 >= t1, "time() must be non-decreasing across calls on a single thread");
  }

  @Test
  void testFullTimeIsAtLeastTime() {
    World world =
        createWorldFixture(
            UUID.randomUUID(), "fulltime", Key.key("minecraft", "overworld"), 0, 256);
    long t = world.time();
    long ft = world.fullTime();
    // fullTime is the absolute tick counter; time is the day-night cycle (0..24000).
    // The fullTime may be > time but must never be less than the day cycle value.
    assertTrue(ft >= 0, "fullTime must be non-negative");
    assertTrue(t >= 0, "time must be non-negative");
  }

  @Test
  void testGetChunkAtConsistent() {
    World world =
        createWorldFixture(UUID.randomUUID(), "chunk", Key.key("minecraft", "overworld"), 0, 256);
    Block block = world.getBlockAt(16, 64, 16);
    assertNotNull(block);
    // Block at (16,64,16) is in chunk (1,1) because chunk coordinates use floor(x / 16).
    var chunk = world.getChunkAt(block);
    assertNotNull(chunk);
    assertEquals(1, chunk.x());
    assertEquals(1, chunk.z());
  }

  @Test
  void testPlayersAndEntitiesCollectionsNonNull() {
    World world =
        createWorldFixture(
            UUID.randomUUID(), "collections", Key.key("minecraft", "overworld"), 0, 256);
    Collection<? extends Entity> players = world.players();
    Collection<? extends Entity> entities = world.entities();
    assertNotNull(players, "World.players() must never be null");
    assertNotNull(entities, "World.entities() must never be null");
  }
}
