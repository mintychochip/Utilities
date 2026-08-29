package org.aincraft.paper.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.attribute.Attribute;
import org.aincraft.api.domain.attribute.AttributeInstance;
import org.aincraft.api.domain.attribute.AttributeModifier;
import org.aincraft.api.domain.block.BlockFace;
import org.aincraft.api.domain.block.BlockState;
import org.aincraft.api.domain.block.BlockType;
import org.aincraft.api.domain.effect.Enchantment;
import org.aincraft.api.domain.effect.PotionEffectType;
import org.aincraft.api.domain.entity.Entity;
import org.aincraft.api.domain.entity.LivingEntity;
import org.aincraft.api.domain.entity.Player;
import org.aincraft.api.domain.inventory.Inventory;
import org.aincraft.api.domain.inventory.InventoryView;
import org.aincraft.api.domain.inventory.ItemStack;
import org.aincraft.api.domain.inventory.ItemType;
import org.aincraft.api.domain.inventory.PlayerInventory;
import org.aincraft.api.domain.location.BoundingBox;
import org.aincraft.api.domain.location.Location;
import org.aincraft.api.domain.location.Position;
import org.aincraft.api.domain.scoreboard.Criteria;
import org.aincraft.api.domain.scoreboard.Objective;
import org.aincraft.api.domain.scoreboard.Score;
import org.aincraft.api.domain.scoreboard.Scoreboard;
import org.aincraft.api.domain.scoreboard.ScoreboardManager;
import org.aincraft.api.domain.scoreboard.Team;
import org.aincraft.api.domain.server.CommandSender;
import org.aincraft.api.domain.server.Server;
import org.aincraft.api.domain.world.Block;
import org.aincraft.api.domain.world.Chunk;
import org.aincraft.api.domain.world.World;
import org.aincraft.api.domain.world.WorldBorder;
import org.aincraft.bukkit.adapter.BukkitAdapters;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public final class PaperAdapters {

  private PaperAdapters() {}

  public static @NotNull Player adapt(@NotNull org.bukkit.entity.Player player) {
    return new PaperPlayerWrapper(player);
  }

  public static @NotNull World adapt(@NotNull org.bukkit.World world) {
    return new PaperWorldWrapper(world);
  }

  public static @NotNull Server adapt(@NotNull org.bukkit.Server server) {
    return new PaperServerWrapper(server);
  }

  public static @NotNull Location adapt(@NotNull org.bukkit.Location location) {
    return BukkitAdapters.adapt(location);
  }

  public static @NotNull org.bukkit.Location toBukkit(@NotNull Location location) {
    return BukkitAdapters.toBukkit(location);
  }

  public static @NotNull Position adapt(@NotNull Vector vector) {
    return BukkitAdapters.adapt(vector);
  }

  public static @NotNull Vector toBukkit(@NotNull Position position) {
    return BukkitAdapters.toBukkit(position);
  }

  public static @NotNull BoundingBox adapt(@NotNull org.bukkit.util.BoundingBox box) {
    return BukkitAdapters.adapt(box);
  }

  public static @NotNull org.bukkit.util.BoundingBox toBukkit(@NotNull BoundingBox box) {
    return BukkitAdapters.toBukkit(box);
  }

  public static @NotNull Block adapt(@NotNull org.bukkit.block.Block block) {
    return new PaperBlockWrapper(block);
  }

  public static @NotNull org.bukkit.block.Block toBukkit(@NotNull Block block) {
    return BukkitAdapters.toBukkit(block);
  }

  public static @NotNull Chunk adapt(@NotNull org.bukkit.Chunk chunk) {
    return BukkitAdapters.adapt(chunk);
  }

  public static @NotNull org.bukkit.Chunk toBukkit(@NotNull Chunk chunk) {
    return BukkitAdapters.toBukkit(chunk);
  }

  public static @NotNull org.bukkit.World toBukkit(@NotNull World world) {
    return BukkitAdapters.toBukkit(world);
  }

  public static @NotNull WorldBorder adapt(@NotNull org.bukkit.WorldBorder worldBorder) {
    return new PaperWorldBorderWrapper(worldBorder);
  }

  public static @NotNull org.bukkit.WorldBorder toBukkit(@NotNull WorldBorder worldBorder) {
    return BukkitAdapters.toBukkit(worldBorder);
  }

  public static @NotNull org.aincraft.api.domain.entity.EntityType adapt(
      @NotNull org.bukkit.entity.EntityType entityType) {
    return new org.aincraft.bukkit.adapter.BukkitEntityTypeWrapper(entityType);
  }

  public static @NotNull org.bukkit.entity.EntityType toBukkit(
      @NotNull org.aincraft.api.domain.entity.EntityType entityType) {
    return BukkitAdapters.toBukkit(entityType);
  }

  public static @NotNull Entity adapt(@NotNull org.bukkit.entity.Entity entity) {
    if (entity instanceof org.bukkit.entity.Player player) {
      return adapt(player);
    }
    if (entity instanceof org.bukkit.entity.LivingEntity living) {
      return adapt(living);
    }
    return BukkitAdapters.adapt(entity);
  }

  public static @NotNull org.bukkit.entity.Entity toBukkit(@NotNull Entity entity) {
    return BukkitAdapters.toBukkit(entity);
  }

  public static @NotNull LivingEntity adapt(@NotNull org.bukkit.entity.LivingEntity entity) {
    if (entity instanceof org.bukkit.entity.Player player) {
      return adapt(player);
    }
    return new PaperLivingEntityWrapper(entity);
  }

  public static @NotNull org.bukkit.entity.LivingEntity toBukkit(@NotNull LivingEntity entity) {
    return BukkitAdapters.toBukkit(entity);
  }

  public static @NotNull org.bukkit.entity.Player toBukkit(@NotNull Player player) {
    return BukkitAdapters.toBukkit(player);
  }

  public static @NotNull ItemStack adapt(@NotNull org.bukkit.inventory.ItemStack item) {
    return new PaperItemStackWrapper(item);
  }

  public static @NotNull org.aincraft.api.domain.inventory.ItemMeta adapt(
      @NotNull org.bukkit.inventory.meta.ItemMeta meta) {
    return meta instanceof org.bukkit.inventory.meta.Damageable damageable
        ? new PaperDamageableItemMetaWrapper(damageable)
        : new PaperItemMetaWrapper(meta);
  }

  public static @NotNull org.bukkit.inventory.meta.ItemMeta toBukkit(
      @NotNull org.aincraft.api.domain.inventory.ItemMeta meta) {
    return BukkitAdapters.toBukkit(meta);
  }

  public static @NotNull org.bukkit.inventory.ItemStack toBukkit(@NotNull ItemStack item) {
    return BukkitAdapters.toBukkit(item);
  }

  public static @NotNull ItemType adapt(@NotNull Material material) {
    return BukkitAdapters.adapt(material);
  }

  public static @NotNull Material toBukkit(@NotNull ItemType itemType) {
    return BukkitAdapters.toBukkit(itemType);
  }

  public static @NotNull Enchantment adapt(
      @NotNull org.bukkit.enchantments.Enchantment enchantment) {
    return new PaperEnchantmentWrapper(enchantment);
  }

  public static @NotNull PotionEffectType adapt(@NotNull org.bukkit.potion.PotionEffectType type) {
    return new PaperPotionEffectTypeWrapper(type);
  }

  public static @NotNull org.aincraft.api.domain.effect.PotionEffect adapt(
      @NotNull org.bukkit.potion.PotionEffect effect) {
    return new PaperPotionEffectWrapper(effect);
  }

  public static @NotNull Inventory adapt(@NotNull org.bukkit.inventory.Inventory inventory) {
    if (inventory instanceof org.bukkit.inventory.PlayerInventory playerInventory) {
      return adapt(playerInventory);
    }
    return new PaperInventoryWrapper(inventory);
  }

  public static @NotNull org.bukkit.inventory.Inventory toBukkit(@NotNull Inventory inventory) {
    return BukkitAdapters.toBukkit(inventory);
  }

  public static @NotNull PlayerInventory adapt(
      @NotNull org.bukkit.inventory.PlayerInventory inventory) {
    return new PaperPlayerInventoryWrapper(inventory);
  }

  public static @NotNull org.bukkit.inventory.PlayerInventory toBukkit(
      @NotNull PlayerInventory inventory) {
    return BukkitAdapters.toBukkit(inventory);
  }

  public static @NotNull InventoryView adapt(@NotNull org.bukkit.inventory.InventoryView view) {
    return new PaperInventoryViewWrapper(view);
  }

  public static @NotNull org.bukkit.Server toBukkit(@NotNull Server server) {
    return BukkitAdapters.toBukkit(server);
  }

  public static @NotNull CommandSender adapt(@NotNull org.bukkit.command.CommandSender sender) {
    if (sender instanceof org.bukkit.entity.Player player) {
      return adapt(player);
    }
    return BukkitAdapters.adapt(sender);
  }

  public static @NotNull org.bukkit.command.CommandSender toBukkit(@NotNull CommandSender sender) {
    return BukkitAdapters.toBukkit(sender);
  }

  public static @NotNull org.aincraft.api.domain.effect.Sound adaptSound(
      @NotNull org.bukkit.Sound sound) {
    return BukkitAdapters.adaptSound(sound);
  }

  public static @NotNull org.bukkit.Sound toBukkit(
      @NotNull org.aincraft.api.domain.effect.Sound sound) {
    return BukkitAdapters.toBukkit(sound);
  }

  public static @NotNull BlockFace adapt(@NotNull org.bukkit.block.BlockFace face) {
    return BukkitAdapters.adapt(face);
  }

  public static @NotNull org.bukkit.block.BlockFace toBukkit(@NotNull BlockFace face) {
    return BukkitAdapters.toBukkit(face);
  }

  public static @NotNull BlockType adaptBlockMaterial(@NotNull Material material) {
    return BukkitAdapters.adaptBlockMaterial(material);
  }

  public static @NotNull Material toBukkitBlockMaterial(@NotNull BlockType blockType) {
    return BukkitAdapters.toBukkitBlockMaterial(blockType);
  }

  public static @NotNull BlockState adapt(@NotNull BlockData blockData) {
    return new PaperBlockStateWrapper(blockData);
  }

  public static @NotNull org.aincraft.api.domain.effect.Particle adapt(
      @NotNull org.bukkit.Particle particle) {
    return BukkitAdapters.adapt(particle);
  }

  public static @NotNull org.bukkit.Particle toBukkit(
      @NotNull org.aincraft.api.domain.effect.Particle particle) {
    return BukkitAdapters.toBukkit(particle);
  }

  public static @NotNull BlockData toBukkit(@NotNull BlockState blockState) {
    return BukkitAdapters.toBukkit(blockState);
  }

  public static @NotNull Key adapt(@NotNull org.bukkit.attribute.Attribute attribute) {
    return BukkitAdapters.adapt(attribute);
  }

  public static @NotNull Attribute adaptAttribute(
      @NotNull org.bukkit.attribute.Attribute attribute) {
    return BukkitAdapters.adaptAttribute(attribute);
  }

  public static @NotNull org.bukkit.attribute.Attribute toBukkit(@NotNull Key attribute) {
    return BukkitAdapters.toBukkit(attribute);
  }

  public static @NotNull org.bukkit.attribute.Attribute toBukkit(@NotNull Attribute attribute) {
    return BukkitAdapters.toBukkit(attribute);
  }

  public static @NotNull AttributeModifier adapt(
      @NotNull org.bukkit.attribute.AttributeModifier modifier) {
    return BukkitAdapters.adapt(modifier);
  }

  public static @NotNull org.bukkit.attribute.AttributeModifier toBukkit(
      @NotNull AttributeModifier modifier) {
    return BukkitAdapters.toBukkit(modifier);
  }

  public static @NotNull AttributeInstance adapt(
      @NotNull org.bukkit.attribute.AttributeInstance instance) {
    return BukkitAdapters.adapt(instance);
  }

  public static @NotNull org.bukkit.attribute.AttributeInstance toBukkit(
      @NotNull AttributeInstance instance) {
    return BukkitAdapters.toBukkit(instance);
  }

  public static @NotNull ScoreboardManager adapt(
      @NotNull org.bukkit.scoreboard.ScoreboardManager manager) {
    return new PaperScoreboardManagerWrapper(manager);
  }

  public static @NotNull Scoreboard adapt(@NotNull org.bukkit.scoreboard.Scoreboard scoreboard) {
    return new PaperScoreboardWrapper(scoreboard);
  }

  public static @NotNull Objective adapt(@NotNull org.bukkit.scoreboard.Objective objective) {
    return new PaperObjectiveWrapper(objective);
  }

  public static @NotNull Score adapt(@NotNull org.bukkit.scoreboard.Score score) {
    return new PaperScoreWrapper(score);
  }

  public static @NotNull Team adapt(@NotNull org.bukkit.scoreboard.Team team) {
    return new PaperTeamWrapper(team);
  }

  public static @NotNull Criteria adapt(@NotNull org.bukkit.scoreboard.Criteria criteria) {
    return new org.aincraft.bukkit.adapter.BukkitCriteriaWrapper(criteria);
  }

  public static @NotNull org.bukkit.scoreboard.ScoreboardManager toBukkit(
      @NotNull ScoreboardManager manager) {
    return BukkitAdapters.toBukkit(manager);
  }

  public static @NotNull org.bukkit.scoreboard.Scoreboard toBukkit(@NotNull Scoreboard scoreboard) {
    return BukkitAdapters.toBukkit(scoreboard);
  }

  public static @NotNull org.bukkit.scoreboard.Objective toBukkit(@NotNull Objective objective) {
    return BukkitAdapters.toBukkit(objective);
  }

  public static @NotNull org.bukkit.scoreboard.Score toBukkit(@NotNull Score score) {
    return BukkitAdapters.toBukkit(score);
  }

  public static @NotNull org.bukkit.scoreboard.Team toBukkit(@NotNull Team team) {
    return BukkitAdapters.toBukkit(team);
  }

  public static @NotNull org.bukkit.scoreboard.Criteria toBukkit(@NotNull Criteria criteria) {
    return BukkitAdapters.toBukkit(criteria);
  }
}
