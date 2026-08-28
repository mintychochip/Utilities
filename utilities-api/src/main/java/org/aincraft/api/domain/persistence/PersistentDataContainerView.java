package org.aincraft.api.domain.persistence;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * A read-only view of a {@link PersistentDataContainer}.
 *
 * <p>Exposes read operations and key enumeration. Platform adapters that support a native read-only
 * view (such as Paper's {@code PersistentDataContainerView}) should implement this interface.
 */
public interface PersistentDataContainerView {

  /**
   * Returns whether a value with the given key and type is present.
   *
   * @param key the key
   * @param type the expected type
   * @param <T> the value type
   * @return true if present
   */
  <T> boolean has(@NotNull Key key, @NotNull PersistentDataType<T> type);

  /**
   * Returns whether any value with the given key is present.
   *
   * @param key the key
   * @return true if any value is present
   */
  boolean has(@NotNull Key key);

  /**
   * Returns the value for the given key and type, or {@code null} if absent.
   *
   * @param key the key
   * @param type the expected type
   * @param <T> the value type
   * @return the value or null
   */
  <T> @Nullable T get(@NotNull Key key, @NotNull PersistentDataType<T> type);

  /**
   * Returns the value for the given key and type, or the provided default if absent.
   *
   * @param key the key
   * @param type the expected type
   * @param defaultValue fallback value
   * @param <T> the value type
   * @return the stored value or the default
   */
  default <T> @NotNull T getOrDefault(
      @NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T defaultValue) {
    T value = get(key, type);
    return value != null ? value : defaultValue;
  }

  /**
   * Returns all keys currently present in this view.
   *
   * @return an unmodifiable view of the keys
   */
  @NotNull
  Set<Key> keys();

  /**
   * Returns the number of keys present.
   *
   * @return the key count
   */
  default int size() {
    return keys().size();
  }

  /**
   * Returns true if this view contains no keys.
   *
   * @return true if empty
   */
  default boolean isEmpty() {
    return keys().isEmpty();
  }

  /**
   * Copies all entries from this view into the given container.
   *
   * @param other the target container
   */
  void copyTo(@NotNull PersistentDataContainer other);

  /**
   * Copies all entries from this view into the given container.
   *
   * @param other the target container
   * @param replace whether to overwrite existing keys
   */
  void copyTo(@NotNull PersistentDataContainer other, boolean replace);
}
