package org.aincraft.api.domain.persistence;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * The standard set of {@link PersistentDataType} values supported in v1.
 *
 * <p>All types are singletons and are equal by their value class.
 */
public final class PersistentDataTypes {

  public static final PersistentDataType<Byte> BYTE = new StandardType<>(Byte.class);
  public static final PersistentDataType<Short> SHORT = new StandardType<>(Short.class);
  public static final PersistentDataType<Integer> INTEGER = new StandardType<>(Integer.class);
  public static final PersistentDataType<Long> LONG = new StandardType<>(Long.class);
  public static final PersistentDataType<Float> FLOAT = new StandardType<>(Float.class);
  public static final PersistentDataType<Double> DOUBLE = new StandardType<>(Double.class);
  public static final PersistentDataType<Boolean> BOOLEAN = new StandardType<>(Boolean.class);
  public static final PersistentDataType<String> STRING = new StandardType<>(String.class);
  public static final PersistentDataType<byte[]> BYTE_ARRAY = new StandardType<>(byte[].class);
  public static final PersistentDataType<int[]> INTEGER_ARRAY = new StandardType<>(int[].class);
  public static final PersistentDataType<long[]> LONG_ARRAY = new StandardType<>(long[].class);

  private PersistentDataTypes() {}

  private static final class StandardType<T> implements PersistentDataType<T> {

    private final Class<T> type;

    StandardType(@NotNull Class<T> type) {
      this.type = Objects.requireNonNull(type, "type cannot be null");
    }

    @Override
    public @NotNull Class<T> type() {
      return type;
    }

    @Override
    public String toString() {
      return "PersistentDataType{" + type.getName() + '}';
    }

    @Override
    public boolean equals(Object o) {
      return this == o || (o instanceof PersistentDataType<?> that && type.equals(that.type()));
    }

    @Override
    public int hashCode() {
      return type.hashCode();
    }
  }
}
