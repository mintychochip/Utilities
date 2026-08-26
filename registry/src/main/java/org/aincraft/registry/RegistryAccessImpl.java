package org.aincraft.registry;

import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;

public final class RegistryAccessImpl implements RegistryAccess {

  private final Map<Key,Registry<?>> registrar = new HashMap<>();
  @SuppressWarnings("unchecked")
  @Override
  public <T extends Keyed> Registry<T> getRegistry(RegistryAccessKey<T> registryKey) {
    return (Registry<T>) registrar.get(registryKey.getKey());
  }

  @Override
  public <T extends Keyed> void addRegistry(RegistryAccessKey<T> key, Registry<T> registry) {
    registrar.put(key.getKey(),registry);
  }
}
