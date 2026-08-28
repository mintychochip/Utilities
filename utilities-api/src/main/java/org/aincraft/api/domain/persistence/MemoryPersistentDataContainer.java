package org.aincraft.api.domain.persistence;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A detached, in-memory {@link PersistentDataContainer}.
 *
 * <p>This implementation keeps values in a typed map. It does not persist by itself; platform
 * adapters copy its contents into a platform-backed container.
 */
public final class MemoryPersistentDataContainer implements PersistentDataContainer {

  private final Map<Key, TypedValue<?>> values = new LinkedHashMap<>();

  @Override
  public <T> boolean has(@NotNull Key key, @NotNull PersistentDataType<T> type) {
    TypedValue<?> current = values.get(key);
    return current != null && current.type.equals(type);
  }

  @Override
  public boolean has(@NotNull Key key) {
    return values.containsKey(key);
  }

  @Override
  public <T> @Nullable T get(@NotNull Key key, @NotNull PersistentDataType<T> type) {
    TypedValue<?> current = values.get(key);
    if (current == null) {
      return null;
    }
    if (!current.type.equals(type)) {
      return null;
    }
    return (T) current.value;
  }

  @Override
  public <T> void set(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T value) {
    values.put(key, new TypedValue<>(type, value));
  }

  @Override
  public void remove(@NotNull Key key) {
    values.remove(key);
  }

  @Override
  public @NotNull Set<Key> keys() {
    return Collections.unmodifiableSet(values.keySet());
  }

  @Override
  public void copyTo(@NotNull PersistentDataContainer other) {
    copyTo(other, true);
  }

  @Override
  public void copyTo(@NotNull PersistentDataContainer other, boolean replace) {
    for (Map.Entry<Key, TypedValue<?>> entry : values.entrySet()) {
      if (replace || !other.has(entry.getKey())) {
        setOnTarget(entry.getValue(), other, entry.getKey());
      }
    }
  }

  private static <T> void setOnTarget(
      @NotNull TypedValue<T> value, @NotNull PersistentDataContainer target, @NotNull Key key) {
    target.set(key, value.type, value.value);
  }

  private record TypedValue<T>(@NotNull PersistentDataType<T> type, @NotNull T value) {
    TypedValue {
      Objects.requireNonNull(type, "type cannot be null");
      Objects.requireNonNull(value, "value cannot be null");
    }
  }
}
