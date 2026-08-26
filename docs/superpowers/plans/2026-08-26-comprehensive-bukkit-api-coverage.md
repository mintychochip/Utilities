# Comprehensive Bukkit/Spigot API Coverage Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement full domain-agnostic API coverage in `:common` mirroring major Bukkit/Spigot abstractions (Inventory, Items, Living Entities, Effects, Server, WorldBorder) along with live platform adapters in `:utilities-bukkit` and `:utilities-paper`.

**Architecture:** 
- Pure interface types in `:common` with zero concrete backings.
- Live delegating wrappers in `:utilities-bukkit` with Spigot action bar support and strict unwrapping.
- Paper-specialized overrides in `:utilities-paper` with native Adventure dispatch.

**Tech Stack:** Java 25, Spigot API (`1.21.4-R0.1-SNAPSHOT`), Paper API (`26.2.build.119-stable`), Adventure API & Legacy Serializer (`4.18.0`), JUnit 5 (`5.11.3`).

## Global Constraints

- No concrete backings in `:common`.
- Java 25 toolchain across all submodules.
- Package isolation rules strictly enforced.
- Non-generic `Location` throughout.

---

### Task 1: Effects & World Domain Interfaces

**Files:**
- Create: `common/src/main/java/org/aincraft/common/effect/PotionEffect.java`
- Create: `common/src/main/java/org/aincraft/common/effect/PotionEffectType.java`
- Create: `common/src/main/java/org/aincraft/common/effect/Enchantment.java`
- Create: `common/src/main/java/org/aincraft/common/effect/Particle.java`
- Create: `common/src/main/java/org/aincraft/common/effect/SoundCategory.java`
- Create: `common/src/main/java/org/aincraft/common/effect/Biome.java`
- Create: `common/src/main/java/org/aincraft/common/world/Environment.java`
- Create: `common/src/main/java/org/aincraft/common/world/Difficulty.java`
- Create: `common/src/main/java/org/aincraft/common/world/GameMode.java`
- Create: `common/src/main/java/org/aincraft/common/world/WorldBorder.java`
- Create: `common/src/main/java/org/aincraft/common/world/RayTraceResult.java`

- [ ] **Step 1: Implement Effect & World types in `:common`**
- [ ] **Step 2: Run `./gradlew :common:check`**
- [ ] **Step 3: Commit**

---

### Task 2: Inventory & Item Domain Interfaces

**Files:**
- Create: `common/src/main/java/org/aincraft/common/inventory/ItemType.java`
- Create: `common/src/main/java/org/aincraft/common/inventory/ItemMeta.java`
- Create: `common/src/main/java/org/aincraft/common/inventory/ItemStack.java`
- Create: `common/src/main/java/org/aincraft/common/inventory/InventoryType.java`
- Create: `common/src/main/java/org/aincraft/common/inventory/EquipmentSlot.java`
- Create: `common/src/main/java/org/aincraft/common/inventory/Inventory.java`
- Create: `common/src/main/java/org/aincraft/common/inventory/InventoryHolder.java`
- Create: `common/src/main/java/org/aincraft/common/inventory/PlayerInventory.java`

- [ ] **Step 1: Implement Item & Inventory types in `:common`**
- [ ] **Step 2: Run `./gradlew :common:check`**
- [ ] **Step 3: Commit**

---

### Task 3: LivingEntity, Combat & Server Domain Interfaces

**Files:**
- Create: `common/src/main/java/org/aincraft/common/entity/Damageable.java`
- Create: `common/src/main/java/org/aincraft/common/entity/LivingEntity.java`
- Create: `common/src/main/java/org/aincraft/common/entity/Projectile.java`
- Create: `common/src/main/java/org/aincraft/common/entity/ProjectileSource.java`
- Create: `common/src/main/java/org/aincraft/common/entity/EntityType.java`
- Modify: `common/src/main/java/org/aincraft/common/entity/Player.java`
- Modify: `common/src/main/java/org/aincraft/common/world/World.java`
- Create: `common/src/main/java/org/aincraft/common/server/CommandSender.java`
- Create: `common/src/main/java/org/aincraft/common/server/ConsoleCommandSender.java`
- Create: `common/src/main/java/org/aincraft/common/server/Server.java`

- [ ] **Step 1: Implement Entity, Server, and Command types in `:common`**
- [ ] **Step 2: Run `./gradlew :common:check`**
- [ ] **Step 3: Commit**

---

### Task 4: Implement Bukkit Platform Adapters in `:utilities-bukkit`

**Files:**
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitItemTypeWrapper.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitItemStackWrapper.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitInventoryWrapper.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitPlayerInventoryWrapper.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitLivingEntityWrapper.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitWorldBorderWrapper.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitServerWrapper.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitCommandSenderWrapper.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitConsoleCommandSenderWrapper.java`
- Modify: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitPlayerWrapper.java` (add real Spigot action bar bridge)
- Modify: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitAdapters.java` (strict unwrapping & new adapters)

- [ ] **Step 1: Implement Bukkit wrappers and update facade**
- [ ] **Step 2: Run `./gradlew :utilities-bukkit:check`**
- [ ] **Step 3: Commit**

---

### Task 5: Implement Paper Specialized Platform Adapters in `:utilities-paper`

**Files:**
- Create: `utilities-paper/src/main/java/org/aincraft/paper/adapter/PaperServerWrapper.java`
- Modify: `utilities-paper/src/main/java/org/aincraft/paper/adapter/PaperPlayerWrapper.java`
- Modify: `utilities-paper/src/main/java/org/aincraft/paper/adapter/PaperWorldWrapper.java`
- Modify: `utilities-paper/src/main/java/org/aincraft/paper/adapter/PaperAdapters.java`

- [ ] **Step 1: Implement Paper wrappers and update facade**
- [ ] **Step 2: Run `./gradlew :utilities-paper:check`**
- [ ] **Step 3: Commit**

---

### Task 6: Comprehensive Unit Testing & Verification

**Files:**
- Modify: `common/src/test/java/org/aincraft/common/` test suite
- Modify: `utilities-bukkit/src/test/java/org/aincraft/bukkit/adapter/BukkitAdaptersTest.java`
- Modify: `utilities-paper/src/test/java/org/aincraft/paper/adapter/PaperAdaptersTest.java`

- [ ] **Step 1: Add tests for all new abstractions and adapters**
- [ ] **Step 2: Run `./gradlew clean test check generatePomFileForMavenPublication`**
- [ ] **Step 3: Commit**
