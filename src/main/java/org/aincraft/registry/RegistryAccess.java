package org.aincraft.registry;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;

public sealed interface RegistryAccess permits RegistryAccessImpl {

  <T extends Keyed> Registry<T> getRegistry(RegistryAccessKey<T> registryKey);

  <T extends Keyed> void addRegistry(RegistryAccessKey<T> key, Registry<T> registry);

  interface RegistryAccessKey<T extends Keyed> {

    Key getKey();
  }
}
