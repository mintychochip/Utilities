package org.aincraft.common.inventory;

import java.lang.Boolean;
import java.lang.Float;
import java.lang.Integer;
import java.util.List;
import java.util.Set;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.aincraft.common.datacomponent.item.AttackRange;
import org.aincraft.common.datacomponent.item.BannerPatternLayers;
import org.aincraft.common.datacomponent.item.BlockItemDataProperties;
import org.aincraft.common.datacomponent.item.BlocksAttacks;
import org.aincraft.common.datacomponent.item.BundleContents;
import org.aincraft.common.datacomponent.item.ChargedProjectiles;
import org.aincraft.common.datacomponent.item.Consumable;
import org.aincraft.common.datacomponent.item.CustomModelData;
import org.aincraft.common.datacomponent.item.DamageResistant;
import org.aincraft.common.datacomponent.item.DeathProtection;
import org.aincraft.common.datacomponent.item.DyedItemColor;
import org.aincraft.common.datacomponent.item.Enchantable;
import org.aincraft.common.datacomponent.item.Equippable;
import org.aincraft.common.datacomponent.item.FireworkEffect;
import org.aincraft.common.datacomponent.item.Fireworks;
import org.aincraft.common.datacomponent.item.FoodProperties;
import org.aincraft.common.datacomponent.item.ItemAdventurePredicate;
import org.aincraft.common.datacomponent.item.ItemArmorTrim;
import org.aincraft.common.datacomponent.item.ItemContainerContents;
import org.aincraft.common.datacomponent.item.ItemEnchantments;
import org.aincraft.common.datacomponent.item.ItemLore;
import org.aincraft.common.datacomponent.item.JukeboxPlayable;
import org.aincraft.common.datacomponent.item.KineticWeapon;
import org.aincraft.common.datacomponent.item.LodestoneTracker;
import org.aincraft.common.datacomponent.item.MapDecorations;
import org.aincraft.common.datacomponent.item.MapId;
import org.aincraft.common.datacomponent.item.MapItemColor;
import org.aincraft.common.datacomponent.item.OminousBottleAmplifier;
import org.aincraft.common.datacomponent.item.PiercingWeapon;
import org.aincraft.common.datacomponent.item.PotDecorations;
import org.aincraft.common.datacomponent.item.Repairable;
import org.aincraft.common.datacomponent.item.ResolvableProfile;
import org.aincraft.common.datacomponent.item.SeededContainerLoot;
import org.aincraft.common.datacomponent.item.SulfurCubeContent;
import org.aincraft.common.datacomponent.item.SwingAnimation;
import org.aincraft.common.datacomponent.item.Tool;
import org.aincraft.common.datacomponent.item.TooltipDisplay;
import org.aincraft.common.datacomponent.item.UseCooldown;
import org.aincraft.common.datacomponent.item.UseEffects;
import org.aincraft.common.datacomponent.item.UseRemainder;
import org.aincraft.common.datacomponent.item.Weapon;
import org.aincraft.common.datacomponent.item.WritableBookContent;
import org.aincraft.common.datacomponent.item.WrittenBookContent;
import org.aincraft.common.datacomponent.item.attribute.ItemAttributeModifiers;
import org.aincraft.common.datacomponent.potion.PotionContents;
import org.aincraft.common.datacomponent.potion.SuspiciousStewEffects;

/**
 * Standard data component descriptors from the Paper {@code DataComponentTypes} registry.
 */
public final class DataComponentTypes {

  public static final ValuedDataComponentType<Integer> MAX_STACK_SIZE =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "max_stack_size"), Integer.class);

  public static final ValuedDataComponentType<Integer> MAX_DAMAGE =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "max_damage"), Integer.class);

  public static final ValuedDataComponentType<Integer> DAMAGE =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "damage"), Integer.class);

  public static final NonValuedDataComponentType UNBREAKABLE =
      new StandardNonValuedDataComponentType(Key.key("minecraft", "unbreakable"));

  public static final ValuedDataComponentType<UseEffects> USE_EFFECTS =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "use_effects"), UseEffects.class);

  public static final ValuedDataComponentType<Component> CUSTOM_NAME =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "custom_name"), Component.class);

  public static final ValuedDataComponentType<Float> MINIMUM_ATTACK_CHARGE =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "minimum_attack_charge"), Float.class);

  public static final ValuedDataComponentType<Key> DAMAGE_TYPE =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "damage_type"), Key.class);

  public static final ValuedDataComponentType<Component> ITEM_NAME =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "item_name"), Component.class);

  public static final ValuedDataComponentType<Key> ITEM_MODEL =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "item_model"), Key.class);

  public static final ValuedDataComponentType<ItemLore> LORE =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "lore"), ItemLore.class);

  public static final ValuedDataComponentType<ItemRarity> RARITY =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "rarity"), ItemRarity.class);

  public static final ValuedDataComponentType<ItemEnchantments> ENCHANTMENTS =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "enchantments"), ItemEnchantments.class);

  public static final ValuedDataComponentType<ItemAdventurePredicate> CAN_PLACE_ON =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "can_place_on"), ItemAdventurePredicate.class);

  public static final ValuedDataComponentType<ItemAdventurePredicate> CAN_BREAK =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "can_break"), ItemAdventurePredicate.class);

  public static final ValuedDataComponentType<ItemAttributeModifiers> ATTRIBUTE_MODIFIERS =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "attribute_modifiers"), ItemAttributeModifiers.class);

  public static final ValuedDataComponentType<CustomModelData> CUSTOM_MODEL_DATA =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "custom_model_data"), CustomModelData.class);

  public static final ValuedDataComponentType<TooltipDisplay> TOOLTIP_DISPLAY =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "tooltip_display"), TooltipDisplay.class);

  public static final ValuedDataComponentType<Integer> REPAIR_COST =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "repair_cost"), Integer.class);

  public static final ValuedDataComponentType<Boolean> ENCHANTMENT_GLINT_OVERRIDE =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "enchantment_glint_override"), Boolean.class);

  public static final NonValuedDataComponentType INTANGIBLE_PROJECTILE =
      new StandardNonValuedDataComponentType(Key.key("minecraft", "intangible_projectile"));

  public static final ValuedDataComponentType<FoodProperties> FOOD =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "food"), FoodProperties.class);

  public static final ValuedDataComponentType<Consumable> CONSUMABLE =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "consumable"), Consumable.class);

  public static final ValuedDataComponentType<UseRemainder> USE_REMAINDER =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "use_remainder"), UseRemainder.class);

  public static final ValuedDataComponentType<UseCooldown> USE_COOLDOWN =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "use_cooldown"), UseCooldown.class);

  public static final ValuedDataComponentType<DamageResistant> DAMAGE_RESISTANT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "damage_resistant"), DamageResistant.class);

  public static final ValuedDataComponentType<Tool> TOOL =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "tool"), Tool.class);

  public static final ValuedDataComponentType<Weapon> WEAPON =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "weapon"), Weapon.class);

  public static final ValuedDataComponentType<Enchantable> ENCHANTABLE =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "enchantable"), Enchantable.class);

  public static final ValuedDataComponentType<Equippable> EQUIPPABLE =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "equippable"), Equippable.class);

  public static final ValuedDataComponentType<Repairable> REPAIRABLE =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "repairable"), Repairable.class);

  public static final NonValuedDataComponentType GLIDER =
      new StandardNonValuedDataComponentType(Key.key("minecraft", "glider"));

  public static final ValuedDataComponentType<Key> TOOLTIP_STYLE =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "tooltip_style"), Key.class);

  public static final ValuedDataComponentType<DeathProtection> DEATH_PROTECTION =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "death_protection"), DeathProtection.class);

  public static final ValuedDataComponentType<BlocksAttacks> BLOCKS_ATTACKS =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "blocks_attacks"), BlocksAttacks.class);

  public static final ValuedDataComponentType<PiercingWeapon> PIERCING_WEAPON =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "piercing_weapon"), PiercingWeapon.class);

  public static final ValuedDataComponentType<KineticWeapon> KINETIC_WEAPON =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "kinetic_weapon"), KineticWeapon.class);

  public static final ValuedDataComponentType<AttackRange> ATTACK_RANGE =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "attack_range"), AttackRange.class);

  public static final ValuedDataComponentType<SwingAnimation> SWING_ANIMATION =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "swing_animation"), SwingAnimation.class);

  public static final ValuedDataComponentType<ItemEnchantments> STORED_ENCHANTMENTS =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "stored_enchantments"), ItemEnchantments.class);

  public static final ValuedDataComponentType<DyeColor> DYE =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "dye"), DyeColor.class);

  public static final ValuedDataComponentType<DyedItemColor> DYED_COLOR =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "dyed_color"), DyedItemColor.class);

  public static final ValuedDataComponentType<MapItemColor> MAP_COLOR =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "map_color"), MapItemColor.class);

  public static final ValuedDataComponentType<MapId> MAP_ID =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "map_id"), MapId.class);

  public static final ValuedDataComponentType<MapDecorations> MAP_DECORATIONS =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "map_decorations"), MapDecorations.class);

  public static final ValuedDataComponentType<MapPostProcessing> MAP_POST_PROCESSING =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "map_post_processing"), MapPostProcessing.class);

  public static final ValuedDataComponentType<ChargedProjectiles> CHARGED_PROJECTILES =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "charged_projectiles"), ChargedProjectiles.class);

  public static final ValuedDataComponentType<BundleContents> BUNDLE_CONTENTS =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "bundle_contents"), BundleContents.class);

  public static final ValuedDataComponentType<PotionContents> POTION_CONTENTS =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "potion_contents"), PotionContents.class);

  public static final ValuedDataComponentType<Float> POTION_DURATION_SCALE =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "potion_duration_scale"), Float.class);

  public static final ValuedDataComponentType<SuspiciousStewEffects> SUSPICIOUS_STEW_EFFECTS =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "suspicious_stew_effects"), SuspiciousStewEffects.class);

  public static final ValuedDataComponentType<WritableBookContent> WRITABLE_BOOK_CONTENT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "writable_book_content"), WritableBookContent.class);

  public static final ValuedDataComponentType<WrittenBookContent> WRITTEN_BOOK_CONTENT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "written_book_content"), WrittenBookContent.class);

  public static final ValuedDataComponentType<ItemArmorTrim> TRIM =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "trim"), ItemArmorTrim.class);

  public static final ValuedDataComponentType<Key> INSTRUMENT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "instrument"), Key.class);

  public static final ValuedDataComponentType<Key> PROVIDES_TRIM_MATERIAL =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "provides_trim_material"), Key.class);

  public static final ValuedDataComponentType<OminousBottleAmplifier> OMINOUS_BOTTLE_AMPLIFIER =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "ominous_bottle_amplifier"), OminousBottleAmplifier.class);

  public static final ValuedDataComponentType<JukeboxPlayable> JUKEBOX_PLAYABLE =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "jukebox_playable"), JukeboxPlayable.class);

  public static final ValuedDataComponentType<Set> PROVIDES_BANNER_PATTERNS =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "provides_banner_patterns"), Set.class);

  public static final ValuedDataComponentType<List> RECIPES =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "recipes"), List.class);

  public static final ValuedDataComponentType<LodestoneTracker> LODESTONE_TRACKER =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "lodestone_tracker"), LodestoneTracker.class);

  public static final ValuedDataComponentType<FireworkEffect> FIREWORK_EXPLOSION =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "firework_explosion"), FireworkEffect.class);

  public static final ValuedDataComponentType<Fireworks> FIREWORKS =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "fireworks"), Fireworks.class);

  public static final ValuedDataComponentType<ResolvableProfile> PROFILE =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "profile"), ResolvableProfile.class);

  public static final ValuedDataComponentType<Key> NOTE_BLOCK_SOUND =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "note_block_sound"), Key.class);

  public static final ValuedDataComponentType<BannerPatternLayers> BANNER_PATTERNS =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "banner_patterns"), BannerPatternLayers.class);

  public static final ValuedDataComponentType<DyeColor> BASE_COLOR =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "base_color"), DyeColor.class);

  public static final ValuedDataComponentType<PotDecorations> POT_DECORATIONS =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "pot_decorations"), PotDecorations.class);

  public static final ValuedDataComponentType<ItemContainerContents> CONTAINER =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "container"), ItemContainerContents.class);

  public static final ValuedDataComponentType<BlockItemDataProperties> BLOCK_DATA =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "block_data"), BlockItemDataProperties.class);

  public static final ValuedDataComponentType<SulfurCubeContent> SULFUR_CUBE_CONTENT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "sulfur_cube_content"), SulfurCubeContent.class);

  public static final ValuedDataComponentType<SeededContainerLoot> CONTAINER_LOOT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "container_loot"), SeededContainerLoot.class);

  public static final ValuedDataComponentType<Key> BREAK_SOUND =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "break_sound"), Key.class);

  public static final ValuedDataComponentType<Key> VILLAGER_VARIANT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "villager_variant"), Key.class);

  public static final ValuedDataComponentType<Key> WOLF_VARIANT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "wolf_variant"), Key.class);

  public static final ValuedDataComponentType<Key> WOLF_SOUND_VARIANT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "wolf_sound_variant"), Key.class);

  public static final ValuedDataComponentType<DyeColor> WOLF_COLLAR =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "wolf_collar"), DyeColor.class);

  public static final ValuedDataComponentType<Key> FOX_VARIANT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "fox_variant"), Key.class);

  public static final ValuedDataComponentType<Key> SALMON_SIZE =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "salmon_size"), Key.class);

  public static final ValuedDataComponentType<Key> PARROT_VARIANT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "parrot_variant"), Key.class);

  public static final ValuedDataComponentType<Key> TROPICAL_FISH_PATTERN =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "tropical_fish_pattern"), Key.class);

  public static final ValuedDataComponentType<DyeColor> TROPICAL_FISH_BASE_COLOR =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "tropical_fish_base_color"), DyeColor.class);

  public static final ValuedDataComponentType<DyeColor> TROPICAL_FISH_PATTERN_COLOR =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "tropical_fish_pattern_color"), DyeColor.class);

  public static final ValuedDataComponentType<Key> MOOSHROOM_VARIANT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "mooshroom_variant"), Key.class);

  public static final ValuedDataComponentType<Key> RABBIT_VARIANT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "rabbit_variant"), Key.class);

  public static final ValuedDataComponentType<Key> PIG_VARIANT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "pig_variant"), Key.class);

  public static final ValuedDataComponentType<Key> PIG_SOUND_VARIANT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "pig_sound_variant"), Key.class);

  public static final ValuedDataComponentType<Key> COW_VARIANT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "cow_variant"), Key.class);

  public static final ValuedDataComponentType<Key> COW_SOUND_VARIANT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "cow_sound_variant"), Key.class);

  public static final ValuedDataComponentType<Key> CHICKEN_VARIANT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "chicken_variant"), Key.class);

  public static final ValuedDataComponentType<Key> CHICKEN_SOUND_VARIANT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "chicken_sound_variant"), Key.class);

  public static final ValuedDataComponentType<Key> FROG_VARIANT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "frog_variant"), Key.class);

  public static final ValuedDataComponentType<Key> HORSE_VARIANT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "horse_variant"), Key.class);

  public static final ValuedDataComponentType<Key> PAINTING_VARIANT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "painting_variant"), Key.class);

  public static final ValuedDataComponentType<Key> LLAMA_VARIANT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "llama_variant"), Key.class);

  public static final ValuedDataComponentType<Key> AXOLOTL_VARIANT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "axolotl_variant"), Key.class);

  public static final ValuedDataComponentType<Key> ZOMBIE_NAUTILUS_VARIANT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "zombie_nautilus_variant"), Key.class);

  public static final ValuedDataComponentType<Key> CAT_VARIANT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "cat_variant"), Key.class);

  public static final ValuedDataComponentType<Key> CAT_SOUND_VARIANT =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "cat_sound_variant"), Key.class);

  public static final ValuedDataComponentType<DyeColor> CAT_COLLAR =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "cat_collar"), DyeColor.class);

  public static final ValuedDataComponentType<DyeColor> SHEEP_COLOR =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "sheep_color"), DyeColor.class);

  public static final ValuedDataComponentType<DyeColor> SHULKER_COLOR =
      new StandardValuedDataComponentType<>(Key.key("minecraft", "shulker_color"), DyeColor.class);

  /**
   * Common item rarity enum.
   */
  public enum ItemRarity {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC
  }

  /**
   * Common dye color enum.
   */
  public enum DyeColor {
    WHITE,
    ORANGE,
    MAGENTA,
    LIGHT_BLUE,
    YELLOW,
    LIME,
    PINK,
    GRAY,
    LIGHT_GRAY,
    CYAN,
    PURPLE,
    BLUE,
    BROWN,
    GREEN,
    RED,
    BLACK
  }

  /**
   * Common map post processing mode.
   */
  public enum MapPostProcessing {
    LOCK,
    SCALE
  }
}
