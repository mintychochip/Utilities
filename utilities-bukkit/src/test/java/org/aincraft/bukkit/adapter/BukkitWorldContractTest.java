package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.contract.AbstractWorldContractTest;
import org.aincraft.api.domain.location.Location;
import org.aincraft.api.domain.world.World;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.UUID;

public class BukkitWorldContractTest extends AbstractWorldContractTest {

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
                    case "getPlayers" -> List.of();
                    case "getEntities" -> List.of();
                    case "getLoadedChunks" -> List.of();
                    case "getBlockAt" -> {
                      int bx = (int) args[0];
                      int by = (int) args[1];
                      int bz = (int) args[2];
                      yield createBlockProxy(proxy, bx, by, bz);
                    }
                    case "getChunkAt" -> {
                      int cx = (int) args[0];
                      int cz = (int) args[1];
                      yield createChunkProxy(proxy, cx, cz);
                    }
                    case "spawnEntity" -> {
                      org.bukkit.Location loc = (org.bukkit.Location) args[0];
                      EntityType type = (EntityType) args[1];
                      yield Proxy.newProxyInstance(
                          org.bukkit.entity.Entity.class.getClassLoader(),
                          new Class<?>[] {org.bukkit.entity.Entity.class},
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

    return BukkitAdapters.adapt(bWorld);
  }

  private static org.bukkit.block.Block createBlockProxy(
      Object worldProxy, int bx, int by, int bz) {
    return (org.bukkit.block.Block)
        Proxy.newProxyInstance(
            org.bukkit.block.Block.class.getClassLoader(),
            new Class<?>[] {org.bukkit.block.Block.class},
            (bp, bm, ba) -> {
              return switch (bm.getName()) {
                case "getX" -> bx;
                case "getY" -> by;
                case "getZ" -> bz;
                case "getWorld" -> worldProxy;
                case "getType" -> Material.STONE;
                case "setType" -> null;
                default -> null;
              };
            });
  }

  private static org.bukkit.Chunk createChunkProxy(Object worldProxy, int cx, int cz) {
    return (org.bukkit.Chunk)
        Proxy.newProxyInstance(
            org.bukkit.Chunk.class.getClassLoader(),
            new Class<?>[] {org.bukkit.Chunk.class},
            (cp, cm, ca) -> {
              return switch (cm.getName()) {
                case "getX" -> cx;
                case "getZ" -> cz;
                case "getWorld" -> worldProxy;
                case "getBlock" -> {
                  int bx = (int) ca[0];
                  int by = (int) ca[1];
                  int bz = (int) ca[2];
                  yield createBlockProxy(worldProxy, bx, by, bz);
                }
                default -> null;
              };
            });
  }

  @Override
  protected Location createLocationFixture(World world, double x, double y, double z) {
    org.bukkit.World bWorld = ((BukkitWorldWrapper) world).getBukkitWorld();
    return BukkitAdapters.adapt(new org.bukkit.Location(bWorld, x, y, z));
  }
}
