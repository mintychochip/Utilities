package org.aincraft.minestom.adapter;

import static org.junit.jupiter.api.Assertions.*;

import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.world.DimensionType;
import org.aincraft.api.domain.block.BlockFace;
import org.aincraft.api.domain.block.BlockState;
import org.aincraft.api.domain.block.BlockType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class MinestomAdaptersBlockTest {

  @BeforeAll
  static void setup() {
    MinecraftServer.init();
  }

  @Test
  void testBlockFaceMapping() {
    assertEquals(
        net.minestom.server.instance.block.BlockFace.BOTTOM,
        MinestomAdapters.toMinestom(BlockFace.DOWN));
    assertEquals(
        net.minestom.server.instance.block.BlockFace.TOP,
        MinestomAdapters.toMinestom(BlockFace.UP));
    assertEquals(
        net.minestom.server.instance.block.BlockFace.NORTH,
        MinestomAdapters.toMinestom(BlockFace.NORTH));
    assertEquals(
        net.minestom.server.instance.block.BlockFace.SOUTH,
        MinestomAdapters.toMinestom(BlockFace.SOUTH));
    assertEquals(
        net.minestom.server.instance.block.BlockFace.WEST,
        MinestomAdapters.toMinestom(BlockFace.WEST));
    assertEquals(
        net.minestom.server.instance.block.BlockFace.EAST,
        MinestomAdapters.toMinestom(BlockFace.EAST));

    for (net.minestom.server.instance.block.BlockFace minestomFace :
        net.minestom.server.instance.block.BlockFace.values()) {
      BlockFace face = MinestomAdapters.adapt(minestomFace);
      assertEquals(minestomFace, MinestomAdapters.toMinestom(face));
    }

    assertThrows(
        IllegalArgumentException.class, () -> MinestomAdapters.toMinestom(BlockFace.NORTH_EAST));
    assertThrows(IllegalArgumentException.class, () -> MinestomAdapters.toMinestom(BlockFace.SELF));
  }

  @Test
  void testBlockTypeAndState() {
    Block stone = Block.STONE;
    BlockType type = MinestomAdapters.adapt(stone);
    assertEquals("minecraft:stone", type.key().asString());

    BlockState state = MinestomAdapters.adaptState(stone);
    assertEquals(type, state.type());
    assertNotNull(state.asString());

    Block backType = MinestomAdapters.toMinestom(type);
    assertEquals(stone, backType);

    Block backState = MinestomAdapters.toMinestom(state);
    assertEquals(stone, backState);
  }

  @Test
  void testBlockWrapper() {
    Instance instance = new InstanceContainer(UUID.randomUUID(), DimensionType.OVERWORLD);
    instance.setBlock(1, 2, 3, Block.OAK_LOG);

    org.aincraft.api.domain.world.Block block = MinestomAdapters.adapt(instance, 1, 2, 3);
    assertEquals(1, block.x());
    assertEquals(2, block.y());
    assertEquals(3, block.z());
    assertEquals("minecraft:oak_log", block.type().key().asString());
    assertEquals("minecraft:oak_log", block.key().asString());
    assertTrue(block.isSolid());
    assertFalse(block.isAir());
    assertFalse(block.isEmpty());
    assertFalse(block.isLiquid());
    assertFalse(block.isPassable());

    org.aincraft.api.domain.world.Block relative = block.relative(BlockFace.UP);
    assertEquals(1, relative.x());
    assertEquals(3, relative.y());
    assertEquals(3, relative.z());

    org.aincraft.api.domain.world.Block relativeDist = block.relative(BlockFace.NORTH, 2);
    assertEquals(1, relativeDist.x());
    assertEquals(2, relativeDist.y());
    assertEquals(1, relativeDist.z());
  }
}
