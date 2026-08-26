package org.aincraft.common.location;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoundingBoxTest {

  private static BoundingBox createBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
    return new BoundingBox() {
      @Override public double minX() { return minX; }
      @Override public double minY() { return minY; }
      @Override public double minZ() { return minZ; }
      @Override public double maxX() { return maxX; }
      @Override public double maxY() { return maxY; }
      @Override public double maxZ() { return maxZ; }
    };
  }

  private static Position createPos(double x, double y, double z) {
    return new Position() {
      @Override public double x() { return x; }
      @Override public double y() { return y; }
      @Override public double z() { return z; }
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
}
