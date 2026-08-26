package org.aincraft.common.entity;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.aincraft.common.location.Location;
import org.aincraft.common.world.Block;
import org.aincraft.common.world.Chunk;
import org.aincraft.common.world.World;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntityPlayerTest {

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
  void testEntityAndPlayerContract() {
    UUID uuid = UUID.randomUUID();
    World world = createTestWorld("overworld");
    Location<World> location = Location.of(world, 10, 64, 10);
    Key playerType = Key.key("minecraft", "player");
    AtomicBoolean messageSent = new AtomicBoolean(false);

    Player player = new Player() {
      @Override public UUID uniqueId() { return uuid; }
      @Override public String username() { return "Steve"; }
      @Override public boolean isOnline() { return true; }
      @Override public World world() { return world; }
      @Override public Location<World> location() { return location; }
      @Override public Key type() { return playerType; }
      @Override public boolean isValid() { return true; }
      @Override
      public void sendMessage(Component message) {
        messageSent.set(true);
      }
    };

    assertEquals(uuid, player.uniqueId());
    assertEquals(uuid, player.identity().uuid());
    assertEquals("Steve", player.username());
    assertTrue(player.isOnline());
    assertSame(world, player.world());
    assertEquals(playerType, player.type());
    assertEquals(playerType, player.key());
    assertTrue(player.isValid());
    assertSame(location, player.location());

    player.sendMessage(Component.text("Hello World"));
    assertTrue(messageSent.get());
  }

  @Test
  void testGenericEntityContract() {
    UUID uuid = UUID.randomUUID();
    World world = createTestWorld("nether");
    Location<World> location = Location.of(world, 100, 32, -50);
    Key zombieType = Key.key("minecraft", "zombie");

    Entity entity = new Entity() {
      @Override public UUID uniqueId() { return uuid; }
      @Override public World world() { return world; }
      @Override public Location<World> location() { return location; }
      @Override public Key type() { return zombieType; }
      @Override public boolean isValid() { return true; }
    };

    assertEquals(uuid, entity.uniqueId());
    assertEquals(uuid, entity.identity().uuid());
    assertEquals(zombieType, entity.type());
    assertEquals(zombieType, entity.key());
    assertSame(world, entity.world());
    assertSame(location, entity.location());
    assertTrue(entity.isValid());
  }
}
