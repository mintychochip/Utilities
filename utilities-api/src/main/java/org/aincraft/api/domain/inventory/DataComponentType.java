package org.aincraft.api.domain.inventory;

import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

/**
 * A data component type, mirroring Paper's {@code DataComponentType}.
 *
 * <p>{@link DataComponentType.Valued} and {@link DataComponentType.NonValued} are nested marker
 * interfaces that distinguish components with an associated value from presence-only components.
 */
public interface DataComponentType<T> extends Keyed {

  @NotNull
  Class<T> type();

  default boolean isValued() {
    return this instanceof Valued;
  }

  /** A {@link DataComponentType} that has an associated value. */
  interface Valued<T> extends DataComponentType<T> {}

  /**
   * A {@link DataComponentType} that represents only presence or absence, with no associated value.
   */
  interface NonValued extends DataComponentType<Void> {

    @Override
    default @NotNull Class<Void> type() {
      return Void.class;
    }
  }
}
