package org.aincraft.common.entity;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.aincraft.common.effect.PotionEffect;
import org.aincraft.common.effect.PotionEffectType;
import org.aincraft.common.inventory.PlayerInventory;
import org.aincraft.common.location.BoundingBox;
import org.aincraft.common.location.Location;
import org.aincraft.common.location.Position;
import org.aincraft.common.location.Vector3d;
import org.aincraft.common.world.Block;
import org.aincraft.common.world.Chunk;
import org.aincraft.common.world.Difficulty;
import org.aincraft.common.world.Environment;
import org.aincraft.common.world.GameMode;
import org.aincraft.common.world.World;
import org.aincraft.common.world.WorldBorder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntityPlayerTest {

  private static Position createPos(double x, double y, double z) {
    return new Position() {
      @Override public double x() { return x; }
      @Override public double y() { return y; }
      @Override public double z() { return z; }
    };
  }

  private static Vector3d createVec(double x, double y, double z) {
    return new Vector3d() {
      @Override public double x() { return x; }
      @Override public double y() { return y; }
      @Override public double z() { return z; }
    };
  }

  private static BoundingBox createBox(double x, double y, double z) {
    return new BoundingBox() {
      @Override public double minX() { return x - 0.3; }
      @Override public double minY() { return y; }
      @Override public double minZ() { return z - 0.3; }
      @Override public double maxX() { return x + 0.3; }
      @Override public double maxY() { return y + 1.8; }
      @Override public double maxZ() { return z + 0.3; }
    };
  }

  private static Location createLoc(World world, Position position) {
    return new Location() {
      @Override public World world() { return world; }
      @Override public Position position() { return position; }
      @Override public float yaw() { return 0f; }
      @Override public float pitch() { return 0f; }
    };
  }

  private static World createTestWorld(String name) {
    UUID uid = UUID.nameUUIDFromBytes(name.getBytes());
    Key key = Key.key("test", name);
    return new World() {
      @Override public UUID uid() { return uid; }
      @Override public String name() { return name; }
      @Override public Key key() { return key; }
      @Override public Block getBlockAt(int x, int y, int z) { throw new UnsupportedOperationException(); }
      @Override public Chunk getChunkAt(int chunkX, int chunkZ) { throw new UnsupportedOperationException(); }
      @Override public boolean isChunkLoaded(int chunkX, int chunkZ) { return false; }
      @Override public int minHeight() { return 0; }
      @Override public int maxHeight() { return 256; }
      @Override public WorldBorder worldBorder() { return null; }
      @Override public Environment environment() { return Environment.NORMAL; }
      @Override public Difficulty difficulty() { return Difficulty.NORMAL; }
      @Override public long time() { return 0; }
      @Override public long fullTime() { return 0; }
      @Override public Collection<? extends Player> players() { return java.util.List.of(); }
      @Override public Collection<? extends Entity> entities() { return java.util.List.of(); }
      @Override public Collection<? extends Chunk> loadedChunks() { return java.util.List.of(); }
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

    Player player = new Player() {
      @Override public UUID uniqueId() { return uuid; }
      @Override public String username() { return "Steve"; }
      @Override public boolean isOnline() { return true; }
      @Override public int ping() { return 15; }
      @Override public double health() { return 20.0; }
      @Override public void setHealth(double health) {}
      @Override public double maxHealth() { return 20.0; }
      @Override public void damage(double amount) {}
      @Override public void damage(double amount, Entity source) {}
      @Override public double eyeHeight() { return 1.62; }
      @Override public Location eyeLocation() { return location; }
      @Override public boolean hasLineOfSight(Entity other) { return true; }
      @Override public LivingEntity target() { return null; }
      @Override public void setTarget(LivingEntity target) {}
      @Override public boolean isGliding() { return false; }
      @Override public boolean isSwimming() { return false; }
      @Override public boolean isSleeping() { return false; }
      @Override public Collection<? extends PotionEffect> activePotionEffects() { return java.util.List.of(); }
      @Override public void addPotionEffect(PotionEffect effect) {}
      @Override public void removePotionEffect(PotionEffectType type) {}
      @Override public boolean hasPotionEffect(PotionEffectType type) { return false; }
      @Override public int foodLevel() { return 20; }
      @Override public void setFoodLevel(int foodLevel) {}
      @Override public float saturation() { return 5.0f; }
      @Override public void setSaturation(float saturation) {}
      @Override public int level() { return 30; }
      @Override public void setLevel(int level) {}
      @Override public float exp() { return 0.5f; }
      @Override public void setExp(float exp) {}
      @Override public GameMode gameMode() { return GameMode.SURVIVAL; }
      @Override public void setGameMode(GameMode gameMode) {}
      @Override public boolean isSneaking() { return false; }
      @Override public void setSneaking(boolean sneaking) {}
      @Override public boolean isSprinting() { return true; }
      @Override public void setSprinting(boolean sprinting) {}
      @Override public boolean isFlying() { return false; }
      @Override public void setFlying(boolean flying) {}
      @Override public PlayerInventory inventory() { return null; }
      @Override public void kick(Component reason) { kicked.set(true); }
      @Override public World world() { return world; }
      @Override public Location location() { return location; }
      @Override public Position position() { return pos; }
      @Override public Key type() { return playerType; }
      @Override public boolean isValid() { return true; }
      @Override public boolean isDead() { return false; }
      @Override public BoundingBox boundingBox() { return createBox(pos.x(), pos.y(), pos.z()); }
      @Override public Vector3d velocity() { return createVec(0, 0, 0); }
      @Override public boolean isOnGround() { return true; }
      @Override public void teleport(Location targetLocation) {}
      @Override public void remove() {}
      @Override public void sendMessage(Component message) { messageSent.set(true); }
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

    player.sendMessage(Component.text("Hello World"));
    assertTrue(messageSent.get());

    player.kick(Component.text("Bye"));
    assertTrue(kicked.get());
  }
}
