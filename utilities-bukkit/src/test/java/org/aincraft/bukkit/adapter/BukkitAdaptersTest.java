package org.aincraft.bukkit.adapter;

import java.util.UUID;
import net.kyori.adventure.key.Key;
import org.aincraft.common.block.BlockFace;
import org.aincraft.common.block.BlockState;
import org.aincraft.common.block.BlockType;
import org.aincraft.common.entity.Entity;
import org.aincraft.common.entity.Player;
import org.aincraft.common.inventory.Inventory;
import org.aincraft.common.inventory.ItemStack;
import org.aincraft.common.inventory.ItemType;
import org.aincraft.common.inventory.PlayerInventory;
import org.aincraft.common.location.BoundingBox;
import org.aincraft.common.location.Location;
import org.aincraft.common.location.Position;
import org.aincraft.common.server.CommandSender;
import org.aincraft.common.server.Server;
import org.aincraft.common.world.Block;
import org.aincraft.common.world.Chunk;
import org.aincraft.common.world.World;
import org.aincraft.common.world.WorldBorder;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BukkitAdaptersTest {

  @Test
  void testVectorAndPositionAdaptation() {
    Vector vector = new Vector(10.5, 64.0, -12.5);
    Position pos = BukkitAdapters.adapt(vector);

    assertEquals(10.5, pos.x(), 1e-6);
    assertEquals(64.0, pos.y(), 1e-6);
    assertEquals(-12.5, pos.z(), 1e-6);
    assertEquals(10, pos.blockX());
    assertEquals(64, pos.blockY());
    assertEquals(-13, pos.blockZ());

    Vector converted = BukkitAdapters.toBukkit(pos);
    assertEquals(vector, converted);
  }

  @Test
  void testBoundingBoxAdaptation() {
    org.bukkit.util.BoundingBox bukkitBox = new org.bukkit.util.BoundingBox(0, 0, 0, 10, 10, 10);
    BoundingBox box = BukkitAdapters.adapt(bukkitBox);

    assertEquals(0.0, box.minX(), 1e-6);
    assertEquals(10.0, box.maxX(), 1e-6);
    assertTrue(box.contains(5, 5, 5));

    org.bukkit.util.BoundingBox converted = BukkitAdapters.toBukkit(box);
    assertEquals(bukkitBox, converted);
  }

  @Test
  void testBlockFaceBidirectionalMapping() {
    for (org.bukkit.block.BlockFace bFace : org.bukkit.block.BlockFace.values()) {
      BlockFace cFace = BukkitAdapters.adapt(bFace);
      org.bukkit.block.BlockFace roundTrip = BukkitAdapters.toBukkit(cFace);
      assertEquals(bFace, roundTrip);
      assertEquals(bFace.getModX(), cFace.modX());
      assertEquals(bFace.getModY(), cFace.modY());
      assertEquals(bFace.getModZ(), cFace.modZ());
    }
  }

  @Test
  void testBlockTypeAndItemTypeAdaptation() {
    Material stone = Material.STONE;
    BlockType blockType = BukkitAdapters.adaptBlockMaterial(stone);
    assertEquals(Key.key("minecraft", "stone"), blockType.key());
    assertEquals(stone, BukkitAdapters.toBukkitBlockMaterial(blockType));

    ItemType itemType = BukkitAdapters.adapt(stone);
    assertEquals(Key.key("minecraft", "stone"), itemType.key());
    assertEquals(stone, BukkitAdapters.toBukkit(itemType));
  }

  @Test
  void testStrictUnwrappingThrowsOnForeignImplementations() {
    Position foreignPos = new Position() {
      @Override public double x() { return 0; }
      @Override public double y() { return 0; }
      @Override public double z() { return 0; }
    };
    assertThrows(IllegalArgumentException.class, () -> BukkitAdapters.toBukkit(foreignPos));

    BoundingBox foreignBox = new BoundingBox() {
      @Override public double minX() { return 0; }
      @Override public double minY() { return 0; }
      @Override public double minZ() { return 0; }
      @Override public double maxX() { return 1; }
      @Override public double maxY() { return 1; }
      @Override public double maxZ() { return 1; }
    };
    assertThrows(IllegalArgumentException.class, () -> BukkitAdapters.toBukkit(foreignBox));

    Location foreignLoc = new Location() {
      @Override public World world() { return null; }
      @Override public Position position() { return foreignPos; }
      @Override public float yaw() { return 0; }
      @Override public float pitch() { return 0; }
    };
    assertThrows(IllegalArgumentException.class, () -> BukkitAdapters.toBukkit(foreignLoc));

    World foreignWorld = new World() {
      @Override public UUID uid() { return UUID.randomUUID(); }
      @Override public String name() { return "test"; }
      @Override public Key key() { return Key.key("test", "world"); }
      @Override public Block getBlockAt(int x, int y, int z) { return null; }
      @Override public Chunk getChunkAt(int chunkX, int chunkZ) { return null; }
      @Override public boolean isChunkLoaded(int chunkX, int chunkZ) { return false; }
      @Override public int minHeight() { return 0; }
      @Override public int maxHeight() { return 256; }
      @Override public WorldBorder worldBorder() { return null; }
      @Override public org.aincraft.common.world.Environment environment() { return org.aincraft.common.world.Environment.NORMAL; }
      @Override public org.aincraft.common.world.Difficulty difficulty() { return org.aincraft.common.world.Difficulty.NORMAL; }
      @Override public long time() { return 0; }
      @Override public long fullTime() { return 0; }
      @Override public java.util.Collection<? extends Player> players() { return java.util.List.of(); }
      @Override public java.util.Collection<? extends Entity> entities() { return java.util.List.of(); }
      @Override public java.util.Collection<? extends Chunk> loadedChunks() { return java.util.List.of(); }
    };
    assertThrows(IllegalArgumentException.class, () -> BukkitAdapters.toBukkit(foreignWorld));

    Block foreignBlock = new Block() {
      @Override public int x() { return 0; }
      @Override public int y() { return 0; }
      @Override public int z() { return 0; }
      @Override public World world() { return foreignWorld; }
      @Override public Chunk chunk() { return null; }
      @Override public Location location() { return foreignLoc; }
      @Override public Position position() { return foreignPos; }
      @Override public BlockType type() { return () -> Key.key("minecraft", "stone"); }
      @Override public BlockState state() { return null; }
      @Override public boolean isEmpty() { return false; }
      @Override public boolean isLiquid() { return false; }
      @Override public boolean isSolid() { return true; }
      @Override public boolean isAir() { return false; }
      @Override public boolean isPassable() { return false; }
      @Override public BoundingBox boundingBox() { return foreignBox; }
    };
    assertThrows(IllegalArgumentException.class, () -> BukkitAdapters.toBukkit(foreignBlock));

    Chunk foreignChunk = new Chunk() {
      @Override public int x() { return 0; }
      @Override public int z() { return 0; }
      @Override public World world() { return foreignWorld; }
      @Override public Block getBlock(int x, int y, int z) { return foreignBlock; }
      @Override public boolean isLoaded() { return true; }
      @Override public boolean load() { return true; }
      @Override public boolean load(boolean generate) { return true; }
      @Override public boolean unload() { return true; }
      @Override public boolean unload(boolean save) { return true; }
      @Override public java.util.Collection<? extends Entity> entities() { return java.util.List.of(); }
    };
    assertThrows(IllegalArgumentException.class, () -> BukkitAdapters.toBukkit(foreignChunk));

    WorldBorder foreignBorder = new WorldBorder() {
      @Override public double size() { return 1000; }
      @Override public void setSize(double size) {}
      @Override public Location center() { return foreignLoc; }
      @Override public void setCenter(Location center) {}
      @Override public double damageBuffer() { return 5; }
      @Override public void setDamageBuffer(double buffer) {}
      @Override public double damageAmount() { return 0.2; }
      @Override public void setDamageAmount(double amount) {}
      @Override public int warningTime() { return 15; }
      @Override public void setWarningTime(int seconds) {}
      @Override public int warningDistance() { return 5; }
      @Override public void setWarningDistance(int distance) {}
      @Override public boolean isInside(Location location) { return true; }
    };
    assertThrows(IllegalArgumentException.class, () -> BukkitAdapters.toBukkit(foreignBorder));

    ItemType foreignItemType = new ItemType() {
      @Override public Key key() { return Key.key("minecraft", "diamond"); }
      @Override public int maxStackSize() { return 64; }
      @Override public int maxDurability() { return 0; }
      @Override public boolean isBlock() { return false; }
      @Override public boolean isAir() { return false; }
      @Override public boolean isItem() { return true; }
      @Override public boolean isEdible() { return false; }
    };
    assertThrows(IllegalArgumentException.class, () -> BukkitAdapters.toBukkit(foreignItemType));

    ItemStack foreignItem = new ItemStack() {
      @Override public ItemType type() { return foreignItemType; }
      @Override public int amount() { return 1; }
      @Override public void setAmount(int amount) {}
      @Override public net.kyori.adventure.text.Component displayName() { return null; }
      @Override public java.util.List<net.kyori.adventure.text.Component> lore() { return null; }
      @Override public boolean hasItemMeta() { return false; }
      @Override public org.aincraft.common.inventory.ItemMeta meta() { return null; }
      @Override public void setMeta(org.aincraft.common.inventory.ItemMeta meta) {}
      @Override public boolean isSimilar(ItemStack other) { return false; }
      @Override public boolean isEmpty() { return false; }
      @Override public ItemStack withAmount(int amount) { return this; }
    };
    assertThrows(IllegalArgumentException.class, () -> BukkitAdapters.toBukkit(foreignItem));

    Inventory foreignInv = new Inventory() {
      @Override public int size() { return 9; }
      @Override public org.aincraft.common.inventory.InventoryType type() { return org.aincraft.common.inventory.InventoryType.CHEST; }
      @Override public ItemStack getItem(int slot) { return null; }
      @Override public void setItem(int slot, ItemStack item) {}
      @Override public ItemStack[] contents() { return new ItemStack[9]; }
      @Override public void setContents(ItemStack[] items) {}
      @Override public void clear() {}
      @Override public boolean isEmpty() { return true; }
      @Override public Location location() { return null; }
      @Override public org.aincraft.common.inventory.InventoryHolder holder() { return null; }
    };
    assertThrows(IllegalArgumentException.class, () -> BukkitAdapters.toBukkit(foreignInv));

    Server foreignServer = new Server() {
      @Override public String version() { return "1.0"; }
      @Override public String name() { return "Server"; }
      @Override public int port() { return 25565; }
      @Override public String ip() { return "127.0.0.1"; }
      @Override public int maxPlayers() { return 20; }
      @Override public java.util.Collection<? extends Player> onlinePlayers() { return java.util.List.of(); }
      @Override public java.util.Collection<? extends World> worlds() { return java.util.List.of(); }
      @Override public World world(Key key) { return null; }
      @Override public World world(String name) { return null; }
      @Override public World world(UUID uid) { return null; }
      @Override public Player player(UUID uid) { return null; }
      @Override public Player player(String name) { return null; }
      @Override public void broadcast(net.kyori.adventure.text.Component message) {}
      @Override public org.aincraft.common.server.ConsoleCommandSender consoleSender() { return null; }
      @Override public void shutdown() {}
      @Override public void reload() {}
    };
    assertThrows(IllegalArgumentException.class, () -> BukkitAdapters.toBukkit(foreignServer));

    CommandSender foreignSender = new CommandSender() {
      @Override public String name() { return "Test"; }
      @Override public boolean hasPermission(String permission) { return true; }
      @Override public boolean isOp() { return false; }
      @Override public void setOp(boolean op) {}
      @Override public net.kyori.adventure.identity.Identity identity() { return net.kyori.adventure.identity.Identity.nil(); }
    };
  }
}
