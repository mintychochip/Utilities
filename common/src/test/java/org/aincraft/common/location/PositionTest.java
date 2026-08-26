package org.aincraft.common.location;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PositionTest {
  @Test
  void testCoordinatesAndBlockConversion() {
    Position pos = Position.of(10.7, -5.2, 3.0);
    assertEquals(10.7, pos.x(), 1e-6);
    assertEquals(-5.2, pos.y(), 1e-6);
    assertEquals(3.0, pos.z(), 1e-6);

    assertEquals(10, pos.blockX());
    assertEquals(-6, pos.blockY()); // Math.floor(-5.2) == -6
    assertEquals(3, pos.blockZ());
  }

  @Test
  void testDistanceCalculations() {
    Position p1 = Position.of(0, 0, 0);
    Position p2 = Position.of(3, 4, 0);
    assertEquals(25.0, p1.distanceSquared(p2), 1e-6);
    assertEquals(5.0, p1.distance(p2), 1e-6);
  }

  @Test
  void testTransformations() {
    Position p = Position.of(1, 2, 3);
    Position pAdd = p.add(1, -1, 2);
    assertEquals(2, pAdd.x(), 1e-6);
    assertEquals(1, pAdd.y(), 1e-6);
    assertEquals(5, pAdd.z(), 1e-6);

    Position pSub = p.subtract(1, 1, 1);
    assertEquals(0, pSub.x(), 1e-6);
    assertEquals(1, pSub.y(), 1e-6);
    assertEquals(2, pSub.z(), 1e-6);

    Position pMul = p.multiply(2.5);
    assertEquals(2.5, pMul.x(), 1e-6);
    assertEquals(5.0, pMul.y(), 1e-6);
    assertEquals(7.5, pMul.z(), 1e-6);
  }

  @Test
  void testVectors() {
    Vector3d v3d = new Vector3d(1.0, 2.0, 2.0);
    assertEquals(9.0, v3d.lengthSquared(), 1e-6);
    assertEquals(3.0, v3d.length(), 1e-6);

    Vector3i v3i = new Vector3i(1, 2, 3);
    assertEquals(new Vector3i(2, 4, 6), v3i.add(new Vector3i(1, 2, 3)));
  }
}
