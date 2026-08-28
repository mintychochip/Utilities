package org.aincraft.api.domain.persistence;

import static org.junit.jupiter.api.Assertions.*;

import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.Test;

import java.util.Set;

class PersistentDataContainerTest {

  private static final Key KEY = Key.key("aincraft", "test");

  @Test
  void setAndGetAllPrimitiveTypes() {
    PersistentDataContainer pdc = PersistentDataContainers.create();

    pdc.set(KEY, PersistentDataTypes.BYTE, (byte) 1);
    assertEquals((byte) 1, pdc.get(KEY, PersistentDataTypes.BYTE));

    pdc.set(KEY, PersistentDataTypes.SHORT, (short) 2);
    assertEquals((short) 2, pdc.get(KEY, PersistentDataTypes.SHORT));

    pdc.set(KEY, PersistentDataTypes.INTEGER, 3);
    assertEquals(3, pdc.get(KEY, PersistentDataTypes.INTEGER));

    pdc.set(KEY, PersistentDataTypes.LONG, 4L);
    assertEquals(4L, pdc.get(KEY, PersistentDataTypes.LONG));

    pdc.set(KEY, PersistentDataTypes.FLOAT, 5.5f);
    assertEquals(5.5f, pdc.get(KEY, PersistentDataTypes.FLOAT));

    pdc.set(KEY, PersistentDataTypes.DOUBLE, 6.6);
    assertEquals(6.6, pdc.get(KEY, PersistentDataTypes.DOUBLE));

    pdc.set(KEY, PersistentDataTypes.BOOLEAN, true);
    assertEquals(true, pdc.get(KEY, PersistentDataTypes.BOOLEAN));

    pdc.set(KEY, PersistentDataTypes.STRING, "value");
    assertEquals("value", pdc.get(KEY, PersistentDataTypes.STRING));
  }

  @Test
  void setAndGetArrays() {
    PersistentDataContainer pdc = PersistentDataContainers.create();

    byte[] bytes = {1, 2, 3};
    pdc.set(KEY, PersistentDataTypes.BYTE_ARRAY, bytes);
    assertArrayEquals(bytes, pdc.get(KEY, PersistentDataTypes.BYTE_ARRAY));

    int[] ints = {4, 5, 6};
    pdc.set(KEY, PersistentDataTypes.INTEGER_ARRAY, ints);
    assertArrayEquals(ints, pdc.get(KEY, PersistentDataTypes.INTEGER_ARRAY));

    long[] longs = {7, 8, 9};
    pdc.set(KEY, PersistentDataTypes.LONG_ARRAY, longs);
    assertArrayEquals(longs, pdc.get(KEY, PersistentDataTypes.LONG_ARRAY));
  }

  @Test
  void hasWithAndWithoutType() {
    PersistentDataContainer pdc = PersistentDataContainers.create();
    Key other = Key.key("aincraft", "other");

    pdc.set(KEY, PersistentDataTypes.INTEGER, 42);

    assertTrue(pdc.has(KEY));
    assertTrue(pdc.has(KEY, PersistentDataTypes.INTEGER));
    assertFalse(pdc.has(KEY, PersistentDataTypes.STRING));
    assertFalse(pdc.has(other));
  }

  @Test
  void getReturnsNullForMissingOrMismatchedType() {
    PersistentDataContainer pdc = PersistentDataContainers.create();
    assertNull(pdc.get(KEY, PersistentDataTypes.INTEGER));
    pdc.set(KEY, PersistentDataTypes.INTEGER, 1);
    assertNull(pdc.get(KEY, PersistentDataTypes.STRING));
  }

  @Test
  void getOrDefault() {
    PersistentDataContainer pdc = PersistentDataContainers.create();
    assertEquals(42, pdc.getOrDefault(KEY, PersistentDataTypes.INTEGER, 42));
    pdc.set(KEY, PersistentDataTypes.INTEGER, 7);
    assertEquals(7, pdc.getOrDefault(KEY, PersistentDataTypes.INTEGER, 42));
  }

  @Test
  void removeAndIsEmpty() {
    PersistentDataContainer pdc = PersistentDataContainers.create();
    pdc.set(KEY, PersistentDataTypes.INTEGER, 1);
    assertFalse(pdc.isEmpty());
    pdc.remove(KEY);
    assertTrue(pdc.isEmpty());
    assertNull(pdc.get(KEY, PersistentDataTypes.INTEGER));
  }

  @Test
  void keysAndSize() {
    PersistentDataContainer pdc = PersistentDataContainers.create();
    Key a = Key.key("aincraft", "a");
    Key b = Key.key("aincraft", "b");
    pdc.set(a, PersistentDataTypes.INTEGER, 1);
    pdc.set(b, PersistentDataTypes.STRING, "x");

    Set<Key> keys = pdc.keys();
    assertEquals(2, pdc.size());
    assertTrue(keys.contains(a));
    assertTrue(keys.contains(b));
  }

  @Test
  void copyToReplacesByDefault() {
    PersistentDataContainer source = PersistentDataContainers.create();
    PersistentDataContainer target = PersistentDataContainers.create();
    Key a = Key.key("aincraft", "a");
    Key b = Key.key("aincraft", "b");

    source.set(a, PersistentDataTypes.INTEGER, 1);
    source.set(b, PersistentDataTypes.STRING, "from");
    target.set(a, PersistentDataTypes.INTEGER, 99);
    target.set(b, PersistentDataTypes.STRING, "to");

    source.copyTo(target);

    assertEquals(1, target.get(a, PersistentDataTypes.INTEGER));
    assertEquals("from", target.get(b, PersistentDataTypes.STRING));
  }

  @Test
  void copyToNoReplaceSkipsExisting() {
    PersistentDataContainer source = PersistentDataContainers.create();
    PersistentDataContainer target = PersistentDataContainers.create();
    Key a = Key.key("aincraft", "a");
    Key b = Key.key("aincraft", "b");

    source.set(a, PersistentDataTypes.INTEGER, 1);
    source.set(b, PersistentDataTypes.STRING, "from");
    target.set(a, PersistentDataTypes.INTEGER, 99);

    source.copyTo(target, false);

    assertEquals(99, target.get(a, PersistentDataTypes.INTEGER));
    assertEquals("from", target.get(b, PersistentDataTypes.STRING));
  }

  @Test
  void booleanAndByteDoNotCollide() {
    PersistentDataContainer pdc = PersistentDataContainers.create();
    Key boolKey = Key.key("aincraft", "bool");
    Key byteKey = Key.key("aincraft", "byte");

    pdc.set(boolKey, PersistentDataTypes.BOOLEAN, true);
    pdc.set(byteKey, PersistentDataTypes.BYTE, (byte) 1);

    assertEquals(true, pdc.get(boolKey, PersistentDataTypes.BOOLEAN));
    assertEquals((byte) 1, pdc.get(byteKey, PersistentDataTypes.BYTE));
    assertNull(pdc.get(boolKey, PersistentDataTypes.BYTE));
    assertNull(pdc.get(byteKey, PersistentDataTypes.BOOLEAN));
  }
}
