package org.aincraft.bukkit.persistence;

import static org.junit.jupiter.api.Assertions.*;

import net.kyori.adventure.key.Key;
import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.aincraft.api.domain.inventory.ItemStack;
import org.aincraft.api.domain.persistence.PersistentDataContainer;
import org.aincraft.api.domain.persistence.PersistentDataContainers;
import org.aincraft.api.domain.persistence.PersistentDataTypes;
import org.aincraft.bukkit.adapter.BukkitEntityWrapper;
import org.aincraft.bukkit.adapter.BukkitItemStackWrapper;
import org.aincraft.bukkit.adapter.BukkitTileBlockStateWrapper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

class BukkitPersistentDataContainerTest {

  @Test
  void containerRoundTripAllTypes() {
    org.bukkit.persistence.PersistentDataContainer delegate = pdcProxy();
    PersistentDataContainer pdc = new BukkitPersistentDataContainer(delegate);
    Key k = Key.key("aincraft", "test");

    pdc.set(k, PersistentDataTypes.BYTE, (byte) 1);
    assertEquals((byte) 1, pdc.get(k, PersistentDataTypes.BYTE));

    pdc.set(k, PersistentDataTypes.SHORT, (short) 2);
    assertEquals((short) 2, pdc.get(k, PersistentDataTypes.SHORT));

    pdc.set(k, PersistentDataTypes.INTEGER, 3);
    assertEquals(3, pdc.get(k, PersistentDataTypes.INTEGER));

    pdc.set(k, PersistentDataTypes.LONG, 4L);
    assertEquals(4L, pdc.get(k, PersistentDataTypes.LONG));

    pdc.set(k, PersistentDataTypes.FLOAT, 5.5f);
    assertEquals(5.5f, pdc.get(k, PersistentDataTypes.FLOAT));

    pdc.set(k, PersistentDataTypes.DOUBLE, 6.6);
    assertEquals(6.6, pdc.get(k, PersistentDataTypes.DOUBLE));

    pdc.set(k, PersistentDataTypes.BOOLEAN, true);
    assertEquals(true, pdc.get(k, PersistentDataTypes.BOOLEAN));

    pdc.set(k, PersistentDataTypes.STRING, "value");
    assertEquals("value", pdc.get(k, PersistentDataTypes.STRING));

    byte[] bytes = {1, 2, 3};
    pdc.set(k, PersistentDataTypes.BYTE_ARRAY, bytes);
    assertArrayEquals(bytes, pdc.get(k, PersistentDataTypes.BYTE_ARRAY));

    int[] ints = {4, 5, 6};
    pdc.set(k, PersistentDataTypes.INTEGER_ARRAY, ints);
    assertArrayEquals(ints, pdc.get(k, PersistentDataTypes.INTEGER_ARRAY));

    long[] longs = {7, 8, 9};
    pdc.set(k, PersistentDataTypes.LONG_ARRAY, longs);
    assertArrayEquals(longs, pdc.get(k, PersistentDataTypes.LONG_ARRAY));

    assertTrue(pdc.has(k));
    assertTrue(pdc.has(k, PersistentDataTypes.LONG_ARRAY));
    assertFalse(pdc.has(k, PersistentDataTypes.INTEGER));

    pdc.remove(k);
    assertTrue(pdc.isEmpty());
  }

  @Test
  void copyToMemory() {
    org.bukkit.persistence.PersistentDataContainer delegate = pdcProxy();
    PersistentDataContainer source = new BukkitPersistentDataContainer(delegate);
    Key a = Key.key("aincraft", "a");
    Key b = Key.key("aincraft", "b");
    source.set(a, PersistentDataTypes.INTEGER, 1);
    source.set(b, PersistentDataTypes.BOOLEAN, true);

    PersistentDataContainer target = PersistentDataContainers.create();
    source.copyTo(target);

    assertEquals(1, target.get(a, PersistentDataTypes.INTEGER));
    assertEquals(true, target.get(b, PersistentDataTypes.BOOLEAN));
  }

  @Test
  void copyFromMemory() {
    PersistentDataContainer source = PersistentDataContainers.create();
    Key k = Key.key("aincraft", "k");
    source.set(k, PersistentDataTypes.LONG, 42L);

    org.bukkit.persistence.PersistentDataContainer delegate = pdcProxy();
    PersistentDataContainer target = new BukkitPersistentDataContainer(delegate);
    source.copyTo(target);

    assertEquals(42L, target.get(k, PersistentDataTypes.LONG));
  }

  @Test
  void itemStackRoundTrip() {
    Map<NamespacedKey, Object> store = new HashMap<>();
    org.bukkit.persistence.PersistentDataContainer pdc = pdcProxy(store);
    org.bukkit.inventory.meta.ItemMeta meta =
        (org.bukkit.inventory.meta.ItemMeta)
            Proxy.newProxyInstance(
                org.bukkit.inventory.meta.ItemMeta.class.getClassLoader(),
                new Class<?>[] {org.bukkit.inventory.meta.ItemMeta.class},
                (proxy, method, args) ->
                    switch (method.getName()) {
                      case "getPersistentDataContainer" -> pdc;
                      default -> null;
                    });

    FakeItemStack item = new FakeItemStack();
    item.setItemMeta(meta);

    ItemStack wrapped = new BukkitItemStackWrapper(item);
    Key k = Key.key("aincraft", "item");
    wrapped.persistentData().set(k, PersistentDataTypes.STRING, "stored");

    assertEquals("stored", wrapped.persistentData().get(k, PersistentDataTypes.STRING));
    assertEquals("stored", store.get(new NamespacedKey("aincraft", "item")));
  }

  private static final class FakeItemStack extends org.bukkit.inventory.ItemStack {
    private org.bukkit.inventory.meta.ItemMeta meta;

    FakeItemStack() {
      super(Material.DIAMOND_HELMET);
    }

    @Override
    public org.bukkit.inventory.meta.ItemMeta getItemMeta() {
      return meta;
    }

    @Override
    public boolean setItemMeta(org.bukkit.inventory.meta.ItemMeta meta) {
      this.meta = meta;
      return true;
    }
  }

  @Test
  void entityRoundTrip() {
    Map<NamespacedKey, Object> store = new HashMap<>();
    org.bukkit.persistence.PersistentDataContainer pdc = pdcProxy(store);
    org.bukkit.entity.Entity entity =
        (org.bukkit.entity.Entity)
            Proxy.newProxyInstance(
                org.bukkit.entity.Entity.class.getClassLoader(),
                new Class<?>[] {org.bukkit.entity.Entity.class},
                (proxy, method, args) ->
                    switch (method.getName()) {
                      case "getType" -> EntityType.ZOMBIE;
                      case "getUniqueId" -> UUID.randomUUID();
                      case "getWorld" ->
                          (World)
                              Proxy.newProxyInstance(
                                  World.class.getClassLoader(),
                                  new Class<?>[] {World.class},
                                  (p, m, a) -> null);
                      case "getLocation" -> new Location(null, 0, 0, 0);
                      case "getPersistentDataContainer" -> pdc;
                      default -> null;
                    });

    org.aincraft.api.domain.entity.Entity wrapped = new BukkitEntityWrapper(entity);
    Key k = Key.key("aincraft", "entity");
    wrapped.persistentData().set(k, PersistentDataTypes.INTEGER, 123);

    assertEquals(123, wrapped.persistentData().get(k, PersistentDataTypes.INTEGER));
    assertEquals(123, store.get(new NamespacedKey("aincraft", "entity")));
  }

  @Test
  void nonTileBlockStateThrowsUnsupported() {
    org.bukkit.block.BlockState state =
        (org.bukkit.block.BlockState)
            Proxy.newProxyInstance(
                org.bukkit.block.BlockState.class.getClassLoader(),
                new Class<?>[] {org.bukkit.block.BlockState.class},
                (proxy, method, args) -> null);

    BukkitTileBlockStateWrapper tile = new BukkitTileBlockStateWrapper(state);
    UnsupportedCapabilityException ex =
        assertThrows(UnsupportedCapabilityException.class, tile::persistentData);
    assertEquals(Capability.PERSISTENT_DATA, ex.capability());
  }

  private static org.bukkit.persistence.PersistentDataContainer pdcProxy() {
    return pdcProxy(new HashMap<>());
  }

  private static org.bukkit.persistence.PersistentDataContainer pdcProxy(
      Map<NamespacedKey, Object> store) {
    return (org.bukkit.persistence.PersistentDataContainer)
        Proxy.newProxyInstance(
            org.bukkit.persistence.PersistentDataContainer.class.getClassLoader(),
            new Class<?>[] {org.bukkit.persistence.PersistentDataContainer.class},
            (proxy, method, args) -> {
              String name = method.getName();
              if ("has".equals(name)) {
                if (args.length == 1) return store.containsKey(args[0]);
                Object value = store.get(args[0]);
                org.bukkit.persistence.PersistentDataType<?, ?> type =
                    (org.bukkit.persistence.PersistentDataType<?, ?>) args[1];
                return value != null && type.getComplexType().isInstance(value);
              }
              if ("get".equals(name)) {
                Object value = store.get(args[0]);
                org.bukkit.persistence.PersistentDataType<?, ?> type =
                    (org.bukkit.persistence.PersistentDataType<?, ?>) args[1];
                if (value == null || !type.getComplexType().isInstance(value)) {
                  return args.length == 2 ? null : args[2];
                }
                return type.getComplexType().cast(value);
              }
              if ("set".equals(name)) {
                store.put((NamespacedKey) args[0], args[2]);
                return null;
              }
              if ("remove".equals(name)) {
                store.remove(args[0]);
                return null;
              }
              if ("getKeys".equals(name)) {
                return new HashSet<>(store.keySet());
              }
              if ("isEmpty".equals(name)) {
                return store.isEmpty();
              }
              return null;
            });
  }
}
