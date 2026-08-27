package org.aincraft.registry;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public sealed interface RegistryAccess permits RegistryAccessImpl {

  static RegistryAccess registryAccess(@NotNull Plugin plugin) {
    return RegistryAccessAccessor.getRegistryAccess(plugin);
  }

  <T extends Keyed> Registry<T> getRegistry(RegistryAccessKey<T> registryKey);

  <T extends Keyed> void addRegistry(RegistryAccessKey<T> key, Registry<T> registry);

  interface RegistryAccessKey<T extends Keyed> {

    static <T extends Keyed> RegistryAccessKey<T> create(Plugin plugin, String key) {
      return () -> new NamespacedKey(plugin, key);
    }

    Key getKey();
  }
}
