package org.aincraft.paper.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class PaperDataComponentAdapter {

  private PaperDataComponentAdapter() {}

  static <T> T adapt(@NotNull Object value, @NotNull Class<T> expected) {
    Object converted = adapt(value, (Type) expected);
    return expected.cast(converted);
  }

  static Object toPaper(@NotNull Key key, @NotNull Object value) {
    if (value instanceof org.aincraft.api.domain.datacomponent.item.ItemLore lore) {
      return io.papermc.paper.datacomponent.item.ItemLore.lore(lore.lines());
    }
    if (value instanceof org.aincraft.api.domain.datacomponent.item.ItemEnchantments enchantments) {
      Map<org.bukkit.enchantments.Enchantment, Integer> converted = new java.util.LinkedHashMap<>();
      for (Map.Entry<org.aincraft.api.domain.effect.Enchantment, Integer> entry :
          enchantments.enchantments().entrySet()) {
        converted.put(
            org.aincraft.bukkit.adapter.BukkitAdapters.toBukkit(entry.getKey()), entry.getValue());
      }
      return io.papermc.paper.datacomponent.item.ItemEnchantments.itemEnchantments(converted);
    }
    if (value
        instanceof
        org.aincraft.api.domain.datacomponent.item.attribute.ItemAttributeModifiers modifiers) {
      io.papermc.paper.datacomponent.item.ItemAttributeModifiers.Builder builder =
          io.papermc.paper.datacomponent.item.ItemAttributeModifiers.itemAttributes();
      for (org.aincraft.api.domain.datacomponent.item.attribute.ItemAttributeModifiers.Entry entry :
          modifiers.modifiers()) {
        org.bukkit.attribute.Attribute attribute =
            org.bukkit.Registry.ATTRIBUTE.get(entry.attribute());
        if (attribute == null) {
          throw new IllegalArgumentException("Unknown Paper attribute: " + entry.attribute());
        }
        org.bukkit.inventory.EquipmentSlotGroup group =
            org.bukkit.inventory.EquipmentSlotGroup.getByName(entry.group().name());
        if (group == null) group = org.bukkit.inventory.EquipmentSlotGroup.ANY;
        io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay display =
            switch (entry.display().type()) {
              case DEFAULT ->
                  io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay.reset();
              case HIDDEN ->
                  io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay.hidden();
              case OVERRIDE ->
                  io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay.override(
                      entry.display().overrideText());
            };
        builder.addModifier(
            attribute,
            org.aincraft.bukkit.adapter.BukkitAdapters.toBukkit(entry.modifier()),
            group,
            display);
      }
      return builder.build();
    }
    if (value instanceof org.aincraft.api.domain.datacomponent.item.Enchantable enchantable) {
      return enchantable.value();
    }
    if (value instanceof org.aincraft.api.domain.datacomponent.item.MapId mapId) {
      return mapId.id();
    }
    if (value
        instanceof org.aincraft.api.domain.datacomponent.item.OminousBottleAmplifier amplifier) {
      return amplifier.amplification();
    }
    if (value instanceof org.aincraft.api.domain.datacomponent.item.CustomModelData data) {
      io.papermc.paper.datacomponent.item.CustomModelData.Builder builder =
          io.papermc.paper.datacomponent.item.CustomModelData.customModelData();
      builder.addFloats(data.floats());
      builder.addFlags(data.flags());
      builder.addStrings(data.strings());
      for (net.kyori.adventure.text.format.TextColor color : data.colors()) {
        builder.addColor(org.bukkit.Color.fromRGB(color.value()));
      }
      return builder.build();
    }
    if (value instanceof org.aincraft.api.domain.datacomponent.item.FoodProperties food) {
      return io.papermc.paper.datacomponent.item.FoodProperties.food()
          .nutrition(food.nutrition())
          .saturation(food.saturation())
          .canAlwaysEat(food.canAlwaysEat())
          .build();
    }
    if (value instanceof org.aincraft.api.domain.datacomponent.item.UseEffects effects) {
      return io.papermc.paper.datacomponent.item.UseEffects.useEffects()
          .canSprint(effects.canSprint())
          .interactVibrations(effects.interactVibrations())
          .speedMultiplier(effects.speedMultiplier())
          .build();
    }
    if (value instanceof org.aincraft.api.domain.datacomponent.item.Consumable consumable) {
      return io.papermc.paper.datacomponent.item.Consumable.consumable()
          .consumeSeconds(consumable.consumeSeconds())
          .animation(
              io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation.valueOf(
                  consumable.animation().name()))
          .sound(consumable.sound())
          .hasConsumeParticles(consumable.hasConsumeParticles())
          .build();
    }
    if (value instanceof org.aincraft.api.domain.datacomponent.item.Weapon weapon) {
      return io.papermc.paper.datacomponent.item.Weapon.weapon()
          .itemDamagePerAttack(weapon.itemDamagePerAttack())
          .disableBlockingForSeconds(weapon.disableBlockingForSeconds())
          .build();
    }
    if (value instanceof org.aincraft.api.domain.datacomponent.item.UseCooldown cooldown) {
      io.papermc.paper.datacomponent.item.UseCooldown.Builder builder =
          io.papermc.paper.datacomponent.item.UseCooldown.useCooldown(cooldown.seconds());
      if (cooldown.cooldownGroup() != null) builder.cooldownGroup(cooldown.cooldownGroup());
      return builder.build();
    }
    if (value instanceof Enum<?> enumValue && key.value().equals("rarity")) {
      return org.bukkit.inventory.ItemRarity.valueOf(enumValue.name());
    }
    if (value instanceof Enum<?> enumValue && key.value().equals("map_post_processing")) {
      return io.papermc.paper.item.MapPostProcessing.valueOf(enumValue.name());
    }
    return value;
  }

  private static Object adapt(Object value, Type expected) {
    if (value == null) return null;
    if (expected instanceof Class<?> expectedClass) {
      if (expectedClass.isInstance(value)) return value;
      if (expectedClass == Key.class && value instanceof org.bukkit.Keyed keyed) {
        org.bukkit.NamespacedKey key = keyed.getKey();
        return Key.key(key.getNamespace(), key.getKey());
      }
      if (expectedClass
              == org.aincraft.api.domain.datacomponent.item.attribute.AttributeModifierDisplay.class
          && value
              instanceof
              io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay display) {
        if (display
            instanceof
            io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay.OverrideText
                override) {
          return org.aincraft.api.domain.datacomponent.item.attribute.AttributeModifierDisplays
              .override(override.text());
        }
        if (display
            instanceof
            io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay.Hidden) {
          return org.aincraft.api.domain.datacomponent.item.attribute.AttributeModifierDisplays
              .hidden();
        }
        return org.aincraft.api.domain.datacomponent.item.attribute.AttributeModifierDisplays
            .reset();
      }
      if (expectedClass == net.kyori.adventure.text.format.TextColor.class
          && value instanceof org.bukkit.Color color) {
        return net.kyori.adventure.text.format.TextColor.color(
            color.getRed(), color.getGreen(), color.getBlue());
      }
      if (expectedClass == org.aincraft.api.domain.datacomponent.item.Enchantable.class
          && value instanceof Integer integer) {
        return (org.aincraft.api.domain.datacomponent.item.Enchantable) () -> integer;
      }
      if (expectedClass == org.aincraft.api.domain.datacomponent.item.MapId.class
          && value instanceof Integer integer) {
        return (org.aincraft.api.domain.datacomponent.item.MapId) () -> integer;
      }
      if (expectedClass == org.aincraft.api.domain.datacomponent.item.OminousBottleAmplifier.class
          && value instanceof Integer integer) {
        return (org.aincraft.api.domain.datacomponent.item.OminousBottleAmplifier) () -> integer;
      }
      if (expectedClass == ItemStack.class
          && value instanceof org.bukkit.inventory.ItemStack item) {
        return PaperAdapters.adapt(item);
      }
      if (expectedClass == org.aincraft.api.domain.effect.Enchantment.class
          && value instanceof org.bukkit.enchantments.Enchantment enchantment) {
        return PaperAdapters.adapt(enchantment);
      }
      if (expectedClass == org.aincraft.api.domain.effect.PotionEffectType.class
          && value instanceof org.bukkit.potion.PotionEffectType effectType) {
        return PaperAdapters.adapt(effectType);
      }
      if (expectedClass == org.aincraft.api.domain.attribute.Attribute.class
          && value instanceof org.bukkit.attribute.Attribute attribute) {
        return PaperAdapters.adaptAttribute(attribute);
      }
      if (expectedClass == org.aincraft.api.domain.attribute.AttributeModifier.class
          && value instanceof org.bukkit.attribute.AttributeModifier modifier) {
        return org.aincraft.bukkit.adapter.BukkitAdapters.adapt(modifier);
      }
      if (expectedClass == org.aincraft.api.domain.datacomponent.item.EquipmentSlotGroup.class
          && value instanceof org.bukkit.inventory.EquipmentSlotGroup group) {
        return new org.aincraft.bukkit.adapter.BukkitEquipmentSlotGroupWrapper(group);
      }
      if (expectedClass.isEnum() && value instanceof Enum<?> enumValue) {
        @SuppressWarnings({"unchecked", "rawtypes"})
        Object converted = Enum.valueOf((Class<? extends Enum>) expectedClass, enumValue.name());
        return converted;
      }
      if ((expectedClass == List.class
              || expectedClass == Collection.class
              || expectedClass == Set.class)
          && value instanceof io.papermc.paper.registry.set.RegistryKeySet<?> keySet) {
        return adaptCollection(
            keySet.values(), null, expectedClass == Set.class ? Set.class : List.class);
      }
      if (expectedClass == List.class || expectedClass == Collection.class) {
        return adaptCollection((Collection<?>) value, null, List.class);
      }
      if (expectedClass == Set.class) {
        return adaptCollection((Collection<?>) value, null, Set.class);
      }
      if (expectedClass == Map.class && value instanceof Map<?, ?> map) {
        return map;
      }
      if (expectedClass.isInterface()) {
        return proxy(expectedClass, value);
      }
      throw new IllegalArgumentException(
          "Cannot adapt Paper component value "
              + value.getClass().getName()
              + " to "
              + expectedClass.getName());
    }
    if (expected instanceof ParameterizedType parameterized) {
      Type raw = parameterized.getRawType();
      Type[] args = parameterized.getActualTypeArguments();
      if (raw == List.class || raw == Collection.class || raw == Set.class) {
        Type elementType = args.length == 0 ? null : args[0];
        Collection<?> values =
            value instanceof io.papermc.paper.registry.set.RegistryKeySet<?> keySet
                ? keySet.values()
                : (Collection<?>) value;
        return adaptCollection(values, elementType, raw == Set.class ? Set.class : List.class);
      }
      if (raw == Map.class && value instanceof Map<?, ?> map) {
        Type keyType = args.length > 0 ? args[0] : null;
        Type valueType = args.length > 1 ? args[1] : null;
        Map<Object, Object> converted = new java.util.LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
          converted.put(adapt(entry.getKey(), keyType), adapt(entry.getValue(), valueType));
        }
        return Map.copyOf(converted);
      }
      return adapt(value, raw instanceof Class<?> c ? c : Object.class);
    }
    return value;
  }

  private static Object adaptCollection(Collection<?> values, Type elementType, Class<?> raw) {
    List<Object> converted = new ArrayList<>(values.size());
    for (Object value : values)
      converted.add(elementType == null ? value : adapt(value, elementType));
    return raw == Set.class ? Set.copyOf(converted) : List.copyOf(converted);
  }

  private static Object proxy(Class<?> expected, Object backing) {
    InvocationHandler handler = new ComponentInvocationHandler(backing);
    return Proxy.newProxyInstance(expected.getClassLoader(), new Class<?>[] {expected}, handler);
  }

  private static final class ComponentInvocationHandler implements InvocationHandler {

    private final Object backing;

    private ComponentInvocationHandler(Object backing) {
      this.backing = backing;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (method.getDeclaringClass() == Object.class) {
        return switch (method.getName()) {
          case "toString" -> backing.toString();
          case "hashCode" -> backing.hashCode();
          case "equals" -> proxy == args[0];
          default -> method.invoke(backing, args);
        };
      }
      Method delegate = findDelegate(method);
      if (delegate == null) {
        throw new UnsupportedOperationException(
            "Paper component " + backing.getClass().getName() + " has no " + method.getName());
      }
      Object result = delegate.invoke(backing, args);
      return adapt(result, method.getGenericReturnType());
    }

    private Method findDelegate(Method method) {
      for (Method candidate : backing.getClass().getMethods()) {
        if (!candidate.getName().equals(method.getName())
            || candidate.getParameterCount() != method.getParameterCount()) continue;
        return candidate;
      }
      String alias = alias(method.getName());
      if (alias == null) return null;
      for (Method candidate : backing.getClass().getMethods()) {
        if (candidate.getName().equals(alias)
            && candidate.getParameterCount() == method.getParameterCount()) return candidate;
      }
      return null;
    }

    private static String alias(String name) {
      return switch (name) {
        case "saturation" -> "saturationModifier";
        case "cooldownGroup" -> "getCooldownGroup";
        case "amplification" -> "amplifier";
        case "group" -> "getGroup";
        default -> null;
      };
    }
  }
}
