package org.aincraft.minestom.persistence;

import static org.junit.jupiter.api.Assertions.*;

import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.instance.generator.Generator;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.tag.TagHandler;
import net.minestom.server.utils.chunk.ChunkSupplier;
import net.minestom.server.world.DimensionType;
import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.aincraft.api.domain.persistence.PersistentDataContainer;
import org.aincraft.api.domain.persistence.PersistentDataContainers;
import org.aincraft.api.domain.persistence.PersistentDataTypes;
import org.aincraft.minestom.adapter.MinestomTileBlockStateWrapper;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

class MinestomPersistentDataContainerTest {

  @Test
  void tagHandlerRoundTripAllTypes() {
    TagHandler handler = TagHandler.newHandler();
    PersistentDataContainer pdc = new MinestomPersistentDataContainer(handler);
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
  void itemStackReplacesAndKeepsData() {
    AtomicReference<ItemStack> ref = new AtomicReference<>(ItemStack.of(Material.STONE, 1));
    Supplier<ItemStack> getter = ref::get;
    Consumer<ItemStack> setter = ref::set;
    PersistentDataContainer pdc = new MinestomPersistentDataContainer(getter, setter);
    Key k = Key.key("aincraft", "item");

    pdc.set(k, PersistentDataTypes.INTEGER, 42);

    ItemStack next = ref.get();
    assertNotNull(next);
    assertEquals(42, pdc.get(k, PersistentDataTypes.INTEGER));

    pdc.remove(k);
    assertNull(pdc.get(k, PersistentDataTypes.INTEGER));
  }

  @Test
  void copyToMemory() {
    TagHandler handler = TagHandler.newHandler();
    PersistentDataContainer source = new MinestomPersistentDataContainer(handler);
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

    TagHandler handler = TagHandler.newHandler();
    PersistentDataContainer target = new MinestomPersistentDataContainer(handler);
    source.copyTo(target);

    assertEquals(42L, target.get(k, PersistentDataTypes.LONG));
  }

  @Test
  void tileBlockStateThrowsUnsupported() {
    MinestomTileBlockStateWrapper tile =
        new MinestomTileBlockStateWrapper(new FakeInstance(), 0, 0, 0);
    UnsupportedCapabilityException ex =
        assertThrows(UnsupportedCapabilityException.class, tile::persistentData);
    assertEquals(Capability.PERSISTENT_DATA, ex.capability());
  }

  private static final class FakeInstance extends Instance {
    FakeInstance() {
      super(UUID.randomUUID(), RegistryKey.<DimensionType>unsafeOf("minecraft:overworld"));
    }

    @Override
    public Block getBlock(int x, int y, int z, Block.Getter.Condition condition) {
      return Block.STONE;
    }

    @Override
    public void setBlock(int x, int y, int z, Block block, boolean applyPhysics) {}

    @Override
    public boolean placeBlock(BlockHandler.Placement placement, boolean canBreak) {
      return false;
    }

    @Override
    public boolean breakBlock(Player player, Point point, BlockFace face, boolean particles) {
      return false;
    }

    @Override
    public CompletableFuture<Chunk> loadChunk(int chunkX, int chunkZ) {
      return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Chunk> loadOptionalChunk(int chunkX, int chunkZ) {
      return CompletableFuture.completedFuture(null);
    }

    @Override
    public void unloadChunk(Chunk chunk) {}

    @Override
    public Chunk getChunk(int chunkX, int chunkZ) {
      return null;
    }

    @Override
    public CompletableFuture<Void> saveInstance() {
      return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> saveChunkToStorage(Chunk chunk) {
      return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> saveChunksToStorage() {
      return CompletableFuture.completedFuture(null);
    }

    @Override
    public void setChunkSupplier(ChunkSupplier chunkSupplier) {}

    @Override
    public ChunkSupplier getChunkSupplier() {
      return null;
    }

    @Override
    public Generator generator() {
      return null;
    }

    @Override
    public void setGenerator(Generator generator) {}

    @Override
    public CompletableFuture<Void> generateChunk(int chunkX, int chunkZ, Generator generator) {
      return CompletableFuture.completedFuture(null);
    }

    @Override
    public Collection<Chunk> getChunks() {
      return Collections.emptyList();
    }

    @Override
    public void enableAutoChunkLoad(boolean enable) {}

    @Override
    public boolean hasEnabledAutoChunkLoad() {
      return false;
    }

    @Override
    public boolean isInVoid(Point point) {
      return false;
    }
  }
}
