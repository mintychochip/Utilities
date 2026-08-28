package org.aincraft.api.domain.contract;

import static org.junit.jupiter.api.Assertions.*;

import org.aincraft.api.domain.location.BoundingBox;
import org.aincraft.api.domain.location.Rotation;
import org.aincraft.api.domain.world.RayTraceResult;
import org.aincraft.api.domain.world.World;
import org.joml.Vector3dc;
import org.junit.jupiter.api.Test;

/**
 * Contract tests for {@link Rotation}, {@link BoundingBox}, and the {@link
 * org.aincraft.api.domain.location.Location} transform API. Verifies cross-platform invariants:
 * rotation algebra, box identity, shift, expand, and the rayTrace origin-containment property.
 */
public abstract class AbstractLocationContractTest {

  protected abstract Rotation createRotationFixture(float yaw, float pitch);

  protected abstract BoundingBox createBoundingBoxFixture(
      double minX, double minY, double minZ, double maxX, double maxY, double maxZ);

  protected abstract World createWorldFixture();

  // -- Rotation contract --

  @Test
  void testRotationRoundTrip() {
    Rotation r = createRotationFixture(45.0f, 30.0f);
    assertEquals(45.0f, r.yaw(), 0.0001f);
    assertEquals(30.0f, r.pitch(), 0.0001f);
  }

  @Test
  void testRotationAddIsInverseOfSubtract() {
    Rotation a = createRotationFixture(10.0f, 20.0f);
    Rotation b = createRotationFixture(3.0f, 5.0f);
    Rotation sum = a.add(b.yaw(), b.pitch());
    assertEquals(13.0f, sum.yaw(), 0.0001f);
    assertEquals(25.0f, sum.pitch(), 0.0001f);
    Rotation back = sum.subtract(b.yaw(), b.pitch());
    assertEquals(a.yaw(), back.yaw(), 0.0001f);
    assertEquals(a.pitch(), back.pitch(), 0.0001f);
  }

  @Test
  void testRotationAddZeroIsIdentity() {
    Rotation a = createRotationFixture(123.4f, -56.7f);
    Rotation r = a.add(0.0f, 0.0f);
    assertEquals(a.yaw(), r.yaw(), 0.0001f);
    assertEquals(a.pitch(), r.pitch(), 0.0001f);
  }

  // -- BoundingBox contract --

  @Test
  void testBoundingBoxIdentity() {
    BoundingBox box = createBoundingBoxFixture(0, 0, 0, 10, 10, 10);
    assertEquals(10.0, box.widthX(), 0.0);
    assertEquals(10.0, box.heightY(), 0.0);
    assertEquals(10.0, box.depthZ(), 0.0);
    assertEquals(1000.0, box.volume(), 0.0);
  }

  @Test
  void testBoundingBoxContainsInterior() {
    BoundingBox box = createBoundingBoxFixture(0, 0, 0, 10, 10, 10);
    assertTrue(box.contains(5.0, 5.0, 5.0));
    assertTrue(box.contains(0.0, 0.0, 0.0), "Boundary must be inclusive on the min edge");
    assertTrue(box.contains(10.0, 10.0, 10.0), "Boundary must be inclusive on the max edge");
    assertFalse(box.contains(11.0, 5.0, 5.0));
    assertFalse(box.contains(-0.1, 5.0, 5.0));
  }

  @Test
  void testBoundingBoxIntersects() {
    BoundingBox a = createBoundingBoxFixture(0, 0, 0, 10, 10, 10);
    BoundingBox b = createBoundingBoxFixture(5, 5, 5, 15, 15, 15);
    BoundingBox c = createBoundingBoxFixture(11, 11, 11, 20, 20, 20);
    assertTrue(a.intersects(b), "Overlapping boxes must intersect");
    assertFalse(a.intersects(c), "Disjoint boxes must not intersect");
  }

  @Test
  void testBoundingBoxExpandByScalar() {
    BoundingBox box = createBoundingBoxFixture(0, 0, 0, 10, 10, 10);
    BoundingBox expanded = box.expand(2.0, 2.0, 2.0);
    assertEquals(-2.0, expanded.minX(), 0.0, "expand(2) must grow minX by 2");
    assertEquals(12.0, expanded.maxX(), 0.0, "expand(2) must grow maxX by 2");
  }

  @Test
  void testBoundingBoxShift() {
    BoundingBox box = createBoundingBoxFixture(0, 0, 0, 10, 10, 10);
    BoundingBox shifted = box.shift(5.0, 0.0, 0.0);
    assertEquals(5.0, shifted.minX(), 0.0);
    assertEquals(15.0, shifted.maxX(), 0.0);
    // y/z should be unchanged
    assertEquals(0.0, shifted.minY(), 0.0);
    assertEquals(10.0, shifted.maxY(), 0.0);
  }

  @Test
  void testBoundingBoxRayTraceOriginContained() {
    BoundingBox box = createBoundingBoxFixture(0, 0, 0, 10, 10, 10);
    Vector3dc origin = new org.joml.Vector3d(5.0, 5.0, 5.0);
    Vector3dc dir = new org.joml.Vector3d(1.0, 0.0, 0.0);
    RayTraceResult result = box.rayTrace(origin, dir, 100.0);
    if (result != null) {
      assertNotNull(
          result.hitPosition(), "A hit from inside a box must report a non-null hit position");
    }
    // A ray from outside the box pointing in must also produce a hit
    Vector3dc outsideOrigin = new org.joml.Vector3d(-1.0, 5.0, 5.0);
    RayTraceResult outsideHit = box.rayTrace(outsideOrigin, dir, 100.0);
    assertNotNull(outsideHit, "A ray entering the box from outside must report a hit");
  }
}
