package org.aincraft.api.domain.location;

import static org.junit.jupiter.api.Assertions.*;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

class BoundingBoxTest {

  private static BoundingBox createBox(
      double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
    return new BoundingBox() {
      @Override
      public double minX() {
        return minX;
      }

      @Override
      public double minY() {
        return minY;
      }

      @Override
      public double minZ() {
        return minZ;
      }

      @Override
      public double maxX() {
        return maxX;
      }

      @Override
      public double maxY() {
        return maxY;
      }

      @Override
      public double maxZ() {
        return maxZ;
      }
    };
  }

  private static Position createPos(double x, double y, double z) {
    return new Position() {
      @Override
      public double x() {
        return x;
      }

      @Override
      public double y() {
        return y;
      }

      @Override
      public double z() {
        return z;
      }
    };
  }

  @Test
  void testDimensionsAndVolume() {
    BoundingBox box = createBox(1, 2, 3, 5, 8, 9);
    assertEquals(4.0, box.widthX(), 1e-6);
    assertEquals(6.0, box.heightY(), 1e-6);
    assertEquals(6.0, box.depthZ(), 1e-6);
    assertEquals(144.0, box.volume(), 1e-6);

    assertEquals(3.0, box.centerX(), 1e-6);
    assertEquals(5.0, box.centerY(), 1e-6);
    assertEquals(6.0, box.centerZ(), 1e-6);
  }

  @Test
  void testContainsPoint() {
    BoundingBox box = createBox(0, 0, 0, 10, 10, 10);
    assertTrue(box.contains(5, 5, 5));
    assertTrue(box.contains(0, 0, 0));
    assertTrue(box.contains(10, 10, 10));
    assertFalse(box.contains(11, 5, 5));
    assertFalse(box.contains(-1, 5, 5));

    assertTrue(box.contains(createPos(5, 5, 5)));
    assertFalse(box.contains(createPos(12, 5, 5)));
  }

  @Test
  void testIntersects() {
    BoundingBox box1 = createBox(0, 0, 0, 5, 5, 5);
    BoundingBox box2 = createBox(4, 4, 4, 8, 8, 8);
    BoundingBox box3 = createBox(6, 6, 6, 10, 10, 10);

    assertTrue(box1.intersects(box2));
    assertFalse(box1.intersects(box3));
  }

  @Test
  void testExpandSymmetricDelegation() {
    BoundingBox base =
        new BoundingBox() {
          @Override
          public double minX() {
            return 0;
          }

          @Override
          public double minY() {
            return 0;
          }

          @Override
          public double minZ() {
            return 0;
          }

          @Override
          public double maxX() {
            return 1;
          }

          @Override
          public double maxY() {
            return 1;
          }

          @Override
          public double maxZ() {
            return 1;
          }

          @Override
          public @NotNull BoundingBox expand(
              double negativeX,
              double negativeY,
              double negativeZ,
              double positiveX,
              double positiveY,
              double positiveZ) {
            return createBox(
                minX() - negativeX,
                minY() - negativeY,
                minZ() - negativeZ,
                maxX() + positiveX,
                maxY() + positiveY,
                maxZ() + positiveZ);
          }
        };
    BoundingBox expanded = base.expand(1, 0, 0);
    assertEquals(-1.0, expanded.minX(), 1e-9);
    assertEquals(2.0, expanded.maxX(), 1e-9);
    assertEquals(0.0, expanded.minY(), 1e-9);
    assertEquals(1.0, expanded.maxY(), 1e-9);

    BoundingBox expanded2 = base.expand(1, 2, 3);
    assertEquals(-1.0, expanded2.minX(), 1e-9);
    assertEquals(2.0, expanded2.maxX(), 1e-9);
    assertEquals(-2.0, expanded2.minY(), 1e-9);
    assertEquals(3.0, expanded2.maxY(), 1e-9);
    assertEquals(-3.0, expanded2.minZ(), 1e-9);
    assertEquals(4.0, expanded2.maxZ(), 1e-9);
  }

  @Test
  void testGeometryDefaults() {
    BoundingBox box = createBox(0, 0, 0, 1, 1, 1);
    BoundingBox north = box.expand(org.aincraft.api.domain.block.BlockFace.NORTH, 2);
    assertEquals(-2.0, north.minZ(), 1e-9);
    assertEquals(1.0, north.maxZ(), 1e-9);
    assertEquals(1.5, box.shift(1, 2, 3).centerX(), 1e-9);
    BoundingBox union = box.union(createBox(2, 2, 2, 3, 3, 3));
    assertEquals(0.0, union.minX(), 1e-9);
    assertEquals(3.0, union.maxZ(), 1e-9);
    BoundingBox intersection = box.intersection(createBox(0.5, 0.5, 0.5, 2, 2, 2));
    assertNotNull(intersection);
    assertEquals(0.5, intersection.minX(), 1e-9);
    assertEquals(1.0, intersection.maxY(), 1e-9);
    assertNotNull(
        box.rayTrace(new org.joml.Vector3d(-1, 0.5, 0.5), new org.joml.Vector3d(1, 0, 0), 3));
    assertNull(box.rayTrace(new org.joml.Vector3d(-1, 2, 0.5), new org.joml.Vector3d(1, 0, 0), 3));
  }
}
