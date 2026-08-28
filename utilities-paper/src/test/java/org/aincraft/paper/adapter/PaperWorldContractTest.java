package org.aincraft.paper.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.contract.AbstractWorldContractTest;
import org.aincraft.api.domain.location.Location;
import org.aincraft.api.domain.world.World;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Proxy;
import java.util.UUID;

public class PaperWorldContractTest extends AbstractWorldContractTest {

  @Override
  protected World createWorldFixture(UUID uid, String name, Key key, int minHeight, int maxHeight) {
    NamespacedKey bKey = new NamespacedKey(key.namespace(), key.value());

    org.bukkit.World bWorld =
        (org.bukkit.World)
            Proxy.newProxyInstance(
                org.bukkit.World.class.getClassLoader(),
                new Class<?>[] {org.bukkit.World.class},
                (proxy, method, args) -> {
                  return switch (method.getName()) {
                    case "getUID" -> uid;
                    case "getName" -> name;
                    case "getKey" -> bKey;
                    case "getMinHeight" -> minHeight;
                    case "getMaxHeight" -> maxHeight;
                    case "getTime" -> 0L;
                    case "getFullTime" -> 0L;
                    case "getPlayers" -> java.util.List.of();
                    case "getEntities" -> java.util.List.of();
                    case "getLoadedChunks" -> java.util.List.of();
                    case "getChunkAt" -> {
                      int x = (int) args[0];
                      int z = (int) args[1];
                      yield Proxy.newProxyInstance(
                          org.bukkit.Chunk.class.getClassLoader(),
                          new Class<?>[] {org.bukkit.Chunk.class},
                          (cp, cm, ca) -> {
                            return switch (cm.getName()) {
                              case "getX" -> x;
                              case "getZ" -> z;
                              case "getWorld" -> proxy;
                              default -> null;
                            };
                          });
                    }
                    case "getBlockAt" -> {
                      int x = (int) args[0];
                      int y = (int) args[1];
                      int z = (int) args[2];
                      org.bukkit.block.Block bBlock =
                          (org.bukkit.block.Block)
                              Proxy.newProxyInstance(
                                  org.bukkit.block.Block.class.getClassLoader(),
                                  new Class<?>[] {org.bukkit.block.Block.class},
                                  (bp, bm, ba) -> {
                                    return switch (bm.getName()) {
                                      case "getX" -> x;
                                      case "getY" -> y;
                                      case "getZ" -> z;
                                      case "getWorld" -> proxy;
                                      case "getType" -> org.bukkit.Material.STONE;
                                      case "setType" -> null;
                                      default -> null;
                                    };
                                  });
                      yield bBlock;
                    }
                    case "spawnEntity" -> {
                      org.bukkit.Location loc = (org.bukkit.Location) args[0];
                      EntityType type = (EntityType) args[1];
                      yield Proxy.newProxyInstance(
                          org.bukkit.entity.Entity.class.getClassLoader(),
                          new Class<?>[] {
                            org.bukkit.entity.Entity.class, org.bukkit.entity.LivingEntity.class
                          },
                          (ep, em, ea) -> {
                            return switch (em.getName()) {
                              case "getUniqueId" -> UUID.randomUUID();
                              case "getType" -> type;
                              case "getLocation" -> loc;
                              case "getWorld" -> proxy;
                              default -> null;
                            };
                          });
                    }
                    default -> null;
                  };
                });

    return PaperAdapters.adapt(bWorld);
  }

  @Override
  protected Location createLocationFixture(World world, double x, double y, double z) {
    org.bukkit.World bWorld = ((PaperWorldWrapper) world).getBukkitWorld();
    return PaperAdapters.adapt(new org.bukkit.Location(bWorld, x, y, z));
  }
}
