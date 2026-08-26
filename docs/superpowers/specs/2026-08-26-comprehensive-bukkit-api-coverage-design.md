# Comprehensive Bukkit/Spigot API Coverage — Design Spec

**Date:** 2026-08-26  
**Status:** Approved  
**Target Modules:** `:common`, `:utilities-bukkit`, `:utilities-paper`, `:utilities-bom`

## Context & Motivation

To provide full domain-agnostic capability mirroring the major abstractions of Bukkit and Spigot, `:common` requires interfaces for Items, Inventories, LivingEntities, Combat, Effects, Enchantments, Biomes, World Borders, Servers, and CommandSenders. Platform adapters in `:utilities-bukkit` and `:utilities-paper` will wrap and unwrap live Bukkit/Paper objects, including genuine Spigot action bar support and Paper native Adventure optimizations.

## Goals

1. **Items & Inventory Subsystem (`org.aincraft.common.inventory`)**:
   - `ItemStack`: Immutable/mutable item representation with amount, type, display name, lore, meta, and clone/modification helpers.
   - `ItemType`: Keyed item type specification with stack size, durability, and categorization.
   - `ItemMeta`: Component-based display name, lore, enchantments, and custom model data.
   - `Inventory`: Size, type, slot get/set, contents array, clear, location, and holder.
   - `PlayerInventory`: Armor slots, main hand, off hand, held slot index.
   - `InventoryHolder`: Container contract returning an `Inventory`.
   - `InventoryType`: Enum covering standard Minecraft container types.
   - `EquipmentSlot`: Enum covering armor and hand slots.

2. **Entity & Combat Subsystem (`org.aincraft.common.entity`)**:
   - `LivingEntity`: Health, max health, eye height/location, line of sight, damage, target, potion effects, swimming/sleeping/gliding states.
   - `Damageable`: Contract for taking damage and health queries.
   - `Projectile`: Shooter and bounce management.
   - `ProjectileSource`: Contract for launching projectiles.
   - `EntityType`: Keyed entity type identifier.
   - `Player`: Enhanced with `PlayerInventory`, `GameMode`, action bars, and titles.

3. **Effects & Environment Subsystem (`org.aincraft.common.effect`, `org.aincraft.common.world`)**:
   - `PotionEffect` & `PotionEffectType`: Duration, amplifier, ambient, particles, and icon flags.
   - `Enchantment`: Keyed enchantment with levels and target checks.
   - `Biome`: Keyed biome contract.
   - `Particle`: Keyed particle type.
   - `SoundCategory`: Master, music, blocks, weather, players, etc.
   - `Environment`: Normal, Nether, The End, Custom.
   - `Difficulty`: Peaceful, Easy, Normal, Hard.
   - `GameMode`: Survival, Creative, Adventure, Spectator.
   - `WorldBorder`: Center, size, damage buffer/amount, warning time/distance, containment checks.
   - `RayTraceResult`: Hit position, hit block, hit block face, and hit entity.

4. **Server & Command Subsystem (`org.aincraft.common.server`)**:
   - `Server`: Version, port, max players, player list, world list, lookup helpers, broadcasting, console sender.
   - `CommandSender`: Permission checks, op status, audience messaging, and name.
   - `ConsoleCommandSender`: Console command sender contract.

5. **Platform Adapters & Unwrapping**:
   - `utilities-bukkit`: Live wrapper implementations for all new interfaces, strict unwrapping with `IllegalArgumentException` on foreign implementations, and Spigot `ChatMessageType.ACTION_BAR` bridge.
   - `utilities-paper`: Native Adventure forwarding and Paper-specific overrides.
   - `utilities-bom`: Comprehensive constraint alignment.

## Architecture & Layout

```
common/
└── src/main/java/org/aincraft/common/
    ├── effect/
    │   ├── Biome.java
    │   ├── Enchantment.java
    │   ├── Particle.java
    │   ├── PotionEffect.java
    │   ├── PotionEffectType.java
    │   └── SoundCategory.java
    ├── entity/
    │   ├── Damageable.java
    │   ├── Entity.java
    │   ├── EntityType.java
    │   ├── LivingEntity.java
    │   ├── Player.java
    │   ├── Projectile.java
    │   └── ProjectileSource.java
    ├── inventory/
    │   ├── EquipmentSlot.java
    │   ├── Inventory.java
    │   ├── InventoryHolder.java
    │   ├── InventoryType.java
    │   ├── ItemMeta.java
    │   ├── ItemStack.java
    │   ├── ItemType.java
    │   └── PlayerInventory.java
    ├── location/
    │   ├── BoundingBox.java
    │   ├── Location.java
    │   ├── Position.java
    │   ├── Vector3d.java
    │   └── Vector3i.java
    ├── server/
    │   ├── CommandSender.java
    │   ├── ConsoleCommandSender.java
    │   └── Server.java
    └── world/
        ├── Block.java
        ├── BlockFace.java
        ├── BlockState.java
        ├── BlockType.java
        ├── Chunk.java
        ├── Difficulty.java
        ├── Environment.java
        ├── GameMode.java
        ├── RayTraceResult.java
        ├── World.java
        └── WorldBorder.java

utilities-bukkit/
└── src/main/java/org/aincraft/bukkit/adapter/
    ├── BukkitAdapters.java
    ├── BukkitBlockStateWrapper.java
    ├── BukkitBlockTypeWrapper.java
    ├── BukkitBlockWrapper.java
    ├── BukkitBoundingBoxWrapper.java
    ├── BukkitChunkWrapper.java
    ├── BukkitCommandSenderWrapper.java
    ├── BukkitConsoleCommandSenderWrapper.java
    ├── BukkitEntityWrapper.java
    ├── BukkitInventoryWrapper.java
    ├── BukkitItemStackWrapper.java
    ├── BukkitItemTypeWrapper.java
    ├── BukkitLivingEntityWrapper.java
    ├── BukkitLocationWrapper.java
    ├── BukkitPlayerInventoryWrapper.java
    ├── BukkitPlayerWrapper.java
    ├── BukkitPositionWrapper.java
    ├── BukkitServerWrapper.java
    ├── BukkitWorldBorderWrapper.java
    └── BukkitWorldWrapper.java

utilities-paper/
└── src/main/java/org/aincraft/paper/adapter/
    ├── PaperAdapters.java
    ├── PaperPlayerWrapper.java
    ├── PaperServerWrapper.java
    └── PaperWorldWrapper.java
```
