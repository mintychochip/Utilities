package org.aincraft;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.aincraft.config.YamlConfiguration;
import org.aincraft.db.ConnectionSource;
import org.aincraft.db.DatabaseType;
import org.aincraft.db.paper.ConnectionSourceFactory;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    YamlConfiguration configuration = YamlConfiguration.single(this, "config.yml");
    String test = configuration.getString("test");
    Bukkit.getLogger().info(test);

    ConnectionSourceFactory factory = ConnectionSourceFactory.create(this);
    ConfigurationSection dbSection = configuration.getConfigurationSection("database");
    if (dbSection == null) {
      dbSection = createDefaultDatabaseSection();
    }
    try (ConnectionSource source = factory.create(DatabaseType.SQLITE, dbSection)) {
      Connection c = ((ConnectionSource.SQLConnectionSource) source).getConnection();
      try (Statement s = c.createStatement()) {
        s.execute("CREATE TABLE IF NOT EXISTS smoke (id INTEGER PRIMARY KEY, v TEXT)");
        s.execute("INSERT OR REPLACE INTO smoke (id, v) VALUES (1, 'ok')");
        try (ResultSet rs = s.executeQuery("SELECT v FROM smoke WHERE id=1")) {
          if (rs.next()) {
            Bukkit.getLogger().info("smoke=" + rs.getString(1));
          }
        }
      }
    } catch (Exception e) {
      Bukkit.getLogger().severe("smoke failed: " + e);
    }
  }

  private ConfigurationSection createDefaultDatabaseSection() {
    org.bukkit.configuration.file.YamlConfiguration c = new org.bukkit.configuration.file.YamlConfiguration();
    c.set("path", "smoke.db");
    return c;
  }
}
