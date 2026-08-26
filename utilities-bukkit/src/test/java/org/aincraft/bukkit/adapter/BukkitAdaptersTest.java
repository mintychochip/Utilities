package org.aincraft.bukkit.adapter;

import java.util.UUID;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.aincraft.common.block.BlockFace;
import org.aincraft.common.block.BlockType;
import org.aincraft.common.entity.Player;
import org.aincraft.common.location.BoundingBox;
import org.aincraft.common.location.Location;
import org.aincraft.common.location.Position;
import org.aincraft.common.world.World;
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
    assertTrue(box.contains(BukkitAdapters.adapt(new Vector(5, 5, 5))));

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

    org.aincraft.common.inventory.ItemType itemType = BukkitAdapters.adapt(stone);
    assertEquals(Key.key("minecraft", "stone"), itemType.key());
    assertEquals(stone, BukkitAdapters.toBukkit(itemType));
  }
}
