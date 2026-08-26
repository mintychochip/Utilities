package org.aincraft.common.world;

import java.util.UUID;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.key.Key;
import org.aincraft.common.block.BlockState;
import org.aincraft.common.block.BlockType;
import org.aincraft.common.location.Location;
import org.aincraft.common.location.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorldChunkBlockTest {

  @Test
  void testBlockTypeCreationAndKey() {
    Key stoneKey = Key.key("minecraft", "stone");
    BlockType stone = BlockType.of(stoneKey);

    assertEquals(stoneKey, stone.key());
    assertEquals("minecraft:stone", stone.toString());
    assertThrows(NullPointerException.class, () -> BlockType.of(null));
  }

  @Test
  void testWorldAndBlockContracts() {
    UUID uid = UUID.randomUUID();
    Key worldKey = Key.key("minecraft", "overworld");
    BlockType stoneType = BlockType.of(Key.key("minecraft", "stone"));
    BlockState state = () -> stoneType;

    World world = new World() {
      @Override public UUID uid() { return uid; }
      @Override public String name() { return "overworld"; }
      @Override public Key key() { return worldKey; }
      @Override public int minHeight() { return -64; }
      @Override public int maxHeight() { return 320; }
      @Override public boolean isChunkLoaded(int chunkX, int chunkZ) { return chunkX == 0 && chunkZ == 0; }
      @Override public Chunk getChunkAt(int chunkX, int chunkZ) { return null; }
      @Override
      public Block getBlockAt(int x, int y, int z) {
        World currentWorld = this;
        return new Block() {
          @Override public World world() { return currentWorld; }
          @Override public Position position() { return Position.of(x, y, z); }
          @Override public BlockType type() { return stoneType; }
          @Override public BlockState state() { return state; }
        };
      }
    };
    assertEquals(uid, world.uid());
    assertEquals("overworld", world.name());
    assertEquals(worldKey, world.key());
    assertEquals(Identity.identity(uid), world.identity());
    assertEquals(-64, world.minHeight());
    assertEquals(320, world.maxHeight());
    assertTrue(world.isChunkLoaded(0, 0));
    assertFalse(world.isChunkLoaded(1, 0));

    Position pos = Position.of(12.3, 65.8, -4.2);
    Block block = world.getBlockAt(pos);
    assertEquals(12, block.x());
    assertEquals(65, block.y());
    assertEquals(-5, block.z());
    assertEquals(stoneType, block.type());
    assertEquals(state, block.state());
    assertSame(world, block.world());

    Location<World> loc = Location.of(world, pos, 0f, 0f);
    Block locBlock = world.getBlockAt(loc);
    assertEquals(12, locBlock.x());
    assertEquals(65, locBlock.y());
    assertEquals(-5, locBlock.z());
  }

  @Test
  void testChunkContract() {
    UUID uid = UUID.randomUUID();
    Key worldKey = Key.key("minecraft", "overworld");
    BlockType airType = BlockType.of(Key.key("minecraft", "air"));
    BlockState airState = () -> airType;

    World world = new World() {
      @Override public UUID uid() { return uid; }
      @Override public String name() { return "overworld"; }
      @Override public Key key() { return worldKey; }
      @Override public int minHeight() { return 0; }
      @Override public int maxHeight() { return 256; }
      @Override public boolean isChunkLoaded(int chunkX, int chunkZ) { return true; }
      @Override public Chunk getChunkAt(int chunkX, int chunkZ) { return null; }
      @Override public Block getBlockAt(int x, int y, int z) { return null; }
    };

    Chunk chunk = new Chunk() {
      @Override public int x() { return 3; }
      @Override public int z() { return -2; }
      @Override public World world() { return world; }
      @Override public boolean isLoaded() { return true; }
      @Override
      public Block getBlock(int x, int y, int z) {
        return new Block() {
          @Override public World world() { return world; }
          @Override public Position position() { return Position.of(x, y, z); }
          @Override public BlockType type() { return airType; }
          @Override public BlockState state() { return airState; }
        };
      }
    };

    assertEquals(3, chunk.x());
    assertEquals(-2, chunk.z());
    assertSame(world, chunk.world());
    assertTrue(chunk.isLoaded());

    Block block = chunk.getBlock(3 * 16 + 5, 70, -2 * 16 + 8);
    assertEquals(53, block.x());
    assertEquals(70, block.y());
    assertEquals(-24, block.z());
    assertEquals(airType, block.type());
  }
}
