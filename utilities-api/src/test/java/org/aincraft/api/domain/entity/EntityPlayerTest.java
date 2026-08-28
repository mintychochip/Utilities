package org.aincraft.api.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.aincraft.api.domain.effect.Particle;
import org.aincraft.api.domain.effect.PotionEffect;
import org.aincraft.api.domain.effect.PotionEffectType;
import org.aincraft.api.domain.inventory.EntityEquipment;
import org.aincraft.api.domain.inventory.PlayerInventory;
import org.aincraft.api.domain.location.BoundingBox;
import org.aincraft.api.domain.location.Location;
import org.aincraft.api.domain.location.Position;
import org.aincraft.api.domain.world.Block;
import org.aincraft.api.domain.world.Chunk;
import org.aincraft.api.domain.world.Difficulty;
import org.aincraft.api.domain.world.Environment;
import org.aincraft.api.domain.world.GameMode;
import org.aincraft.api.domain.world.World;
import org.aincraft.api.domain.world.WorldBorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

class EntityPlayerTest {

  private static Position createPos(double x, double y, double z) {
    return new Position() {
      @Override
      public double x() {
        return x;
      }

      @Override
      public double y() {
        return y;
      }

      @Override
      public double z() {
        return z;
      }
    };
  }

  private static Vector3dc createVec(double x, double y, double z) {
    return new Vector3d(x, y, z);
  }

  private static BoundingBox createBox(double x, double y, double z) {
    return new BoundingBox() {
      @Override
      public double minX() {
        return x - 0.3;
      }

      @Override
      public double minY() {
        return y;
      }

      @Override
      public double minZ() {
        return z - 0.3;
      }

      @Override
      public double maxX() {
        return x + 0.3;
      }

      @Override
      public double maxY() {
        return y + 1.8;
      }

      @Override
      public double maxZ() {
        return z + 0.3;
      }
    };
  }

  private static Location createLoc(World world, Position position) {
    return new Location() {
      @Override
      public @NotNull World world() {
        return world;
      }

      @Override
      public @NotNull Position position() {
        return position;
      }

      @Override
      public float yaw() {
        return 0;
      }

      @Override
      public float pitch() {
        return 0;
      }

      @Override
      public @NotNull org.joml.Vector3dc direction() {
        return new org.joml.Vector3d(0, 0, 1);
      }

      @Override
      public long toBlockKey() {
        return 0L;
      }

      @Override
      public boolean isChunkLoaded() {
        return false;
      }

      @Override
      public java.util.Collection<? extends LivingEntity> nearbyLivingEntities(
          double xRadius, double yRadius, double zRadius) {
        return java.util.List.of();
      }

      @Override
      public java.util.Collection<? extends Player> nearbyPlayers(
          double xRadius, double yRadius, double zRadius) {
        return java.util.List.of();
      }

      @Override
      public java.util.Collection<? extends Player> nearbyPlayers(double radius) {
        return java.util.List.of();
      }

      @Override
      public java.util.Collection<? extends Entity> nearbyEntities(
          double xRadius, double yRadius, double zRadius) {
        return java.util.List.of();
      }

      @Override
      public java.util.Collection<? extends Entity> nearbyEntities(double radius) {
        return java.util.List.of();
      }

      @Override
      public Location toCenterLocation() {
        return this;
      }

      @Override
      public Location toBlockLocation() {
        return this;
      }

      @Override
      public Location withRotation(float yaw, float pitch) {
        return this;
      }

      @Override
      public Location withOffset(@NotNull Vector3dc offset) {
        return this;
      }

      @Override
      public Location withOffset(double dx, double dy, double dz) {
        return this;
      }
    };
  }

  private static World createTestWorld(String name) {
    return new World() {
      @Override
      public @NotNull UUID uid() {
        return UUID.randomUUID();
      }

      @Override
      public @NotNull String name() {
        return name;
      }

      @Override
      public @NotNull Block getBlockAt(int x, int y, int z) {
        return null;
      }

      @Override
      public @NotNull Chunk getChunkAt(int chunkX, int chunkZ) {
        return null;
      }

      @Override
      public boolean isChunkLoaded(int chunkX, int chunkZ) {
        return false;
      }

      @Override
      public int minHeight() {
        return -64;
      }

      @Override
      public int maxHeight() {
        return 320;
      }

      @Override
      public @NotNull WorldBorder worldBorder() {
        return null;
      }

      @Override
      public @NotNull Environment environment() {
        return Environment.NORMAL;
      }

      @Override
      public @NotNull Difficulty difficulty() {
        return Difficulty.NORMAL;
      }

      @Override
      public long time() {
        return 0;
      }

      @Override
      public long fullTime() {
        return 0;
      }

      @Override
      public @NotNull Collection<? extends org.aincraft.api.domain.entity.Player> players() {
        return List.of();
      }

      @Override
      public @NotNull Collection<? extends Entity> entities() {
        return List.of();
      }

      @Override
      public @NotNull Collection<? extends Chunk> loadedChunks() {
        return List.of();
      }

      @Override
      public void playSound(
          @NotNull Location location,
          @NotNull Sound.Type sound,
          @Nullable Sound.Source source,
          float volume,
          float pitch) {
        throw new UnsupportedOperationException();
      }

      @Override
      public void spawnParticle(
          @NotNull Particle particle,
          @NotNull Location location,
          int count,
          double offsetX,
          double offsetY,
          double offsetZ,
          double extra) {
        throw new UnsupportedOperationException();
      }

      @Override
      public @NotNull Key key() {
        return Key.key("minecraft", name);
      }
    };
  }

  @Test
  void testEntityAndPlayerContract() {
    UUID uuid = UUID.randomUUID();
    World world = createTestWorld("overworld");
    Position pos = createPos(10, 64, 10);
    Location location = createLoc(world, pos);
    Key playerType = Key.key("minecraft", "player");
    AtomicBoolean messageSent = new AtomicBoolean(false);
    AtomicBoolean kicked = new AtomicBoolean(false);
    AtomicReference<Component> customName = new AtomicReference<>();

    Player player =
        new Player() {
          @Override
          public UUID uniqueId() {
            return uuid;
          }

          @Override
          public String username() {
            return "Steve";
          }

          @Override
          public Component displayName() {
            return Component.text(username());
          }

          @Override
          public void displayName(Component displayName) {}

          @Override
          public boolean isOnline() {
            return true;
          }

          @Override
          public int ping() {
            return 15;
          }

          @Override
          public double health() {
            return 20.0;
          }

          @Override
          public void setHealth(double health) {}

          @Override
          public double maxHealth() {
            return 20.0;
          }

          @Override
          public double absorptionAmount() {
            return 0.0;
          }

          @Override
          public void setAbsorptionAmount(double amount) {}

          @Override
          public void kill() {}

          @Override
          public void damage(double amount) {}

          @Override
          public org.aincraft.api.domain.attribute.AttributeInstance getAttribute(
              net.kyori.adventure.key.Key attribute) {
            return null;
          }

          @Override
          public void damage(double amount, Entity source) {}

          @Override
          public double eyeHeight() {
            return 1.62;
          }

          @Override
          public Location eyeLocation() {
            return location;
          }

          @Override
          public boolean hasLineOfSight(Entity other) {
            return true;
          }

          @Override
          public LivingEntity target() {
            return null;
          }

          @Override
          public void setTarget(LivingEntity target) {}

          @Override
          public boolean isGliding() {
            return false;
          }

          @Override
          public boolean isSwimming() {
            return false;
          }

          @Override
          public boolean isSleeping() {
            return false;
          }

          @Override
          public boolean isInvisible() {
            return false;
          }

          @Override
          public void setInvisible(boolean invisible) {}

          @Override
          public EntityEquipment equipment() {
            return null;
          }

          @Override
          public void attack(Entity target) {}

          @Override
          public void swingMainHand() {}

          @Override
          public void swingOffHand() {}

          @Override
          public PotionEffect potionEffect(PotionEffectType type) {
            return null;
          }

          @Override
          public Collection<? extends PotionEffect> activePotionEffects() {
            return List.of();
          }

          @Override
          public void addPotionEffect(PotionEffect effect) {}

          @Override
          public boolean addPotionEffect(PotionEffect effect, boolean force) {
            return true;
          }

          @Override
          public boolean clearActivePotionEffects() {
            return false;
          }

          @Override
          public void removePotionEffect(PotionEffectType type) {}

          @Override
          public boolean hasPotionEffect(PotionEffectType type) {
            return false;
          }

          @Override
          public int foodLevel() {
            return 20;
          }

          @Override
          public void setFoodLevel(int foodLevel) {}

          @Override
          public float saturation() {
            return 5.0f;
          }

          @Override
          public void setSaturation(float saturation) {}

          @Override
          public int level() {
            return 30;
          }

          @Override
          public void setLevel(int level) {}

          @Override
          public float exp() {
            return 0.5f;
          }

          @Override
          public void setExp(float exp) {}

          @Override
          public GameMode gameMode() {
            return GameMode.SURVIVAL;
          }

          @Override
          public void setGameMode(GameMode gameMode) {}

          @Override
          public boolean isSneaking() {
            return false;
          }

          @Override
          public void setSneaking(boolean sneaking) {}

          @Override
          public boolean isSprinting() {
            return true;
          }

          @Override
          public void setSprinting(boolean sprinting) {}

          @Override
          public boolean isFlying() {
            return false;
          }

          @Override
          public void setFlying(boolean flying) {}

          @Override
          public boolean allowFlight() {
            return false;
          }

          @Override
          public void setAllowFlight(boolean allow) {}

          @Override
          public boolean hasPermission(String permission) {
            return true;
          }

          @Override
          public boolean isOp() {
            return false;
          }

          @Override
          public void setOp(boolean op) {}

          @Override
          public PlayerInventory inventory() {
            return null;
          }

          @Override
          public void kick(Component reason) {
            kicked.set(true);
          }

          @Override
          public World world() {
            return world;
          }

          @Override
          public Location location() {
            return location;
          }

          @Override
          public Position position() {
            return pos;
          }

          @Override
          public float yaw() {
            return 0;
          }

          @Override
          public float pitch() {
            return 0;
          }

          @Override
          public void setRotation(float yaw, float pitch) {}

          @Override
          public Key type() {
            return playerType;
          }

          @Override
          public boolean isValid() {
            return true;
          }

          @Override
          public boolean isDead() {
            return false;
          }

          @Override
          public BoundingBox boundingBox() {
            return createBox(pos.x(), pos.y(), pos.z());
          }

          @Override
          public Vector3dc velocity() {
            return createVec(0, 0, 0);
          }

          @Override
          public void setVelocity(Vector3dc velocity) {}

          @Override
          public boolean isOnGround() {
            return true;
          }

          @Override
          public Collection<? extends Entity> nearbyEntities(double x, double y, double z) {
            return List.of();
          }

          @Override
          public List<? extends Entity> passengers() {
            return List.of();
          }

          @Override
          public boolean addPassenger(Entity passenger) {
            return false;
          }

          @Override
          public boolean removePassenger(Entity passenger) {
            return false;
          }

          @Override
          public boolean eject() {
            return false;
          }

          @Override
          public boolean isInsideVehicle() {
            return false;
          }

          @Override
          public boolean leaveVehicle() {
            return false;
          }

          @Override
          public Entity vehicle() {
            return null;
          }

          @Override
          public boolean isGlowing() {
            return false;
          }

          @Override
          public void setGlowing(boolean glowing) {}

          @Override
          public boolean isInvulnerable() {
            return false;
          }

          @Override
          public void setInvulnerable(boolean invulnerable) {}

          @Override
          public boolean isCustomNameVisible() {
            return false;
          }

          @Override
          public void setCustomNameVisible(boolean visible) {}

          @Override
          public Component customName() {
            return customName.get();
          }

          @Override
          public void customName(Component name) {
            customName.set(name);
          }

          @Override
          public void teleport(Location targetLocation) {}

          @Override
          public void remove() {}

          @Override
          public void sendMessage(Component message) {
            messageSent.set(true);
          }

          @Override
          public void setBedSpawnLocation(@Nullable Location location, boolean force) {}

          @Override
          public @Nullable Location bedSpawnLocation() {
            return null;
          }

          @Override
          public float exhaustion() {
            return 0f;
          }

          @Override
          public @Nullable org.aincraft.api.domain.inventory.Inventory enderChest() {
            return null;
          }

          @Override
          public int entityId() {
            return 0;
          }

          @Override
          public double width() {
            return 0.6;
          }

          @Override
          public void setItemOnCursor(@Nullable org.aincraft.api.domain.inventory.ItemStack item) {}

          @Override
          public @Nullable org.aincraft.api.domain.inventory.ItemStack itemOnCursor() {
            return null;
          }

          @Override
          public void setExhaustion(float exhaustion) {}

          @Override
          public double height() {
            return 1.8;
          }
        };

    assertEquals(uuid, player.uniqueId());
    assertEquals(uuid, player.identity().uuid());
    assertEquals("Steve", player.username());
    assertTrue(player.isOnline());
    assertEquals(15, player.ping());
    assertEquals(20.0, player.health());
    assertEquals(GameMode.SURVIVAL, player.gameMode());
    assertTrue(player.isSprinting());
    assertFalse(player.isFlying());
    assertSame(world, player.world());
    assertEquals(playerType, player.type());
    assertEquals(playerType, player.key());
    assertTrue(player.isValid());
    assertFalse(player.isDead());
    assertSame(location, player.location());
    assertEquals(10.0, player.x());
    assertEquals(64.0, player.y());
    assertEquals(10.0, player.z());
    assertFalse(player.hasCustomName());

    player.customName(Component.text("Named"));
    assertTrue(player.hasCustomName());

    player.sendMessage(Component.text("Hello World"));
    assertTrue(messageSent.get());

    player.kick(Component.text("Bye"));
    assertTrue(kicked.get());
  }
}
