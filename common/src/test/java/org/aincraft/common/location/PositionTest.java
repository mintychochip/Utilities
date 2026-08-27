package org.aincraft.common.location;

import static org.junit.jupiter.api.Assertions.*;

import org.joml.Vector3d;
import org.joml.Vector3i;
import org.junit.jupiter.api.Test;

class PositionTest {

  private static Position createPosition(double x, double y, double z) {
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
  void testCoordinatesAndBlockConversion() {
    Position pos = createPosition(10.7, -5.2, 3.0);
    assertEquals(10.7, pos.x(), 1e-6);
    assertEquals(-5.2, pos.y(), 1e-6);
    assertEquals(3.0, pos.z(), 1e-6);

    assertEquals(10, pos.blockX());
    assertEquals(-6, pos.blockY()); // Math.floor(-5.2) == -6
    assertEquals(3, pos.blockZ());
  }

  @Test
  void testDistanceCalculations() {
    Position p1 = createPosition(0, 0, 0);
    Position p2 = createPosition(3, 4, 0);
    assertEquals(25.0, p1.distanceSquared(p2), 1e-6);
    assertEquals(5.0, p1.distance(p2), 1e-6);
    assertEquals(25.0, p1.distanceSquared(3, 4, 0), 1e-6);
    assertEquals(5.0, p1.distance(3, 4, 0), 1e-6);
  }

  @Test
  void testVectorCalculations() {
    Vector3d v3d1 = new Vector3d(1.0, 2.0, 2.0);
    assertEquals(9.0, v3d1.lengthSquared(), 1e-6);
    assertEquals(3.0, v3d1.length(), 1e-6);

    Vector3d v3d2 = new Vector3d(4.0, 6.0, 2.0);
    assertEquals(25.0, v3d1.distanceSquared(v3d2), 1e-6);
    assertEquals(5.0, v3d1.distance(v3d2), 1e-6);
    assertEquals(20.0, v3d1.dot(v3d2), 1e-6); // 1*4 + 2*6 + 2*2 = 4 + 12 + 4 = 20

    Vector3i v3i1 = new Vector3i(1, 2, 2);
    assertEquals(9L, v3i1.lengthSquared());
    assertEquals(3.0, v3i1.length(), 1e-6);

    Vector3i v3i2 = new Vector3i(4, 6, 2);
    assertEquals(25L, v3i1.distanceSquared(v3i2));
    assertEquals(5.0, v3i1.distance(v3i2), 1e-6);
  }
}
