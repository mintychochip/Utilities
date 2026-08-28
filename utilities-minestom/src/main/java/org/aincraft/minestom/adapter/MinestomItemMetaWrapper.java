package org.aincraft.minestom.adapter;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponent;
import net.minestom.server.component.DataComponents;
import net.minestom.server.item.component.AttributeList;
import net.minestom.server.item.component.CustomModelData;
import net.minestom.server.item.component.EnchantmentList;
import net.minestom.server.item.component.TooltipDisplay;
import org.aincraft.api.domain.attribute.AttributeModifier;
import org.aincraft.api.domain.effect.Enchantment;
import org.aincraft.api.domain.inventory.DamageableItemMeta;
import org.aincraft.api.domain.inventory.DataComponentType;
import org.aincraft.api.domain.inventory.EquipmentSlot;
import org.aincraft.api.domain.inventory.ItemFlag;
import org.aincraft.api.domain.persistence.PersistentDataContainer;
import org.aincraft.minestom.persistence.MinestomPersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class MinestomItemMetaWrapper implements DamageableItemMeta {

  private final MinestomItemStackWrapper owner;

  public MinestomItemMetaWrapper(@NotNull MinestomItemStackWrapper owner) {
    this.owner = Objects.requireNonNull(owner, "owner cannot be null");
  }

  @NotNull
  net.minestom.server.item.ItemStack snapshot() {
    return owner.getMinestomItemStack();
  }

  private void replace(@NotNull net.minestom.server.item.ItemStack item) {
    owner.replace(item);
  }

  @SuppressWarnings("unchecked")
  private static <T> DataComponent<T> component(@NotNull DataComponentType<T> type) {
    DataComponent<?> component = DataComponent.fromKey(type.key());
    if (component == null) {
      throw new UnsupportedOperationException("Unknown Minestom data component: " + type.key());
    }
    return (DataComponent<T>) component;
  }

  @SuppressWarnings("unchecked")
  private <T> T value(@NotNull DataComponent<T> component) {
    return owner.getMinestomItemStack().get(component);
  }

  @Override
  public @Nullable Component displayName() {
    return value(DataComponents.CUSTOM_NAME);
  }

  @Override
  public @Nullable Component customName() {
    return value(DataComponents.CUSTOM_NAME);
  }

  @Override
  public void customName(@Nullable Component name) {
    setDisplayName(name);
  }

  @Override
  public boolean hasCustomName() {
    return hasDisplayName();
  }

  @Override
  public @Nullable Component itemName() {
    return value(DataComponents.ITEM_NAME);
  }

  @Override
  public void itemName(@Nullable Component name) {
    replace(
        name == null
            ? owner.getMinestomItemStack().without(DataComponents.ITEM_NAME)
            : owner.getMinestomItemStack().with(DataComponents.ITEM_NAME, name));
  }

  @Override
  public boolean hasItemName() {
    return owner.getMinestomItemStack().has(DataComponents.ITEM_NAME);
  }

  @Override
  public void setDisplayName(@Nullable Component name) {
    replace(
        name == null
            ? owner.getMinestomItemStack().without(DataComponents.CUSTOM_NAME)
            : owner.getMinestomItemStack().with(DataComponents.CUSTOM_NAME, name));
  }

  @Override
  public @Nullable List<Component> lore() {
    return value(DataComponents.LORE);
  }

  @Override
  public void setLore(@Nullable List<Component> lore) {
    replace(
        lore == null
            ? owner.getMinestomItemStack().without(DataComponents.LORE)
            : owner.getMinestomItemStack().with(DataComponents.LORE, List.copyOf(lore)));
  }

  @Override
  public boolean hasDisplayName() {
    return owner.getMinestomItemStack().has(DataComponents.CUSTOM_NAME);
  }

  @Override
  public boolean hasLore() {
    return owner.getMinestomItemStack().has(DataComponents.LORE);
  }

  @Override
  public boolean isUnbreakable() {
    return owner.getMinestomItemStack().has(DataComponents.UNBREAKABLE);
  }

  @Override
  public void setUnbreakable(boolean unbreakable) {
    replace(
        unbreakable
            ? owner.getMinestomItemStack().with(DataComponents.UNBREAKABLE)
            : owner.getMinestomItemStack().without(DataComponents.UNBREAKABLE));
  }

  @Override
  public int customModelData() {
    CustomModelData data = value(DataComponents.CUSTOM_MODEL_DATA);
    return data == null || data.floats().isEmpty() ? 0 : Math.round(data.floats().getFirst());
  }

  @Override
  public void setCustomModelData(int data) {
    CustomModelData previous = value(DataComponents.CUSTOM_MODEL_DATA);
    CustomModelData replacement =
        new CustomModelData(
            List.of((float) data),
            previous == null ? List.of() : previous.flags(),
            previous == null ? List.of() : previous.strings(),
            previous == null ? List.of() : previous.colors());
    replace(owner.getMinestomItemStack().with(DataComponents.CUSTOM_MODEL_DATA, replacement));
  }

  @Override
  public int damage() {
    return owner.getMinestomItemStack().get(DataComponents.DAMAGE, 0);
  }

  @Override
  public void setDamage(int damage) {
    if (damage < 0) throw new IllegalArgumentException("Damage cannot be negative");
    replace(owner.getMinestomItemStack().with(DataComponents.DAMAGE, damage));
  }

  @Override
  public int maxDamage() {
    return owner.getMinestomItemStack().material().prototype().get(DataComponents.MAX_DAMAGE, 0);
  }

  @Override
  public boolean hasDamage() {
    return owner.getMinestomItemStack().has(DataComponents.DAMAGE);
  }

  private EnchantmentList enchantmentList() {
    EnchantmentList list = value(DataComponents.ENCHANTMENTS);
    return list == null ? EnchantmentList.EMPTY : list;
  }

  @Override
  public @NotNull Map<Enchantment, Integer> enchantments() {
    Map<Enchantment, Integer> result = new HashMap<>();
    for (Map.Entry<
            net.minestom.server.registry.RegistryKey<net.minestom.server.item.enchant.Enchantment>,
            Integer>
        entry : enchantmentList().enchantments().entrySet()) {
      result.put(new MinestomEnchantmentWrapper(entry.getKey()), entry.getValue());
    }
    return Map.copyOf(result);
  }

  private net.minestom.server.registry.RegistryKey<net.minestom.server.item.enchant.Enchantment>
      enchantmentKey(@NotNull Enchantment enchantment) {
    return enchantment instanceof MinestomEnchantmentWrapper wrapper
        ? wrapper.getMinestomKey()
        : net.minestom.server.registry.RegistryKey.unsafeOf(enchantment.key());
  }

  @Override
  public boolean hasEnchant(@NotNull Enchantment enchantment) {
    return enchantmentList().has(enchantmentKey(enchantment));
  }

  @Override
  public int enchantLevel(@NotNull Enchantment enchantment) {
    return enchantmentList().level(enchantmentKey(enchantment));
  }

  @Override
  public void addEnchant(
      @NotNull Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
    net.minestom.server.registry.RegistryKey<net.minestom.server.item.enchant.Enchantment> key =
        enchantmentKey(enchantment);
    replace(
        owner
            .getMinestomItemStack()
            .with(DataComponents.ENCHANTMENTS, enchantmentList().with(key, level)));
  }

  @Override
  public void removeEnchant(@NotNull Enchantment enchantment) {
    replace(
        owner
            .getMinestomItemStack()
            .with(
                DataComponents.ENCHANTMENTS,
                enchantmentList().remove(enchantmentKey(enchantment))));
  }

  private AttributeList attributeList() {
    AttributeList list = value(DataComponents.ATTRIBUTE_MODIFIERS);
    return list == null ? AttributeList.EMPTY : list;
  }

  private static net.minestom.server.entity.attribute.Attribute minestomAttribute(Key key) {
    net.minestom.server.entity.attribute.Attribute attribute =
        net.minestom.server.entity.attribute.Attribute.fromKey(key);
    if (attribute == null) throw new IllegalArgumentException("Unknown attribute: " + key);
    return attribute;
  }

  private static net.minestom.server.entity.EquipmentSlotGroup minestomGroup(
      @Nullable org.aincraft.api.domain.datacomponent.item.EquipmentSlotGroup group,
      @Nullable EquipmentSlot slot) {
    String name =
        group != null
            ? group.name()
            : slot == null
                ? "any"
                : switch (slot) {
                  case HAND -> "hand";
                  case OFF_HAND -> "offhand";
                  case FEET -> "feet";
                  case LEGS -> "legs";
                  case CHEST -> "chest";
                  case HEAD -> "head";
                  case BODY -> "body";
                  case SADDLE -> "saddle";
                };
    String normalized = name.toUpperCase(java.util.Locale.ROOT);
    if ("MAINHAND".equals(normalized)) normalized = "MAIN_HAND";
    if ("OFFHAND".equals(normalized)) normalized = "OFF_HAND";
    try {
      return net.minestom.server.entity.EquipmentSlotGroup.valueOf(normalized);
    } catch (IllegalArgumentException ignored) {
      return net.minestom.server.entity.EquipmentSlotGroup.ANY;
    }
  }

  private static AttributeList.Modifier toMinestomModifier(
      Key attribute, AttributeModifier modifier) {
    return new AttributeList.Modifier(
        minestomAttribute(attribute),
        MinestomAdapters.toMinestom(modifier),
        minestomGroup(modifier.slotGroup(), modifier.slot()));
  }

  private static AttributeModifier adaptModifier(AttributeList.Modifier modifier) {
    return new MinestomAttributeModifierWrapper(
        modifier.modifier(), null, new MinestomEquipmentSlotGroupWrapper(modifier.slot()));
  }

  @Override
  public @NotNull Map<Key, Collection<AttributeModifier>> attributeModifiers() {
    Map<Key, Collection<AttributeModifier>> result = new HashMap<>();
    for (AttributeList.Modifier modifier : attributeList().modifiers()) {
      result
          .computeIfAbsent(modifier.attribute().key(), ignored -> new java.util.ArrayList<>())
          .add(adaptModifier(modifier));
    }
    result.replaceAll((ignored, modifiers) -> List.copyOf(modifiers));
    return Map.copyOf(result);
  }

  @Override
  public @Nullable Collection<AttributeModifier> getAttributeModifiers(@NotNull Key attribute) {
    return attributeModifiers().get(attribute);
  }

  @Override
  public boolean hasAttributeModifiers() {
    return !attributeList().modifiers().isEmpty();
  }

  @Override
  public void addAttributeModifier(@NotNull Key attribute, @NotNull AttributeModifier modifier) {
    replace(
        owner
            .getMinestomItemStack()
            .with(
                DataComponents.ATTRIBUTE_MODIFIERS,
                attributeList().with(toMinestomModifier(attribute, modifier))));
  }

  @Override
  public void removeAttributeModifier(@NotNull Key attribute) {
    List<AttributeList.Modifier> remaining =
        attributeList().modifiers().stream()
            .filter(modifier -> !attribute.equals(modifier.attribute().key()))
            .toList();
    replace(
        owner
            .getMinestomItemStack()
            .with(DataComponents.ATTRIBUTE_MODIFIERS, new AttributeList(remaining)));
  }

  @Override
  public void removeAttributeModifier(@NotNull Key attribute, @NotNull AttributeModifier modifier) {
    List<AttributeList.Modifier> remaining =
        attributeList().modifiers().stream()
            .filter(
                entry ->
                    !attribute.equals(entry.attribute().key())
                        || !modifier.key().equals(entry.modifier().id()))
            .toList();
    replace(
        owner
            .getMinestomItemStack()
            .with(DataComponents.ATTRIBUTE_MODIFIERS, new AttributeList(remaining)));
  }

  @Override
  public @Nullable Map<Key, Collection<AttributeModifier>> getAttributeModifiers(
      @NotNull EquipmentSlot slot) {
    net.minestom.server.entity.EquipmentSlot minestomSlot =
        switch (slot) {
          case HAND -> net.minestom.server.entity.EquipmentSlot.MAIN_HAND;
          case OFF_HAND -> net.minestom.server.entity.EquipmentSlot.OFF_HAND;
          case FEET -> net.minestom.server.entity.EquipmentSlot.BOOTS;
          case LEGS -> net.minestom.server.entity.EquipmentSlot.LEGGINGS;
          case CHEST -> net.minestom.server.entity.EquipmentSlot.CHESTPLATE;
          case HEAD -> net.minestom.server.entity.EquipmentSlot.HELMET;
          case BODY -> net.minestom.server.entity.EquipmentSlot.BODY;
          case SADDLE -> net.minestom.server.entity.EquipmentSlot.SADDLE;
        };
    Map<Key, Collection<AttributeModifier>> result = new HashMap<>();
    for (AttributeList.Modifier modifier : attributeList().modifiers()) {
      if (modifier.slot().test(minestomSlot)) {
        result
            .computeIfAbsent(modifier.attribute().key(), ignored -> new java.util.ArrayList<>())
            .add(adaptModifier(modifier));
      }
    }
    if (result.isEmpty()) return Map.of();
    result.replaceAll((ignored, modifiers) -> List.copyOf(modifiers));
    return Map.copyOf(result);
  }

  @Override
  public void setAttributeModifiers(@NotNull Map<Key, Collection<AttributeModifier>> modifiers) {
    List<AttributeList.Modifier> entries = new java.util.ArrayList<>();
    for (Map.Entry<Key, Collection<AttributeModifier>> entry : modifiers.entrySet()) {
      for (AttributeModifier modifier : entry.getValue()) {
        entries.add(toMinestomModifier(entry.getKey(), modifier));
      }
    }
    replace(
        owner
            .getMinestomItemStack()
            .with(DataComponents.ATTRIBUTE_MODIFIERS, new AttributeList(entries)));
  }

  @Override
  public boolean removeAttributeModifier(@NotNull EquipmentSlot slot) {
    int before = attributeList().modifiers().size();
    Map<Key, Collection<AttributeModifier>> matching = getAttributeModifiers(slot);
    if (matching == null || matching.isEmpty()) return false;
    for (Map.Entry<Key, Collection<AttributeModifier>> entry : matching.entrySet()) {
      for (AttributeModifier modifier : entry.getValue()) {
        removeAttributeModifier(entry.getKey(), modifier);
      }
    }
    return before != attributeList().modifiers().size();
  }

  @Override
  public boolean hasData(@NotNull DataComponentType<?> type) {
    return owner.getMinestomItemStack().has(component(type));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> @Nullable T getData(@NotNull DataComponentType<T> type) {
    Object raw = owner.getMinestomItemStack().get(component(type));
    return raw == null ? null : (T) MinestomDataComponentAdapter.fromPlatform(raw, type.type());
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> void setData(@NotNull DataComponentType<T> type, @Nullable T value) {
    DataComponent<T> component = component(type);
    Object converted = value == null ? null : MinestomDataComponentAdapter.toPlatform(value);
    replace(
        converted == null
            ? owner.getMinestomItemStack().without(component)
            : owner.getMinestomItemStack().with((DataComponent<Object>) component, converted));
  }

  @Override
  public void resetData(@NotNull DataComponentType<?> type) {
    replace(owner.getMinestomItemStack().reset(component(type)));
  }

  @Override
  public @NotNull Set<DataComponentType<?>> dataComponentTypes() {
    Set<DataComponentType<?>> result = new HashSet<>();
    for (DataComponent.Value entry : owner.getMinestomItemStack().componentPatch().entrySet()) {
      result.add(
          new DataComponentType<Object>() {
            @Override
            public @NotNull Key key() {
              return entry.component().key();
            }

            @Override
            public @NotNull Class<Object> type() {
              return Object.class;
            }
          });
    }
    return result;
  }

  private TooltipDisplay tooltipDisplay() {
    TooltipDisplay display = value(DataComponents.TOOLTIP_DISPLAY);
    return display == null ? TooltipDisplay.EMPTY : display;
  }

  private static DataComponent<?> hiddenComponent(@NotNull ItemFlag flag) {
    return switch (flag) {
      case HIDE_ENCHANTS -> DataComponents.ENCHANTMENTS;
      case HIDE_ATTRIBUTES -> DataComponents.ATTRIBUTE_MODIFIERS;
      case HIDE_UNBREAKABLE -> DataComponents.UNBREAKABLE;
      case HIDE_DESTROYS -> DataComponents.CAN_BREAK;
      case HIDE_PLACED_ON -> DataComponents.CAN_PLACE_ON;
      case HIDE_ADDITIONAL_TOOLTIP -> DataComponents.TOOLTIP_DISPLAY;
      case HIDE_DYE -> DataComponents.DYED_COLOR;
      case HIDE_ARMOR_TRIM -> DataComponents.TRIM;
    };
  }

  @Override
  public @NotNull Set<ItemFlag> itemFlags() {
    Set<ItemFlag> result = new HashSet<>();
    Set<DataComponent<?>> hidden = tooltipDisplay().hiddenComponents();
    for (ItemFlag flag : ItemFlag.values()) {
      if (hidden.contains(hiddenComponent(flag))) result.add(flag);
    }
    return Set.copyOf(result);
  }

  @Override
  public boolean hasItemFlag(@NotNull ItemFlag flag) {
    return itemFlags().contains(flag);
  }

  @Override
  public void addItemFlags(@NotNull ItemFlag... flags) {
    TooltipDisplay display = tooltipDisplay();
    for (ItemFlag flag : flags) display = display.with(hiddenComponent(flag));
    replace(owner.getMinestomItemStack().with(DataComponents.TOOLTIP_DISPLAY, display));
  }

  @Override
  public void removeItemFlags(@NotNull ItemFlag... flags) {
    TooltipDisplay display = tooltipDisplay();
    for (ItemFlag flag : flags) display = display.without(hiddenComponent(flag));
    replace(owner.getMinestomItemStack().with(DataComponents.TOOLTIP_DISPLAY, display));
  }

  @Override
  public @NotNull PersistentDataContainer persistentData() {
    return new MinestomPersistentDataContainer(owner::getMinestomItemStack, owner::replace);
  }
}
