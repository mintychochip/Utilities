package org.aincraft.api.domain.persistence;

import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.jetbrains.annotations.NotNull;

/**
 * An object that can carry a {@link PersistentDataContainer}.
 *
 * <p>Implementations may throw {@link UnsupportedCapabilityException} with {@link
 * Capability#PERSISTENT_DATA} when the platform does not support persistence on the concrete object
 * (for example, Minestom {@code TileBlockState} in v1).
 */
public interface PersistentDataHolder {

  /**
   * Returns the persistent data container attached to this object.
   *
   * @return the container
   */
  @NotNull
  default PersistentDataContainer persistentData() {
    throw new UnsupportedCapabilityException(Capability.PERSISTENT_DATA);
  }
}
