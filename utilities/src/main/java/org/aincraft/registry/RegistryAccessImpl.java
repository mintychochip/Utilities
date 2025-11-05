package org.aincraft.registry;

import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.key.Keyed;

public class RegistryAccessImpl implements RegistryAccess {

  private final RegistryContainer container;
  private RegistryAccessImpl(RegistryContainer container) {
    this.container = container;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Keyed> Registry<T> getRegistry(RegistryAccessKey<T> registryKey) {
    return (Registry<T>) container.getRegistry(registryKey.getKey());
  }
}
