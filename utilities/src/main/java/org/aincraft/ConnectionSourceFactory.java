package org.aincraft;

import java.lang.reflect.Proxy;
import org.aincraft.db.ConnectionException;
import org.aincraft.db.ConnectionSource;
import org.aincraft.db.DatabaseType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

public interface ConnectionSourceFactory {

  static ConnectionSourceFactory create(Plugin plugin) {
    SQLConnectionSourceFactoryImpl sqlFactory = new SQLConnectionSourceFactoryImpl(
        plugin);
    MongoConnectionSourceFactoryImpl mongoFactory = new MongoConnectionSourceFactoryImpl();
    return (ConnectionSourceFactory) Proxy.newProxyInstance(plugin.getClass().getClassLoader(),
        new Class[]{ConnectionSourceFactory.class}, (proxy, method, args) -> {
          if ("create".equals(method.getName())) {
            DatabaseType type = (DatabaseType) args[0];
            if (type == DatabaseType.MONGO) {
              return method.invoke(mongoFactory,args);
            }
          }
          return method.invoke(sqlFactory,args);
        });
  }

  ConnectionSource create(DatabaseType type, ConfigurationSection configuration)
      throws IllegalArgumentException, ConnectionException;
}
