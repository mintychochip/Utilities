package org.aincraft.common.inventory;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

/**
 * Immutable, metadata-only descriptor for a data component. These objects carry only the key and
 * value type; they do not wrap or back any server object and may therefore live in {@code :common}.
 */
public class StandardDataComponentType<T> implements DataComponentType<T> {

  private final Key key;
  private final Class<T> type;

  public StandardDataComponentType(@NotNull Key key, @NotNull Class<T> type) {
    this.key = key;
    this.type = type;
  }

  @Override
  public @NotNull Key key() {
    return key;
  }

  @Override
  public @NotNull Class<T> type() {
    return type;
  }

  @Override
  public String toString() {
    return "DataComponentType{" + key + ", " + type.getName() + '}';
  }

  @Override
  public boolean equals(Object o) {
    return this == o || (o instanceof DataComponentType<?> that && key.equals(that.key()));
  }

  @Override
  public int hashCode() {
    return key.hashCode();
  }
}
