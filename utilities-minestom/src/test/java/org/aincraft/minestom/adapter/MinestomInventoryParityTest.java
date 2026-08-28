package org.aincraft.minestom.adapter;

import static org.junit.jupiter.api.Assertions.*;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.aincraft.api.domain.inventory.DamageableItemMeta;
import org.aincraft.api.domain.inventory.Inventory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MinestomInventoryParityTest {

  @BeforeAll
  static void setup() {
    MinecraftServer.init();
  }

  @Test
  void itemStackAndMetadataRoundTrip() {
    org.aincraft.api.domain.inventory.ItemStack domain =
        MinestomAdapters.adapt(ItemStack.of(Material.DIAMOND_SWORD, 2));

    assertEquals("minecraft:diamond_sword", domain.type().key().asString());
    assertEquals(2, domain.amount());
    assertNull(domain.displayName());
    assertNull(domain.lore());

    domain.editMeta(
        meta -> {
          meta.setDisplayName(Component.text("Sword"));
          meta.setLore(java.util.List.of(Component.text("line")));
          meta.setUnbreakable(true);
        });

    assertEquals(Component.text("Sword"), domain.displayName());
    assertEquals(java.util.List.of(Component.text("line")), domain.lore());
    assertNotNull(domain.meta());
    assertTrue(domain.meta().isUnbreakable());

    org.aincraft.api.domain.datacomponent.item.ItemLore lore =
        new org.aincraft.api.domain.datacomponent.item.ItemLore() {
          @Override
          public java.util.List<Component> lines() {
            return java.util.List.of(Component.text("component"));
          }

          @Override
          public java.util.List<Component> styledLines() {
            return lines();
          }
        };
    domain.setData(org.aincraft.api.domain.datacomponent.item.DataComponentTypes.LORE, lore);
    assertEquals(
        java.util.List.of(Component.text("component")),
        domain.getData(org.aincraft.api.domain.datacomponent.item.DataComponentTypes.LORE).lines());
    domain.setData(org.aincraft.api.domain.datacomponent.item.DataComponentTypes.UNBREAKABLE);
    assertTrue(
        domain.hasData(org.aincraft.api.domain.datacomponent.item.DataComponentTypes.UNBREAKABLE));
    assertEquals(domain, domain.clone());
    assertSame(MinestomAdapters.toMinestom(domain), MinestomAdapters.toMinestom(domain));
  }

  @Test
  void inventoryBulkOperationsUsePlatformSlots() {
    net.minestom.server.inventory.Inventory nativeInventory =
        new net.minestom.server.inventory.Inventory(
            InventoryType.CHEST_1_ROW, Component.text("Chest"));
    Inventory inventory = MinestomAdapters.adapt(nativeInventory);
    org.aincraft.api.domain.inventory.ItemStack item =
        MinestomAdapters.adapt(ItemStack.of(Material.APPLE, 3));

    assertTrue(inventory.isEmpty());
    assertEquals(0, inventory.firstEmpty());
    assertTrue(inventory.addItem(item).isEmpty());
    assertTrue(inventory.contains(item));
    assertTrue(inventory.containsAtLeast(item, 3));
    assertEquals(0, inventory.first(item));
    assertTrue(inventory.removeItem(item.withAmount(2)).isEmpty());
    assertEquals(1, inventory.getItem(0).amount());
    inventory.clear();
    assertTrue(inventory.isEmpty());
  }

  @Test
  void metadataFactoryReturnsLiveMutableMetadata() {
    org.aincraft.api.domain.inventory.ItemStack item =
        MinestomAdapters.adapt(ItemStack.of(Material.IRON_PICKAXE));
    DamageableItemMeta meta = new MinestomItemMetaWrapper((MinestomItemStackWrapper) item);
    meta.setDamage(4);
    assertEquals(4, meta.damage());
    assertTrue(meta.hasDamage());
    assertTrue(meta.maxDamage() > 0);
  }
}
