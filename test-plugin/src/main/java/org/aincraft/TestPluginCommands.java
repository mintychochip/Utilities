package org.aincraft;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.FinePosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.Locale;

public final class TestPluginCommands {

  private static final SuggestionProvider<CommandSourceStack> COORDINATE_SUGGESTIONS =
      (ctx, builder) -> {
        builder.suggest("~");
        builder.suggest("0");
        builder.suggest("~1");
        builder.suggest("~-1");
        return builder.buildFuture();
      };

  private static final SuggestionProvider<CommandSourceStack> MATERIAL_SUGGESTIONS =
      (ctx, builder) -> {
        for (Material material : Material.values()) {
          if (material.isBlock()) {
            builder.suggest(material.name().toLowerCase(Locale.ROOT));
          }
        }
        return builder.buildFuture();
      };

  private static final SuggestionProvider<CommandSourceStack> ENTITY_SUGGESTIONS =
      (ctx, builder) -> {
        for (EntityType type : EntityType.values()) {
          if (type.isSpawnable()) {
            builder.suggest(type.name().toLowerCase(Locale.ROOT));
          }
        }
        return builder.buildFuture();
      };

  private static final SuggestionProvider<CommandSourceStack> PARTICLE_SUGGESTIONS =
      (ctx, builder) -> {
        for (Particle particle : Particle.values()) {
          builder.suggest(particle.name().toLowerCase(Locale.ROOT));
        }
        return builder.buildFuture();
      };

  private static final SuggestionProvider<CommandSourceStack> COUNT_SUGGESTIONS =
      (ctx, builder) -> {
        builder.suggest(10);
        builder.suggest(50);
        builder.suggest(100);
        return builder.buildFuture();
      };

  private static final List<String> TESTPLUGIN_SUBS =
      List.of("all", "api", "common", "config", "adapt");

  private final TestPluginCommandExecutor executor;

  public TestPluginCommands(TestPluginCommandExecutor executor) {
    this.executor = executor;
  }

  public void register(Commands commands) {
    commands.register(hello(), "Say hello");
    commands.register(testplugin(), "Run TestPlugin diagnostics", List.of("tpcheck", "utiltest"));
    commands.register(setblock(), "Set a block via the domain API");
    commands.register(spawnmob(), "Spawn a mob via the domain API");
    commands.register(testparticle(), "Spawn particles via the domain API");
  }

  private LiteralCommandNode<CommandSourceStack> hello() {
    return Commands.literal("hello")
        .executes(ctx -> run(ctx, (sender, origin) -> executor.hello(sender)))
        .build();
  }

  private LiteralCommandNode<CommandSourceStack> testplugin() {
    LiteralArgumentBuilder<CommandSourceStack> root =
        Commands.literal("testplugin")
            .executes(ctx -> run(ctx, (sender, origin) -> executor.runAll(sender)));
    for (String sub : TESTPLUGIN_SUBS) {
      root.then(
          Commands.literal(sub)
              .executes(
                  ctx ->
                      run(
                          ctx,
                          (sender, origin) ->
                              executor.runTestPlugin(sender, ctx.getInput().split(" ")[0], sub))));
    }
    return root.build();
  }

  private LiteralCommandNode<CommandSourceStack> setblock() {
    RequiredArgumentBuilder<CommandSourceStack, String> materialArg =
        Commands.argument("material", StringArgumentType.word())
            .suggests(MATERIAL_SUGGESTIONS)
            .executes(
                ctx ->
                    run(
                        ctx,
                        (sender, origin) -> {
                          BlockPosition position =
                              ctx.getArgument("position", BlockPositionResolver.class)
                                  .resolve(ctx.getSource());
                          return executor.setBlock(
                              sender,
                              origin,
                              position.blockX(),
                              position.blockY(),
                              position.blockZ(),
                              StringArgumentType.getString(ctx, "material"));
                        }));

    RequiredArgumentBuilder<CommandSourceStack, BlockPositionResolver> positionArg =
        Commands.argument("position", ArgumentTypes.blockPosition())
            .suggests(COORDINATE_SUGGESTIONS)
            .then(materialArg);

    return Commands.literal("setblock").then(positionArg).build();
  }

  private LiteralCommandNode<CommandSourceStack> spawnmob() {
    RequiredArgumentBuilder<CommandSourceStack, FinePositionResolver> positionArg =
        Commands.argument("position", ArgumentTypes.finePosition())
            .suggests(COORDINATE_SUGGESTIONS)
            .executes(
                ctx ->
                    run(
                        ctx,
                        (sender, origin) -> {
                          FinePosition position =
                              ctx.getArgument("position", FinePositionResolver.class)
                                  .resolve(ctx.getSource());
                          return executor.spawnMob(
                              sender,
                              origin,
                              StringArgumentType.getString(ctx, "type"),
                              position.x(),
                              position.y(),
                              position.z());
                        }));

    RequiredArgumentBuilder<CommandSourceStack, String> typeArg =
        Commands.argument("type", StringArgumentType.word())
            .suggests(ENTITY_SUGGESTIONS)
            .executes(
                ctx ->
                    run(
                        ctx,
                        (sender, origin) ->
                            executor.spawnMob(
                                sender, origin, StringArgumentType.getString(ctx, "type"))))
            .then(positionArg);

    return Commands.literal("spawnmob").then(typeArg).build();
  }

  private LiteralCommandNode<CommandSourceStack> testparticle() {
    RequiredArgumentBuilder<CommandSourceStack, FinePositionResolver> positionArg =
        Commands.argument("position", ArgumentTypes.finePosition())
            .suggests(COORDINATE_SUGGESTIONS)
            .executes(
                ctx ->
                    run(
                        ctx,
                        (sender, origin) -> {
                          FinePosition position =
                              ctx.getArgument("position", FinePositionResolver.class)
                                  .resolve(ctx.getSource());
                          return executor.testParticle(
                              sender,
                              origin,
                              StringArgumentType.getString(ctx, "particle"),
                              IntegerArgumentType.getInteger(ctx, "count"),
                              position.x(),
                              position.y(),
                              position.z());
                        }));

    RequiredArgumentBuilder<CommandSourceStack, Integer> countArg =
        Commands.argument("count", IntegerArgumentType.integer(1))
            .suggests(COUNT_SUGGESTIONS)
            .executes(
                ctx ->
                    run(
                        ctx,
                        (sender, origin) ->
                            executor.testParticle(
                                sender,
                                origin,
                                StringArgumentType.getString(ctx, "particle"),
                                IntegerArgumentType.getInteger(ctx, "count"))))
            .then(positionArg);

    RequiredArgumentBuilder<CommandSourceStack, String> particleArg =
        Commands.argument("particle", StringArgumentType.word())
            .suggests(PARTICLE_SUGGESTIONS)
            .executes(
                ctx ->
                    run(
                        ctx,
                        (sender, origin) ->
                            executor.testParticle(
                                sender, origin, StringArgumentType.getString(ctx, "particle"))))
            .then(countArg);

    return Commands.literal("testparticle").then(particleArg).build();
  }

  private int run(CommandContext<CommandSourceStack> ctx, CommandHandler handler)
      throws CommandSyntaxException {
    CommandSourceStack source = ctx.getSource();
    CommandSender sender = source.getSender();
    Location origin = source.getLocation();
    if (origin == null) {
      origin = sender instanceof org.bukkit.entity.Player p ? p.getLocation() : null;
    }
    return handler.execute(sender, origin) ? Command.SINGLE_SUCCESS : 0;
  }

  @FunctionalInterface
  private interface CommandHandler {
    boolean execute(CommandSender sender, Location origin) throws CommandSyntaxException;
  }
}
