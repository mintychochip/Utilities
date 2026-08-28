package org.aincraft.api.domain.inventory;

import net.kyori.adventure.text.Component;
import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.aincraft.api.domain.effect.Enchantment;
import org.aincraft.api.domain.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface ItemStack {

  @NotNull
  ItemType type();

  int amount();

  void setAmount(int amount);

  @Nullable
  Component displayName();

  @Nullable
  List<Component> lore();

  boolean hasItemMeta();

  @Nullable
  ItemMeta meta();

  void setMeta(@Nullable ItemMeta meta);

  boolean isSimilar(@Nullable ItemStack other);

  boolean isEmpty();

  @NotNull
  ItemStack clone();

  int maxStackSize();

  boolean editMeta(@NotNull java.util.function.Consumer<ItemMeta> consumer);

  /**
   * Edits the persistent data on this item's {@link ItemMeta}.
   *
   * @param consumer the consumer to operate on the persistent data container
   * @return true if the item had meta and the edit was applied
   */
  default boolean editPersistentData(
      @NotNull java.util.function.Consumer<PersistentDataContainer> consumer) {
    java.util.Objects.requireNonNull(consumer, "consumer cannot be null");
    return editMeta(meta -> consumer.accept(meta.persistentData()));
  }

  /**
   * Returns the persistent data container for this item's meta.
   *
   * @throws UnsupportedCapabilityException if this item has no meta
   */
  @NotNull
  default PersistentDataContainer persistentData() {
    ItemMeta meta = meta();
    if (meta == null) {
      throw new UnsupportedCapabilityException(Capability.PERSISTENT_DATA);
    }
    return meta.persistentData();
  }

  @NotNull
  ItemStack asOne();

  @NotNull
  ItemStack asQuantity(int amount);

  @NotNull
  ItemStack withAmount(int amount);

  default boolean hasData(@NotNull DataComponentType<?> type) {
    throw new UnsupportedCapabilityException(Capability.ITEM_DATA_COMPONENT);
  }

  default <T> @Nullable T getData(@NotNull DataComponentType<T> type) {
    throw new UnsupportedCapabilityException(Capability.ITEM_DATA_COMPONENT);
  }

  default <T> void setData(@NotNull DataComponentType<T> type, @Nullable T value) {
    throw new UnsupportedCapabilityException(Capability.ITEM_DATA_COMPONENT);
  }

  default void setData(@NotNull DataComponentType.NonValued type) {
    throw new UnsupportedCapabilityException(Capability.ITEM_DATA_COMPONENT);
  }

  default void resetData(@NotNull DataComponentType<?> type) {
    throw new UnsupportedCapabilityException(Capability.ITEM_DATA_COMPONENT);
  }

  @NotNull
  default java.util.Set<DataComponentType<?>> dataComponentTypes() {
    throw new UnsupportedCapabilityException(Capability.ITEM_DATA_COMPONENT);
  }

  @NotNull
  default java.util.Set<DataComponentType<?>> dataTypes() {
    return dataComponentTypes();
  }

  default boolean hasEnchant(@NotNull Enchantment enchantment) {
    throw new UnsupportedOperationException();
  }

  default int enchantLevel(@NotNull Enchantment enchantment) {
    throw new UnsupportedOperationException();
  }

  default @NotNull Map<Enchantment, Integer> enchantments() {
    throw new UnsupportedOperationException();
  }

  default void addEnchant(
      @NotNull Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
    throw new UnsupportedOperationException();
  }

  default void addEnchant(@NotNull Enchantment enchantment, int level) {
    addEnchant(enchantment, level, false);
  }

  default int removeEnchant(@NotNull Enchantment enchantment) {
    throw new UnsupportedOperationException();
  }
}
