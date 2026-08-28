package org.aincraft.paper.adapter;

import static org.junit.jupiter.api.Assertions.*;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.aincraft.api.domain.block.BlockState;
import org.aincraft.api.domain.entity.Player;
import org.aincraft.api.domain.inventory.ItemStack;
import org.aincraft.api.domain.server.Server;
import org.aincraft.api.domain.world.World;
import org.aincraft.bukkit.adapter.BukkitBlockStateWrapper;
import org.aincraft.bukkit.adapter.BukkitItemStackWrapper;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

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

    org.bukkit.World bWorld =
        (org.bukkit.World)
            Proxy.newProxyInstance(
                org.bukkit.World.class.getClassLoader(),
                new Class<?>[] {org.bukkit.World.class},
                (proxy, method, args) ->
                    switch (method.getName()) {
                      case "getUID" -> UUID.randomUUID();
                      case "getName" -> "world";
                      case "getKey" -> NamespacedKey.minecraft("overworld");
                      case "hashCode" -> 1;
                      case "equals" -> proxy == args[0];
                      default -> null;
                    });

    org.bukkit.entity.Player bPlayer =
        (org.bukkit.entity.Player)
            Proxy.newProxyInstance(
                org.bukkit.entity.Player.class.getClassLoader(),
                new Class<?>[] {org.bukkit.entity.Player.class},
                (proxy, method, args) ->
                    switch (method.getName()) {
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
                    });

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

    org.bukkit.World bWorld =
        (org.bukkit.World)
            Proxy.newProxyInstance(
                org.bukkit.World.class.getClassLoader(),
                new Class<?>[] {org.bukkit.World.class},
                (proxy, method, args) ->
                    switch (method.getName()) {
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
                    });

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

    org.bukkit.Server bServer =
        (org.bukkit.Server)
            Proxy.newProxyInstance(
                org.bukkit.Server.class.getClassLoader(),
                new Class<?>[] {org.bukkit.Server.class},
                (proxy, method, args) ->
                    switch (method.getName()) {
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
                    });

    Server server = PaperAdapters.adapt(bServer);
    assertTrue(server instanceof PaperServerWrapper);
    assertEquals("1.21.4-Paper", server.version());
    assertEquals("Paper", server.name());
    assertEquals(25565, server.port());

    server.broadcast(Component.text("Global Announcement"));
    assertTrue(serverBroadcast.get());
  }

  @Test
  void testPaperWorldBorderWrapper() {
    AtomicBoolean resetCalled = new AtomicBoolean(false);

    org.bukkit.WorldBorder bBorder =
        (org.bukkit.WorldBorder)
            Proxy.newProxyInstance(
                org.bukkit.WorldBorder.class.getClassLoader(),
                new Class<?>[] {org.bukkit.WorldBorder.class},
                (proxy, method, args) ->
                    switch (method.getName()) {
                      case "reset" -> {
                        resetCalled.set(true);
                        yield null;
                      }
                      default -> null;
                    });

    // verify reset() delegates to the underlying bukkit border
    org.aincraft.api.domain.world.WorldBorder wrapper = new PaperWorldBorderWrapper(bBorder);
    wrapper.reset();
    assertTrue(resetCalled.get(), "reset() should delegate to the bukkit border");

    // verify PaperAdapters.adapt(WorldBorder) returns PaperWorldBorderWrapper
    assertTrue(
        PaperAdapters.adapt(bBorder) instanceof PaperWorldBorderWrapper,
        "PaperAdapters.adapt(WorldBorder) should return PaperWorldBorderWrapper");
  }

  @Test
  void testPaperBlockWrapper() {
    AtomicBoolean replaceableCalled = new AtomicBoolean(false);
    AtomicBoolean collidableCalled = new AtomicBoolean(false);
    AtomicBoolean buildableCalled = new AtomicBoolean(false);
    AtomicBoolean burnableCalled = new AtomicBoolean(false);
    AtomicBoolean suffocatingCalled = new AtomicBoolean(false);
    AtomicBoolean breakCalled = new AtomicBoolean(false);
    AtomicBoolean breakWithToolCalled = new AtomicBoolean(false);
    AtomicBoolean canPlaceCalled = new AtomicBoolean(false);
    org.bukkit.World[] bWorldHolder = new org.bukkit.World[1];
    BlockData bData =
        (BlockData)
            Proxy.newProxyInstance(
                BlockData.class.getClassLoader(),
                new Class<?>[] {BlockData.class},
                (proxy, method, args) ->
                    switch (method.getName()) {
                      case "getMaterial" -> Material.STONE;
                      case "getAsString" -> "minecraft:stone";
                      case "clone" -> proxy;
                      case "hashCode" -> 1;
                      case "equals" -> proxy == args[0];
                      default -> null;
                    });
    BlockState state = new BukkitBlockStateWrapper(bData);
    org.bukkit.inventory.ItemStack bTool = new org.bukkit.inventory.ItemStack() {};
    ItemStack tool = new BukkitItemStackWrapper(bTool);

    org.bukkit.block.Block bBlock =
        (org.bukkit.block.Block)
            Proxy.newProxyInstance(
                org.bukkit.block.Block.class.getClassLoader(),
                new Class<?>[] {org.bukkit.block.Block.class},
                (proxy, method, args) ->
                    switch (method.getName()) {
                      case "isReplaceable" -> {
                        replaceableCalled.set(true);
                        yield true;
                      }
                      case "isCollidable" -> {
                        collidableCalled.set(true);
                        yield true;
                      }
                      case "isBuildable" -> {
                        buildableCalled.set(true);
                        yield true;
                      }
                      case "isBurnable" -> {
                        burnableCalled.set(true);
                        yield true;
                      }
                      case "isSuffocating" -> {
                        suffocatingCalled.set(true);
                        yield true;
                      }
                      case "breakNaturally" -> {
                        if (args != null && args.length == 1) {
                          assertSame(bTool, args[0]);
                          breakWithToolCalled.set(true);
                        } else {
                          assertTrue(args == null || args.length == 0);
                        }
                        breakCalled.set(true);
                        yield true;
                      }
                      case "canPlace" -> {
                        assertSame(bData, args[0]);
                        canPlaceCalled.set(true);
                        yield true;
                      }
                      case "getWorld" -> bWorldHolder[0];
                      case "hashCode" -> 1;
                      case "equals" -> proxy == args[0];
                      default -> null;
                    });

    org.aincraft.api.domain.world.Block block = PaperAdapters.adapt(bBlock);
    assertTrue(block instanceof PaperBlockWrapper);
    assertTrue(block.isReplaceable());
    assertTrue(replaceableCalled.get());
    assertTrue(block.isCollidable());
    assertTrue(block.isBuildable());
    assertTrue(block.isBurnable());
    assertTrue(block.isSuffocating());
    assertTrue(collidableCalled.get());
    assertTrue(buildableCalled.get());
    assertTrue(burnableCalled.get());
    assertTrue(suffocatingCalled.get());
    assertTrue(block.breakNaturally());
    assertTrue(block.canPlace(state));
    assertTrue(block.breakNaturally(tool));
    assertTrue(breakCalled.get());
    assertTrue(canPlaceCalled.get());
    assertTrue(breakWithToolCalled.get());

    org.bukkit.World bWorld =
        (org.bukkit.World)
            Proxy.newProxyInstance(
                org.bukkit.World.class.getClassLoader(),
                new Class<?>[] {org.bukkit.World.class},
                (proxy, method, args) ->
                    switch (method.getName()) {
                      case "getKey" -> NamespacedKey.minecraft("world");
                      case "getBlockAt" -> bBlock;
                      default -> null;
                    });
    bWorldHolder[0] = bWorld;
    assertTrue(block.world() instanceof PaperWorldWrapper);
    World world = PaperAdapters.adapt(bWorld);
    assertTrue(world.getBlockAt(1, 2, 3) instanceof PaperBlockWrapper);
  }
}
