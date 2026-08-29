package org.aincraft.paper.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import net.kyori.adventure.text.Component;
import org.aincraft.api.domain.scoreboard.Criteria;
import org.aincraft.api.domain.scoreboard.Objective;
import org.aincraft.api.domain.scoreboard.RenderType;
import org.aincraft.api.domain.scoreboard.Scoreboard;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

class PaperScoreboardAdapterTest {

  @Test
  void objectiveUsesNativeAdventureComponents() {
    AtomicReference<Component> displayName = new AtomicReference<>(Component.text("Paper"));
    org.bukkit.scoreboard.Objective nativeObjective = objectiveProxy(displayName);

    Objective objective = new PaperObjectiveWrapper(nativeObjective);

    assertEquals(Component.text("Paper"), objective.displayName());
    objective.displayName(Component.text("Native"));
    assertEquals(Component.text("Native"), displayName.get());
  }

  @Test
  void paperScoreboardPreservesPaperObjectiveWrappers() {
    AtomicReference<Component> displayName = new AtomicReference<>(Component.empty());
    org.bukkit.scoreboard.Objective nativeObjective = objectiveProxy(displayName);
    org.bukkit.scoreboard.Scoreboard nativeScoreboard =
        (org.bukkit.scoreboard.Scoreboard)
            Proxy.newProxyInstance(
                org.bukkit.scoreboard.Scoreboard.class.getClassLoader(),
                new Class<?>[] {org.bukkit.scoreboard.Scoreboard.class},
                (proxy, method, args) ->
                    method.getName().equals("getObjectives")
                        ? Set.of(nativeObjective)
                        : defaultValue(method.getReturnType()));

    Scoreboard scoreboard = new PaperScoreboardWrapper(nativeScoreboard);

    assertInstanceOf(PaperObjectiveWrapper.class, scoreboard.objectives().iterator().next());
  }

  @Test
  void paperScoreboardRegistersObjectivesWithAdventureComponents() {
    AtomicReference<Component> displayName = new AtomicReference<>(Component.empty());
    org.bukkit.scoreboard.Objective nativeObjective = objectiveProxy(displayName);
    AtomicReference<Object[]> arguments = new AtomicReference<>();
    org.bukkit.scoreboard.Criteria nativeCriteria =
        (org.bukkit.scoreboard.Criteria)
            Proxy.newProxyInstance(
                org.bukkit.scoreboard.Criteria.class.getClassLoader(),
                new Class<?>[] {org.bukkit.scoreboard.Criteria.class},
                (proxy, method, args) -> defaultValue(method.getReturnType()));
    Criteria criteria = new org.aincraft.bukkit.adapter.BukkitCriteriaWrapper(nativeCriteria);
    org.bukkit.scoreboard.Scoreboard nativeScoreboard =
        (org.bukkit.scoreboard.Scoreboard)
            Proxy.newProxyInstance(
                org.bukkit.scoreboard.Scoreboard.class.getClassLoader(),
                new Class<?>[] {org.bukkit.scoreboard.Scoreboard.class},
                (proxy, method, args) -> {
                  if (method.getName().startsWith("registerNewObjective")) {
                    arguments.set(args);
                    return nativeObjective;
                  }
                  return defaultValue(method.getReturnType());
                });

    Scoreboard scoreboard = new PaperScoreboardWrapper(nativeScoreboard);

    Objective objective =
        scoreboard.registerObjective(
            "sidebar", criteria, Component.text("Styled"), RenderType.INTEGER);

    assertInstanceOf(PaperObjectiveWrapper.class, objective);
    assertEquals(Component.text("Styled"), arguments.get()[2]);
  }

  @Test
  void paperPlayerServerPreservesPaperServerWrapper() {
    org.bukkit.Server nativeServer =
        (org.bukkit.Server)
            Proxy.newProxyInstance(
                org.bukkit.Server.class.getClassLoader(),
                new Class<?>[] {org.bukkit.Server.class},
                (proxy, method, args) -> defaultValue(method.getReturnType()));
    org.bukkit.entity.Player nativePlayer =
        (org.bukkit.entity.Player)
            Proxy.newProxyInstance(
                org.bukkit.entity.Player.class.getClassLoader(),
                new Class<?>[] {org.bukkit.entity.Player.class},
                (proxy, method, args) ->
                    switch (method.getName()) {
                      case "getServer" -> nativeServer;
                      case "getType" -> org.bukkit.entity.EntityType.PLAYER;
                      default -> defaultValue(method.getReturnType());
                    });

    org.aincraft.api.domain.entity.Player player = new PaperPlayerWrapper(nativePlayer);

    assertInstanceOf(PaperServerWrapper.class, player.server());
  }

  private static org.bukkit.scoreboard.Objective objectiveProxy(
      AtomicReference<Component> displayName) {
    return (org.bukkit.scoreboard.Objective)
        Proxy.newProxyInstance(
            org.bukkit.scoreboard.Objective.class.getClassLoader(),
            new Class<?>[] {org.bukkit.scoreboard.Objective.class},
            (proxy, method, args) -> {
              if (!method.getName().equals("displayName")) {
                return defaultValue(method.getReturnType());
              }
              if (args == null) {
                return displayName.get();
              }
              displayName.set((Component) args[0]);
              return null;
            });
  }

  private static Object defaultValue(Class<?> type) {
    if (!type.isPrimitive()) {
      return null;
    }
    if (type == boolean.class) {
      return false;
    }
    if (type == byte.class) {
      return (byte) 0;
    }
    if (type == short.class) {
      return (short) 0;
    }
    if (type == int.class) {
      return 0;
    }
    if (type == long.class) {
      return 0L;
    }
    if (type == float.class) {
      return 0F;
    }
    if (type == double.class) {
      return 0D;
    }
    if (type == char.class) {
      return '\0';
    }
    return null;
  }
}
