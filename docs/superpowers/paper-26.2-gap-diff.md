# Paper API 26.2 Gap Diff: `utilities-api` Live Source vs `context-gap-report.json`

**Paper API**: `io.papermc.paper:paper-api` `26.2.build.119-stable`
**Report**: `docs/superpowers/context-gap-report.json` (8 domains, 122 gap items)
**Method**: every claim verified against `utilities-api/src/main/java/org/aincraft/api/domain/` source files.

Each entry is one of:
- **PRESENT** — full implementation in live API
- **DEFAULT UOE** — declared on existing interface, body is `throw new UnsupportedOperationException()`; needs live adapter implementation
- **ABSENT** — not present anywhere in the live API
- **WRONG PATH** — capability exists but under a different interface than the gap report states
- **ADAPTER GAP** — type exists but adapter conversion is absent

---

## inventory

### PRESENT — gap report false positives

| Gap report claim | Live source | Verdict |
|---|---|---|
| `common.inventory.EntityEquipment` | `api.domain.inventory.EntityEquipment` | PRESENT |
| `common.inventory.ItemFlag` | `api.domain.inventory.ItemFlag` — 8 constants | PRESENT |
| `common.inventory.InventoryView` | `api.domain.inventory.InventoryView` | PRESENT |
| `common.inventory.ItemMeta` | `api.domain.inventory.ItemMeta` | PRESENT |
| `common.inventory.ItemStack.clone()` | `ItemStack.clone()` line 37 | PRESENT |
| `common.inventory.ItemStack.editMeta()` | `ItemStack.editMeta(Consumer<ItemMeta>)` line 42 | PRESENT |
| `common.inventory.Inventory.addItem()` | `Inventory.addItem(ItemStack...)` line 27 | PRESENT |
| `common.inventory.Inventory.removeItem()` | `Inventory.removeItem(ItemStack...)` line 30 | PRESENT |
| `common.inventory.Inventory.contains()` | `Inventory.contains(ItemType/ItemStack)` lines 32–36 | PRESENT |
| `common.inventory.Inventory.first()` / `firstEmpty()` | `Inventory.first(ItemStack)` / `firstEmpty()` lines 38–40 | PRESENT |
| `common.inventory.PlayerInventory.armorContents()` | `PlayerInventory.armorContents()` — returns `Collection<ItemStack>` | PRESENT |
| `common.inventory.EquipmentSlotGroup` | `api.domain.datacomponent.item.EquipmentSlotGroup` | PRESENT — gap report had wrong package (`common.inventory`) |

### DEFAULT UOE — declared but not implemented

| Interface | Method | Verdict |
|---|---|---|
| `ItemStack` | `hasEnchant(Enchantment)` | DEFAULT UOE |
| `ItemStack` | `enchantLevel(Enchantment)` | DEFAULT UOE |
| `ItemStack` | `enchantments()` | DEFAULT UOE |
| `ItemStack` | `addEnchant(Enchantment, int, boolean)` | DEFAULT UOE |
| `ItemStack` | `removeEnchant(Enchantment)` | DEFAULT UOE |

### ABSENT

| Interface | Missing method | Verdict |
|---|---|---|
| `ItemMeta` | `getAttributeModifiers(EquipmentSlot slot)` | ABSENT |
| `ItemMeta` | `setAttributeModifiers(Map<Attribute, Collection<AttributeModifier>>)` | ABSENT |
| `ItemMeta` | `removeAttributeModifier(EquipmentSlot slot)` | ABSENT |

---

## entity

### PRESENT — gap report false positives

| Gap report claim | Live source | Verdict |
|---|---|---|
| `common.entity.Entity.yaw()` / `pitch()` / `setRotation()` | `Entity` lines 45–49 | PRESENT |
| `common.entity.Entity.setVelocity()` | `Entity.setVelocity(Vector3dc)` line 64 | PRESENT |
| `common.entity.Entity.passengers()` / `addPassenger()` / `eject()` | `Entity` lines 71–80 | PRESENT |
| `common.entity.Entity.vehicle()` / `leaveVehicle()` / `isInsideVehicle()` | `Entity` lines 82–87 | PRESENT |
| `common.entity.Entity.isGlowing()` / `setGlowing()` / `isInvulnerable()` / `setInvulnerable()` | `Entity` lines 89–95 | PRESENT |
| `common.entity.Entity.nearbyEntities(double,double,double)` | `Entity.nearbyEntities(...)` line 71 | PRESENT |
| `common.entity.Nameable` | `api.domain.entity.Nameable` | PRESENT |
| `common.entity.LivingEntity.equipment()` | `LivingEntity.equipment()` line 38 | PRESENT |
| `common.entity.LivingEntity.attack()` / `swingMainHand()` / `swingOffHand()` | `LivingEntity` lines 40–44 | PRESENT |
| `common.entity.LivingEntity.potionEffect()` / `addPotionEffect(…, force)` / `clearActivePotionEffects()` | `LivingEntity` lines 47–56 | PRESENT |
| `common.entity.Damageable.absorptionAmount()` / `setAbsorptionAmount()` / `kill()` | `Damageable` lines 13–21 | PRESENT |
| `common.entity.Player.displayName()` / `setAllowFlight()` | `Player` lines 22–24, 63–65 | PRESENT |

### ABSENT

| Interface | Missing method | Verdict |
|---|---|---|
| `Entity` | `height()` / `width()` / `entityId()` | ABSENT |
| `LivingEntity` | `remainingAir()` / `setRemainingAir(int)` | ABSENT |
| `LivingEntity` | `hasAI()` / `setAI(boolean)` | ABSENT |
| `Player` | `bedSpawnLocation()` / `setBedSpawnLocation(Location, boolean)` | ABSENT |
| `HumanEntity` | Interface itself absent — `Player` extends `LivingEntity`, not `HumanEntity`; `enderChest()`, `exhaustion()`, `itemOnCursor()` have no home in the API | ABSENT |

---

## world

### PRESENT — gap report false positives

| Gap report claim | Live source | Verdict |
|---|---|---|
| `common.world.World.time()` / `fullTime()` (read-only) | `World.time()` / `fullTime()` lines 78–80 | PRESENT (read-only) |
| `common.world.World.getHighestBlockAt(int,int)` | `World.getHighestBlockAt(int,int)` line 130 | PRESENT |
| `common.world.World.playSound(...)` | `World` lines 95–105 | PRESENT |
| `common.world.World.spawnParticle(...)` | `World` lines 107–128 | PRESENT |
| `common.world.Block.biome()` / `setBiome(Key)` | `Block` lines 76–78 | PRESENT |
| `common.world.HeightMap` | `api.domain.world.HeightMap` — 6 constants including `OCEAN_FLOOR_WG`, `WORLD_SURFACE_WG` | PRESENT |

### WRONG PATH — capability exists, different interface

| Gap report path | Actual location | Verdict |
|---|---|---|
| `common.world.World.uid()` | Lives on `Server.uid()`, not on `World` | WRONG PATH |
| `common.world.World.world(UUID)` | Lives on `Server.world(UUID)`, not on `World` | WRONG PATH |

### DEFAULT UOE

| Interface | Method | Verdict |
|---|---|---|
| `World` | `getHighestBlockAt(int,int,HeightMap)` | DEFAULT UOE |
| `World` | `spawnEntity(Location, Key)` | DEFAULT UOE |
| `Block` | `setType(BlockType)` (1-arg) | DEFAULT UOE |

### ABSENT

| Interface | Missing method or type | Verdict |
|---|---|---|
| `World` | `FluidCollisionMode` enum — needed by rayTrace signatures | ABSENT |
| `World` | `rayTraceBlocks(Location, Vector3d, double, FluidCollisionMode, boolean)` | ABSENT |
| `World` | `rayTrace(Location, Vector3d, double, FluidCollisionMode, boolean, double)` | ABSENT |
| `World` | `nearbyEntities(Location, double, double, double)` / `nearbyEntities(BoundingBox)` | ABSENT |
| `World` | `entity(UUID)` | ABSENT |
| `World` | `setTime(long)` / `setFullTime(long)` / `isDayTime()` / `gameTime()` | ABSENT |
| `World` | `hasStorm()` / `setStorm()` / `isThundering()` / `setThundering()` / `weatherDuration()` / `setWeatherDuration()` | ABSENT |
| `World` | `spawnLocation()` / `setSpawnLocation(Location)` | ABSENT |
| `World` | `setDifficulty(Difficulty)` | ABSENT |
| `World` | `createExplosion(Location, float, boolean, boolean)` | ABSENT |
| `WorldBorder` | `changeSize(double, long seconds)` | ABSENT |
| `WorldBorder` | `reset()` | ABSENT |
| `WorldBorder` | `world()` | ABSENT |
| `Chunk` | `isGenerated()` | ABSENT |
| `Chunk` | `isForceLoaded()` / `setForceLoaded(boolean)` | ABSENT |
| `Block` | `setType(BlockType, boolean applyPhysics)` | ABSENT |
| `Block` | `setState(BlockState, boolean)` | ABSENT |
| `Block` | `lightLevel()` / `lightFromSky()` / `lightFromBlocks()` | ABSENT |
| `Block` | `blockPower()` / `blockPower(BlockFace)` | ABSENT |
| `Block` | `isPowered()` / `isIndirectlyPowered()` / `isFacePowered(BlockFace)` / `isFaceIndirectlyPowered(BlockFace)` | ABSENT |
| `Block` | `isReplaceable()` / `isCollidable()` / `isBuildable()` / `isBurnable()` / `isSuffocating()` | ABSENT |
| `Block` | `drops()` / `drops(ItemStack)` / `drops(ItemStack, Entity)` | ABSENT |
| `Block` | `breakNaturally()` / `breakNaturally(ItemStack)` | ABSENT |
| `Block` | `canPlace(BlockState)` | ABSENT |
| `Block` | `blockKey()` | ABSENT |
| `Block` | `breakSpeed(Player)` | ABSENT |
| `BlockState` | `copy()` | ABSENT |
| `BlockState` | `merge(BlockState)` | ABSENT |
| `BlockState` | `matches(BlockState)` | ABSENT |
| `BlockState` | `lightEmission()` | ABSENT |
| `BlockState` | `isOccluding()` | ABSENT |
| `BlockState` | `requiresCorrectToolForDrops()` | ABSENT |
| `BlockState` | `isReplaceable()` | ABSENT |
| `BlockState` | `isRandomlyTicked()` | ABSENT |
| `BlockState` | `destroySpeed(ItemStack)` | ABSENT |
| `BlockType` | `translationKey()` | ABSENT |
| `FluidCollisionMode` | enum | ABSENT |
| `TileBlockState` | interface | ABSENT |
| `PistonMoveReaction` | enum | ABSENT |

---

## effect

### PRESENT — gap report false positives

| Gap report claim | Live source | Verdict |
|---|---|---|
| `common.effect.Enchantment.conflictsWith()` / `canEnchant(ItemStack)` | `Enchantment` lines 16–18 | PRESENT |
| `common.effect.PotionEffectType.createEffect()` | `PotionEffectType.createEffect(int,int)` line 17 | PRESENT |
| `common.effect.PotionEffectType.category()` | `PotionEffectType.category()` line 14 | PRESENT |
| `common.effect.PotionEffectTypeCategory` | `api.domain.effect.PotionEffectTypeCategory` — `BENEFICIAL`/`HARMFUL`/`NEUTRAL` | PRESENT |
| `common.effect.PotionEffect.isInfinite()` / `withDuration()` / `withAmplifier()` | `PotionEffect` lines 20–26 | PRESENT |
| `common.effect.Particle.dataType()` | `Particle.dataType()` line 9 | PRESENT |
| `common.world.World.spawnParticle()` | `World` lines 107–128 | PRESENT |
| `common.world.World.playSound()` | `World` lines 95–105 | PRESENT |

### Sound note
The live API uses `net.kyori.adventure.sound.Sound.Type` directly on `World.playSound()`. The gap report proposes a custom `interface Sound extends Keyed`. Whether this is a gap depends on whether a custom wrapper (for platform-neutral casting or extra behavior) is needed vs. using the Kyori type directly. The gap report's intent is a custom wrapper, listed here as ABSENT.

### ABSENT

| Interface / Type | Missing item | Verdict |
|---|---|---|
| `Sound` | Custom `interface Sound extends Keyed` — separate from Kyori `Sound.Type` | ABSENT |
| `Enchantment` | `displayName(int level)` | ABSENT |
| `Enchantment` | `description()` | ABSENT |
| `Enchantment` | `isTradeable()` / `isDiscoverable()` | ABSENT |
| `PotionEffectType` | `effectAttributes()` / `attributeModifierAmount(Attribute, int)` | ABSENT |
| `EntityEffect` | enum | ABSENT |
| `BukkitAdapters` | `adapt(PotionEffect)` / `toBukkit(PotionEffect)` | ADAPTER GAP |
| `BukkitAdapters` | `adapt(Enchantment)` | ADAPTER GAP |
| `BukkitAdapters` | `adapt(Particle)` | ADAPTER GAP |

---

## attribute

### PRESENT — gap report false positives

| Gap report claim | Live source | Verdict |
|---|---|---|
| `common.attribute.AttributeInstance.removeModifier(UUID)` / `getModifier(UUID)` | `AttributeInstance` lines 26–31 | PRESENT |
| `common.attribute.AttributeModifier.key()` / `amount()` / `operation()` / `slot()` | `AttributeModifier` extends `Keyed`; these accessors exist | PRESENT |

### ABSENT

| Interface | Missing method | Verdict |
|---|---|---|
| `Attribute` | Interface itself — no `org.aincraft.api.domain.attribute.Attribute` file exists | ABSENT |
| `Attribute` | `defaultValue()` | ABSENT |
| `Attribute` | `sentiment()` / `Sentiment` enum | ABSENT |
| `AttributeInstance` | `addTransientModifier(AttributeModifier)` | ABSENT |
| `AttributeInstance` | `defaultValue()` | ABSENT |
| `AttributeInstance` | `getModifier(Key)` — only UUID variant exists | ABSENT |
| `AttributeInstance` | `removeModifier(Key)` — only UUID variant exists | ABSENT |
| `AttributeModifier` | `slotGroup()` | ABSENT |
| `Attributable` | `registerAttribute(Attribute)` | ABSENT |
| `Attributes` | Interface (constants holder: `MAX_HEALTH`, `ATTACK_DAMAGE`, etc.) | ABSENT |
| `AttributeRegistry` | Interface (`Key → Attribute` lookup) | ABSENT |
| `AttributeModifierFactory` | Interface (platform-neutral modifier construction) | ABSENT |
| `ItemMeta` | `getAttributeModifiers(EquipmentSlot)` | ABSENT |
| `ItemMeta` | `setAttributeModifiers(Map<Attribute, Collection<AttributeModifier>>)` | ABSENT |
| `ItemMeta` | `removeAttributeModifier(EquipmentSlot)` | ABSENT |
| `MinestomLivingEntityWrapper` | `modifiers()` / `addModifier()` / `removeModifier()` / `getModifier()` — attribute modifier API entirely stubbed | ADAPTER GAP |

---

## server

### PRESENT — gap report false positives

| Gap report claim | Live source | Verdict |
|---|---|---|
| `common.server.Server.world(UUID/Key/String)` | `Server.world(UUID/Key/String)` lines 36–42 | PRESENT |
| `common.server.Server.player(UUID/String)` | `Server.player(UUID/String)` lines 44–48 | PRESENT |
| `common.server.Server.broadcast(Component)` | `Server.broadcast(Component)` line 50 | PRESENT |
| `common.server.OfflinePlayer` | `api.domain.server.OfflinePlayer` — exists in live API; gap report listed this in `newInterfaces` section | PRESENT |

### ABSENT

| Interface | Missing method | Verdict |
|---|---|---|
| `Server` | `minecraftVersion()` — `version()` returns Bukkit impl string | ABSENT |
| `Server` | `dispatchCommand(CommandSender, String)` | ABSENT |
| `Server` | `entity(UUID)` | ABSENT |
| `Server` | `isPrimaryThread()` | ABSENT |
| `Server` | `currentTick()` | ABSENT |
| `Server` | `offlinePlayer(UUID)` — `OfflinePlayer` interface exists but no factory on `Server` | ABSENT |
| `Server` | `offlinePlayer(String)` | ABSENT |
| `Server` | `playerExact(String)` | ABSENT |
| `Server` | `matchPlayers(String)` | ABSENT |
| `Server` | `defaultGameMode()` / `setDefaultGameMode(GameMode)` | ABSENT |
| `Server` | `motd()` / `setMotd(Component)` | ABSENT |
| `Server` | `broadcast(Component, String permission)` — only `broadcast(Component)` without permission scope | ABSENT |
| `Server` | `createInventory(InventoryHolder, int, Component)` | ABSENT |
| `Server` | `createInventory(InventoryHolder, InventoryType)` | ABSENT |
| `Server` | `savePlayers()` | ABSENT |
| `Server` | `onlineMode()` | ABSENT |
| `CommandSender` | `server()` | ABSENT |
| `BukkitCommandSenderWrapper` | `sendMessage(Component)` — uses legacy serializer, not Paper native | ADAPTER GAP |

---

## location

### PRESENT / DEFAULT UOE

| Gap report claim | Live source | Verdict |
|---|---|---|
| `common.world.HeightMap` | `api.domain.world.HeightMap` — used by `World.getHighestBlockAt` and `Location.toHighestLocation` | PRESENT |
| `common.location.BoundingBox` — all mutator/query methods | `BoundingBox` lines 77–114 — all declared with **default UOE** | DEFAULT UOE |
| `common.location.Location.toHighestLocation(HeightMap)` | `Location` line 65 — **default UOE** | DEFAULT UOE |

### ABSENT

| Interface | Missing method | Verdict |
|---|---|---|
| `Location` | `direction()` | ABSENT |
| `Location` | `withOffset(double, double, double)` / `withOffset(Vector3d)` | ABSENT |
| `Location` | `withRotation(float, float)` | ABSENT |
| `Location` | `toBlockLocation()` | ABSENT |
| `Location` | `toCenterLocation()` | ABSENT |
| `Location` | `nearbyEntities(double)` / `nearbyEntities(double, double, double)` | ABSENT |
| `Location` | `nearbyPlayers(double)` | ABSENT |
| `Location` | `nearbyLivingEntities(double, double, double)` | ABSENT |
| `Location` | `isChunkLoaded()` | ABSENT |
| `Location` | `toBlockKey()` | ABSENT |
| `Rotation` | interface (gap report `newInterfaces`) | ABSENT |
| `Vector3d` | interface at `api.domain.location.Vector3d` | ABSENT |
| `BukkitBoundingBoxWrapper` | `intersects()` reimplementing AABB instead of delegating to `org.bukkit.util.BoundingBox.overlaps()` | ADAPTER GAP |
