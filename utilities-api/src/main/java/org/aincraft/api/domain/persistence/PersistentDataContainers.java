package org.aincraft.api.domain.persistence;

import org.jetbrains.annotations.NotNull;

/**
 * Factory for detached {@link PersistentDataContainer} instances.
 *
 * <p>Containers created through this factory are in-memory only. They become persistent when copied
 * into a {@link PersistentDataHolder} on a supported platform.
 */
public final class PersistentDataContainers {

  private PersistentDataContainers() {}

  /**
   * Creates a new, empty, in-memory persistent data container.
   *
   * @return a new in-memory container
   */
  @NotNull
  public static PersistentDataContainer create() {
    return new MemoryPersistentDataContainer();
  }
}
