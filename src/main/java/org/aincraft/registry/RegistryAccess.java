package org.aincraft.registry;

import net.kyori.adventure.key.Keyed;

public interface RegistryAccess {

  <T extends Keyed> Registry<T> getRegistry(RegistryAccessKey<T> registryKey);

  interface RegistryAccessKey<T extends Keyed> {

    String getKey();
  }
}
