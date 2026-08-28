package org.aincraft.minestom.adapter;

import net.kyori.adventure.text.Component;
import org.aincraft.api.domain.datacomponent.item.CustomModelData;
import org.aincraft.api.domain.datacomponent.item.FoodProperties;
import org.aincraft.api.domain.datacomponent.item.ItemEnchantments;
import org.aincraft.api.domain.datacomponent.item.ItemLore;
import org.aincraft.api.domain.datacomponent.item.UseCooldown;
import org.aincraft.api.domain.datacomponent.item.UseEffects;
import org.aincraft.api.domain.datacomponent.item.Weapon;
import org.aincraft.api.domain.effect.Enchantment;
import org.aincraft.api.domain.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class MinestomDataComponentAdapter {

  private MinestomDataComponentAdapter() {}

  static Object fromPlatform(@NotNull Object value, @NotNull Class<?> expected) {
    if (expected.isInstance(value)) return value;
    if (expected == ItemLore.class && value instanceof List<?> lines) {
      @SuppressWarnings("unchecked")
      List<Component> components = (List<Component>) lines;
      return new ItemLore() {
        @Override
        public List<Component> lines() {
          return List.copyOf(components);
        }

        @Override
        public List<Component> styledLines() {
          return List.copyOf(components);
        }
      };
    }
    if (expected == ItemEnchantments.class
        && value instanceof net.minestom.server.item.component.EnchantmentList list) {
      return (ItemEnchantments)
          () -> {
            Map<Enchantment, Integer> result = new java.util.LinkedHashMap<>();
            for (Map.Entry<
                    net.minestom.server.registry.RegistryKey<
                        net.minestom.server.item.enchant.Enchantment>,
                    Integer>
                entry : list.enchantments().entrySet()) {
              result.put(MinestomAdapters.adapt(entry.getKey()), entry.getValue());
            }
            return Map.copyOf(result);
          };
    }
    if (expected == FoodProperties.class
        && value instanceof net.minestom.server.item.component.Food food) {
      return new FoodProperties() {
        @Override
        public int nutrition() {
          return food.nutrition();
        }

        @Override
        public float saturation() {
          return food.saturationModifier();
        }

        @Override
        public boolean canAlwaysEat() {
          return food.canAlwaysEat();
        }
      };
    }
    if (expected == UseEffects.class
        && value instanceof net.minestom.server.item.component.UseEffects effects) {
      return new UseEffects() {
        @Override
        public boolean canSprint() {
          return effects.canSprint();
        }

        @Override
        public boolean interactVibrations() {
          return effects.interactVibrations();
        }

        @Override
        public float speedMultiplier() {
          return effects.speedMultiplier();
        }
      };
    }
    if (expected == UseCooldown.class
        && value instanceof net.minestom.server.item.component.UseCooldown cooldown) {
      return new UseCooldown() {
        @Override
        public float seconds() {
          return cooldown.seconds();
        }

        @Override
        public net.kyori.adventure.key.Key cooldownGroup() {
          return cooldown.cooldownGroup() == null
              ? null
              : net.kyori.adventure.key.Key.key(cooldown.cooldownGroup());
        }
      };
    }
    if (expected == Weapon.class
        && value instanceof net.minestom.server.item.component.Weapon weapon) {
      return new Weapon() {
        @Override
        public int itemDamagePerAttack() {
          return weapon.itemDamagePerAttack();
        }

        @Override
        public float disableBlockingForSeconds() {
          return weapon.disableBlockingForSeconds();
        }
      };
    }
    if (expected == CustomModelData.class
        && value instanceof net.minestom.server.item.component.CustomModelData data) {
      return new CustomModelData() {
        @Override
        public List<Float> floats() {
          return data.floats();
        }

        @Override
        public List<Boolean> flags() {
          return data.flags();
        }

        @Override
        public List<String> strings() {
          return data.strings();
        }

        @Override
        public List<net.kyori.adventure.text.format.TextColor> colors() {
          return data.colors().stream()
              .map(
                  color ->
                      net.kyori.adventure.text.format.TextColor.color(
                          color.red(), color.green(), color.blue()))
              .toList();
        }
      };
    }
    if (expected == ItemStack.class && value instanceof net.minestom.server.item.ItemStack item) {
      return MinestomAdapters.adapt(item);
    }
    throw new IllegalArgumentException(
        "Cannot adapt Minestom component value "
            + value.getClass().getName()
            + " to "
            + expected.getName());
  }

  static Object toPlatform(@NotNull Object value) {
    if (value instanceof ItemLore lore) return List.copyOf(lore.lines());
    if (value instanceof ItemEnchantments enchantments) {
      Map<
              net.minestom.server.registry.RegistryKey<
                  net.minestom.server.item.enchant.Enchantment>,
              Integer>
          result = new java.util.LinkedHashMap<>();
      for (Map.Entry<Enchantment, Integer> entry : enchantments.enchantments().entrySet()) {
        result.put(MinestomAdapters.toMinestom(entry.getKey()), entry.getValue());
      }
      return new net.minestom.server.item.component.EnchantmentList(result);
    }
    if (value instanceof FoodProperties food) {
      return new net.minestom.server.item.component.Food(
          food.nutrition(), food.saturation(), food.canAlwaysEat());
    }
    if (value instanceof UseEffects effects) {
      return new net.minestom.server.item.component.UseEffects(
          effects.canSprint(), effects.interactVibrations(), effects.speedMultiplier());
    }
    if (value instanceof UseCooldown cooldown) {
      return new net.minestom.server.item.component.UseCooldown(
          cooldown.seconds(),
          cooldown.cooldownGroup() == null ? null : cooldown.cooldownGroup().asString());
    }
    if (value instanceof Weapon weapon) {
      return new net.minestom.server.item.component.Weapon(
          weapon.itemDamagePerAttack(), weapon.disableBlockingForSeconds());
    }
    if (value instanceof CustomModelData data) {
      List<net.kyori.adventure.util.RGBLike> colors = new ArrayList<>(data.colors());
      return new net.minestom.server.item.component.CustomModelData(
          data.floats(), data.flags(), data.strings(), colors);
    }
    if (value instanceof ItemStack item) return MinestomAdapters.toMinestom(item);
    return value;
  }
}
