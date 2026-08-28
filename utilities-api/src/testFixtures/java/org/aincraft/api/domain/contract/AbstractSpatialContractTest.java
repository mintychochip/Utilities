package org.aincraft.api.domain.contract;

import static org.junit.jupiter.api.Assertions.*;

import org.aincraft.api.domain.block.BlockFace;
import org.aincraft.api.domain.location.BoundingBox;
import org.aincraft.api.domain.location.Position;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Shared contract test suite verifying that spatial coordinate wrappers, bounding box calculations,
 * and block face offsets conform to expected {@code :utilities-api} behavior.
 */
public abstract class AbstractSpatialContractTest {

  protected abstract Position createPositionFixture(double x, double y, double z);

  protected abstract BoundingBox createBoundingBoxFixture(
      double minX, double minY, double minZ, double maxX, double maxY, double maxZ);

  protected abstract BlockFace adaptBlockFace(BlockFace face);

  @Test
  void testPositionCoordinateMathAndDiscretization() {
    Position pos = createPositionFixture(10.75, 64.25, -20.90);

    assertEquals(10.75, pos.x(), 1e-6);
    assertEquals(64.25, pos.y(), 1e-6);
    assertEquals(-20.90, pos.z(), 1e-6);

    assertEquals(10, pos.blockX(), "Positive X floor conversion");
    assertEquals(64, pos.blockY(), "Positive Y floor conversion");
    assertEquals(-21, pos.blockZ(), "Negative Z floor conversion (-20.90 -> -21)");

    assertEquals(0.0, pos.distance(10.75, 64.25, -20.90), 1e-6);
    assertEquals(5.0, pos.distance(10.75 + 3.0, 64.25 + 4.0, -20.90), 1e-6);
  }

  @Test
  void testBoundingBoxContainmentAndInvariants() {
    BoundingBox box = createBoundingBoxFixture(-10.0, 0.0, -10.0, 10.0, 20.0, 10.0);

    assertEquals(-10.0, box.minX(), 1e-6);
    assertEquals(10.0, box.maxX(), 1e-6);
    assertEquals(0.0, box.minY(), 1e-6);
    assertEquals(20.0, box.maxY(), 1e-6);

    assertTrue(box.contains(0.0, 10.0, 0.0), "Center point inside box");
    assertTrue(box.contains(-5.0, 5.0, -5.0), "Interior point inside box");
    assertTrue(box.contains(5.0, 15.0, 5.0), "Interior point inside box");

    assertFalse(box.contains(15.0, 10.0, 0.0), "X overflow outside box");
    assertFalse(box.contains(0.0, -1.0, 0.0), "Y underflow outside box");
    assertFalse(box.contains(0.0, 10.0, 15.0), "Z overflow outside box");
  }

  @Test
  void testBlockFaceOffsetsAndInvariants() {
    List<BlockFace> primary3dFaces =
        List.of(
            BlockFace.NORTH,
            BlockFace.SOUTH,
            BlockFace.EAST,
            BlockFace.WEST,
            BlockFace.UP,
            BlockFace.DOWN);

    for (BlockFace face : primary3dFaces) {
      BlockFace adapted = adaptBlockFace(face);
      assertEquals(face, adapted, "BlockFace adaptation must preserve enum identity");
      assertEquals(face.modX(), adapted.modX(), "modX must match");
      assertEquals(face.modY(), adapted.modY(), "modY must match");
      assertEquals(face.modZ(), adapted.modZ(), "modZ must match");
      assertEquals(face.opposite(), adapted.opposite(), "opposite face must match");
    }
  }
}
