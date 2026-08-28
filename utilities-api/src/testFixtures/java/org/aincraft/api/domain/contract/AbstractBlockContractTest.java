package org.aincraft.api.domain.contract;

import static org.junit.jupiter.api.Assertions.*;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.block.BlockType;
import org.aincraft.api.domain.inventory.ItemStack;
import org.aincraft.api.domain.world.Block;
import org.aincraft.api.domain.world.World;
import org.junit.jupiter.api.Test;

import java.util.Collection;

/**
 * Contract tests for {@link Block} and {@link BlockType}. Verifies coordinate preservation, type
 * identity, light level bounds, and that stone/dirt block fixtures drop at least one item.
 */
public abstract class AbstractBlockContractTest {

  protected abstract World createWorldFixture();

  protected abstract Block createBlockFixture(World world, int x, int y, int z);

  @Test
  void testBlockCoordinatesPreserved() {
    World w = createWorldFixture();
    Block block = createBlockFixture(w, 7, 64, -3);
    assertEquals(7, block.x());
    assertEquals(64, block.y());
    assertEquals(-3, block.z());
    assertSame(w, block.world());
  }

  @Test
  void testGetTypeReturnsKeyed() {
    World w = createWorldFixture();
    Block block = createBlockFixture(w, 0, 0, 0);
    BlockType type = block.type();
    assertNotNull(type, "Block.type() must never be null");
    assertNotNull(type.key(), "BlockType.key() must never be null");
  }

  @Test
  void testKeyValueNonEmpty() {
    World w = createWorldFixture();
    Block block = createBlockFixture(w, 0, 0, 0);
    Key key = block.type().key();
    assertNotNull(key.namespace(), "BlockType.key().namespace() must be present");
    assertFalse(key.value().isEmpty(), "BlockType.key().value() must be non-empty");
  }

  @Test
  void testLightLevelInRange() {
    World w = createWorldFixture();
    Block block = createBlockFixture(w, 0, 64, 0);
    int ll = block.lightLevel();
    int sky = block.lightFromSky();
    int blocks = block.lightFromBlocks();
    assertTrue(ll >= 0 && ll <= 15, "lightLevel must be in [0,15]");
    assertTrue(sky >= 0 && sky <= 15, "lightFromSky must be in [0,15]");
    assertTrue(blocks >= 0 && blocks <= 15, "lightFromBlocks must be in [0,15]");
  }

  @Test
  void testDropsNonEmptyForSolidBlock() {
    World w = createWorldFixture();
    Block block = createBlockFixture(w, 0, 0, 0);
    Collection<? extends ItemStack> drops = block.drops();
    assertNotNull(drops, "Block.drops() must never be null");
    // Stone and dirt both drop themselves when mined by hand; whichever
    // the test fixture places must yield at least one item.
    assertFalse(
        drops.isEmpty(),
        "A solid block (stone or dirt) must drop at least one item without a tool");
  }
}
