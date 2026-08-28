package org.aincraft.api.domain.persistence;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * A typed, persistent key-value container.
 *
 * <p>Implementations are backed by platform-specific persistence (Bukkit/Spigot/Paper {@code
 * PersistentDataContainer}, Minestom tags/NBT, or an in-memory map for detached use).
 */
public interface PersistentDataContainer extends PersistentDataContainerView {

  /**
   * Returns true if a value with the given key and type is present.
   *
   * @param key the key
   * @param type the expected type
   * @param <T> the value type
   * @return true if present
   */
  <T> boolean has(@NotNull Key key, @NotNull PersistentDataType<T> type);

  /**
   * Returns true if any value with the given key is present.
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
   * Stores a value under the given key. The value must not be {@code null}; use {@link
   * #remove(Key)} to delete a value.
   *
   * @param key the key
   * @param type the type of the value to store
   * @param value the value to store
   * @param <T> the value type
   */
  <T> void set(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T value);

  /**
   * Removes the value associated with the given key, if any.
   *
   * @param key the key
   */
  void remove(@NotNull Key key);

  /**
   * Returns all keys currently present in this container.
   *
   * @return an unmodifiable view of the keys
   */
  @NotNull
  Set<Key> keys();

  /**
   * Returns the number of keys present.
   *
   * @return the number of keys
   */
  default int size() {
    return keys().size();
  }

  /**
   * Returns true if this container contains no values.
   *
   * @return true if empty
   */
  default boolean isEmpty() {
    return keys().isEmpty();
  }

  /**
   * Copies all entries from this container into the given one, replacing existing keys.
   *
   * @param other the target container
   */
  void copyTo(@NotNull PersistentDataContainer other);

  /**
   * Copies all entries from this container into the given one.
   *
   * @param other the target container
   * @param replace whether to overwrite existing keys
   */
  void copyTo(@NotNull PersistentDataContainer other, boolean replace);
}
