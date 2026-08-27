package org.aincraft.common.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DataComponentTypesTest {

  private static final Set<String> EXPECTED_NAMES =
      new HashSet<>(
          Arrays.asList(
              "MAX_STACK_SIZE",
              "MAX_DAMAGE",
              "DAMAGE",
              "UNBREAKABLE",
              "USE_EFFECTS",
              "CUSTOM_NAME",
              "MINIMUM_ATTACK_CHARGE",
              "DAMAGE_TYPE",
              "ITEM_NAME",
              "ITEM_MODEL",
              "LORE",
              "RARITY",
              "ENCHANTMENTS",
              "CAN_PLACE_ON",
              "CAN_BREAK",
              "ATTRIBUTE_MODIFIERS",
              "CUSTOM_MODEL_DATA",
              "TOOLTIP_DISPLAY",
              "REPAIR_COST",
              "ENCHANTMENT_GLINT_OVERRIDE",
              "INTANGIBLE_PROJECTILE",
              "FOOD",
              "CONSUMABLE",
              "USE_REMAINDER",
              "USE_COOLDOWN",
              "DAMAGE_RESISTANT",
              "TOOL",
              "WEAPON",
              "ENCHANTABLE",
              "EQUIPPABLE",
              "REPAIRABLE",
              "GLIDER",
              "TOOLTIP_STYLE",
              "DEATH_PROTECTION",
              "BLOCKS_ATTACKS",
              "PIERCING_WEAPON",
              "KINETIC_WEAPON",
              "ATTACK_RANGE",
              "SWING_ANIMATION",
              "STORED_ENCHANTMENTS",
              "DYE",
              "DYED_COLOR",
              "MAP_COLOR",
              "MAP_ID",
              "MAP_DECORATIONS",
              "MAP_POST_PROCESSING",
              "CHARGED_PROJECTILES",
              "BUNDLE_CONTENTS",
              "POTION_CONTENTS",
              "POTION_DURATION_SCALE",
              "SUSPICIOUS_STEW_EFFECTS",
              "WRITABLE_BOOK_CONTENT",
              "WRITTEN_BOOK_CONTENT",
              "TRIM",
              "INSTRUMENT",
              "PROVIDES_TRIM_MATERIAL",
              "OMINOUS_BOTTLE_AMPLIFIER",
              "JUKEBOX_PLAYABLE",
              "PROVIDES_BANNER_PATTERNS",
              "RECIPES",
              "LODESTONE_TRACKER",
              "FIREWORK_EXPLOSION",
              "FIREWORKS",
              "PROFILE",
              "NOTE_BLOCK_SOUND",
              "BANNER_PATTERNS",
              "BASE_COLOR",
              "POT_DECORATIONS",
              "CONTAINER",
              "BLOCK_DATA",
              "SULFUR_CUBE_CONTENT",
              "CONTAINER_LOOT",
              "BREAK_SOUND",
              "VILLAGER_VARIANT",
              "WOLF_VARIANT",
              "WOLF_SOUND_VARIANT",
              "WOLF_COLLAR",
              "FOX_VARIANT",
              "SALMON_SIZE",
              "PARROT_VARIANT",
              "TROPICAL_FISH_PATTERN",
              "TROPICAL_FISH_BASE_COLOR",
              "TROPICAL_FISH_PATTERN_COLOR",
              "MOOSHROOM_VARIANT",
              "RABBIT_VARIANT",
              "PIG_VARIANT",
              "PIG_SOUND_VARIANT",
              "COW_VARIANT",
              "COW_SOUND_VARIANT",
              "CHICKEN_VARIANT",
              "CHICKEN_SOUND_VARIANT",
              "FROG_VARIANT",
              "HORSE_VARIANT",
              "PAINTING_VARIANT",
              "LLAMA_VARIANT",
              "AXOLOTL_VARIANT",
              "ZOMBIE_NAUTILUS_VARIANT",
              "CAT_VARIANT",
              "CAT_SOUND_VARIANT",
              "CAT_COLLAR",
              "SHEEP_COLOR",
              "SHULKER_COLOR"));

  @Test
  void catalogCoversAllStandardComponents() {
    Set<String> actual = new HashSet<>();
    for (Field field : DataComponentTypes.class.getDeclaredFields()) {
      if (field.getType() == DataComponentType.Valued.class
          || field.getType() == DataComponentType.NonValued.class) {
        actual.add(field.getName());
      }
    }

    assertEquals(102, EXPECTED_NAMES.size(), "Expected 102 data components");
    assertEquals(EXPECTED_NAMES, actual, "DataComponentTypes must match the standard catalog");
  }

  @Test
  void allDescriptorsHaveMinecraftKeyAndType() {
    for (Field field : DataComponentTypes.class.getDeclaredFields()) {
      if (field.getType() != DataComponentType.Valued.class
          && field.getType() != DataComponentType.NonValued.class) {
        continue;
      }
      DataComponentType<?> type;
      try {
        type = (DataComponentType<?>) field.get(null);
      } catch (IllegalAccessException e) {
        throw new AssertionError(e);
      }
      assertNotNull(type, "Descriptor " + field.getName() + " must not be null");
      Key key = type.key();
      assertNotNull(key, "Descriptor " + field.getName() + " must have a key");
      assertEquals("minecraft", key.namespace(), "Key namespace must be minecraft");
      assertEquals(
          field.getName().toLowerCase(), key.value(), "Key value must match field name lowercased");
      assertNotNull(type.type(), "Descriptor " + field.getName() + " must have a value class");
    }
  }

  @Test
  void loreDescriptorPointsToItemLoreContract() {
    assertEquals(
        org.aincraft.common.datacomponent.item.ItemLore.class, DataComponentTypes.LORE.type());
  }

  @Test
  void nonValuedDescriptorsReturnVoidType() {
    assertEquals(Void.class, DataComponentTypes.UNBREAKABLE.type());
    assertEquals(Void.class, DataComponentTypes.INTANGIBLE_PROJECTILE.type());
    assertEquals(Void.class, DataComponentTypes.GLIDER.type());
  }

  @Test
  void primitiveDescriptorsUseBoxedClasses() {
    assertEquals(Integer.class, DataComponentTypes.MAX_STACK_SIZE.type());
    assertEquals(Float.class, DataComponentTypes.MINIMUM_ATTACK_CHARGE.type());
    assertEquals(Boolean.class, DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE.type());
  }
}
