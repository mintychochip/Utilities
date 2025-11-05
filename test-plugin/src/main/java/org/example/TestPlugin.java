package org.example;

import org.aincraft.config.YamlConfiguration;
import org.aincraft.registry.Registry;
import org.bukkit.Keyed;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    YamlConfiguration configuration = YamlConfiguration.single(this, "config.yml");
    configuration.get("test");
  }
}

