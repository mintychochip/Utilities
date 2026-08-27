package org.aincraft.minestom.adapter;

import static org.junit.jupiter.api.Assertions.*;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.world.DimensionType;
import org.aincraft.common.world.World;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class MinestomAdaptersWorldTest {

  @BeforeAll
  static void setup() {
    MinecraftServer.init();
  }

  @Test
  void testWorldWrapperPropertiesAndAudience() {
    UUID uuid = UUID.randomUUID();
    Instance instance = new InstanceContainer(uuid, DimensionType.OVERWORLD);

    World world = MinestomAdapters.adapt(instance);
    assertEquals(uuid, world.uid());
    assertEquals(-64, world.minHeight());
    assertEquals(320, world.maxHeight());
    assertNotNull(world.name());
    assertNotNull(world.key());
    assertNotNull(world.worldBorder());

    Component msg = Component.text("Hello Minestom World");
    assertDoesNotThrow(() -> world.sendMessage(msg));
    assertDoesNotThrow(() -> world.sendActionBar(msg));

    assertSame(instance, MinestomAdapters.toMinestom(world));
  }
}
