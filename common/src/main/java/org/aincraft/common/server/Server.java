package org.aincraft.common.server;

import java.util.Collection;
import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.aincraft.common.entity.Player;
import org.aincraft.common.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Server extends Audience {

  @NotNull String version();

  @NotNull String name();

  int port();

  @NotNull String ip();

  int maxPlayers();

  @NotNull Collection<? extends Player> onlinePlayers();

  @NotNull Collection<? extends World> worlds();

  @Nullable World world(@NotNull Key key);

  @Nullable World world(@NotNull String name);

  @Nullable World world(@NotNull UUID uid);

  @Nullable Player player(@NotNull UUID uid);

  @Nullable Player player(@NotNull String name);

  void broadcast(@NotNull Component message);

  @NotNull ConsoleCommandSender consoleSender();

  void shutdown();

  void reload();
}
