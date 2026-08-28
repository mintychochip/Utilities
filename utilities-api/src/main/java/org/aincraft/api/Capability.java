package org.aincraft.api;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

/**
 * A named capability that an adapter may or may not support. Adapters advertise their support
 * matrix; unsupported calls throw {@link UnsupportedCapabilityException} rather than silently
 * fabricating state.
 */
public interface Capability {

  @NotNull
  String name();

  @NotNull
  Key key();

  @NotNull Capability ATTRIBUTE_MODIFIER = of("attribute_modifier");
  @NotNull Capability CREATE_INVENTORY = of("create_inventory");
  @NotNull Capability OFFLINE_PLAYER = of("offline_player");
  @NotNull Capability TILE_BLOCK_STATE = of("tile_block_state");
  @NotNull Capability RAYTRACE = of("raytrace");
  @NotNull Capability BLOCK_QUERY = of("block_query");
  @NotNull Capability VOXEL_SHAPE = of("voxel_shape");
  @NotNull Capability PISTON_REACTION = of("piston_reaction");
  @NotNull Capability BLOCK_SUPPORT = of("block_support");
  @NotNull Capability DISPATCH_COMMAND = of("dispatch_command");
  @NotNull Capability MOTD = of("motd");
  @NotNull Capability PRIMARY_THREAD = of("primary_thread");
  @NotNull Capability ENDER_CHEST = of("ender_chest");
  @NotNull Capability CURSOR_ITEM = of("cursor_item");
  @NotNull Capability EXHAUSTION = of("exhaustion");
  @NotNull Capability BED_SPAWN = of("bed_spawn");
  @NotNull Capability WEATHER = of("weather");
  @NotNull Capability TIME_SET = of("time_set");
  @NotNull Capability EXPLOSION = of("explosion");
  @NotNull Capability REDSTONE = of("redstone");
  @NotNull Capability DAMAGEABLE_ABSORPTION = of("damageable_absorption");
  @NotNull Capability COMBAT = of("combat");
  @NotNull Capability BED_SPAWN_FORCE = of("bed_spawn_force");
  @NotNull Capability WORLD_BORDER_ANIMATE = of("world_border_animate");
  @NotNull Capability LOCATION_NEARBY = of("location_nearby");
  @NotNull Capability PARTICLE = of("particle");
  @NotNull Capability SOUND = of("sound");
  @NotNull Capability ENTITY_EFFECT = of("entity_effect");
  @NotNull Capability ENTITY_SPAWN = of("entity_spawn");
  @NotNull Capability ENTITY_LOOKUP = of("entity_lookup");
  @NotNull Capability WORLD_CONFIGURATION = of("world_configuration");
  @NotNull Capability SERVER_INFO = of("server_info");
  @NotNull Capability SERVER_TICK = of("server_tick");
  @NotNull Capability PLAYER_LOOKUP = of("player_lookup");
  @NotNull Capability PERMISSION_BROADCAST = of("permission_broadcast");
  @NotNull Capability SERVER_PERSISTENCE = of("server_persistence");
  @NotNull Capability LIVING_AIR = of("living_air");
  @NotNull Capability LIVING_AI = of("living_ai");
  @NotNull Capability ENCHANTMENT_METADATA = of("enchantment_metadata");
  @NotNull Capability POTION_EFFECT_ATTRIBUTES = of("potion_effect_attributes");
  @NotNull Capability INVENTORY_VIEW = of("inventory_view");
  @NotNull Capability ITEM_DATA_COMPONENT = of("item_data_component");
  @NotNull Capability PERSISTENT_DATA = of("persistent_data");

  static @NotNull Capability of(@NotNull String name) {
    return new Capability() {
      private final Key key = Key.key("aincraft", name);

      @Override
      public @NotNull String name() {
        return name;
      }

      @Override
      public @NotNull Key key() {
        return key;
      }

      @Override
      public String toString() {
        return key.asString();
      }
    };
  }
}
