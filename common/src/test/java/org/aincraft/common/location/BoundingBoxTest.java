package org.aincraft.common.location;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoundingBoxTest {
  @Test
  void testContainsPoint() {
    BoundingBox box = new BoundingBox(0, 0, 0, 10, 10, 10);
    assertTrue(box.contains(5, 5, 5));
    assertTrue(box.contains(0, 0, 0));
    assertTrue(box.contains(10, 10, 10));
    assertFalse(box.contains(11, 5, 5));
    assertFalse(box.contains(-1, 5, 5));

    assertTrue(box.contains(Position.of(5, 5, 5)));
  }

  @Test
  void testIntersects() {
    BoundingBox box1 = new BoundingBox(0, 0, 0, 5, 5, 5);
    BoundingBox box2 = new BoundingBox(4, 4, 4, 8, 8, 8);
    BoundingBox box3 = new BoundingBox(6, 6, 6, 10, 10, 10);

    assertTrue(box1.intersects(box2));
    assertFalse(box1.intersects(box3));
  }

  @Test
  void testOfPositions() {
    Position p1 = Position.of(10, 20, 30);
    Position p2 = Position.of(0, 5, 40);
    BoundingBox box = BoundingBox.of(p1, p2);

    assertEquals(0, box.minX(), 1e-6);
    assertEquals(5, box.minY(), 1e-6);
    assertEquals(30, box.minZ(), 1e-6);
    assertEquals(10, box.maxX(), 1e-6);
    assertEquals(20, box.maxY(), 1e-6);
    assertEquals(40, box.maxZ(), 1e-6);
  }

  @Test
  void testInvalidBoundsThrows() {
    assertThrows(IllegalArgumentException.class, () -> new BoundingBox(10, 0, 0, 5, 0, 0));
  }
}
