package org.aincraft.minestom.adapter;

import static org.junit.jupiter.api.Assertions.*;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.world.DimensionType;
import org.aincraft.api.domain.location.Location;
import org.aincraft.api.domain.world.FluidCollisionMode;
import org.aincraft.api.domain.world.HeightMap;
import org.aincraft.api.domain.world.RayTraceResult;
import org.aincraft.api.domain.world.World;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class MinestomWorldParityTest {

  @BeforeAll
  static void setup() {
    MinecraftServer.init();
  }

  @Test
  void highestBlockAndRayTraceDelegateToInstanceBlocks() {
    InstanceContainer instance = new InstanceContainer(UUID.randomUUID(), DimensionType.OVERWORLD);
    MinecraftServer.getInstanceManager().registerInstance(instance);
    instance.loadChunk(0, 0).join();
    instance.setBlock(3, 70, 0, Block.STONE);
    World world = MinestomAdapters.adapt(instance);

    assertEquals(70, world.getHighestBlockAt(3, 0, HeightMap.WORLD_SURFACE).y());

    Location start = MinestomAdapters.adapt(instance, new Pos(2.5, 70.5, 0.5));
    RayTraceResult result =
        world.rayTraceBlocks(
            start,
            MinestomAdapters.adapt(new Vec(2.0, 0.0, 0.0)),
            5.0,
            FluidCollisionMode.NEVER,
            false);
    assertNotNull(result);
    assertEquals(3, result.hitBlock().x());
    assertEquals(org.aincraft.api.domain.block.BlockFace.WEST, result.hitBlockFace());
    assertEquals(3.0, result.hitPosition().x(), 1e-9);
    org.aincraft.api.domain.entity.Entity spawned =
        world.spawnEntity(start, net.kyori.adventure.key.Key.key("minecraft", "zombie"));
    assertInstanceOf(org.aincraft.api.domain.entity.LivingEntity.class, spawned);
  }

  @Test
  void serverExposesRegisteredInstancesAndInventoryFactory() {
    InstanceContainer instance = new InstanceContainer(UUID.randomUUID(), DimensionType.OVERWORLD);
    MinecraftServer.getInstanceManager().registerInstance(instance);
    org.aincraft.api.domain.server.Server server = MinestomAdapters.adaptServer();

    assertSame(instance, MinestomAdapters.toMinestom(server.world(instance.getUuid())));
    assertEquals(
        9, server.createInventory(null, 9, net.kyori.adventure.text.Component.text("test")).size());
    assertNotNull(
        server
            .itemFactory()
            .createItemStack(net.kyori.adventure.key.Key.key("minecraft", "stone")));
  }

  @Test
  void twentySixTwoSulfurCubeIsRegistryBackedAndSpawnable() {
    net.minestom.server.entity.EntityType sulfurCube =
        net.minestom.server.entity.EntityType.fromKey(
            net.kyori.adventure.key.Key.key("minecraft", "sulfur_cube"));
    assertNotNull(sulfurCube);

    org.aincraft.api.domain.entity.EntityType domain = MinestomAdapters.adapt(sulfurCube);
    assertEquals("minecraft:sulfur_cube", domain.key().asString());
    assertTrue(domain.isAlive());
    assertTrue(domain.isSpawnable());
    assertSame(sulfurCube, MinestomAdapters.toMinestom(domain));
    InstanceContainer instance = new InstanceContainer(UUID.randomUUID(), DimensionType.OVERWORLD);
    MinecraftServer.getInstanceManager().registerInstance(instance);
    instance.loadChunk(0, 0).join();
    World world = MinestomAdapters.adapt(instance);
    org.aincraft.api.domain.entity.Entity spawned =
        world.spawnEntity(
            MinestomAdapters.adapt(instance, new Pos(0.5, 64, 0.5)),
            net.kyori.adventure.key.Key.key("minecraft", "sulfur_cube"));
    assertEquals("minecraft:sulfur_cube", spawned.type().asString());
    assertInstanceOf(org.aincraft.api.domain.entity.LivingEntity.class, spawned);
  }

  @Test
  void twentySixTwoMobRegistryIncludesNewMobTypes() {
    String[] mobKeys = {
      "copper_golem",
      "happy_ghast",
      "mannequin",
      "nautilus",
      "parched",
      "sulfur_cube",
      "zombie_nautilus"
    };
    for (String mobKey : mobKeys) {
      net.minestom.server.entity.EntityType type =
          net.minestom.server.entity.EntityType.fromKey(
              net.kyori.adventure.key.Key.key("minecraft", mobKey));
      assertNotNull(type, mobKey);
      org.aincraft.api.domain.entity.EntityType domain = MinestomAdapters.adapt(type);
      assertEquals("minecraft:" + mobKey, domain.key().asString());
      assertTrue(domain.isAlive(), mobKey);
      assertTrue(domain.isSpawnable(), mobKey);
    }
  }
}
