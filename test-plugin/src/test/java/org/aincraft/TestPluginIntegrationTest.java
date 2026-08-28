package org.aincraft;

import static org.junit.jupiter.api.Assertions.*;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.aincraft.api.domain.block.BlockFace;
import org.aincraft.api.domain.datacomponent.item.DataComponentTypes;
import org.aincraft.api.domain.location.BoundingBox;
import org.aincraft.api.domain.location.Position;
import org.aincraft.api.event.AbstractCancellableEvent;
import org.aincraft.api.event.EventBus;
import org.aincraft.api.math.RandomSelector;
import org.aincraft.bukkit.adapter.BukkitAdapters;
import org.aincraft.event.EventBuses;
import org.aincraft.math.RandomSelectors;
import org.aincraft.paper.adapter.PaperAdapters;
import org.aincraft.registry.Registry;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestPluginIntegrationTest {

  @Test
  void testCommonDataComponentTypesIntegration() {
    assertNotNull(DataComponentTypes.MAX_STACK_SIZE);
    assertEquals("minecraft:max_stack_size", DataComponentTypes.MAX_STACK_SIZE.key().asString());
    assertEquals(Integer.class, DataComponentTypes.MAX_STACK_SIZE.type());

    assertNotNull(DataComponentTypes.CUSTOM_NAME);
    assertEquals("minecraft:custom_name", DataComponentTypes.CUSTOM_NAME.key().asString());
  }

  @Test
  void testUtilitiesApiDomainModelsIntegration() {
    Position pos = BukkitAdapters.adapt(new Vector(10.5, 64.0, -20.5));
    assertEquals(10, pos.blockX());
    assertEquals(64, pos.blockY());
    assertEquals(-21, pos.blockZ());

    BoundingBox box = BukkitAdapters.adapt(new org.bukkit.util.BoundingBox(0, 0, 0, 10, 10, 10));
    assertTrue(box.contains(5, 5, 5));
    assertFalse(box.contains(15, 5, 5));

    BlockFace face = BukkitAdapters.adapt(org.bukkit.block.BlockFace.NORTH);
    assertEquals(BlockFace.NORTH, face);
    assertEquals(-1, face.modZ());
  }

  @Test
  void testPaperAdaptersIntegration() {
    UUID worldUid = UUID.randomUUID();
    NamespacedKey netherKey = NamespacedKey.minecraft("the_nether");
    World bukkitWorld =
        (World)
            Proxy.newProxyInstance(
                World.class.getClassLoader(),
                new Class<?>[] {World.class},
                (proxy, method, args) -> {
                  return switch (method.getName()) {
                    case "getUID" -> worldUid;
                    case "getName" -> "world_nether";
                    case "getKey" -> netherKey;
                    default -> null;
                  };
                });

    var domainWorld = PaperAdapters.adapt(bukkitWorld);
    assertEquals("world_nether", domainWorld.name());
    assertEquals("minecraft:the_nether", domainWorld.key().asString());
    assertEquals(worldUid, domainWorld.uid());

    UUID playerUid = UUID.randomUUID();
    Player bukkitPlayer =
        (Player)
            Proxy.newProxyInstance(
                Player.class.getClassLoader(),
                new Class<?>[] {Player.class},
                (proxy, method, args) -> {
                  return switch (method.getName()) {
                    case "getUniqueId" -> playerUid;
                    case "getName" -> "Steve";
                    case "isOnline" -> true;
                    case "getPing" -> 20;
                    case "getType" -> EntityType.PLAYER;
                    default -> null;
                  };
                });

    var domainPlayer = PaperAdapters.adapt(bukkitPlayer);
    assertEquals("Steve", domainPlayer.username());
    assertEquals(playerUid, domainPlayer.uniqueId());
    assertTrue(domainPlayer.isOnline());
  }

  @Test
  void testEventBusIntegration() {
    EventBus bus = EventBuses.create();
    AtomicBoolean handled = new AtomicBoolean(false);

    class CustomServerEvent extends AbstractCancellableEvent {
      final String payload;

      CustomServerEvent(String payload) {
        this.payload = payload;
      }
    }

    bus.subscribe(
        CustomServerEvent.class,
        event -> {
          handled.set(true);
          assertEquals("event_payload_ok", event.payload);
          event.setCancelled(true);
        });

    CustomServerEvent event = new CustomServerEvent("event_payload_ok");
    bus.post(event);

    assertTrue(handled.get(), "Event must be handled by registered listener");
    assertTrue(event.isCancelled(), "Event cancellation state must be preserved");
  }

  @Test
  void testMathRandomSelectorIntegration() {
    RandomSelector.UniformRandomSelector<String> selector = RandomSelectors.uniform();
    selector.addObject("A");
    selector.addObject("B");
    selector.addObject("C");

    java.util.random.RandomGenerator rng = new java.util.Random(42);
    String selected = selector.getObject(rng);
    assertNotNull(selected);
    assertTrue(List.of("A", "B", "C").contains(selected));
  }

  @Test
  void testRegistryAccessIntegration() {
    Registry<KeyedItem> registry = Registry.create();
    KeyedItem gold = new KeyedItem(Key.key("custom", "gold"));
    registry.register(gold);

    assertTrue(registry.isRegistered(Key.key("custom", "gold")));
    assertEquals(gold, registry.get(Key.key("custom", "gold")));
  }

  record KeyedItem(Key key) implements net.kyori.adventure.key.Keyed {}

  @Test
  void testPluginCommandsExecution() {
    World mockWorld =
        (World)
            Proxy.newProxyInstance(
                World.class.getClassLoader(),
                new Class<?>[] {World.class},
                (proxy, method, args) -> {
                  return switch (method.getName()) {
                    case "getUID" -> UUID.randomUUID();
                    case "getName" -> "world";
                    case "getKey" -> NamespacedKey.minecraft("overworld");
                    default -> null;
                  };
                });

    org.bukkit.Server mockServer =
        (org.bukkit.Server)
            Proxy.newProxyInstance(
                org.bukkit.Server.class.getClassLoader(),
                new Class<?>[] {org.bukkit.Server.class},
                (proxy, method, args) -> {
                  if (method.getName().equals("getWorlds")) {
                    return List.of(mockWorld);
                  }
                  return null;
                });

    TestPluginCommandExecutor executor =
        new TestPluginCommandExecutor(() -> null, () -> mockServer);

    List<Component> messages = new ArrayList<>();
    CommandSender sender =
        (CommandSender)
            Proxy.newProxyInstance(
                CommandSender.class.getClassLoader(),
                new Class<?>[] {CommandSender.class},
                (proxy, method, args) -> {
                  if (method.getName().equals("sendMessage")
                      && args.length > 0
                      && args[0] instanceof Component c) {
                    messages.add(c);
                  }
                  return null;
                });

    Command helloCommand =
        new Command("hello") {
          @Override
          public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            return false;
          }
        };

    Command testCommand =
        new Command("testplugin") {
          @Override
          public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            return false;
          }
        };

    Command setBlockCommand =
        new Command("setblock") {
          @Override
          public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            return false;
          }
        };

    Command spawnMobCommand =
        new Command("spawnmob") {
          @Override
          public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            return false;
          }
        };

    Command particleCommand =
        new Command("testparticle") {
          @Override
          public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            return false;
          }
        };

    // 1. /hello
    assertTrue(executor.onCommand(sender, helloCommand, "hello", new String[0]));
    assertFalse(messages.isEmpty());

    // 2. /testplugin api
    messages.clear();
    assertTrue(executor.onCommand(sender, testCommand, "testplugin", new String[] {"api"}));
    assertFalse(messages.isEmpty());

    // 3. /testplugin common
    messages.clear();
    assertTrue(executor.onCommand(sender, testCommand, "testplugin", new String[] {"common"}));
    assertFalse(messages.isEmpty());

    // 4. /testplugin all
    messages.clear();
    assertTrue(executor.onCommand(sender, testCommand, "testplugin", new String[] {"all"}));
    assertFalse(messages.isEmpty());

    // 5. /testplugin unknown
    messages.clear();
    assertTrue(executor.onCommand(sender, testCommand, "testplugin", new String[] {"invalid_sub"}));
    assertFalse(messages.isEmpty());

    // 6. /setblock usage check
    messages.clear();
    assertTrue(executor.onCommand(sender, setBlockCommand, "setblock", new String[0]));
    assertFalse(messages.isEmpty());

    // 7. /spawnmob usage check
    messages.clear();
    assertTrue(executor.onCommand(sender, spawnMobCommand, "spawnmob", new String[0]));
    assertFalse(messages.isEmpty());

    // 8. /testparticle usage check
    messages.clear();
    assertTrue(executor.onCommand(sender, particleCommand, "testparticle", new String[0]));
    assertFalse(messages.isEmpty());
  }
}
