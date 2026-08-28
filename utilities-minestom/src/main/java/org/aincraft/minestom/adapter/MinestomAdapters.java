package org.aincraft.minestom.adapter;

import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import org.aincraft.api.domain.block.BlockFace;
import org.aincraft.api.domain.block.BlockState;
import org.aincraft.api.domain.block.BlockType;
import org.aincraft.api.domain.entity.Entity;
import org.aincraft.api.domain.entity.Player;
import org.aincraft.api.domain.location.BoundingBox;
import org.aincraft.api.domain.location.Location;
import org.aincraft.api.domain.location.Position;
import org.aincraft.api.domain.world.Block;
import org.aincraft.api.domain.world.Chunk;
import org.aincraft.api.domain.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class MinestomAdapters {

  private MinestomAdapters() {}

  public static @NotNull Location adapt(@NotNull Instance instance, @NotNull Pos pos) {
    Objects.requireNonNull(instance, "instance cannot be null");
    Objects.requireNonNull(pos, "pos cannot be null");
    return new MinestomLocationWrapper(adapt(instance), pos);
  }

  public static @NotNull Pos toMinestomPos(@NotNull Location location) {
    Objects.requireNonNull(location, "location cannot be null");
    return new Pos(location.x(), location.y(), location.z(), location.yaw(), location.pitch());
  }

  public static @NotNull Position adapt(@NotNull Point point) {
    Objects.requireNonNull(point, "point cannot be null");
    return new MinestomPositionWrapper(point);
  }

  public static @NotNull Vec toMinestomVec(@NotNull Position position) {
    Objects.requireNonNull(position, "position cannot be null");
    return new Vec(position.x(), position.y(), position.z());
  }

  public static @NotNull BoundingBox adapt(@NotNull net.minestom.server.collision.BoundingBox box) {
    Objects.requireNonNull(box, "box cannot be null");
    return new MinestomBoundingBoxWrapper(box);
  }

  public static @NotNull net.minestom.server.collision.BoundingBox toMinestom(
      @NotNull BoundingBox box) {
    Objects.requireNonNull(box, "box cannot be null");
    return new net.minestom.server.collision.BoundingBox(
        box.maxX() - box.minX(),
        box.maxY() - box.minY(),
        box.maxZ() - box.minZ(),
        new Vec(box.minX(), box.minY(), box.minZ()));
  }

  public static @NotNull World adapt(@NotNull Instance instance) {
    Objects.requireNonNull(instance, "instance cannot be null");
    return new MinestomWorldWrapper(instance);
  }

  public static @NotNull World adapt(
      @NotNull Instance instance, @NotNull String name, @NotNull Key key) {
    Objects.requireNonNull(instance, "instance cannot be null");
    Objects.requireNonNull(name, "name cannot be null");
    Objects.requireNonNull(key, "key cannot be null");
    return new MinestomWorldWrapper(instance, name, key);
  }

  public static @NotNull Instance toMinestom(@NotNull World world) {
    Objects.requireNonNull(world, "world cannot be null");
    if (world instanceof MinestomWorldWrapper wrapper) {
      return wrapper.getMinestomInstance();
    }
    throw new IllegalArgumentException("World is not a MinestomWorldWrapper: " + world);
  }

  public static @NotNull Block adapt(@NotNull Instance instance, int x, int y, int z) {
    Objects.requireNonNull(instance, "instance cannot be null");
    return new MinestomBlockWrapper(instance, x, y, z);
  }

  public static @NotNull Chunk adapt(@NotNull net.minestom.server.instance.Chunk chunk) {
    Objects.requireNonNull(chunk, "chunk cannot be null");
    return new MinestomChunkWrapper(chunk);
  }

  public static @NotNull net.minestom.server.instance.Chunk toMinestom(@NotNull Chunk chunk) {
    Objects.requireNonNull(chunk, "chunk cannot be null");
    if (chunk instanceof MinestomChunkWrapper wrapper) {
      return wrapper.getMinestomChunk();
    }
    throw new IllegalArgumentException("Chunk is not a MinestomChunkWrapper: " + chunk);
  }

  public static @NotNull org.aincraft.api.domain.server.Server adaptServer() {
    return new MinestomServerWrapper();
  }

  public static @NotNull org.aincraft.api.domain.entity.EntityType adapt(
      @NotNull net.minestom.server.entity.EntityType entityType) {
    Objects.requireNonNull(entityType, "entityType cannot be null");
    return new MinestomEntityTypeWrapper(entityType);
  }

  public static @NotNull net.minestom.server.entity.EntityType toMinestom(
      @NotNull org.aincraft.api.domain.entity.EntityType entityType) {
    Objects.requireNonNull(entityType, "entityType cannot be null");
    if (entityType instanceof MinestomEntityTypeWrapper wrapper) {
      return wrapper.getMinestomEntityType();
    }
    net.minestom.server.entity.EntityType result =
        net.minestom.server.entity.EntityType.fromKey(entityType.key());
    if (result == null)
      throw new IllegalArgumentException("Unknown entity type: " + entityType.key());
    return result;
  }

  public static @NotNull Entity adapt(@NotNull net.minestom.server.entity.Entity entity) {
    Objects.requireNonNull(entity, "entity cannot be null");
    if (entity instanceof net.minestom.server.entity.Player player) {
      return adapt(player);
    }
    if (entity instanceof net.minestom.server.entity.LivingEntity living) {
      return adapt(living);
    }
    return new MinestomEntityWrapper(entity);
  }

  public static @NotNull org.aincraft.api.domain.entity.LivingEntity adapt(
      @NotNull net.minestom.server.entity.LivingEntity livingEntity) {
    Objects.requireNonNull(livingEntity, "livingEntity cannot be null");
    return new MinestomLivingEntityWrapper(livingEntity);
  }

  public static @NotNull net.minestom.server.entity.Entity toMinestom(@NotNull Entity entity) {
    Objects.requireNonNull(entity, "entity cannot be null");
    if (entity instanceof MinestomEntityWrapper wrapper) {
      return wrapper.getMinestomEntity();
    }
    throw new IllegalArgumentException("Entity is not a MinestomEntityWrapper: " + entity);
  }

  public static @NotNull Player adapt(@NotNull net.minestom.server.entity.Player player) {
    Objects.requireNonNull(player, "player cannot be null");
    return new MinestomPlayerWrapper(player);
  }

  public static @NotNull net.minestom.server.entity.Player toMinestom(@NotNull Player player) {
    Objects.requireNonNull(player, "player cannot be null");
    if (player instanceof MinestomPlayerWrapper wrapper) {
      return wrapper.getMinestomPlayer();
    }
    throw new IllegalArgumentException("Player is not a MinestomPlayerWrapper: " + player);
  }

  public static @NotNull org.aincraft.api.domain.inventory.Inventory adapt(
      @NotNull net.minestom.server.inventory.AbstractInventory inventory) {
    Objects.requireNonNull(inventory, "inventory cannot be null");
    if (inventory instanceof net.minestom.server.inventory.PlayerInventory playerInventory) {
      return new MinestomPlayerInventoryWrapper(playerInventory, null);
    }
    return new MinestomInventoryWrapper(inventory, null);
  }

  public static @NotNull net.minestom.server.inventory.AbstractInventory toMinestom(
      @NotNull org.aincraft.api.domain.inventory.Inventory inventory) {
    Objects.requireNonNull(inventory, "inventory cannot be null");
    if (inventory instanceof MinestomInventoryWrapper wrapper) {
      return wrapper.getMinestomInventory();
    }
    throw new IllegalArgumentException("Inventory is not a MinestomInventoryWrapper: " + inventory);
  }

  public static @NotNull org.aincraft.api.domain.inventory.ItemType adapt(
      @NotNull net.minestom.server.item.Material material) {
    Objects.requireNonNull(material, "material cannot be null");
    return new MinestomItemTypeWrapper(material);
  }

  public static @NotNull net.minestom.server.item.Material toMinestom(
      @NotNull org.aincraft.api.domain.inventory.ItemType itemType) {
    Objects.requireNonNull(itemType, "itemType cannot be null");
    if (itemType instanceof MinestomItemTypeWrapper wrapper) {
      return wrapper.getMinestomMaterial();
    }
    net.minestom.server.item.Material material =
        net.minestom.server.item.Material.fromKey(itemType.key());
    if (material == null) {
      throw new IllegalArgumentException("Unknown item type: " + itemType.key());
    }
    return material;
  }

  public static @NotNull org.aincraft.api.domain.inventory.ItemStack adapt(
      @NotNull net.minestom.server.item.ItemStack item) {
    Objects.requireNonNull(item, "item cannot be null");
    return new MinestomItemStackWrapper(item);
  }

  public static @NotNull net.minestom.server.item.ItemStack toMinestom(
      @NotNull org.aincraft.api.domain.inventory.ItemStack item) {
    Objects.requireNonNull(item, "item cannot be null");
    if (item instanceof MinestomItemStackWrapper wrapper) {
      return wrapper.getMinestomItemStack();
    }
    throw new IllegalArgumentException("ItemStack is not a MinestomItemStackWrapper: " + item);
  }

  public static @NotNull org.aincraft.api.domain.effect.Enchantment adapt(
      @NotNull
          net.minestom.server.registry.RegistryKey<net.minestom.server.item.enchant.Enchantment>
              enchantment) {
    Objects.requireNonNull(enchantment, "enchantment cannot be null");
    return new MinestomEnchantmentWrapper(enchantment);
  }

  public static @NotNull net.minestom.server.registry.RegistryKey<
          net.minestom.server.item.enchant.Enchantment>
      toMinestom(@NotNull org.aincraft.api.domain.effect.Enchantment enchantment) {
    Objects.requireNonNull(enchantment, "enchantment cannot be null");
    return enchantment instanceof MinestomEnchantmentWrapper wrapper
        ? wrapper.getMinestomKey()
        : net.minestom.server.registry.RegistryKey.unsafeOf(enchantment.key());
  }

  public static @NotNull org.aincraft.api.domain.effect.Particle adapt(
      @NotNull net.minestom.server.particle.Particle particle) {
    Objects.requireNonNull(particle, "particle cannot be null");
    return new MinestomParticleWrapper(particle);
  }

  public static @NotNull net.minestom.server.particle.Particle toMinestom(
      @NotNull org.aincraft.api.domain.effect.Particle particle) {
    Objects.requireNonNull(particle, "particle cannot be null");
    if (particle instanceof MinestomParticleWrapper wrapper) {
      return wrapper.getMinestomParticle();
    }
    net.minestom.server.particle.Particle minestomParticle =
        net.minestom.server.particle.Particle.fromKey(particle.asString());
    if (minestomParticle == null) {
      throw new IllegalArgumentException("Unknown particle: " + particle.asString());
    }
    return minestomParticle;
  }

  public static @NotNull org.aincraft.api.domain.attribute.Attribute adapt(
      @NotNull net.minestom.server.entity.attribute.Attribute attribute) {
    Objects.requireNonNull(attribute, "attribute cannot be null");
    return new MinestomAttributeWrapper(attribute);
  }

  public static @NotNull net.minestom.server.entity.attribute.Attribute toMinestom(
      @NotNull org.aincraft.api.domain.attribute.Attribute attribute) {
    Objects.requireNonNull(attribute, "attribute cannot be null");
    if (attribute instanceof MinestomAttributeWrapper wrapper) {
      return wrapper.getMinestomAttribute();
    }
    net.minestom.server.entity.attribute.Attribute minestom =
        net.minestom.server.entity.attribute.Attribute.fromKey(attribute.key());
    if (minestom == null)
      throw new IllegalArgumentException("Unknown attribute: " + attribute.key());
    return minestom;
  }

  public static @NotNull org.aincraft.api.domain.attribute.AttributeModifier adapt(
      @NotNull net.minestom.server.entity.attribute.AttributeModifier modifier) {
    Objects.requireNonNull(modifier, "modifier cannot be null");
    return new MinestomAttributeModifierWrapper(modifier);
  }

  public static @NotNull net.minestom.server.entity.attribute.AttributeModifier toMinestom(
      @NotNull org.aincraft.api.domain.attribute.AttributeModifier modifier) {
    Objects.requireNonNull(modifier, "modifier cannot be null");
    if (modifier instanceof MinestomAttributeModifierWrapper wrapper) {
      return wrapper.getMinestomModifier();
    }
    net.minestom.server.entity.attribute.AttributeOperation operation =
        switch (modifier.operation()) {
          case ADD_NUMBER -> net.minestom.server.entity.attribute.AttributeOperation.ADD_VALUE;
          case ADD_SCALAR ->
              net.minestom.server.entity.attribute.AttributeOperation.ADD_MULTIPLIED_BASE;
          case MULTIPLY_SCALAR_1 ->
              net.minestom.server.entity.attribute.AttributeOperation.ADD_MULTIPLIED_TOTAL;
        };
    return new net.minestom.server.entity.attribute.AttributeModifier(
        modifier.key(), modifier.amount(), operation);
  }

  public static @NotNull org.aincraft.api.domain.attribute.AttributeInstance adapt(
      @NotNull net.minestom.server.entity.attribute.AttributeInstance instance) {
    Objects.requireNonNull(instance, "instance cannot be null");
    return new MinestomAttributeInstanceWrapper(instance);
  }

  public static @NotNull org.aincraft.api.domain.attribute.AttributeRegistry attributeRegistry() {
    return new MinestomAttributeRegistry();
  }

  public static @NotNull org.aincraft.api.domain.attribute.AttributeModifierFactory
      attributeModifierFactory() {
    return new MinestomAttributeModifierFactory();
  }

  public static @NotNull org.aincraft.api.domain.effect.PotionEffectType adapt(
      @NotNull net.minestom.server.potion.PotionEffect effect) {
    Objects.requireNonNull(effect, "effect cannot be null");
    return new MinestomPotionEffectTypeWrapper(effect);
  }

  public static @NotNull org.aincraft.api.domain.effect.PotionEffect adapt(
      @NotNull net.minestom.server.potion.Potion potion) {
    Objects.requireNonNull(potion, "potion cannot be null");
    return new MinestomPotionEffectWrapper(potion);
  }

  public static @NotNull net.minestom.server.potion.PotionEffect toMinestom(
      @NotNull org.aincraft.api.domain.effect.PotionEffectType effect) {
    Objects.requireNonNull(effect, "effect cannot be null");
    if (effect instanceof MinestomPotionEffectTypeWrapper wrapper) {
      return wrapper.getMinestomPotionEffect();
    }
    net.minestom.server.potion.PotionEffect minestom =
        net.minestom.server.potion.PotionEffect.fromKey(effect.key());
    if (minestom == null)
      throw new IllegalArgumentException("Unknown potion effect: " + effect.key());
    return minestom;
  }

  public static @NotNull net.minestom.server.potion.Potion toMinestom(
      @NotNull org.aincraft.api.domain.effect.PotionEffect effect) {
    Objects.requireNonNull(effect, "effect cannot be null");
    if (effect instanceof MinestomPotionEffectWrapper wrapper) {
      return wrapper.getMinestomPotion();
    }
    return new net.minestom.server.potion.Potion(
        toMinestom(effect.type()), effect.duration(), effect.amplifier());
  }

  public static @NotNull org.aincraft.api.domain.effect.Sound adaptSound(
      @NotNull net.minestom.server.sound.SoundEvent sound) {
    Objects.requireNonNull(sound, "sound cannot be null");
    return new MinestomSoundWrapper(sound);
  }

  public static @NotNull net.minestom.server.sound.SoundEvent toMinestomSound(
      @NotNull org.aincraft.api.domain.effect.Sound sound) {
    Objects.requireNonNull(sound, "sound cannot be null");
    if (sound instanceof MinestomSoundWrapper wrapper) return wrapper.getMinestomSound();
    net.minestom.server.sound.SoundEvent result =
        net.minestom.server.sound.SoundEvent.fromKey(sound.key());
    if (result == null) throw new IllegalArgumentException("Unknown sound: " + sound.key());
    return result;
  }

  public static @NotNull BlockType adapt(@NotNull net.minestom.server.instance.block.Block block) {
    Objects.requireNonNull(block, "block cannot be null");
    return new MinestomBlockTypeWrapper(block);
  }

  public static @NotNull net.minestom.server.instance.block.Block toMinestom(
      @NotNull BlockType blockType) {
    Objects.requireNonNull(blockType, "blockType cannot be null");
    net.minestom.server.instance.block.Block block =
        net.minestom.server.instance.block.Block.fromKey(blockType.key());
    if (block == null) {
      throw new IllegalArgumentException("Unknown block type: " + blockType.key());
    }
    return block;
  }

  public static @NotNull BlockState adaptState(
      @NotNull net.minestom.server.instance.block.Block block) {
    Objects.requireNonNull(block, "block cannot be null");
    return new MinestomBlockStateWrapper(block);
  }

  public static @NotNull net.minestom.server.instance.block.Block toMinestom(
      @NotNull BlockState blockState) {
    Objects.requireNonNull(blockState, "blockState cannot be null");
    net.minestom.server.instance.block.Block block =
        net.minestom.server.instance.block.Block.fromState(blockState.asString());
    if (block == null) {
      throw new IllegalArgumentException("Unknown block state: " + blockState.asString());
    }
    return block;
  }

  public static @NotNull BlockFace adapt(
      @NotNull net.minestom.server.instance.block.BlockFace face) {
    Objects.requireNonNull(face, "face cannot be null");
    return switch (face) {
      case BOTTOM -> BlockFace.DOWN;
      case TOP -> BlockFace.UP;
      case NORTH -> BlockFace.NORTH;
      case SOUTH -> BlockFace.SOUTH;
      case WEST -> BlockFace.WEST;
      case EAST -> BlockFace.EAST;
    };
  }

  public static @NotNull net.minestom.server.instance.block.BlockFace toMinestom(
      @NotNull BlockFace face) {
    Objects.requireNonNull(face, "face cannot be null");
    return switch (face) {
      case DOWN -> net.minestom.server.instance.block.BlockFace.BOTTOM;
      case UP -> net.minestom.server.instance.block.BlockFace.TOP;
      case NORTH -> net.minestom.server.instance.block.BlockFace.NORTH;
      case SOUTH -> net.minestom.server.instance.block.BlockFace.SOUTH;
      case WEST -> net.minestom.server.instance.block.BlockFace.WEST;
      case EAST -> net.minestom.server.instance.block.BlockFace.EAST;
      default ->
          throw new IllegalArgumentException(
              "Minestom does not support 2D/compound block faces: " + face);
    };
  }
}
