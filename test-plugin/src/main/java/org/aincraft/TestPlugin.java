package org.aincraft;

import org.aincraft.config.YamlConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    YamlConfiguration configuration = YamlConfiguration.single(this, "config.yml");
    String test = configuration.getString("test");
    Bukkit.getLogger().info(test);
  }
}

