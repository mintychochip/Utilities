package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.common.attribute.AttributeInstance;
import org.aincraft.common.attribute.AttributeModifier;
import org.aincraft.common.block.BlockFace;
import org.aincraft.common.block.BlockState;
import org.aincraft.common.block.BlockType;
import org.aincraft.common.effect.Enchantment;
import org.aincraft.common.effect.Particle;
import org.aincraft.common.effect.PotionEffect;
import org.aincraft.common.effect.PotionEffectType;
import net.kyori.adventure.sound.Sound;
import org.aincraft.common.entity.Entity;
import org.aincraft.common.entity.LivingEntity;
import org.aincraft.common.entity.Player;
import org.aincraft.common.inventory.Inventory;
import org.aincraft.common.inventory.ItemStack;
import org.aincraft.common.inventory.ItemType;
import org.aincraft.common.inventory.PlayerInventory;
import org.aincraft.common.location.BoundingBox;
import org.aincraft.common.location.Location;
import org.aincraft.common.location.Position;
import org.aincraft.common.server.CommandSender;
import org.aincraft.common.server.ConsoleCommandSender;
import org.aincraft.common.server.Server;
import org.aincraft.common.world.Block;
import org.aincraft.common.world.Chunk;
import org.aincraft.common.world.World;
import org.aincraft.common.world.WorldBorder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;
import org.aincraft.common.inventory.InventoryType;
import org.aincraft.common.world.GameMode;
import org.aincraft.common.server.OfflinePlayer;
import org.aincraft.common.inventory.InventoryView;
import org.aincraft.common.inventory.InventoryHolder;
import org.aincraft.common.entity.EntityType;
import org.aincraft.common.world.RayTraceResult;
import org.aincraft.common.location.Vector3d;
import org.jetbrains.annotations.NotNull;

public final class BukkitAdapters {

  private BukkitAdapters() {}

  public static @NotNull Location adapt(@NotNull org.bukkit.Location location) {
    return new BukkitLocationWrapper(location);
  }

  public static @NotNull org.bukkit.Location toBukkit(@NotNull Location location) {
    if (location instanceof BukkitLocationWrapper wrapper) {
      return wrapper.getBukkitLocation();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign Location implementation: " + location.getClass().getName());
  }

  public static @NotNull Position adapt(@NotNull Vector vector) {
    return new BukkitPositionWrapper(vector);
  }

  public static @NotNull Vector toBukkit(@NotNull Position position) {
    if (position instanceof BukkitPositionWrapper wrapper) {
      return wrapper.getBukkitVector();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign Position implementation: " + position.getClass().getName());
  }

  public static @NotNull Vector toBukkit(@NotNull Vector3d vector) {
    if (vector instanceof Position position) {
      return toBukkit(position);
    }
    return new Vector(vector.x(), vector.y(), vector.z());
  }

  public static @NotNull BoundingBox adapt(@NotNull org.bukkit.util.BoundingBox box) {
    return new BukkitBoundingBoxWrapper(box);
  }

  public static @NotNull org.bukkit.util.BoundingBox toBukkit(@NotNull BoundingBox box) {
    if (box instanceof BukkitBoundingBoxWrapper wrapper) {
      return wrapper.getBukkitBoundingBox();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign BoundingBox implementation: " + box.getClass().getName());
  }

  public static @NotNull Block adapt(@NotNull org.bukkit.block.Block block) {
    return new BukkitBlockWrapper(block);
  }

  public static @NotNull org.bukkit.block.Block toBukkit(@NotNull Block block) {
    if (block instanceof BukkitBlockWrapper wrapper) {
      return wrapper.getBukkitBlock();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign Block implementation: " + block.getClass().getName());
  }

  public static @NotNull Chunk adapt(@NotNull org.bukkit.Chunk chunk) {
    return new BukkitChunkWrapper(chunk);
  }

  public static @NotNull org.bukkit.Chunk toBukkit(@NotNull Chunk chunk) {
    if (chunk instanceof BukkitChunkWrapper wrapper) {
      return wrapper.getBukkitChunk();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign Chunk implementation: " + chunk.getClass().getName());
  }

  public static @NotNull World adapt(@NotNull org.bukkit.World world) {
    return new BukkitWorldWrapper(world);
  }

  public static @NotNull org.bukkit.World toBukkit(@NotNull World world) {
    if (world instanceof BukkitWorldWrapper wrapper) {
      return wrapper.getBukkitWorld();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign World implementation: " + world.getClass().getName());
  }

  public static @NotNull WorldBorder adapt(@NotNull org.bukkit.WorldBorder worldBorder) {
    return new BukkitWorldBorderWrapper(worldBorder);
  }

  public static @NotNull org.bukkit.WorldBorder toBukkit(@NotNull WorldBorder worldBorder) {
    if (worldBorder instanceof BukkitWorldBorderWrapper wrapper) {
      return wrapper.getBukkitWorldBorder();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign WorldBorder implementation: " + worldBorder.getClass().getName());
  }

  public static @NotNull Entity adapt(@NotNull org.bukkit.entity.Entity entity) {
    if (entity instanceof org.bukkit.entity.Player player) {
      return adapt(player);
    }
    if (entity instanceof org.bukkit.entity.LivingEntity living) {
      return adapt(living);
    }
    return new BukkitEntityWrapper(entity);
  }

  public static @NotNull org.bukkit.entity.Entity toBukkit(@NotNull Entity entity) {
    if (entity instanceof BukkitEntityWrapper wrapper) {
      return wrapper.getBukkitEntity();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign Entity implementation: " + entity.getClass().getName());
  }

  public static @NotNull LivingEntity adapt(@NotNull org.bukkit.entity.LivingEntity entity) {
    if (entity instanceof org.bukkit.entity.Player player) {
      return adapt(player);
    }
    return new BukkitLivingEntityWrapper(entity);
  }

  public static @NotNull org.bukkit.entity.LivingEntity toBukkit(@NotNull LivingEntity entity) {
    if (entity instanceof BukkitLivingEntityWrapper wrapper) {
      return wrapper.getBukkitLivingEntity();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign LivingEntity implementation: " + entity.getClass().getName());
  }

  public static @NotNull Player adapt(@NotNull org.bukkit.entity.Player player) {
    return new BukkitPlayerWrapper(player);
  }

  public static @NotNull org.bukkit.entity.Player toBukkit(@NotNull Player player) {
    if (player instanceof BukkitPlayerWrapper wrapper) {
      return wrapper.getBukkitPlayer();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign Player implementation: " + player.getClass().getName());
  }

  public static @NotNull ItemStack adapt(@NotNull org.bukkit.inventory.ItemStack item) {
    return new BukkitItemStackWrapper(item);
  }

  public static @NotNull org.bukkit.inventory.ItemStack toBukkit(@NotNull ItemStack item) {
    if (item instanceof BukkitItemStackWrapper wrapper) {
      return wrapper.getBukkitItemStack();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign ItemStack implementation: " + item.getClass().getName());
  }

  public static @NotNull ItemType adapt(@NotNull Material material) {
    return new BukkitItemTypeWrapper(material);
  }

  public static @NotNull Material toBukkit(@NotNull ItemType itemType) {
    if (itemType instanceof BukkitItemTypeWrapper wrapper) {
      return wrapper.getBukkitMaterial();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign ItemType implementation: " + itemType.getClass().getName());
  }

  public static @NotNull Inventory adapt(@NotNull org.bukkit.inventory.Inventory inventory) {
    if (inventory instanceof org.bukkit.inventory.PlayerInventory playerInventory) {
      return adapt(playerInventory);
    }
    return new BukkitInventoryWrapper(inventory);
  }

  public static @NotNull org.bukkit.inventory.Inventory toBukkit(@NotNull Inventory inventory) {
    if (inventory instanceof BukkitInventoryWrapper wrapper) {
      return wrapper.getBukkitInventory();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign Inventory implementation: " + inventory.getClass().getName());
  }

  public static @NotNull PlayerInventory adapt(@NotNull org.bukkit.inventory.PlayerInventory inventory) {
    return new BukkitPlayerInventoryWrapper(inventory);
  }

  public static @NotNull org.bukkit.inventory.PlayerInventory toBukkit(@NotNull PlayerInventory inventory) {
    if (inventory instanceof BukkitPlayerInventoryWrapper wrapper) {
      return wrapper.getBukkitPlayerInventory();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign PlayerInventory implementation: " + inventory.getClass().getName());
  }

  public static @NotNull Server adapt(@NotNull org.bukkit.Server server) {
    return new BukkitServerWrapper(server);
  }

  public static @NotNull org.bukkit.Server toBukkit(@NotNull Server server) {
    if (server instanceof BukkitServerWrapper wrapper) {
      return wrapper.getBukkitServer();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign Server implementation: " + server.getClass().getName());
  }

  public static @NotNull CommandSender adapt(@NotNull org.bukkit.command.CommandSender sender) {
    if (sender instanceof org.bukkit.entity.Player player) {
      return adapt(player);
    }
    if (sender instanceof org.bukkit.command.ConsoleCommandSender console) {
      return new BukkitConsoleCommandSenderWrapper(console);
    }
    return new BukkitCommandSenderWrapper(sender);
  }

  public static @NotNull org.bukkit.command.CommandSender toBukkit(@NotNull CommandSender sender) {
    if (sender instanceof BukkitPlayerWrapper wrapper) {
      return wrapper.getBukkitPlayer();
    }
    if (sender instanceof BukkitCommandSenderWrapper wrapper) {
      return wrapper.getBukkitCommandSender();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign CommandSender implementation: " + sender.getClass().getName());
  }

  public static @NotNull BlockFace adapt(@NotNull org.bukkit.block.BlockFace face) {
    return switch (face) {
      case NORTH -> BlockFace.NORTH;
      case EAST -> BlockFace.EAST;
      case SOUTH -> BlockFace.SOUTH;
      case WEST -> BlockFace.WEST;
      case UP -> BlockFace.UP;
      case DOWN -> BlockFace.DOWN;
      case NORTH_EAST -> BlockFace.NORTH_EAST;
      case NORTH_WEST -> BlockFace.NORTH_WEST;
      case SOUTH_EAST -> BlockFace.SOUTH_EAST;
      case SOUTH_WEST -> BlockFace.SOUTH_WEST;
      case WEST_NORTH_WEST -> BlockFace.WEST_NORTH_WEST;
      case NORTH_NORTH_WEST -> BlockFace.NORTH_NORTH_WEST;
      case NORTH_NORTH_EAST -> BlockFace.NORTH_NORTH_EAST;
      case EAST_NORTH_EAST -> BlockFace.EAST_NORTH_EAST;
      case EAST_SOUTH_EAST -> BlockFace.EAST_SOUTH_EAST;
      case SOUTH_SOUTH_EAST -> BlockFace.SOUTH_SOUTH_EAST;
      case SOUTH_SOUTH_WEST -> BlockFace.SOUTH_SOUTH_WEST;
      case WEST_SOUTH_WEST -> BlockFace.WEST_SOUTH_WEST;
      case SELF -> BlockFace.SELF;
    };
  }

  public static @NotNull org.bukkit.block.BlockFace toBukkit(@NotNull BlockFace face) {
    return switch (face) {
      case NORTH -> org.bukkit.block.BlockFace.NORTH;
      case EAST -> org.bukkit.block.BlockFace.EAST;
      case SOUTH -> org.bukkit.block.BlockFace.SOUTH;
      case WEST -> org.bukkit.block.BlockFace.WEST;
      case UP -> org.bukkit.block.BlockFace.UP;
      case DOWN -> org.bukkit.block.BlockFace.DOWN;
      case NORTH_EAST -> org.bukkit.block.BlockFace.NORTH_EAST;
      case NORTH_WEST -> org.bukkit.block.BlockFace.NORTH_WEST;
      case SOUTH_EAST -> org.bukkit.block.BlockFace.SOUTH_EAST;
      case SOUTH_WEST -> org.bukkit.block.BlockFace.SOUTH_WEST;
      case WEST_NORTH_WEST -> org.bukkit.block.BlockFace.WEST_NORTH_WEST;
      case NORTH_NORTH_WEST -> org.bukkit.block.BlockFace.NORTH_NORTH_WEST;
      case NORTH_NORTH_EAST -> org.bukkit.block.BlockFace.NORTH_NORTH_EAST;
      case EAST_NORTH_EAST -> org.bukkit.block.BlockFace.EAST_NORTH_EAST;
      case EAST_SOUTH_EAST -> org.bukkit.block.BlockFace.EAST_SOUTH_EAST;
      case SOUTH_SOUTH_EAST -> org.bukkit.block.BlockFace.SOUTH_SOUTH_EAST;
      case SOUTH_SOUTH_WEST -> org.bukkit.block.BlockFace.SOUTH_SOUTH_WEST;
      case WEST_SOUTH_WEST -> org.bukkit.block.BlockFace.WEST_SOUTH_WEST;
      case SELF -> org.bukkit.block.BlockFace.SELF;
    };
  }

  public static @NotNull BlockType adaptBlockMaterial(@NotNull Material material) {
    return new BukkitBlockTypeWrapper(material);
  }

  public static @NotNull Material toBukkitBlockMaterial(@NotNull BlockType blockType) {
    if (blockType instanceof BukkitBlockTypeWrapper wrapper) {
      return wrapper.getBukkitMaterial();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign BlockType implementation: " + blockType.getClass().getName());
  }

  public static @NotNull BlockState adapt(@NotNull BlockData blockData) {
    return new BukkitBlockStateWrapper(blockData);
  }

  public static @NotNull BlockData toBukkit(@NotNull BlockState blockState) {
    if (blockState instanceof BukkitBlockStateWrapper wrapper) {
      return wrapper.getBukkitBlockData();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign BlockState implementation: " + blockState.getClass().getName());
  }

  public static @NotNull Key adapt(@NotNull org.bukkit.attribute.Attribute attribute) {
    return new BukkitAttributeWrapper(attribute);
  }

  public static @NotNull org.bukkit.attribute.Attribute toBukkit(@NotNull Key attribute) {
    if (attribute instanceof BukkitAttributeWrapper wrapper) {
      return wrapper.getBukkitAttribute();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign Attribute implementation: " + attribute.getClass().getName());
  }

  public static @NotNull Key adapt(@NotNull org.bukkit.block.Biome biome) {
    return new BukkitBiomeWrapper(biome);
  }

  public static @NotNull org.bukkit.block.Biome toBukkitBiome(@NotNull Key biome) {
    if (biome instanceof BukkitBiomeWrapper wrapper) {
      return wrapper.getBukkitBiome();
    }
    org.bukkit.block.Biome bBiome = org.bukkit.Registry.BIOME.get(NamespacedKey.fromString(biome.asString()));
    if (bBiome == null) {
      throw new IllegalArgumentException("Cannot resolve Biome for key: " + biome.asString());
    }
    return bBiome;
  }

  public static @NotNull AttributeModifier adapt(@NotNull org.bukkit.attribute.AttributeModifier modifier) {
    return new BukkitAttributeModifierWrapper(modifier);
  }

  public static @NotNull org.bukkit.attribute.AttributeModifier toBukkit(@NotNull AttributeModifier modifier) {
    if (modifier instanceof BukkitAttributeModifierWrapper wrapper) {
      return wrapper.getBukkitAttributeModifier();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign AttributeModifier implementation: " + modifier.getClass().getName());
  }

  public static @NotNull AttributeInstance adapt(@NotNull org.bukkit.attribute.AttributeInstance instance) {
    return new BukkitAttributeInstanceWrapper(instance);
  }

  public static @NotNull org.bukkit.attribute.AttributeInstance toBukkit(@NotNull AttributeInstance instance) {
    if (instance instanceof BukkitAttributeInstanceWrapper wrapper) {
      return wrapper.getBukkitAttributeInstance();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign AttributeInstance implementation: " + instance.getClass().getName());
  }
  public static @NotNull PotionEffectType adapt(@NotNull org.bukkit.potion.PotionEffectType type) {
    return new BukkitPotionEffectTypeWrapper(type);
  }

  public static @NotNull org.bukkit.potion.PotionEffectType toBukkit(@NotNull PotionEffectType type) {
    if (type instanceof BukkitPotionEffectTypeWrapper wrapper) {
      return wrapper.getBukkitPotionEffectType();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign PotionEffectType implementation: " + type.getClass().getName());
  }

  public static @NotNull PotionEffect adapt(@NotNull org.bukkit.potion.PotionEffect effect) {
    return new BukkitPotionEffectWrapper(effect);
  }

  public static @NotNull org.bukkit.potion.PotionEffect toBukkit(@NotNull PotionEffect effect) {
    if (effect instanceof BukkitPotionEffectWrapper wrapper) {
      return wrapper.getBukkitPotionEffect();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign PotionEffect implementation: " + effect.getClass().getName());
  }

  public static @NotNull Sound.Type adapt(@NotNull org.bukkit.Sound sound) {
    return new BukkitSoundWrapper(sound);
  }

  public static @NotNull org.bukkit.Sound toBukkit(@NotNull Sound.Type sound) {
    if (sound instanceof BukkitSoundWrapper wrapper) {
      return wrapper.getBukkitSound();
    }
    org.bukkit.Sound bSound = org.bukkit.Registry.SOUNDS.get(NamespacedKey.fromString(sound.key().asString()));
    if (bSound == null) {
      throw new IllegalArgumentException("Cannot resolve Sound for key: " + sound.key());
    }
    return bSound;
  }

  public static @NotNull Particle adapt(@NotNull org.bukkit.Particle particle) {
    return new BukkitParticleWrapper(particle);
  }

  public static @NotNull org.bukkit.Particle toBukkit(@NotNull Particle particle) {
    if (particle instanceof BukkitParticleWrapper wrapper) {
      return wrapper.getBukkitParticle();
    }
    org.bukkit.Particle bParticle = org.bukkit.Registry.PARTICLE_TYPE.get(NamespacedKey.fromString(particle.key().asString()));
    if (bParticle == null) {
      throw new IllegalArgumentException("Cannot resolve Particle for key: " + particle.key());
    }
    return bParticle;
  }

  public static @NotNull Enchantment adapt(@NotNull org.bukkit.enchantments.Enchantment enchantment) {
    return new BukkitEnchantmentWrapper(enchantment);
  }

  public static @NotNull org.bukkit.enchantments.Enchantment toBukkit(@NotNull Enchantment enchantment) {
    if (enchantment instanceof BukkitEnchantmentWrapper wrapper) {
      return wrapper.getBukkitEnchantment();
    }
    org.bukkit.enchantments.Enchantment bEnchantment = org.bukkit.enchantments.Enchantment.getByKey(NamespacedKey.fromString(enchantment.key().asString()));
    if (bEnchantment == null) {
      throw new IllegalArgumentException("Cannot resolve Enchantment for key: " + enchantment.key());
    }
    return bEnchantment;
  }

  public static @NotNull EntityType adapt(@NotNull org.bukkit.entity.EntityType entityType) {
    return new BukkitEntityTypeWrapper(entityType);
  }

  public static @NotNull org.bukkit.entity.EntityType toBukkit(@NotNull EntityType entityType) {
    if (entityType instanceof BukkitEntityTypeWrapper wrapper) {
      return wrapper.getBukkitEntityType();
    }
    org.bukkit.entity.EntityType bType = org.bukkit.entity.EntityType.fromName(entityType.key().value());
    if (bType == null) {
      bType = org.bukkit.Registry.ENTITY_TYPE.get(NamespacedKey.fromString(entityType.key().asString()));
    }
    if (bType == null) {
      throw new IllegalArgumentException("Cannot resolve EntityType for key: " + entityType.key());
    }
    return bType;
  }

  public static @NotNull InventoryType adapt(@NotNull org.bukkit.event.inventory.InventoryType inventoryType) {
    return InventoryType.valueOf(inventoryType.name());
  }

  public static @NotNull org.bukkit.event.inventory.InventoryType toBukkit(@NotNull InventoryType inventoryType) {
    return org.bukkit.event.inventory.InventoryType.valueOf(inventoryType.name());
  }

  public static @NotNull InventoryHolder adapt(@NotNull org.bukkit.inventory.InventoryHolder holder) {
    return new BukkitInventoryHolderWrapper(holder);
  }

  public static @NotNull org.bukkit.inventory.InventoryHolder toBukkit(@NotNull InventoryHolder holder) {
    if (holder instanceof BukkitInventoryHolderWrapper wrapper) {
      return wrapper.getBukkitInventoryHolder();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign InventoryHolder implementation: " + holder.getClass().getName());
  }

  public static @NotNull GameMode adapt(@NotNull org.bukkit.GameMode gameMode) {
    return switch (gameMode) {
      case SURVIVAL -> GameMode.SURVIVAL;
      case CREATIVE -> GameMode.CREATIVE;
      case ADVENTURE -> GameMode.ADVENTURE;
      case SPECTATOR -> GameMode.SPECTATOR;
    };
  }

  public static @NotNull org.bukkit.GameMode toBukkit(@NotNull GameMode gameMode) {
    return switch (gameMode) {
      case SURVIVAL -> org.bukkit.GameMode.SURVIVAL;
      case CREATIVE -> org.bukkit.GameMode.CREATIVE;
      case ADVENTURE -> org.bukkit.GameMode.ADVENTURE;
      case SPECTATOR -> org.bukkit.GameMode.SPECTATOR;
    };
  }

  public static @NotNull OfflinePlayer adapt(@NotNull org.bukkit.OfflinePlayer player) {
    return new BukkitOfflinePlayerWrapper(player);
  }

  public static @NotNull org.bukkit.OfflinePlayer toBukkit(@NotNull OfflinePlayer player) {
    if (player instanceof BukkitOfflinePlayerWrapper wrapper) {
      return wrapper.getBukkitOfflinePlayer();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign OfflinePlayer implementation: " + player.getClass().getName());
  }

  public static @NotNull InventoryView adapt(@NotNull org.bukkit.inventory.InventoryView view) {
    return new BukkitInventoryViewWrapper(view);
  }

  public static @NotNull org.bukkit.inventory.InventoryView toBukkit(@NotNull InventoryView view) {
    if (view instanceof BukkitInventoryViewWrapper wrapper) {
      return wrapper.getBukkitInventoryView();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign InventoryView implementation: " + view.getClass().getName());
  }

  public static @NotNull RayTraceResult adapt(@NotNull org.bukkit.util.RayTraceResult result) {
    return new BukkitRayTraceResultWrapper(result);
  }

  public static @NotNull org.aincraft.common.entity.ProjectileSource adapt(@NotNull org.bukkit.projectiles.ProjectileSource source) {
    return new BukkitProjectileSourceWrapper(source);
  }

  public static @NotNull org.bukkit.projectiles.ProjectileSource toBukkit(@NotNull org.aincraft.common.entity.ProjectileSource source) {
    if (source instanceof BukkitProjectileSourceWrapper wrapper) {
      return wrapper.getBukkitProjectileSource();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign ProjectileSource implementation: " + source.getClass().getName());
  }

  public static @NotNull org.aincraft.common.entity.Projectile adapt(@NotNull org.bukkit.entity.Projectile projectile) {
    return new BukkitProjectileWrapper(projectile);
  }

  public static @NotNull org.bukkit.entity.Projectile toBukkit(@NotNull org.aincraft.common.entity.Projectile projectile) {
    if (projectile instanceof BukkitProjectileWrapper wrapper) {
      return wrapper.getBukkitProjectile();
    }
    throw new IllegalArgumentException("Cannot unwrap foreign Projectile implementation: " + projectile.getClass().getName());
  }
}
