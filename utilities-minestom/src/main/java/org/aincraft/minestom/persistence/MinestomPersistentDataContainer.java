package org.aincraft.minestom.persistence;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.ByteArrayBinaryTag;
import net.kyori.adventure.nbt.ByteBinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.DoubleBinaryTag;
import net.kyori.adventure.nbt.FloatBinaryTag;
import net.kyori.adventure.nbt.IntArrayBinaryTag;
import net.kyori.adventure.nbt.IntBinaryTag;
import net.kyori.adventure.nbt.LongArrayBinaryTag;
import net.kyori.adventure.nbt.LongBinaryTag;
import net.kyori.adventure.nbt.NumberBinaryTag;
import net.kyori.adventure.nbt.ShortBinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagHandler;
import org.aincraft.api.domain.persistence.PersistentDataContainer;
import org.aincraft.api.domain.persistence.PersistentDataType;
import org.aincraft.api.domain.persistence.PersistentDataTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A Minestom-backed {@link PersistentDataContainer}.
 *
 * <p>Values are stored under a single NBT compound tag keyed {@code "aincraft:pdc"}. Each entry is
 * itself a compound with a {@code "type"} string and a {@code "value"} tag, allowing unambiguous
 * type detection (for example {@code Byte} vs {@code Boolean}).
 */
public final class MinestomPersistentDataContainer implements PersistentDataContainer {

  private static final String PDC_ROOT_KEY = "aincraft:pdc";
  private static final Tag<BinaryTag> PDC_ROOT = Tag.NBT(PDC_ROOT_KEY);
  private static final String TYPE_FIELD = "type";
  private static final String VALUE_FIELD = "value";

  private static final Map<Class<?>, TypeHandler<?>> HANDLERS_BY_CLASS = new LinkedHashMap<>();
  private static final Map<String, TypeHandler<?>> HANDLERS_BY_NAME = new LinkedHashMap<>();
  private static final Map<String, PersistentDataType<?>> TYPES_BY_NAME = new LinkedHashMap<>();

  static {
    register(
        Byte.class,
        "byte",
        ByteBinaryTag::byteBinaryTag,
        tag -> ((NumberBinaryTag) tag).byteValue(),
        PersistentDataTypes.BYTE);
    register(
        Short.class,
        "short",
        ShortBinaryTag::shortBinaryTag,
        tag -> ((NumberBinaryTag) tag).shortValue(),
        PersistentDataTypes.SHORT);
    register(
        Integer.class,
        "int",
        IntBinaryTag::intBinaryTag,
        tag -> ((NumberBinaryTag) tag).intValue(),
        PersistentDataTypes.INTEGER);
    register(
        Long.class,
        "long",
        LongBinaryTag::longBinaryTag,
        tag -> ((NumberBinaryTag) tag).longValue(),
        PersistentDataTypes.LONG);
    register(
        Float.class,
        "float",
        FloatBinaryTag::floatBinaryTag,
        tag -> ((NumberBinaryTag) tag).floatValue(),
        PersistentDataTypes.FLOAT);
    register(
        Double.class,
        "double",
        DoubleBinaryTag::doubleBinaryTag,
        tag -> ((NumberBinaryTag) tag).doubleValue(),
        PersistentDataTypes.DOUBLE);
    register(
        Boolean.class,
        "boolean",
        value -> ByteBinaryTag.byteBinaryTag(value ? (byte) 1 : (byte) 0),
        tag -> ((NumberBinaryTag) tag).byteValue() != 0,
        PersistentDataTypes.BOOLEAN);
    register(
        String.class,
        "string",
        StringBinaryTag::stringBinaryTag,
        tag -> ((StringBinaryTag) tag).value(),
        PersistentDataTypes.STRING);
    register(
        byte[].class,
        "byte_array",
        ByteArrayBinaryTag::byteArrayBinaryTag,
        tag -> ((ByteArrayBinaryTag) tag).value(),
        PersistentDataTypes.BYTE_ARRAY);
    register(
        int[].class,
        "int_array",
        IntArrayBinaryTag::intArrayBinaryTag,
        tag -> ((IntArrayBinaryTag) tag).value(),
        PersistentDataTypes.INTEGER_ARRAY);
    register(
        long[].class,
        "long_array",
        LongArrayBinaryTag::longArrayBinaryTag,
        tag -> ((LongArrayBinaryTag) tag).value(),
        PersistentDataTypes.LONG_ARRAY);
  }

  private final TagHandler handler;
  private final Supplier<ItemStack> itemGetter;
  private final Consumer<ItemStack> itemSetter;

  /** Back this container by a live {@link TagHandler} (typically an entity). */
  public MinestomPersistentDataContainer(@NotNull TagHandler handler) {
    this.handler = Objects.requireNonNull(handler, "handler cannot be null");
    this.itemGetter = null;
    this.itemSetter = null;
  }

  /** Back this container by an immutable {@link ItemStack} that must be replaced after writes. */
  public MinestomPersistentDataContainer(
      @NotNull Supplier<ItemStack> itemGetter, @NotNull Consumer<ItemStack> itemSetter) {
    this.handler = null;
    this.itemGetter = Objects.requireNonNull(itemGetter, "itemGetter cannot be null");
    this.itemSetter = Objects.requireNonNull(itemSetter, "itemSetter cannot be null");
  }

  @Override
  public <T> boolean has(@NotNull Key key, @NotNull PersistentDataType<T> type) {
    TypeHandler<T> handler = handlerFor(type);
    if (handler == null) {
      return false;
    }
    CompoundBinaryTag root = root();
    if (!root.contains(key.asString())) {
      return false;
    }
    BinaryTag markerTag = root.get(key.asString());
    if (!(markerTag instanceof CompoundBinaryTag marker)) {
      return false;
    }
    String stored = marker.getString(TYPE_FIELD, null);
    return handler.name.equals(stored);
  }

  @Override
  public boolean has(@NotNull Key key) {
    return root().contains(key.asString());
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> @Nullable T get(@NotNull Key key, @NotNull PersistentDataType<T> type) {
    TypeHandler<T> handler = handlerFor(type);
    if (handler == null) {
      return null;
    }
    CompoundBinaryTag root = root();
    if (!root.contains(key.asString())) {
      return null;
    }
    BinaryTag markerTag = root.get(key.asString());
    if (!(markerTag instanceof CompoundBinaryTag marker)) {
      return null;
    }
    String stored = marker.getString(TYPE_FIELD, null);
    if (!handler.name.equals(stored)) {
      return null;
    }
    try {
      return (T) handler.decoder.apply(marker.get(VALUE_FIELD));
    } catch (ClassCastException e) {
      return null;
    }
  }

  @Override
  public <T> void set(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T value) {
    TypeHandler<T> handler = handlerFor(type);
    if (handler == null) {
      throw new IllegalArgumentException("Unsupported PDC type: " + type.type().getName());
    }
    CompoundBinaryTag marker =
        CompoundBinaryTag.empty()
            .putString(TYPE_FIELD, handler.name)
            .put(VALUE_FIELD, handler.encoder.apply(value));
    writeRoot(root().put(key.asString(), marker));
  }

  @Override
  public void remove(@NotNull Key key) {
    if (!root().contains(key.asString())) {
      return;
    }
    writeRoot(root().remove(key.asString()));
  }

  @Override
  public @NotNull Set<Key> keys() {
    Set<Key> result = new HashSet<>();
    for (String key : root().keySet()) {
      try {
        result.add(Key.key(key));
      } catch (IllegalArgumentException ignored) {
        // skip malformed keys
      }
    }
    return Collections.unmodifiableSet(result);
  }

  @Override
  public void copyTo(@NotNull PersistentDataContainer other) {
    copyTo(other, true);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void copyTo(@NotNull PersistentDataContainer other, boolean replace) {
    if (other instanceof MinestomPersistentDataContainer that) {
      CompoundBinaryTag targetRoot = that.root();
      CompoundBinaryTag merged = targetRoot;
      for (String key : root().keySet()) {
        if (!replace && targetRoot.contains(key)) {
          continue;
        }
        merged = merged.put(key, root().get(key));
      }
      that.writeRoot(merged);
      return;
    }

    for (String key : root().keySet()) {
      Key aKey = Key.key(key);
      if (!replace && other.has(aKey)) {
        continue;
      }
      BinaryTag markerTag = root().get(key);
      if (!(markerTag instanceof CompoundBinaryTag marker)) {
        continue;
      }
      String typeName = marker.getString(TYPE_FIELD, null);
      TypeHandler<?> typeHandler = HANDLERS_BY_NAME.get(typeName);
      PersistentDataType<?> pdType = TYPES_BY_NAME.get(typeName);
      if (typeHandler == null || pdType == null) {
        continue;
      }
      try {
        Object value = typeHandler.decoder.apply(marker.get(VALUE_FIELD));
        other.set(aKey, (PersistentDataType<Object>) pdType, value);
      } catch (ClassCastException ignored) {
      }
    }
  }

  private @NotNull CompoundBinaryTag root() {
    BinaryTag tag;
    if (handler != null) {
      tag = handler.getTag(PDC_ROOT);
    } else {
      tag = itemGetter.get().getTag(PDC_ROOT);
    }
    return tag instanceof CompoundBinaryTag compound ? compound : CompoundBinaryTag.empty();
  }

  private void writeRoot(@NotNull CompoundBinaryTag compound) {
    if (handler != null) {
      if (compound.isEmpty()) {
        handler.removeTag(PDC_ROOT);
      } else {
        handler.setTag(PDC_ROOT, compound);
      }
    } else {
      ItemStack current = itemGetter.get();
      ItemStack next = current.withTag(PDC_ROOT, compound);
      itemSetter.accept(next);
    }
  }

  @SuppressWarnings("unchecked")
  private static <T> @Nullable TypeHandler<T> handlerFor(@NotNull PersistentDataType<T> type) {
    TypeHandler<?> h = HANDLERS_BY_CLASS.get(type.type());
    return h != null ? (TypeHandler<T>) h : null;
  }

  private static <T> void register(
      @NotNull Class<T> type,
      @NotNull String name,
      @NotNull Function<T, BinaryTag> encoder,
      @NotNull Function<BinaryTag, T> decoder,
      @NotNull PersistentDataType<T> pdType) {
    TypeHandler<T> handler = new TypeHandler<>(name, encoder, decoder);
    HANDLERS_BY_CLASS.put(type, handler);
    HANDLERS_BY_NAME.put(name, handler);
    TYPES_BY_NAME.put(name, pdType);
  }

  private record TypeHandler<T>(
      @NotNull String name,
      @NotNull Function<T, BinaryTag> encoder,
      @NotNull Function<BinaryTag, T> decoder) {}
}
