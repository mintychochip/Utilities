package org.aincraft.bukkit.persistence;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.persistence.PersistentDataContainer;
import org.aincraft.api.domain.persistence.PersistentDataType;
import org.aincraft.api.domain.persistence.PersistentDataTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A Bukkit-backed {@link PersistentDataContainer}.
 *
 * <p>Wraps {@link org.bukkit.persistence.PersistentDataContainer} and maps aincraft {@link
 * PersistentDataType} values to Bukkit {@link org.bukkit.persistence.PersistentDataType} constants
 * for the v1 type set.
 */
public final class BukkitPersistentDataContainer implements PersistentDataContainer {

  private static final Map<Class<?>, org.bukkit.persistence.PersistentDataType<?, ?>> BUKKIT_TYPES =
      new LinkedHashMap<>();

  static {
    BUKKIT_TYPES.put(Byte.class, org.bukkit.persistence.PersistentDataType.BYTE);
    BUKKIT_TYPES.put(Short.class, org.bukkit.persistence.PersistentDataType.SHORT);
    BUKKIT_TYPES.put(Integer.class, org.bukkit.persistence.PersistentDataType.INTEGER);
    BUKKIT_TYPES.put(Long.class, org.bukkit.persistence.PersistentDataType.LONG);
    BUKKIT_TYPES.put(Float.class, org.bukkit.persistence.PersistentDataType.FLOAT);
    BUKKIT_TYPES.put(Double.class, org.bukkit.persistence.PersistentDataType.DOUBLE);
    BUKKIT_TYPES.put(Boolean.class, org.bukkit.persistence.PersistentDataType.BOOLEAN);
    BUKKIT_TYPES.put(String.class, org.bukkit.persistence.PersistentDataType.STRING);
    BUKKIT_TYPES.put(byte[].class, org.bukkit.persistence.PersistentDataType.BYTE_ARRAY);
    BUKKIT_TYPES.put(int[].class, org.bukkit.persistence.PersistentDataType.INTEGER_ARRAY);
    BUKKIT_TYPES.put(long[].class, org.bukkit.persistence.PersistentDataType.LONG_ARRAY);
  }

  private final org.bukkit.persistence.PersistentDataContainer delegate;

  public BukkitPersistentDataContainer(
      @NotNull org.bukkit.persistence.PersistentDataContainer delegate) {
    this.delegate = Objects.requireNonNull(delegate, "delegate cannot be null");
  }

  @Override
  public <T> boolean has(@NotNull Key key, @NotNull PersistentDataType<T> type) {
    org.bukkit.NamespacedKey nsk = toBukkitKey(key);
    org.bukkit.persistence.PersistentDataType<?, ?> pdt = toBukkitType(type);
    return pdt != null && delegate.has(nsk, castType(pdt));
  }

  @Override
  public boolean has(@NotNull Key key) {
    return delegate.has(toBukkitKey(key));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> @Nullable T get(@NotNull Key key, @NotNull PersistentDataType<T> type) {
    org.bukkit.NamespacedKey nsk = toBukkitKey(key);
    org.bukkit.persistence.PersistentDataType<?, T> pdt = toBukkitTypeChecked(type);
    return pdt != null ? delegate.get(nsk, pdt) : null;
  }

  @Override
  public <T> void set(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T value) {
    org.bukkit.NamespacedKey nsk = toBukkitKey(key);
    org.bukkit.persistence.PersistentDataType<?, T> pdt = toBukkitTypeChecked(type);
    if (pdt == null) {
      throw new IllegalArgumentException("Unsupported PDC value type: " + type.type().getName());
    }
    delegate.set(nsk, pdt, value);
  }

  @Override
  public void remove(@NotNull Key key) {
    delegate.remove(toBukkitKey(key));
  }

  @Override
  public @NotNull Set<Key> keys() {
    Set<Key> result = new HashSet<>();
    for (org.bukkit.NamespacedKey nsk : delegate.getKeys()) {
      result.add(Key.key(nsk.getNamespace(), nsk.getKey()));
    }
    return result;
  }

  @Override
  public void copyTo(@NotNull PersistentDataContainer other) {
    copyTo(other, true);
  }

  @Override
  public void copyTo(@NotNull PersistentDataContainer other, boolean replace) {
    if (other instanceof BukkitPersistentDataContainer that) {
      delegate.copyTo(that.delegate, replace);
      return;
    }

    for (org.bukkit.NamespacedKey nsk : delegate.getKeys()) {
      Key key = Key.key(nsk.getNamespace(), nsk.getKey());
      if (!replace && other.has(key)) {
        continue;
      }
      for (Map.Entry<Class<?>, org.bukkit.persistence.PersistentDataType<?, ?>> e :
          BUKKIT_TYPES.entrySet()) {
        @SuppressWarnings("unchecked")
        org.bukkit.persistence.PersistentDataType<?, Object> pdt =
            (org.bukkit.persistence.PersistentDataType<?, Object>) e.getValue();
        if (delegate.has(nsk, pdt)) {
          Object value = delegate.get(nsk, pdt);
          @SuppressWarnings("unchecked")
          PersistentDataType<Object> ainType = (PersistentDataType<Object>) typeFor(e.getKey());
          other.set(key, ainType, value);
          break;
        }
      }
    }
  }

  org.bukkit.persistence.PersistentDataContainer getDelegate() {
    return delegate;
  }

  private static @NotNull org.bukkit.NamespacedKey toBukkitKey(@NotNull Key key) {
    return new org.bukkit.NamespacedKey(key.namespace(), key.value());
  }

  @SuppressWarnings("unchecked")
  private static <T> org.bukkit.persistence.PersistentDataType<?, T> toBukkitTypeChecked(
      @NotNull PersistentDataType<T> type) {
    org.bukkit.persistence.PersistentDataType<?, ?> pdt = toBukkitType(type);
    return pdt != null ? (org.bukkit.persistence.PersistentDataType<?, T>) pdt : null;
  }

  private static @Nullable org.bukkit.persistence.PersistentDataType<?, ?> toBukkitType(
      @NotNull PersistentDataType<?> type) {
    return BUKKIT_TYPES.get(type.type());
  }

  private static @Nullable PersistentDataType<?> typeFor(@NotNull Class<?> typeClass) {
    if (typeClass == Byte.class) return PersistentDataTypes.BYTE;
    if (typeClass == Short.class) return PersistentDataTypes.SHORT;
    if (typeClass == Integer.class) return PersistentDataTypes.INTEGER;
    if (typeClass == Long.class) return PersistentDataTypes.LONG;
    if (typeClass == Float.class) return PersistentDataTypes.FLOAT;
    if (typeClass == Double.class) return PersistentDataTypes.DOUBLE;
    if (typeClass == Boolean.class) return PersistentDataTypes.BOOLEAN;
    if (typeClass == String.class) return PersistentDataTypes.STRING;
    if (typeClass == byte[].class) return PersistentDataTypes.BYTE_ARRAY;
    if (typeClass == int[].class) return PersistentDataTypes.INTEGER_ARRAY;
    if (typeClass == long[].class) return PersistentDataTypes.LONG_ARRAY;
    return null;
  }

  @SuppressWarnings("unchecked")
  private static <T> org.bukkit.persistence.PersistentDataType<T, T> castType(
      org.bukkit.persistence.PersistentDataType<?, ?> pdt) {
    return (org.bukkit.persistence.PersistentDataType<T, T>) pdt;
  }

  @SuppressWarnings("unchecked")
  private static <T> @NotNull Class<T> rawClass(Class<?> cls) {
    return (Class<T>) cls;
  }
}
