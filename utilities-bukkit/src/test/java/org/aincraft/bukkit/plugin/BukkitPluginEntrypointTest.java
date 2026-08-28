package org.aincraft.bukkit.plugin;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

class BukkitPluginEntrypointTest {

  @Test
  void exposesFinalBukkitLifecycleCallbacks() throws NoSuchMethodException {
    assertTrue(JavaPlugin.class.isAssignableFrom(BukkitPluginEntrypoint.class));
    assertTrue(
        Modifier.isFinal(BukkitPluginEntrypoint.class.getDeclaredMethod("onLoad").getModifiers()));
    assertTrue(
        Modifier.isFinal(
            BukkitPluginEntrypoint.class.getDeclaredMethod("onEnable").getModifiers()));
    assertTrue(
        Modifier.isFinal(
            BukkitPluginEntrypoint.class.getDeclaredMethod("onDisable").getModifiers()));
  }
}
