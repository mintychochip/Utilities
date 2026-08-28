package org.aincraft.minestom.adapter;

import net.minestom.server.coordinate.Vec;
import org.aincraft.api.domain.block.BlockFace;
import org.aincraft.api.domain.contract.AbstractSpatialContractTest;
import org.aincraft.api.domain.location.BoundingBox;
import org.aincraft.api.domain.location.Position;

public class MinestomSpatialContractTest extends AbstractSpatialContractTest {

  @Override
  protected Position createPositionFixture(double x, double y, double z) {
    return MinestomAdapters.adapt(new Vec(x, y, z));
  }

  @Override
  protected BoundingBox createBoundingBoxFixture(
      double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
    // Minestom BoundingBox is symmetric on X/Z (width, height, depth) -> [-width/2..width/2,
    // 0..height, -depth/2..depth/2]
    double width = maxX - minX;
    double height = maxY - minY;
    double depth = maxZ - minZ;
    return MinestomAdapters.adapt(
        new net.minestom.server.collision.BoundingBox(width, height, depth));
  }

  @Override
  protected BlockFace adaptBlockFace(BlockFace face) {
    net.minestom.server.instance.block.BlockFace mFace = MinestomAdapters.toMinestom(face);
    return MinestomAdapters.adapt(mFace);
  }
}
