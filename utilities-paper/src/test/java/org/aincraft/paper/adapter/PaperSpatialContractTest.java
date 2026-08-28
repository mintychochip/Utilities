package org.aincraft.paper.adapter;

import org.aincraft.api.domain.block.BlockFace;
import org.aincraft.api.domain.contract.AbstractSpatialContractTest;
import org.aincraft.api.domain.location.BoundingBox;
import org.aincraft.api.domain.location.Position;
import org.aincraft.bukkit.adapter.BukkitAdapters;
import org.bukkit.util.Vector;

public class PaperSpatialContractTest extends AbstractSpatialContractTest {

  @Override
  protected Position createPositionFixture(double x, double y, double z) {
    return BukkitAdapters.adapt(new Vector(x, y, z));
  }

  @Override
  protected BoundingBox createBoundingBoxFixture(
      double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
    return BukkitAdapters.adapt(
        new org.bukkit.util.BoundingBox(minX, minY, minZ, maxX, maxY, maxZ));
  }

  @Override
  protected BlockFace adaptBlockFace(BlockFace face) {
    org.bukkit.block.BlockFace bFace = BukkitAdapters.toBukkit(face);
    return BukkitAdapters.adapt(bFace);
  }
}
