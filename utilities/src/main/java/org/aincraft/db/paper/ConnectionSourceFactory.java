package org.aincraft.db.paper;

import org.aincraft.db.ConnectionException;
import org.aincraft.db.ConnectionSource;
import org.aincraft.db.DatabaseType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

public interface ConnectionSourceFactory {

  static ConnectionSourceFactory create(Plugin plugin) {
    return new ConnectionSourceFactoryImpl(plugin);
  }

  ConnectionSource create(DatabaseType type, ConfigurationSection configuration)
      throws IllegalArgumentException, ConnectionException;
}
