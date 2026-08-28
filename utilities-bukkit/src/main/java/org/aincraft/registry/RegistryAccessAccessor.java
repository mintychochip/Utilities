package org.aincraft.registry;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.NotNull;

final class RegistryAccessAccessor {

  @NotNull
  static RegistryAccess getRegistryAccess(@NotNull Plugin plugin) {
    ServicesManager servicesManager = Bukkit.getServicesManager();
    RegisteredServiceProvider<RegistryAccess> registration =
        servicesManager.getRegistration(RegistryAccess.class);
    if (registration == null) {
      RegistryAccess access = new RegistryAccessImpl();
      servicesManager.register(RegistryAccess.class, access, plugin, ServicePriority.Highest);
      return access;
    }
    return registration.getProvider();
  }
}
