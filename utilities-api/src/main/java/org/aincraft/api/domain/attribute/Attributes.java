package org.aincraft.api.domain.attribute;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

/**
 * Provides access to well-known attribute instances by key. Corresponds to the static sentinel
 * fields on {@code org.bukkit.attribute.Attribute} (e.g. {@code Attribute.GENERIC_MAX_HEALTH}) but
 * accessed via {@link AttributeRegistry} lookup so plugins do not hold direct Bukkit references.
 *
 * <p>Usage: {@code Attribute maxHealth = Attributes.get(Attribute.MAX_HEALTH);}
 *
 * @see Attribute
 * @see AttributeRegistry
 */
public interface Attributes {

  /** Looks up an attribute by its namespaced key. */
  @NotNull
  Attribute get(@NotNull Key key);

  default @NotNull Attribute maxHealth() {
    return get(MAX_HEALTH);
  }

  default @NotNull Attribute attackDamage() {
    return get(ATTACK_DAMAGE);
  }

  default @NotNull Attribute movementSpeed() {
    return get(MOVEMENT_SPEED);
  }

  default @NotNull Attribute attackSpeed() {
    return get(ATTACK_SPEED);
  }

  default @NotNull Attribute armor() {
    return get(ARMOR);
  }

  default @NotNull Attribute armorToughness() {
    return get(ARMOR_TOUGHNESS);
  }

  default @NotNull Attribute knockbackResistance() {
    return get(KNOCKBACK_RESISTANCE);
  }

  default @NotNull Attribute luck() {
    return get(LUCK);
  }

  default @NotNull Attribute followRange() {
    return get(FOLLOW_RANGE);
  }

  default @NotNull Attribute flyingSpeed() {
    return get(FLYING_SPEED);
  }

  default @NotNull Attribute jumpStrength() {
    return get(JUMP_STRENGTH);
  }

  // Sentinel well-known attribute keys. Values are obtained via get(Key).
  // These constants are the canonical key values; the actual Attribute
  // instances come from the server registry at runtime.

  @NotNull Key MAX_HEALTH = Key.key("minecraft", "max_health");
  @NotNull Key MAX_ABSORPTION = Key.key("minecraft", "max_absorption");
  @NotNull Key FOLLOW_RANGE = Key.key("minecraft", "follow_range");
  @NotNull Key KNOCKBACK_RESISTANCE = Key.key("minecraft", "knockback_resistance");
  @NotNull Key MOVEMENT_SPEED = Key.key("minecraft", "movement_speed");
  @NotNull Key FLYING_SPEED = Key.key("minecraft", "flying_speed");
  @NotNull Key ARMOR = Key.key("minecraft", "armor");
  @NotNull Key ARMOR_TOUGHNESS = Key.key("minecraft", "armor_toughness");
  @NotNull Key ATTACK_DAMAGE = Key.key("minecraft", "attack_damage");
  @NotNull Key ATTACK_KNOCKBACK = Key.key("minecraft", "attack_knockback");
  @NotNull Key ATTACK_SPEED = Key.key("minecraft", "attack_speed");
  @NotNull Key LUCK = Key.key("minecraft", "luck");
  @NotNull Key JUMP_STRENGTH = Key.key("minecraft", "jump_strength");
  @NotNull Key BLOCK_BREAK_SPEED = Key.key("minecraft", "block_break_speed");
  @NotNull Key SUBMERGED_MINING_SPEED = Key.key("minecraft", "submerged_mining_speed");
  @NotNull Key MINING_EFFICIENCY = Key.key("minecraft", "mining_efficiency");
  @NotNull Key MOVEMENT_EFFICIENCY = Key.key("minecraft", "movement_efficiency");
  @NotNull Key WATER_MOVEMENT_EFFICIENCY = Key.key("minecraft", "water_movement_efficiency");
  @NotNull Key SNEAKING_SPEED = Key.key("minecraft", "sneaking_speed");
  @NotNull Key SPAWN_REINFORCEMENTS = Key.key("minecraft", "spawn_reinforcements");
  @NotNull Key SAFE_FALL_DISTANCE = Key.key("minecraft", "safe_fall_distance");
  @NotNull Key FALL_DAMAGE_MULTIPLIER = Key.key("minecraft", "fall_damage_multiplier");
  @NotNull Key BURNING_TIME = Key.key("minecraft", "burning_time");

  @NotNull
  Key EXPLOSION_KNOCKBACK_RESISTANCE = Key.key("minecraft", "explosion_knockback_resistance");

  @NotNull Key ENTITY_INTERACTION_RANGE = Key.key("minecraft", "entity_interaction_range");
  @NotNull Key BLOCK_INTERACTION_RANGE = Key.key("minecraft", "block_interaction_range");
  @NotNull Key GRAVITY = Key.key("minecraft", "gravity");
  @NotNull Key STEP_HEIGHT = Key.key("minecraft", "step_height");
  @NotNull Key AIR_DRAG_MODIFIER = Key.key("minecraft", "air_drag_modifier");
  @NotNull Key FRICTION_MODIFIER = Key.key("minecraft", "friction_modifier");
  @NotNull Key OXYGEN_BONUS = Key.key("minecraft", "oxygen_bonus");
  @NotNull Key CAMERA_DISTANCE = Key.key("minecraft", "camera_distance");
  @NotNull Key FOLLOW_RANGE_WORKAROUND = FOLLOW_RANGE;
  @NotNull Key NAME_TAG_DISTANCE = Key.key("minecraft", "name_tag_distance");
  @NotNull Key SCALE = Key.key("minecraft", "scale");
  @NotNull Key HEALTH_SCALE = Key.key("minecraft", "health_scale");
}
