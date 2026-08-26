package org.aincraft.paper.adapter;

import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.aincraft.common.entity.Player;
import org.aincraft.common.server.Server;
import org.aincraft.common.world.World;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaperAdaptersTest {

  @Test
  void testPaperPlayerWrapper() {
    UUID uuid = UUID.randomUUID();
    AtomicBoolean sentMessage = new AtomicBoolean(false);
    AtomicBoolean sentActionBar = new AtomicBoolean(false);
    AtomicBoolean shownTitle = new AtomicBoolean(false);
    AtomicBoolean clearedTitle = new AtomicBoolean(false);
    AtomicBoolean resetTitle = new AtomicBoolean(false);
    AtomicBoolean kicked = new AtomicBoolean(false);

    org.bukkit.World bWorld = (org.bukkit.World) Proxy.newProxyInstance(
        org.bukkit.World.class.getClassLoader(),
        new Class<?>[]{org.bukkit.World.class},
        (proxy, method, args) -> switch (method.getName()) {
          case "getUID" -> UUID.randomUUID();
          case "getName" -> "world";
          case "getKey" -> NamespacedKey.minecraft("overworld");
          case "hashCode" -> 1;
          case "equals" -> proxy == args[0];
          default -> null;
        }
    );

    org.bukkit.entity.Player bPlayer = (org.bukkit.entity.Player) Proxy.newProxyInstance(
        org.bukkit.entity.Player.class.getClassLoader(),
        new Class<?>[]{org.bukkit.entity.Player.class},
        (proxy, method, args) -> switch (method.getName()) {
          case "getUniqueId" -> uuid;
          case "getName" -> "Steve";
          case "isOnline" -> true;
          case "getPing" -> 20;
          case "getHealth" -> 20.0;
          case "getFoodLevel" -> 20;
          case "getSaturation" -> 5.0f;
          case "getLevel" -> 10;
          case "getExp" -> 0.5f;
          case "getGameMode" -> GameMode.SURVIVAL;
          case "getWorld" -> bWorld;
          case "getLocation" -> new Location(bWorld, 0, 64, 0);
          case "getType" -> EntityType.PLAYER;
          case "getAttribute" -> null;
          case "getInventory" -> null;
          case "sendMessage" -> {
            sentMessage.set(true);
            yield null;
          }
          case "sendActionBar" -> {
            sentActionBar.set(true);
            yield null;
          }
          case "showTitle" -> {
            shownTitle.set(true);
            yield null;
          }
          case "clearTitle" -> {
            clearedTitle.set(true);
            yield null;
          }
          case "resetTitle" -> {
            resetTitle.set(true);
            yield null;
          }
          case "kick" -> {
            kicked.set(true);
            yield null;
          }
          case "hashCode" -> uuid.hashCode();
          case "equals" -> proxy == args[0];
          default -> null;
        }
    );

    Player player = PaperAdapters.adapt(bPlayer);
    assertTrue(player instanceof PaperPlayerWrapper);
    assertEquals(uuid, player.uniqueId());
    assertEquals("Steve", player.username());
    assertTrue(player.isOnline());
    assertEquals(20, player.ping());

    player.sendMessage(Component.text("Test"));
    assertTrue(sentMessage.get());

    player.sendActionBar(Component.text("Action"));
    assertTrue(sentActionBar.get());

    player.showTitle(Title.title(Component.text("Title"), Component.text("Subtitle")));
    assertTrue(shownTitle.get());

    player.clearTitle();
    assertTrue(clearedTitle.get());

    player.resetTitle();
    assertTrue(resetTitle.get());

    player.kick(Component.text("Kick reason"));
    assertTrue(kicked.get());
  }

  @Test
  void testPaperWorldWrapper() {
    UUID uuid = UUID.randomUUID();
    AtomicBoolean worldMessage = new AtomicBoolean(false);
    AtomicBoolean worldTitle = new AtomicBoolean(false);

    org.bukkit.World bWorld = (org.bukkit.World) Proxy.newProxyInstance(
        org.bukkit.World.class.getClassLoader(),
        new Class<?>[]{org.bukkit.World.class},
        (proxy, method, args) -> switch (method.getName()) {
          case "getUID" -> uuid;
          case "getName" -> "nether";
          case "getKey" -> NamespacedKey.minecraft("the_nether");
          case "getMinHeight" -> 0;
          case "getMaxHeight" -> 256;
          case "getEnvironment" -> org.bukkit.World.Environment.NETHER;
          case "getDifficulty" -> org.bukkit.Difficulty.HARD;
          case "getWorldBorder" -> null;
          case "sendMessage" -> {
            worldMessage.set(true);
            yield null;
          }
          case "showTitle" -> {
            worldTitle.set(true);
            yield null;
          }
          case "hashCode" -> uuid.hashCode();
          case "equals" -> proxy == args[0];
          default -> null;
        }
    );

    World world = PaperAdapters.adapt(bWorld);
    assertTrue(world instanceof PaperWorldWrapper);
    assertEquals(uuid, world.uid());
    assertEquals("nether", world.name());
    assertEquals(0, world.minHeight());
    assertEquals(256, world.maxHeight());

    world.sendMessage(Component.text("Broadcast"));
    assertTrue(worldMessage.get());

    world.showTitle(Title.title(Component.text("World Title"), Component.text("Subtitle")));
    assertTrue(worldTitle.get());
  }

  @Test
  void testPaperServerWrapper() {
    AtomicBoolean serverBroadcast = new AtomicBoolean(false);

    org.bukkit.Server bServer = (org.bukkit.Server) Proxy.newProxyInstance(
        org.bukkit.Server.class.getClassLoader(),
        new Class<?>[]{org.bukkit.Server.class},
        (proxy, method, args) -> switch (method.getName()) {
          case "getVersion" -> "1.21.4-Paper";
          case "getName" -> "Paper";
          case "getPort" -> 25565;
          case "getIp" -> "127.0.0.1";
          case "getMaxPlayers" -> 100;
          case "getOnlinePlayers" -> java.util.List.of();
          case "getWorlds" -> java.util.List.of();
          case "broadcast" -> {
            serverBroadcast.set(true);
            yield 1;
          }
          case "hashCode" -> 1;
          case "equals" -> proxy == args[0];
          default -> null;
        }
    );

    Server server = PaperAdapters.adapt(bServer);
    assertTrue(server instanceof PaperServerWrapper);
    assertEquals("1.21.4-Paper", server.version());
    assertEquals("Paper", server.name());
    assertEquals(25565, server.port());

    server.broadcast(Component.text("Global Announcement"));
    assertTrue(serverBroadcast.get());
  }
}
