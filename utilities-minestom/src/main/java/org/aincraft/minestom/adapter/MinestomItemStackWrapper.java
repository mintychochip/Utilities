package org.aincraft.minestom.adapter;

import net.kyori.adventure.text.Component;
import org.aincraft.api.domain.effect.Enchantment;
import org.aincraft.api.domain.inventory.ItemMeta;
import org.aincraft.api.domain.inventory.ItemStack;
import org.aincraft.api.domain.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public final class MinestomItemStackWrapper implements ItemStack {

  private net.minestom.server.item.ItemStack item;

  public MinestomItemStackWrapper(@NotNull net.minestom.server.item.ItemStack item) {
    this.item = Objects.requireNonNull(item, "item cannot be null");
  }

  public synchronized @NotNull net.minestom.server.item.ItemStack getMinestomItemStack() {
    return item;
  }

  synchronized void replace(@NotNull net.minestom.server.item.ItemStack item) {
    this.item = Objects.requireNonNull(item, "item cannot be null");
  }

  @Override
  public @NotNull ItemType type() {
    return MinestomAdapters.adapt(item.material());
  }

  @Override
  public int amount() {
    return item.amount();
  }

  @Override
  public void setAmount(int amount) {
    item = item.withAmount(amount);
  }

  @Override
  public @Nullable Component displayName() {
    ItemMeta meta = meta();
    return meta == null ? null : meta.displayName();
  }

  @SuppressWarnings("unchecked")
  private static <T> net.minestom.server.component.DataComponent<T> component(
      @NotNull org.aincraft.api.domain.inventory.DataComponentType<T> type) {
    net.minestom.server.component.DataComponent<?> component =
        net.minestom.server.component.DataComponent.fromKey(type.key());
    if (component == null) {
      throw new UnsupportedOperationException("Unknown Minestom data component: " + type.key());
    }
    return (net.minestom.server.component.DataComponent<T>) component;
  }

  @Override
  public boolean hasData(@NotNull org.aincraft.api.domain.inventory.DataComponentType<?> type) {
    return item.has(component(type));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> @Nullable T getData(
      @NotNull org.aincraft.api.domain.inventory.DataComponentType<T> type) {
    net.minestom.server.component.DataComponent<T> component = component(type);
    if (type.type() == Void.class) return null;
    Object raw = item.get(component);
    return raw == null ? null : (T) MinestomDataComponentAdapter.fromPlatform(raw, type.type());
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> void setData(
      @NotNull org.aincraft.api.domain.inventory.DataComponentType<T> type, @Nullable T value) {
    net.minestom.server.component.DataComponent<T> component = component(type);
    if (type.type() == Void.class) {
      item =
          value == null
              ? item.without(component)
              : item.with(
                  (net.minestom.server.component.DataComponent<net.minestom.server.utils.Unit>)
                      component);
    } else {
      Object converted = value == null ? null : MinestomDataComponentAdapter.toPlatform(value);
      item =
          converted == null
              ? item.without(component)
              : item.with(
                  (net.minestom.server.component.DataComponent<Object>) component, converted);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public void setData(@NotNull org.aincraft.api.domain.inventory.DataComponentType.NonValued type) {
    item =
        item.with(
            (net.minestom.server.component.DataComponent<net.minestom.server.utils.Unit>)
                (net.minestom.server.component.DataComponent<?>) component(type));
  }

  @Override
  public void resetData(@NotNull org.aincraft.api.domain.inventory.DataComponentType<?> type) {
    item = item.reset(component(type));
  }

  @Override
  public @NotNull java.util.Set<org.aincraft.api.domain.inventory.DataComponentType<?>>
      dataComponentTypes() {
    java.util.Set<org.aincraft.api.domain.inventory.DataComponentType<?>> result =
        new java.util.HashSet<>();
    for (net.minestom.server.component.DataComponent.Value entry :
        item.componentPatch().entrySet()) {
      result.add(
          new org.aincraft.api.domain.inventory.DataComponentType<Object>() {
            @Override
            public @NotNull net.kyori.adventure.key.Key key() {
              return entry.component().key();
            }

            @Override
            public @NotNull Class<Object> type() {
              return Object.class;
            }
          });
    }
    return java.util.Set.copyOf(result);
  }

  @Override
  public @NotNull java.util.Set<org.aincraft.api.domain.inventory.DataComponentType<?>>
      dataTypes() {
    return dataComponentTypes();
  }

  @Override
  public @Nullable List<Component> lore() {
    ItemMeta meta = meta();
    return meta == null ? null : meta.lore();
  }

  @Override
  public boolean hasItemMeta() {
    return !item.componentPatch().isEmpty();
  }

  @Override
  public @Nullable ItemMeta meta() {
    return hasItemMeta() ? new MinestomItemMetaWrapper(this) : null;
  }

  @Override
  public void setMeta(@Nullable ItemMeta meta) {
    if (meta == null) {
      item = net.minestom.server.item.ItemStack.of(item.material(), item.amount());
    } else if (meta instanceof MinestomItemMetaWrapper wrapper) {
      replace(wrapper.snapshot());
    } else {
      throw new IllegalArgumentException(
          "ItemMeta is not a MinestomItemMetaWrapper: " + meta.getClass().getName());
    }
  }

  @Override
  public boolean isSimilar(@Nullable ItemStack other) {
    return other instanceof MinestomItemStackWrapper wrapper && item.isSimilar(wrapper.item);
  }

  @Override
  public boolean isEmpty() {
    return item.isAir() || item.amount() <= 0;
  }

  @Override
  public @NotNull ItemStack clone() {
    return new MinestomItemStackWrapper(item);
  }

  @Override
  public int maxStackSize() {
    return item.maxStackSize();
  }

  @Override
  public boolean editMeta(@NotNull Consumer<ItemMeta> consumer) {
    Objects.requireNonNull(consumer, "consumer cannot be null");
    MinestomItemMetaWrapper wrapper = new MinestomItemMetaWrapper(this);
    consumer.accept(wrapper);
    replace(wrapper.snapshot());
    return true;
  }

  @Override
  public @NotNull ItemStack asOne() {
    return withAmount(1);
  }

  @Override
  public @NotNull ItemStack asQuantity(int amount) {
    return withAmount(amount);
  }

  @Override
  public @NotNull ItemStack withAmount(int amount) {
    return new MinestomItemStackWrapper(item.withAmount(amount));
  }

  @Override
  public boolean hasEnchant(@NotNull Enchantment enchantment) {
    return meta() != null && meta().hasEnchant(enchantment);
  }

  @Override
  public int enchantLevel(@NotNull Enchantment enchantment) {
    return meta() == null ? 0 : meta().enchantLevel(enchantment);
  }

  @Override
  public @NotNull Map<Enchantment, Integer> enchantments() {
    return meta() == null ? Map.of() : meta().enchantments();
  }

  @Override
  public void addEnchant(
      @NotNull Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
    editMeta(meta -> meta.addEnchant(enchantment, level, ignoreLevelRestriction));
  }

  @Override
  public int removeEnchant(@NotNull Enchantment enchantment) {
    int level = enchantLevel(enchantment);
    if (level > 0) editMeta(meta -> meta.removeEnchant(enchantment));
    return level;
  }

  @Override
  public boolean equals(Object other) {
    return this == other
        || (other instanceof MinestomItemStackWrapper wrapper && item.equals(wrapper.item));
  }

  @Override
  public int hashCode() {
    return item.hashCode();
  }

  @Override
  public String toString() {
    return "MinestomItemStackWrapper{type=" + type() + ", amount=" + amount() + "}";
  }
}
