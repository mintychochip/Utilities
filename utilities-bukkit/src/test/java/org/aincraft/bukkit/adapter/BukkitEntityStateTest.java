package org.aincraft.bukkit.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import net.kyori.adventure.text.Component;
import org.aincraft.common.entity.Entity;
import org.aincraft.common.entity.LivingEntity;
import org.aincraft.common.entity.Player;
import org.aincraft.common.inventory.EntityEquipment;
import org.aincraft.common.location.Vector3d;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BukkitEntityStateTest {

  @Mock
  org.bukkit.World bukkitWorld;

  @Mock
  org.bukkit.entity.LivingEntity bukkitLiving;

  @Mock
  org.bukkit.entity.Player bukkitPlayer;

  @Mock
  org.bukkit.inventory.EntityEquipment bukkitEquipment;

  @Test
  void testEntityVelocityAndRotation() {
    when(bukkitLiving.getWorld()).thenReturn(bukkitWorld);
    when(bukkitLiving.getLocation()).thenReturn(new Location(bukkitWorld, 0, 64, 0));
    when(bukkitLiving.getVelocity()).thenReturn(new Vector(1, 2, 3));
    when(bukkitLiving.getType()).thenReturn(org.bukkit.entity.EntityType.ZOMBIE);

    LivingEntity entity = BukkitAdapters.adapt(bukkitLiving);
    Vector3d velocity = entity.velocity();
    assertEquals(1.0, velocity.x(), 1e-6);
    assertEquals(2.0, velocity.y(), 1e-6);
    assertEquals(3.0, velocity.z(), 1e-6);

    entity.setVelocity(new Vector3d() {
      @Override public double x() { return 4; }
      @Override public double y() { return 5; }
      @Override public double z() { return 6; }
    });
    verify(bukkitLiving).setVelocity(argThat(v ->
        v.getX() == 4.0 && v.getY() == 5.0 && v.getZ() == 6.0));

    entity.setRotation(90.0f, 45.0f);
    verify(bukkitLiving).setRotation(90.0f, 45.0f);
  }

  @Test
  void testEntityGlowingAndInvulnerable() {
    when(bukkitLiving.getWorld()).thenReturn(bukkitWorld);
    when(bukkitLiving.getLocation()).thenReturn(new Location(bukkitWorld, 0, 64, 0));
    when(bukkitLiving.getType()).thenReturn(org.bukkit.entity.EntityType.ZOMBIE);

    LivingEntity entity = BukkitAdapters.adapt(bukkitLiving);

    when(bukkitLiving.isGlowing()).thenReturn(true);
    assertTrue(entity.isGlowing());

    entity.setGlowing(false);
    verify(bukkitLiving).setGlowing(false);

    when(bukkitLiving.isInvulnerable()).thenReturn(false);
    assertFalse(entity.isInvulnerable());

    entity.setInvulnerable(true);
    verify(bukkitLiving).setInvulnerable(true);
  }

  @Test
  void testCustomNameRoundTrip() {
    when(bukkitLiving.getWorld()).thenReturn(bukkitWorld);
    when(bukkitLiving.getLocation()).thenReturn(new Location(bukkitWorld, 0, 64, 0));
    when(bukkitLiving.getType()).thenReturn(org.bukkit.entity.EntityType.ZOMBIE);

    LivingEntity entity = BukkitAdapters.adapt(bukkitLiving);

    when(bukkitLiving.getCustomName()).thenReturn("§aNamed");
    assertEquals("Named", ((net.kyori.adventure.text.TextComponent) entity.customName()).content());

    entity.customName(Component.text("Hello"));
    verify(bukkitLiving).setCustomName("Hello");
  }

  @Test
  void testLivingEntityEquipment() {
    when(bukkitLiving.getWorld()).thenReturn(bukkitWorld);
    when(bukkitLiving.getLocation()).thenReturn(new Location(bukkitWorld, 0, 64, 0));
    when(bukkitLiving.getType()).thenReturn(org.bukkit.entity.EntityType.ZOMBIE);
    when(bukkitLiving.getEquipment()).thenReturn(bukkitEquipment);

    LivingEntity entity = BukkitAdapters.adapt(bukkitLiving);
    EntityEquipment equipment = entity.equipment();
    assertNotNull(equipment);

    org.bukkit.inventory.ItemStack helmet = new org.bukkit.inventory.ItemStack(org.bukkit.Material.DIAMOND_HELMET);
    when(bukkitEquipment.getItem(EquipmentSlot.HEAD)).thenReturn(helmet);

    assertNotNull(equipment.helmet());
    assertEquals("minecraft:diamond_helmet", equipment.helmet().type().key().asString());
  }

  @Test
  void testLivingEntityPotionAndInvisible() {
    when(bukkitLiving.getWorld()).thenReturn(bukkitWorld);
    when(bukkitLiving.getLocation()).thenReturn(new Location(bukkitWorld, 0, 64, 0));
    when(bukkitLiving.getType()).thenReturn(org.bukkit.entity.EntityType.ZOMBIE);

    LivingEntity entity = BukkitAdapters.adapt(bukkitLiving);

    when(bukkitLiving.isInvisible()).thenReturn(true);
    assertTrue(entity.isInvisible());
    entity.setInvisible(false);
    verify(bukkitLiving).setInvisible(false);

    entity.swingMainHand();
    verify(bukkitLiving).swingMainHand();

    entity.swingOffHand();
    verify(bukkitLiving).swingOffHand();
  }

  @Test
  void testPlayerDisplayNameAndFlight() {
    when(bukkitPlayer.getWorld()).thenReturn(bukkitWorld);
    when(bukkitPlayer.getLocation()).thenReturn(new Location(bukkitWorld, 0, 64, 0));
    when(bukkitPlayer.getType()).thenReturn(org.bukkit.entity.EntityType.PLAYER);

    Player player = BukkitAdapters.adapt(bukkitPlayer);

    when(bukkitPlayer.getDisplayName()).thenReturn("§bDisplay");
    assertEquals("Display", ((net.kyori.adventure.text.TextComponent) player.displayName()).content());

    player.displayName(Component.text("NewName"));
    verify(bukkitPlayer).setDisplayName("NewName");

    when(bukkitPlayer.getAllowFlight()).thenReturn(true);
    assertTrue(player.allowFlight());

    player.setAllowFlight(false);
    verify(bukkitPlayer).setAllowFlight(false);
  }

  @Test
  void testPlayerVehicleAndPassenger() {
    org.bukkit.entity.Entity vehicle = mock(org.bukkit.entity.Entity.class);
    when(vehicle.getWorld()).thenReturn(bukkitWorld);
    when(vehicle.getLocation()).thenReturn(new Location(bukkitWorld, 1, 64, 1));
    when(vehicle.getType()).thenReturn(org.bukkit.entity.EntityType.OAK_BOAT);

    when(bukkitPlayer.getWorld()).thenReturn(bukkitWorld);
    when(bukkitPlayer.getLocation()).thenReturn(new Location(bukkitWorld, 0, 64, 0));
    when(bukkitPlayer.getType()).thenReturn(org.bukkit.entity.EntityType.PLAYER);
    when(bukkitPlayer.getVehicle()).thenReturn(vehicle);

    Player player = BukkitAdapters.adapt(bukkitPlayer);
    Entity v = player.vehicle();
    assertNotNull(v);
    assertEquals("minecraft:oak_boat", v.type().asString());

    when(bukkitPlayer.isInsideVehicle()).thenReturn(true);
    assertTrue(player.isInsideVehicle());

    player.leaveVehicle();
    verify(bukkitPlayer).leaveVehicle();
  }
}
