package org.aincraft;

import static org.junit.jupiter.api.Assertions.*;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.world.DimensionType;
import org.aincraft.api.domain.block.BlockFace;
import org.aincraft.api.domain.entity.Player;
import org.aincraft.api.domain.location.BoundingBox;
import org.aincraft.api.domain.location.Position;
import org.aincraft.api.domain.world.World;
import org.aincraft.bukkit.adapter.BukkitAdapters;
import org.aincraft.minestom.adapter.MinestomAdapters;
import org.aincraft.paper.adapter.PaperAdapters;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class CrossPlatformApiEquivalenceTest {

  @BeforeAll
  static void initMinestom() {
    MinecraftServer.init();
  }

  // --- Platform-Agnostic Consumer Logic written against :utilities-api only ---

  /** Platform-agnostic spatial check: determines if a point is within a protected region */
  private boolean isInsideSafeZone(BoundingBox safeZone, Position target) {
    return safeZone.contains(target.x(), target.y(), target.z());
  }

  /** Platform-agnostic audience dispatch check */
  private void sendWelcomeNotification(Player player, String message) {
    player.sendMessage(Component.text("Welcome " + player.username() + ": " + message));
  }

  // --- Tests verifying identical behavior across Paper and Minestom ---

  @Test
  void testSpatialCalculationEquivalenceAcrossPlatforms() {
    // 1. Paper / Bukkit source objects covering [-10..10, 0..20, -10..10]
    org.bukkit.util.BoundingBox bukkitBox =
        new org.bukkit.util.BoundingBox(-10.0, 0.0, -10.0, 10.0, 20.0, 10.0);
    Vector bukkitPosInside = new Vector(0.0, 5.0, 0.0);
    Vector bukkitPosOutside = new Vector(50.0, 5.0, 50.0);

    BoundingBox paperDomainBox = BukkitAdapters.adapt(bukkitBox);
    Position paperDomainPosInside = BukkitAdapters.adapt(bukkitPosInside);
    Position paperDomainPosOutside = BukkitAdapters.adapt(bukkitPosOutside);

    // 2. Minestom source objects: width=20, height=20, depth=20 -> [-10..10, 0..20, -10..10]
    net.minestom.server.collision.BoundingBox minestomBox =
        new net.minestom.server.collision.BoundingBox(20.0, 20.0, 20.0);
    Vec minestomPosInside = new Vec(0.0, 5.0, 0.0);
    Vec minestomPosOutside = new Vec(50.0, 5.0, 50.0);

    BoundingBox minestomDomainBox = MinestomAdapters.adapt(minestomBox);
    Position minestomDomainPosInside = MinestomAdapters.adapt(minestomPosInside);
    Position minestomDomainPosOutside = MinestomAdapters.adapt(minestomPosOutside);

    // 3. Execute the exact same consumer logic on both
    assertTrue(
        isInsideSafeZone(paperDomainBox, paperDomainPosInside), "Paper point inside safe zone");
    assertFalse(
        isInsideSafeZone(paperDomainBox, paperDomainPosOutside), "Paper point outside safe zone");

    assertTrue(
        isInsideSafeZone(minestomDomainBox, minestomDomainPosInside),
        "Minestom point inside safe zone");
    assertFalse(
        isInsideSafeZone(minestomDomainBox, minestomDomainPosOutside),
        "Minestom point outside safe zone");

    // Coordinate parity check
    assertEquals(paperDomainPosInside.blockX(), minestomDomainPosInside.blockX());
    assertEquals(paperDomainPosInside.blockY(), minestomDomainPosInside.blockY());
    assertEquals(paperDomainPosInside.blockZ(), minestomDomainPosInside.blockZ());

    // Distance calculation parity check
    assertEquals(
        paperDomainPosInside.distance(0, 0, 0), minestomDomainPosInside.distance(0, 0, 0), 1e-6);
  }

  @Test
  void testDirectionalOffsetEquivalenceAcrossPlatforms() {
    Position paperBase = BukkitAdapters.adapt(new Vector(10.2, 64.0, -15.8));
    Position minestomBase = MinestomAdapters.adapt(new Vec(10.2, 64.0, -15.8));

    for (BlockFace face : BlockFace.values()) {
      int paperX = paperBase.blockX() + face.modX();
      int minestomX = minestomBase.blockX() + face.modX();
      int paperY = paperBase.blockY() + face.modY();
      int minestomY = minestomBase.blockY() + face.modY();
      int paperZ = paperBase.blockZ() + face.modZ();
      int minestomZ = minestomBase.blockZ() + face.modZ();

      assertEquals(paperX, minestomX, "X offset matches for " + face);
      assertEquals(paperY, minestomY, "Y offset matches for " + face);
      assertEquals(paperZ, minestomZ, "Z offset matches for " + face);
    }
  }

  @Test
  void testAudienceNotificationEquivalenceAcrossPlatforms() {
    UUID playerUid = UUID.randomUUID();
    AtomicReference<Component> paperReceived = new AtomicReference<>();

    // Paper player mock
    org.bukkit.entity.Player bukkitPlayer =
        (org.bukkit.entity.Player)
            Proxy.newProxyInstance(
                org.bukkit.entity.Player.class.getClassLoader(),
                new Class<?>[] {org.bukkit.entity.Player.class},
                (proxy, method, args) -> {
                  if (method.getName().equals("sendMessage")
                      && args.length > 0
                      && args[0] instanceof Component c) {
                    paperReceived.set(c);
                    return null;
                  }
                  return switch (method.getName()) {
                    case "getUniqueId" -> playerUid;
                    case "getName" -> "Alex";
                    case "isOnline" -> true;
                    case "getType" -> EntityType.PLAYER;
                    default -> null;
                  };
                });

    Player paperDomainPlayer = PaperAdapters.adapt(bukkitPlayer);
    sendWelcomeNotification(paperDomainPlayer, "Welcome to the server!");

    assertNotNull(paperReceived.get());
    assertEquals(
        "Welcome Alex: Welcome to the server!",
        PlainTextComponentSerializer.plainText().serialize(paperReceived.get()));

    // Minestom player mock / instance
    AtomicReference<Component> minestomReceived = new AtomicReference<>();
    PlayerConnection dummyConn =
        new net.minestom.server.network.player.PlayerConnection() {
          @Override
          public void sendPacket(net.minestom.server.network.packet.server.SendablePacket packet) {
            if (packet
                instanceof net.minestom.server.network.packet.server.play.SystemChatPacket chat) {
              minestomReceived.set(chat.message());
            }
          }

          @Override
          public java.net.SocketAddress getRemoteAddress() {
            return new java.net.InetSocketAddress("127.0.0.1", 25565);
          }
        };

    net.minestom.server.entity.Player minestomPlayer =
        new net.minestom.server.entity.Player(dummyConn, new GameProfile(playerUid, "Alex"));

    Player minestomDomainPlayer = MinestomAdapters.adapt(minestomPlayer);
    sendWelcomeNotification(minestomDomainPlayer, "Welcome to the server!");

    assertNotNull(minestomReceived.get());
    assertEquals(
        "Welcome Alex: Welcome to the server!",
        PlainTextComponentSerializer.plainText().serialize(minestomReceived.get()));

    // Confirm message content parity across both platforms
    assertEquals(
        PlainTextComponentSerializer.plainText().serialize(paperReceived.get()),
        PlainTextComponentSerializer.plainText().serialize(minestomReceived.get()));
  }

  @Test
  void testWorldCoordinateAndKeyEquivalence() {
    UUID worldId = UUID.randomUUID();

    // 1. Paper World
    org.bukkit.World bukkitWorld =
        (org.bukkit.World)
            Proxy.newProxyInstance(
                org.bukkit.World.class.getClassLoader(),
                new Class<?>[] {org.bukkit.World.class},
                (proxy, method, args) -> {
                  return switch (method.getName()) {
                    case "getUID" -> worldId;
                    case "getName" -> "overworld";
                    case "getKey" -> NamespacedKey.minecraft(worldId.toString());
                    default -> null;
                  };
                });
    World paperDomainWorld = PaperAdapters.adapt(bukkitWorld);

    // 2. Minestom Instance
    InstanceContainer minestomInstance = new InstanceContainer(worldId, DimensionType.OVERWORLD);
    World minestomDomainWorld = MinestomAdapters.adapt(minestomInstance);

    // Both satisfy :utilities-api World contract identically
    assertEquals(worldId, paperDomainWorld.uid());
    assertEquals(worldId, minestomDomainWorld.uid());

    assertEquals("minecraft:" + worldId, paperDomainWorld.key().asString());
    assertEquals("minecraft:" + worldId, minestomDomainWorld.key().asString());
  }
}
