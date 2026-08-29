package org.aincraft.paper.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import net.kyori.adventure.text.Component;
import org.aincraft.api.domain.scoreboard.Objective;
import org.aincraft.api.domain.scoreboard.Scoreboard;
import org.junit.jupiter.api.Test;

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
