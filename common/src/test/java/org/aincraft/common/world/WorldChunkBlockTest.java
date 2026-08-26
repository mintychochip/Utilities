package org.aincraft.common.world;

import java.util.Collection;
import java.util.UUID;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.key.Key;
import org.aincraft.common.block.BlockFace;
import org.aincraft.common.block.BlockState;
import org.aincraft.common.block.BlockType;
import org.aincraft.common.entity.Entity;
import org.aincraft.common.entity.Player;
import org.aincraft.common.location.BoundingBox;
import org.aincraft.common.location.Location;
import org.aincraft.common.location.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorldChunkBlockTest {

  private static Position createPos(double x, double y, double z) {
    return new Position() {
      @Override public double x() { return x; }
      @Override public double y() { return y; }
      @Override public double z() { return z; }
    };
  }
  private static Location createLoc(World world, Position position) {
    return new Location() {
      @Override public World world() { return world; }
      @Override public Position position() { return position; }
      @Override public float yaw() { return 0f; }
      @Override public float pitch() { return 0f; }
    };
  }

  @Test
  void testBlockFace() {
    assertEquals(0, BlockFace.NORTH.modX());
    assertEquals(0, BlockFace.NORTH.modY());
    assertEquals(-1, BlockFace.NORTH.modZ());
    assertEquals(BlockFace.SOUTH, BlockFace.NORTH.opposite());
    assertEquals(BlockFace.NORTH, BlockFace.SOUTH.opposite());
    assertEquals(BlockFace.WEST, BlockFace.EAST.opposite());
    assertEquals(BlockFace.EAST, BlockFace.WEST.opposite());
    assertEquals(BlockFace.DOWN, BlockFace.UP.opposite());
    assertEquals(BlockFace.UP, BlockFace.DOWN.opposite());
    assertTrue(BlockFace.NORTH.isCartesian());
    assertFalse(BlockFace.NORTH_EAST.isCartesian());
  }

  @Test
  void testWorldChunkAndBlockContracts() {
    UUID uid = UUID.randomUUID();
    Key worldKey = Key.key("minecraft", "overworld");
    Key stoneKey = Key.key("minecraft", "stone");
    BlockType stoneType = () -> stoneKey;
    BlockState stoneState = new BlockState() {
      @Override public BlockType type() { return stoneType; }
      @Override public String asString() { return "minecraft:stone"; }
    };

    World world = new World() {
      @Override public UUID uid() { return uid; }
      @Override public String name() { return "overworld"; }
      @Override public Key key() { return worldKey; }
      @Override public int minHeight() { return -64; }
      @Override public int maxHeight() { return 320; }
      @Override public WorldBorder worldBorder() { return null; }
      @Override public Environment environment() { return Environment.NORMAL; }
      @Override public Difficulty difficulty() { return Difficulty.NORMAL; }
      @Override public long time() { return 6000L; }
      @Override public long fullTime() { return 18000L; }
      @Override public Collection<? extends Player> players() { return java.util.List.of(); }
      @Override public Collection<? extends Entity> entities() { return java.util.List.of(); }
      @Override public Collection<? extends Chunk> loadedChunks() { return java.util.List.of(); }
      @Override public boolean isChunkLoaded(int chunkX, int chunkZ) { return chunkX == 0 && chunkZ == 0; }
      @Override
      public Chunk getChunkAt(int chunkX, int chunkZ) {
        World currentWorld = this;
        return new Chunk() {
          @Override public int x() { return chunkX; }
          @Override public int z() { return chunkZ; }
          @Override public World world() { return currentWorld; }
          @Override public boolean isLoaded() { return true; }
          @Override public boolean load() { return true; }
          @Override public boolean load(boolean generate) { return true; }
          @Override public boolean unload() { return true; }
          @Override public boolean unload(boolean save) { return true; }
          @Override public Collection<? extends Entity> entities() { return java.util.List.of(); }
          @Override public Block getBlock(int x, int y, int z) { return getBlockAt(x, y, z); }
        };
      }

      @Override
      public Block getBlockAt(int x, int y, int z) {
        World currentWorld = this;
        return new Block() {
          @Override public int x() { return x; }
          @Override public int y() { return y; }
          @Override public int z() { return z; }
          @Override public World world() { return currentWorld; }
          @Override public Chunk chunk() { return getChunkAt(x >> 4, z >> 4); }
          @Override public Location location() { return createLoc(currentWorld, createPos(x, y, z)); }
          @Override public Position position() { return createPos(x, y, z); }
          @Override public BlockType type() { return stoneType; }
          @Override public BlockState state() { return stoneState; }
          @Override public boolean isEmpty() { return false; }
          @Override public boolean isLiquid() { return false; }
          @Override public boolean isSolid() { return true; }
          @Override public boolean isAir() { return false; }
          @Override public boolean isPassable() { return false; }
          @Override public BoundingBox boundingBox() { return new BoundingBox() {
            @Override public double minX() { return x; }
            @Override public double minY() { return y; }
            @Override public double minZ() { return z; }
            @Override public double maxX() { return x + 1; }
            @Override public double maxY() { return y + 1; }
            @Override public double maxZ() { return z + 1; }
          }; }
        };
      }
    };

    assertEquals(uid, world.uid());
    assertEquals("overworld", world.name());
    assertEquals(worldKey, world.key());
    assertEquals(Identity.identity(uid), world.identity());
    assertEquals(6000L, world.time());
    assertEquals(18000L, world.fullTime());

    Position pos = createPos(10, 64, 10);
    Block block = world.getBlockAt(pos);
    assertEquals(10, block.x());
    assertEquals(64, block.y());
    assertEquals(10, block.z());
    assertEquals(stoneKey, block.key());
    assertEquals(stoneType, block.type());
    assertEquals("minecraft:stone", block.state().asString());
    assertFalse(block.isEmpty());
    assertTrue(block.isSolid());

    // Relative block test
    Block northBlock = block.relative(BlockFace.NORTH);
    assertEquals(10, northBlock.x());
    assertEquals(64, northBlock.y());
    assertEquals(9, northBlock.z());

    Block upBlock = block.relative(BlockFace.UP, 3);
    assertEquals(10, upBlock.x());
    assertEquals(67, upBlock.y());
    assertEquals(10, upBlock.z());

    // Chunk test
    Chunk chunk = block.chunk();
    assertEquals(10 >> 4, chunk.x());
    assertEquals(10 >> 4, chunk.z());
    assertEquals((((long) (10 >> 4)) << 32) | ((10 >> 4) & 0xFFFFFFFFL), chunk.chunkKey());
    assertTrue(chunk.isLoaded());
  }
}
