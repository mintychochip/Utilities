package org.aincraft.bukkit.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Proxy;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.aincraft.api.domain.scoreboard.Objective;
import org.aincraft.api.domain.scoreboard.Scoreboard;
import org.junit.jupiter.api.Test;

class BukkitScoreboardAdapterTest {

  @Test
  void objectiveComponentUsesLegacySerializer() {
    org.bukkit.scoreboard.Objective nativeObjective =
        mock(org.bukkit.scoreboard.Objective.class);
    when(nativeObjective.getDisplayName()).thenReturn("§aTitle");

    Objective objective = new BukkitObjectiveWrapper(nativeObjective);

    assertEquals(Component.text("Title").color(NamedTextColor.GREEN), objective.displayName());
    objective.displayName(Component.text("Next").color(NamedTextColor.RED));
    verify(nativeObjective).setDisplayName("§cNext");
  }

  @Test
  void scoreboardMapsSidebarLookupAndWrapsObjective() {
    org.bukkit.scoreboard.Scoreboard nativeScoreboard =
        mock(org.bukkit.scoreboard.Scoreboard.class);
    org.bukkit.scoreboard.Objective nativeObjective =
        mock(org.bukkit.scoreboard.Objective.class);
    when(nativeScoreboard.getObjective(org.bukkit.scoreboard.DisplaySlot.SIDEBAR))
        .thenReturn(nativeObjective);

    Objective objective =
        new BukkitScoreboardWrapper(nativeScoreboard)
            .objective(org.aincraft.api.domain.scoreboard.DisplaySlot.SIDEBAR);

    assertInstanceOf(BukkitObjectiveWrapper.class, objective);
    verify(nativeScoreboard).getObjective(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);
  }

  @Test
  void foreignScoreboardCannotBeUnwrapped() {
    Scoreboard foreign =
        Scoreboard.class.cast(
            Proxy.newProxyInstance(
                Scoreboard.class.getClassLoader(),
                new Class<?>[] {Scoreboard.class},
                (proxy, method, args) -> null));

    assertThrows(IllegalArgumentException.class, () -> BukkitAdapters.toBukkit(foreign));
  }
}
