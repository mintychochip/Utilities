package org.aincraft.minestom.adapter;

import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.world.DimensionType;
import org.aincraft.api.domain.contract.AbstractWorldContractTest;
import org.aincraft.api.domain.location.Location;
import org.aincraft.api.domain.world.World;
import org.junit.jupiter.api.BeforeAll;

import java.util.UUID;

public class MinestomWorldContractTest extends AbstractWorldContractTest {

  @BeforeAll
  static void initMinestom() {
    MinecraftServer.init();
  }

  @Override
  protected World createWorldFixture(UUID uid, String name, Key key, int minHeight, int maxHeight) {
    InstanceContainer instance = new InstanceContainer(uid, DimensionType.OVERWORLD);
    MinecraftServer.getInstanceManager().registerInstance(instance);
    instance.loadChunk(1, 1).join();
    return MinestomAdapters.adapt(instance, name, key);
  }

  @Override
  protected Location createLocationFixture(World world, double x, double y, double z) {
    Instance inst = ((MinestomWorldWrapper) world).getMinestomInstance();
    return MinestomAdapters.adapt(inst, new Pos(x, y, z));
  }
}
