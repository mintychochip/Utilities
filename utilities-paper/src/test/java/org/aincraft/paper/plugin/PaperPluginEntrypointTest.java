package org.aincraft.paper.plugin;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.aincraft.bukkit.plugin.BukkitPluginEntrypoint;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;

class PaperPluginEntrypointTest {

  @Test
  void isAJavaPluginUsingTheSharedBukkitBridge() {
    assertTrue(JavaPlugin.class.isAssignableFrom(PaperPluginEntrypoint.class));
    assertTrue(BukkitPluginEntrypoint.class.isAssignableFrom(PaperPluginEntrypoint.class));
  }
}
