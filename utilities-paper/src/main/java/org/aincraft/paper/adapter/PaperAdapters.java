package org.aincraft.paper.adapter;

import org.aincraft.bukkit.adapter.BukkitAdapters;
import org.aincraft.common.attribute.Attribute;
import org.aincraft.common.attribute.AttributeInstance;
import org.aincraft.common.attribute.AttributeModifier;
import org.aincraft.common.block.BlockFace;
import org.aincraft.common.block.BlockState;
import org.aincraft.common.block.BlockType;
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
import org.aincraft.common.server.Server;
import org.aincraft.common.world.Block;
import org.aincraft.common.world.Chunk;
import org.aincraft.common.world.World;
import org.aincraft.common.world.WorldBorder;
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
    return BukkitAdapters.adapt(block);
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
    return BukkitAdapters.adapt(worldBorder);
  }

  public static @NotNull org.bukkit.WorldBorder toBukkit(@NotNull WorldBorder worldBorder) {
    return BukkitAdapters.toBukkit(worldBorder);
  }

  public static @NotNull Entity adapt(@NotNull org.bukkit.entity.Entity entity) {
    if (entity instanceof org.bukkit.entity.Player player) {
      return adapt(player);
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
    return BukkitAdapters.adapt(entity);
  }

  public static @NotNull org.bukkit.entity.LivingEntity toBukkit(@NotNull LivingEntity entity) {
    return BukkitAdapters.toBukkit(entity);
  }

  public static @NotNull org.bukkit.entity.Player toBukkit(@NotNull Player player) {
    return BukkitAdapters.toBukkit(player);
  }

  public static @NotNull ItemStack adapt(@NotNull org.bukkit.inventory.ItemStack item) {
    return BukkitAdapters.adapt(item);
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

  public static @NotNull Inventory adapt(@NotNull org.bukkit.inventory.Inventory inventory) {
    return BukkitAdapters.adapt(inventory);
  }

  public static @NotNull org.bukkit.inventory.Inventory toBukkit(@NotNull Inventory inventory) {
    return BukkitAdapters.toBukkit(inventory);
  }

  public static @NotNull PlayerInventory adapt(@NotNull org.bukkit.inventory.PlayerInventory inventory) {
    return BukkitAdapters.adapt(inventory);
  }

  public static @NotNull org.bukkit.inventory.PlayerInventory toBukkit(@NotNull PlayerInventory inventory) {
    return BukkitAdapters.toBukkit(inventory);
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
    return BukkitAdapters.adapt(blockData);
  }

  public static @NotNull BlockData toBukkit(@NotNull BlockState blockState) {
    return BukkitAdapters.toBukkit(blockState);
  }

  public static @NotNull Attribute adapt(@NotNull org.bukkit.attribute.Attribute attribute) {
    return BukkitAdapters.adapt(attribute);
  }

  public static @NotNull org.bukkit.attribute.Attribute toBukkit(@NotNull Attribute attribute) {
    return BukkitAdapters.toBukkit(attribute);
  }

  public static @NotNull AttributeModifier adapt(@NotNull org.bukkit.attribute.AttributeModifier modifier) {
    return BukkitAdapters.adapt(modifier);
  }

  public static @NotNull org.bukkit.attribute.AttributeModifier toBukkit(@NotNull AttributeModifier modifier) {
    return BukkitAdapters.toBukkit(modifier);
  }

  public static @NotNull AttributeInstance adapt(@NotNull org.bukkit.attribute.AttributeInstance instance) {
    return BukkitAdapters.adapt(instance);
  }

  public static @NotNull org.bukkit.attribute.AttributeInstance toBukkit(@NotNull AttributeInstance instance) {
    return BukkitAdapters.toBukkit(instance);
  }
}
