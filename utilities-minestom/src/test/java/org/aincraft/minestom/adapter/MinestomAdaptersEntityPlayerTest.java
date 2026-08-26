package org.aincraft.minestom.adapter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.world.DimensionType;
import org.aincraft.common.entity.Entity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MinestomAdaptersEntityPlayerTest {

  @BeforeAll
  static void setup() {
    MinecraftServer.init();
  }

  @Test
  void testPlayerWrapperDelegationAndAudience() {
    Instance instance = new InstanceContainer(UUID.randomUUID(), DimensionType.OVERWORLD);
    UUID uuid = UUID.randomUUID();
    GameProfile profile = new GameProfile(uuid, "Steve");
    PlayerConnection conn = new PlayerConnection() {
      @Override public void sendPacket(net.minestom.server.network.packet.server.SendablePacket packet) {}
      @Override public java.net.SocketAddress getRemoteAddress() { return new java.net.InetSocketAddress("127.0.0.1", 25565); }
    };
    Player minestomPlayer = new Player(conn, profile);
    minestomPlayer.setInstance(instance, new Pos(0, 64, 0));

    org.aincraft.common.entity.Player player = MinestomAdapters.adapt(minestomPlayer);
    assertEquals("Steve", player.username());
    assertEquals("Steve", player.name());
    assertEquals(uuid, player.uniqueId());
    assertNotNull(player.inventory());
    assertEquals(org.aincraft.common.world.GameMode.SURVIVAL, player.gameMode());

    player.setGameMode(org.aincraft.common.world.GameMode.CREATIVE);
    assertEquals(GameMode.CREATIVE, minestomPlayer.getGameMode());

    player.setFlying(true);
    assertTrue(minestomPlayer.isFlying());

    player.setSneaking(true);
    assertTrue(minestomPlayer.isSneaking());

    player.setSprinting(true);
    assertTrue(minestomPlayer.isSprinting());

    player.setFoodLevel(15);
    assertEquals(15, minestomPlayer.getFood());

    player.setSaturation(4.5f);
    assertEquals(4.5f, minestomPlayer.getFoodSaturation());

    player.setLevel(25);
    assertEquals(25, minestomPlayer.getLevel());

    player.setExp(0.8f);
    assertEquals(0.8f, minestomPlayer.getExp());

    Component msg = Component.text("Welcome!");
    assertDoesNotThrow(() -> player.sendMessage(msg));
    assertDoesNotThrow(() -> player.sendActionBar(msg));

    assertSame(minestomPlayer, MinestomAdapters.toMinestom(player));
    assertSame(minestomPlayer, MinestomAdapters.toMinestom((Entity) player));
  }
}
