package org.aincraft.bukkit.adapter;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

class BukkitScoreboardAccessTest {

  @Test
  void serverExposesScoreboardManager() {
    org.bukkit.Server nativeServer = mock(org.bukkit.Server.class);
    org.bukkit.scoreboard.ScoreboardManager nativeManager =
        mock(org.bukkit.scoreboard.ScoreboardManager.class);
    when(nativeServer.getScoreboardManager()).thenReturn(nativeManager);

    org.aincraft.api.domain.server.Server server = new BukkitServerWrapper(nativeServer);

    assertInstanceOf(BukkitScoreboardManagerWrapper.class, server.scoreboardManager());
    verify(nativeServer).getScoreboardManager();
  }

  @Test
  void playerGetsAndSetsPortableScoreboard() {
    org.bukkit.entity.Player nativePlayer = mock(org.bukkit.entity.Player.class);
    when(nativePlayer.getType()).thenReturn(org.bukkit.entity.EntityType.PLAYER);
    org.bukkit.scoreboard.Scoreboard current = mock(org.bukkit.scoreboard.Scoreboard.class);
    org.bukkit.scoreboard.Scoreboard replacement =
        mock(org.bukkit.scoreboard.Scoreboard.class);
    when(nativePlayer.getScoreboard()).thenReturn(current);

    org.aincraft.api.domain.entity.Player player = new BukkitPlayerWrapper(nativePlayer);

    assertInstanceOf(BukkitScoreboardWrapper.class, player.scoreboard());
    player.scoreboard(new BukkitScoreboardWrapper(replacement));
    verify(nativePlayer).getScoreboard();
    verify(nativePlayer).setScoreboard(replacement);
  }
}
