package org.aincraft;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.aincraft.api.domain.block.BlockFace;
import org.aincraft.api.domain.datacomponent.item.DataComponentTypes;
import org.aincraft.api.domain.effect.Particle;
import org.aincraft.api.domain.location.BoundingBox;
import org.aincraft.api.domain.location.Position;
import org.aincraft.bukkit.adapter.BukkitAdapters;
import org.aincraft.config.YamlConfiguration;
import org.aincraft.paper.adapter.PaperAdapters;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.Supplier;

public class TestPluginCommandExecutor implements CommandExecutor {

  private final Supplier<YamlConfiguration> configurationSupplier;
  private final Supplier<Server> serverSupplier;

  public TestPluginCommandExecutor(
      @NotNull Supplier<YamlConfiguration> configurationSupplier,
      @NotNull Supplier<Server> serverSupplier) {
    this.configurationSupplier = configurationSupplier;
    this.serverSupplier = serverSupplier;
  }

  @Override
  public boolean onCommand(
      @NotNull CommandSender sender,
      @NotNull Command command,
      @NotNull String label,
      @NotNull String[] args) {
    String cmdName = command.getName().toLowerCase(Locale.ROOT);
    Location origin = getOriginLocation(sender);

    return switch (cmdName) {
      case "hello" -> hello(sender);
      case "testplugin" -> {
        String sub = args.length > 0 ? args[0].toLowerCase(Locale.ROOT) : "all";
        yield runTestPlugin(sender, label, sub);
      }
      case "setblock" -> setBlockFromArgs(sender, origin, args);
      case "spawnmob" -> spawnMobFromArgs(sender, origin, args);
      case "testparticle" -> testParticleFromArgs(sender, origin, args);
      default -> false;
    };
  }

  public boolean hello(CommandSender sender) {
    sender.sendMessage(
        Component.text(
            "Hello from TestPlugin! Utilities library is active.", NamedTextColor.GREEN));
    return true;
  }

  public boolean runAll(CommandSender sender) {
    sender.sendMessage(
        Component.text("=== Running All TestPlugin Diagnostics ===", NamedTextColor.GOLD));
    testConfig(sender);
    testCommon(sender);
    testApi(sender);
    testAdapters(sender);
    sender.sendMessage(Component.text("=== All Diagnostics Completed ===", NamedTextColor.GOLD));
    return true;
  }

  public boolean runTestPlugin(CommandSender sender, String label, String sub) {
    switch (sub) {
      case "api" -> testApi(sender);
      case "common" -> testCommon(sender);
      case "config" -> testConfig(sender);
      case "adapt" -> testAdapters(sender);
      case "all" -> runAll(sender);
      default ->
          sender.sendMessage(
              Component.text(
                  "Usage: /" + label + " [all|api|common|config|adapt]", NamedTextColor.RED));
    }
    return true;
  }

  public boolean setBlock(CommandSender sender, Location origin, String material) {
    return setBlock(sender, origin, "~", "~", "~", material);
  }

  public boolean setBlock(
      CommandSender sender, Location origin, int x, int y, int z, String material) {
    World bWorld = origin != null ? origin.getWorld() : getTargetWorld(sender);
    if (bWorld == null) {
      sender.sendMessage(Component.text("No world available to set block", NamedTextColor.RED));
      return true;
    }

    org.aincraft.api.domain.world.World domainWorld = PaperAdapters.adapt(bWorld);
    org.aincraft.api.domain.world.Block domainBlock = domainWorld.getBlockAt(x, y, z);
    domainBlock.setType(Key.key("minecraft", material.toLowerCase(Locale.ROOT)));

    sender.sendMessage(
        Component.text(
            "Successfully set block at ("
                + domainBlock.x()
                + ", "
                + domainBlock.y()
                + ", "
                + domainBlock.z()
                + ") to "
                + domainBlock.key()
                + " (solid="
                + domainBlock.isSolid()
                + ", empty="
                + domainBlock.isEmpty()
                + ") via domain Block API",
            NamedTextColor.GREEN));
    return true;
  }

  public boolean setBlock(
      CommandSender sender,
      Location origin,
      String xArg,
      String yArg,
      String zArg,
      String material) {
    int originX = origin != null ? origin.getBlockX() : 0;
    int originY = origin != null ? origin.getBlockY() : 64;
    int originZ = origin != null ? origin.getBlockZ() : 0;

    int x;
    int y;
    int z;
    try {
      x = parseBlockCoordinate(xArg, originX);
      y = parseBlockCoordinate(yArg, originY);
      z = parseBlockCoordinate(zArg, originZ);
    } catch (NumberFormatException e) {
      sender.sendMessage(
          Component.text("Invalid coordinates: " + e.getMessage(), NamedTextColor.RED));
      return true;
    }

    return setBlock(sender, origin, x, y, z, material);
  }

  private boolean setBlockFromArgs(CommandSender sender, Location origin, String[] args) {
    if (args.length == 1) {
      return setBlock(sender, origin, args[0]);
    }
    if (args.length == 4) {
      return setBlock(sender, origin, args[0], args[1], args[2], args[3]);
    }
    sender.sendMessage(
        Component.text(
            "Usage: /setblock <x> <y> <z> <material> or /setblock <material>", NamedTextColor.RED));
    return true;
  }

  public boolean spawnMob(CommandSender sender, Location origin, String type) {
    return spawnMob(sender, origin, type, "~", "~", "~");
  }

  public boolean spawnMob(
      CommandSender sender, Location origin, String type, double x, double y, double z) {
    World bWorld = origin != null ? origin.getWorld() : getTargetWorld(sender);
    if (bWorld == null) {
      sender.sendMessage(Component.text("No world available to spawn mob", NamedTextColor.RED));
      return true;
    }

    org.aincraft.api.domain.world.World domainWorld = PaperAdapters.adapt(bWorld);
    org.aincraft.api.domain.location.Location domainLoc =
        BukkitAdapters.adapt(new Location(bWorld, x, y, z));
    org.aincraft.api.domain.entity.Entity domainEntity =
        domainWorld.spawnEntity(domainLoc, Key.key("minecraft", type.toLowerCase(Locale.ROOT)));

    sender.sendMessage(
        Component.text(
            "Spawned entity "
                + domainEntity.type()
                + " with UUID "
                + domainEntity.uniqueId()
                + " at position ("
                + domainEntity.location().blockX()
                + ", "
                + domainEntity.location().blockY()
                + ", "
                + domainEntity.location().blockZ()
                + ") boundingBox="
                + domainEntity.boundingBox()
                + " via domain World API",
            NamedTextColor.GREEN));
    return true;
  }

  public boolean spawnMob(
      CommandSender sender, Location origin, String type, String xArg, String yArg, String zArg) {
    double originX = origin != null ? origin.getX() : 0.5;
    double originY = origin != null ? origin.getY() : 65.0;
    double originZ = origin != null ? origin.getZ() : 0.5;

    double x;
    double y;
    double z;
    try {
      x = parseCoordinate(xArg, originX);
      y = parseCoordinate(yArg, originY);
      z = parseCoordinate(zArg, originZ);
    } catch (NumberFormatException e) {
      sender.sendMessage(
          Component.text("Invalid coordinates: " + e.getMessage(), NamedTextColor.RED));
      return true;
    }

    return spawnMob(sender, origin, type, x, y, z);
  }

  private boolean spawnMobFromArgs(CommandSender sender, Location origin, String[] args) {
    if (args.length == 1) {
      return spawnMob(sender, origin, args[0]);
    }
    if (args.length == 4) {
      return spawnMob(sender, origin, args[0], args[1], args[2], args[3]);
    }
    sender.sendMessage(Component.text("Usage: /spawnmob <type> [<x> <y> <z>]", NamedTextColor.RED));
    return true;
  }

  public boolean testParticle(CommandSender sender, Location origin, String particleName) {
    return testParticle(sender, origin, particleName, 10);
  }

  public boolean testParticle(
      CommandSender sender, Location origin, String particleName, int count) {
    return testParticle(sender, origin, particleName, count, "~", "~", "~");
  }

  public boolean testParticle(
      CommandSender sender,
      Location origin,
      String particleName,
      int count,
      double x,
      double y,
      double z) {
    World bWorld = origin != null ? origin.getWorld() : getTargetWorld(sender);
    if (bWorld == null) {
      sender.sendMessage(Component.text("No world available", NamedTextColor.RED));
      return true;
    }

    org.bukkit.Particle bParticle;
    try {
      bParticle = org.bukkit.Particle.valueOf(particleName.toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException e) {
      sender.sendMessage(Component.text("Unknown particle: " + particleName, NamedTextColor.RED));
      return true;
    }

    org.aincraft.api.domain.world.World domainWorld = PaperAdapters.adapt(bWorld);
    Particle domainParticle = BukkitAdapters.adapt(bParticle);
    org.aincraft.api.domain.location.Location domainLoc =
        BukkitAdapters.adapt(new Location(bWorld, x, y, z));

    domainWorld.spawnParticle(domainParticle, domainLoc, count, 0.5, 0.5, 0.5, 0.05);
    sender.sendMessage(
        Component.text(
            "Spawned " + count + " particles of " + domainParticle.key() + " via domain World API",
            NamedTextColor.GREEN));
    return true;
  }

  public boolean testParticle(
      CommandSender sender,
      Location origin,
      String particleName,
      int count,
      String xArg,
      String yArg,
      String zArg) {
    double originX = origin != null ? origin.getX() : 0.0;
    double originY = origin != null ? origin.getY() : 65.0;
    double originZ = origin != null ? origin.getZ() : 0.0;

    double x;
    double y;
    double z;
    try {
      x = parseCoordinate(xArg, originX);
      y = parseCoordinate(yArg, originY);
      z = parseCoordinate(zArg, originZ);
    } catch (NumberFormatException e) {
      sender.sendMessage(
          Component.text("Invalid coordinates: " + e.getMessage(), NamedTextColor.RED));
      return true;
    }

    return testParticle(sender, origin, particleName, count, x, y, z);
  }

  private boolean testParticleFromArgs(CommandSender sender, Location origin, String[] args) {
    if (args.length == 1) {
      return testParticle(sender, origin, args[0]);
    }
    if (args.length == 2) {
      try {
        return testParticle(sender, origin, args[0], Integer.parseInt(args[1]));
      } catch (NumberFormatException e) {
        sender.sendMessage(Component.text("Invalid count: " + e.getMessage(), NamedTextColor.RED));
        return true;
      }
    }
    if (args.length == 4) {
      return testParticle(sender, origin, args[0], 10, args[1], args[2], args[3]);
    }
    if (args.length == 5) {
      try {
        return testParticle(
            sender, origin, args[0], Integer.parseInt(args[1]), args[2], args[3], args[4]);
      } catch (NumberFormatException e) {
        sender.sendMessage(Component.text("Invalid count: " + e.getMessage(), NamedTextColor.RED));
        return true;
      }
    }
    sender.sendMessage(
        Component.text(
            "Usage: /testparticle <particle_name> [count] [<x> <y> <z>]", NamedTextColor.RED));
    return true;
  }

  private double parseCoordinate(String arg, double origin) throws NumberFormatException {
    if (arg.startsWith("~")) {
      String offset = arg.substring(1);
      if (offset.isEmpty()) {
        return origin;
      }
      return origin + Double.parseDouble(offset);
    }
    return Double.parseDouble(arg);
  }

  private int parseBlockCoordinate(String arg, int origin) throws NumberFormatException {
    if (arg.startsWith("~")) {
      String offset = arg.substring(1);
      if (offset.isEmpty()) {
        return origin;
      }
      return origin + Integer.parseInt(offset);
    }
    return Integer.parseInt(arg);
  }

  private Location getOriginLocation(CommandSender sender) {
    if (sender instanceof Player p) {
      return p.getLocation();
    }
    World world = getTargetWorld(sender);
    if (world == null) {
      return null;
    }
    return new Location(world, 0.5, 64.0, 0.5);
  }

  private World getTargetWorld(CommandSender sender) {
    if (sender instanceof Player p) {
      return p.getWorld();
    }
    Server s = serverSupplier.get();
    if (s != null && !s.getWorlds().isEmpty()) {
      return s.getWorlds().get(0);
    }
    return null;
  }

  public void testConfig(CommandSender sender) {
    YamlConfiguration config = configurationSupplier.get();
    String val = config != null ? config.getString("test") : "null";
    sender.sendMessage(Component.text("[Config] test key = '" + val + "'", NamedTextColor.AQUA));
  }

  public void testCommon(CommandSender sender) {
    sender.sendMessage(
        Component.text(
            "[Common] Max stack size: "
                + DataComponentTypes.MAX_STACK_SIZE.key()
                + " ("
                + DataComponentTypes.MAX_STACK_SIZE.type().getSimpleName()
                + ")",
            NamedTextColor.GREEN));
    sender.sendMessage(
        Component.text(
            "[Common] Custom name: " + DataComponentTypes.CUSTOM_NAME.key(), NamedTextColor.GREEN));
  }

  public void testApi(CommandSender sender) {
    Position pos = BukkitAdapters.adapt(new Vector(12.5, 64.0, -18.5));
    BoundingBox box = BukkitAdapters.adapt(new org.bukkit.util.BoundingBox(0, 0, 0, 10, 10, 10));
    BlockFace face = BukkitAdapters.adapt(org.bukkit.block.BlockFace.EAST);
    sender.sendMessage(
        Component.text(
            "[API] Position block coordinates: ("
                + pos.blockX()
                + ", "
                + pos.blockY()
                + ", "
                + pos.blockZ()
                + ")",
            NamedTextColor.YELLOW));
    sender.sendMessage(
        Component.text(
            "[API] Bounding box contains (5,5,5): " + box.contains(5, 5, 5),
            NamedTextColor.YELLOW));
    sender.sendMessage(
        Component.text(
            "[API] BlockFace EAST modX: " + face.modX() + ", modZ: " + face.modZ(),
            NamedTextColor.YELLOW));
  }

  public void testAdapters(CommandSender sender) {
    if (sender instanceof Player player) {
      var domainPlayer = PaperAdapters.adapt(player);
      sender.sendMessage(
          Component.text(
              "[Adapters] Adapted Player: "
                  + domainPlayer.username()
                  + " UUID: "
                  + domainPlayer.uniqueId(),
              NamedTextColor.LIGHT_PURPLE));
    }
    Server s = serverSupplier.get();
    if (s != null) {
      s.getWorlds()
          .forEach(
              world -> {
                var domainWorld = PaperAdapters.adapt(world);
                sender.sendMessage(
                    Component.text(
                        "[Adapters] Adapted World: "
                            + domainWorld.name()
                            + " ("
                            + domainWorld.key()
                            + ")",
                        NamedTextColor.LIGHT_PURPLE));
              });
    }
  }
}
