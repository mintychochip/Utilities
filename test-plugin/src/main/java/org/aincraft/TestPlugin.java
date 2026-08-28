package org.aincraft;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.aincraft.config.YamlConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin {

  private YamlConfiguration configuration;

  @Override
  public void onEnable() {
    this.configuration = YamlConfiguration.single(this, "config.yml");
    TestPluginCommandExecutor executor =
        new TestPluginCommandExecutor(() -> configuration, this::getServer);

    // Run startup diagnostics / smoke
    executor.runAll(Bukkit.getConsoleSender());
    Bukkit.getLogger().info("smoke=ok");

    // Register Brigadier command trees
    getLifecycleManager()
        .registerEventHandler(
            LifecycleEvents.COMMANDS,
            event -> new TestPluginCommands(executor).register(event.registrar()));
  }
}
