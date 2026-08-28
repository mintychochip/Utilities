# Paper 26.2 API Parity — Source-Indexed Gap Register

## Scope and interpretation

This is the going-forward gap register for the current repository state.

**Compared:**

- `docs/superpowers/context-gap-report.json` — the original Paper/Bukkit gap proposal
- `utilities-api/src/main/java/org/aincraft/api/domain/` — the current platform-neutral API
- `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/` — Bukkit/Spigot adapters
- `utilities-paper/src/main/java/org/aincraft/paper/adapter/` — Paper adapters
- `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/` — Minestom adapters

**Classification:**

- **API-PRESENT:** the contract/type exists in `utilities-api`; an abstract method is API coverage, not an API gap.
- **API-ABSENT:** the requested contract/type does not exist in `utilities-api`.
- **API-STUB:** the API contract exists but its default body throws.
- **ADAPTER-REAL:** the concrete adapter overrides the method with a platform delegate or a defined conversion.
- **ADAPTER-THROW:** the concrete adapter overrides the method but throws.
- **ADAPTER-PLACEHOLDER:** the concrete adapter returns a constant, empty value, null, or performs a no-op where real platform state is expected.
- **ADAPTER-INHERITED-STUB:** the adapter does not override the method and therefore inherits an API stub or a stubbed base adapter method.
- **PLATFORM-LIMITATION:** the underlying compile surface does not expose the capability; the limitation is intentional and must remain explicit unless a platform-specific API is selected.
- **SHAPE-MISMATCH:** the capability exists, but the current API signature differs from the report proposal.

Line references below point to the current source files that were audited.

---

## Executive conclusion

`utilities-api` is **not full Paper 26.2 API parity**. The original gap report is stale in the opposite direction: many types and methods it called absent are now declared in `utilities-api`, but several methods remain API defaults or have incomplete platform adapters.

### Paper status

Paper resolves the Bukkit/Spigot limitations for:

- `Block.isReplaceable()`, `isCollidable()`, `isBuildable()`, and `isSuffocating()` via `PaperBlockWrapper`
- `Server.minecraftVersion()` and `Server.currentTick()` via `PaperServerWrapper`
- native Paper Adventure messaging/broadcast/MOTD paths where Paper wrappers explicitly override them
- `World` spawn, ray-trace, nearby-entity, weather, time, highest-block, spawn-location, difficulty, and explosion paths through inherited `BukkitWorldWrapper` implementations

Paper is **not complete** because it has no Paper-specific `BlockState` or `BoundingBox` wrapper. Paper therefore inherits these Bukkit paths:

- `BukkitBlockStateWrapper.isReplaceable()`, `isRandomlyTicked()`, and `destroySpeed(ItemStack)` still throw
- `BukkitBoundingBoxWrapper.expand(BlockFace, …)`, `shift`, `union`, `intersection`, and `rayTrace` still fall through to API defaults
- `BukkitEnchantmentWrapper.description()` still throws; `isTradeable()` and `isDiscoverable()` remain API defaults
- `PotionEffectType.effectAttributes()` and `attributeModifierAmount()` remain API defaults
- `Attributable.registerAttribute(Attribute)` remains the API default because no Bukkit living-entity adapter override exists
- `PaperServerWrapper` inherits Bukkit’s legacy-string title conversion for `createInventory(..., Component)`
- `PaperPlayerWrapper` inherits Bukkit’s legacy serializer for player `displayName()` accessors

Bukkit/Spigot and Paper are separate results. A Bukkit limitation is not automatically a Paper limitation; a Paper result must follow the Paper wrapper inheritance chain.

Minestom has the largest remaining implementation surface: no server wrapper, no item/effect/attribute conversion layer, multiple world/block/location/chunk/entity/inventory placeholders, and no particle conversion.

---

# 1. API contracts currently present but stubbed

These methods exist in `utilities-api`; the default body is unsupported. A concrete adapter can override them. The API stub alone does **not** prove every adapter is incomplete.

## 1.1 `World.java`

Source: `utilities-api/src/main/java/org/aincraft/api/domain/world/World.java:91-250`.

| Method | API fallback |
|---|---|
| `spawnEntity(Location, Key)` | `UnsupportedCapabilityException(ENTITY_SPAWN)` at `:91-93` |
| `spawnParticle(Particle, Location, int, double, double, double, double)` | `UnsupportedCapabilityException(PARTICLE)` at `:124-133` |
| `getHighestBlockAt(int, int, HeightMap)` | `UnsupportedCapabilityException(BLOCK_QUERY)` at `:143-145` |
| `rayTraceBlocks(Location, Position, double, FluidCollisionMode, boolean)` | `UnsupportedCapabilityException(RAYTRACE)` at `:154-161` |
| `rayTrace(Location, Position, double, FluidCollisionMode, boolean, double)` | `UnsupportedCapabilityException(RAYTRACE)` at `:163-171` |
| `nearbyEntities(Location, double, double, double)` | `UnsupportedCapabilityException(LOCATION_NEARBY)` at `:173-176` |
| `nearbyEntities(BoundingBox)` | `UnsupportedCapabilityException(LOCATION_NEARBY)` at `:178-180` |
| `entity(UUID)` | `UnsupportedCapabilityException(ENTITY_LOOKUP)` at `:182-184` |
| `hasStorm()` / `setStorm(boolean)` / `isThundering()` / `setThundering(boolean)` / `weatherDuration()` / `setWeatherDuration(int)` | `UnsupportedCapabilityException(WEATHER)` at `:188-210` |
| `setTime(long)` / `setFullTime(long)` / `isDayTime()` / `gameTime()` | `UnsupportedCapabilityException(TIME_SET)` at `:214-228` |
| `spawnLocation()` / `setSpawnLocation(Location)` / `setDifficulty(Difficulty)` | `UnsupportedCapabilityException(WORLD_CONFIGURATION)` at `:232-242` |
| `createExplosion(Location, float, boolean, boolean)` | `UnsupportedCapabilityException(EXPLOSION)` at `:246-249` |

The overloads `getHighestBlockAt(int,int)`, `getHighestBlockAt(Location)`, and `getHighestBlockAt(Location,HeightMap)` are real delegation defaults at `:135-149`; they reach the height-map fallback unless an adapter overrides the terminal method.

## 1.2 `Block.java`

Source: `utilities-api/src/main/java/org/aincraft/api/domain/world/Block.java:41-188`.

| Method | API fallback |
|---|---|
| `setType(BlockType)` | `UnsupportedOperationException` at `:41-43` |
| `setState(BlockState)` | `UnsupportedOperationException` at `:53-55` |
| `isReplaceable()` / `isCollidable()` / `isBuildable()` / `isBurnable()` / `isSuffocating()` | `UnsupportedOperationException` at `:111-129` |
| `isPowered()` / `isIndirectlyPowered()` / `isFacePowered(BlockFace)` / `isFaceIndirectlyPowered(BlockFace)` | `UnsupportedOperationException` at `:133-147` |
| `blockPower()` / `blockPower(BlockFace)` | `UnsupportedOperationException` at `:149-155` |
| `breakNaturally()` / `breakNaturally(ItemStack)` | `UnsupportedOperationException` at `:171-177` |
| `canPlace(BlockState)` | `UnsupportedOperationException` at `:179-181` |
| `blockKey()` | `UnsupportedOperationException` at `:183-185` |
| `breakSpeed(Player)` | `UnsupportedOperationException` at `:187-189` |

`setType(Key)`, `setType(BlockType,boolean)`, and `setState(BlockState,boolean)` are delegation defaults at `:45-59`; they are only as capable as their terminal methods.

## 1.3 `BlockState.java`

Source: `utilities-api/src/main/java/org/aincraft/api/domain/block/BlockState.java:17-60`.

| Method | API fallback |
|---|---|
| `copy()` / `merge(BlockState)` / `matches(BlockState)` | `UnsupportedCapabilityException(BLOCK_QUERY)` at `:17-29` |
| `lightEmission()` / `isOccluding()` / `requiresCorrectToolForDrops()` | `UnsupportedCapabilityException(BLOCK_QUERY)` at `:31-41` |
| `isReplaceable()` / `isRandomlyTicked()` / `destroySpeed(ItemStack)` | `UnsupportedCapabilityException(BLOCK_QUERY)` at `:43-53` |
| `isFaceSturdy(BlockFace, BlockSupport)` | `UnsupportedCapabilityException(BLOCK_SUPPORT)` at `:55-57` |
| `pistonMoveReaction()` | `UnsupportedCapabilityException(PISTON_REACTION)` at `:59-61` |

There are **11** default methods here, including `isFaceSturdy`; it must not be omitted from the register.

## 1.4 `Location.java`

Source: `utilities-api/src/main/java/org/aincraft/api/domain/location/Location.java`.

| Method/path | Fallback |
|---|---|
| `toHighestLocation()` / `toHighestLocation(HeightMap)` | API defaults throw `UnsupportedOperationException` at `:113-119` |
| `Location.of(...).nearbyEntities(double)` | Factory implementation throws at `:230-233` |
| `Location.of(...).nearbyEntities(double,double,double)` | Factory implementation throws at `:235-239` |
| `Location.of(...).nearbyPlayers(double)` | Factory implementation throws at `:241-244` |
| `Location.of(...).nearbyPlayers(double,double,double)` | Factory implementation throws at `:246-250` |
| `Location.of(...).nearbyLivingEntities(double,double,double)` | Factory implementation throws at `:252-256` |

`direction()`, both `withOffset` overloads, `withRotation`, `toBlockLocation`, `toCenterLocation`, `isChunkLoaded`, and `toBlockKey` are API contracts at `:69-111`; the static factory implements those paths, except for the five nearby-query methods above.

## 1.5 `BoundingBox.java`

Source: `utilities-api/src/main/java/org/aincraft/api/domain/location/BoundingBox.java:77-113`.

| Method | API fallback |
|---|---|
| `expand(negativeX,negativeY,negativeZ,positiveX,positiveY,positiveZ)` | `UnsupportedOperationException("expand")` at `:77-85` |
| `expand(BlockFace,double)` | `UnsupportedOperationException` at `:91-93` |
| `shift(double,double,double)` | `UnsupportedOperationException` at `:95-97` |
| `union(BoundingBox)` | `UnsupportedOperationException` at `:103-105` |
| `intersection(BoundingBox)` | `UnsupportedOperationException` at `:107-109` |
| `rayTrace(Vector3dc,Vector3dc,double)` | `UnsupportedOperationException` at `:111-114` |

The three-axis `expand(double,double,double)` and vector `shift(Vector3dc)` overloads delegate to the unsupported terminal methods at `:87-100`.

## 1.6 Inventory, attribute, effect, entity, and server defaults

| Source | API-stubbed methods |
|---|---|
| `ItemStack.java:53-75` | `hasEnchant`, `enchantLevel`, `enchantments`, `addEnchant(Enchantment,int,boolean)`, `removeEnchant` throw `UnsupportedOperationException`. The two-argument `addEnchant` at `:70-72` delegates and is not an independent stub. |
| `ItemMeta.java:67-79` | `getAttributeModifiers(EquipmentSlot)`, `setAttributeModifiers(Map<Key,Collection<AttributeModifier>>)`, `removeAttributeModifier(EquipmentSlot)` throw `UnsupportedCapabilityException(ATTRIBUTE_MODIFIER)`. |
| `AttributeInstance.java:29-40` | `addTransientModifier(AttributeModifier)` and `defaultValue()` throw `UnsupportedCapabilityException(ATTRIBUTE_MODIFIER)`. Key-based remove/get at `:47-61` are real lookup defaults. |
| `Attributable.java:19-23` | `registerAttribute(Attribute)` throws `UnsupportedCapabilityException(ATTRIBUTE_MODIFIER)`. `getAttributeValue` and `setAttributeBaseValue` at `:25-35` are real conditional defaults. |
| `Enchantment.java:24-37` | `displayName(int)`, `description()`, `isTradeable()`, `isDiscoverable()` throw `UnsupportedCapabilityException(ENCHANTMENT_METADATA)`. |
| `PotionEffectType.java:26-32` | `effectAttributes()` and `attributeModifierAmount(Attribute,int)` throw `UnsupportedCapabilityException(POTION_EFFECT_ATTRIBUTES)`. |
| `LivingEntity.java:64-77` | `remainingAir`, `setRemainingAir`, `hasAI`, `setAI` throw capability exceptions. |
| `Server.java:68-140` | `minecraftVersion`, `dispatchCommand`, `entity(UUID)`, `isPrimaryThread`, `currentTick`, both `offlinePlayer` overloads, `playerExact`, `matchPlayers`, `defaultGameMode`, `setDefaultGameMode`, `motd`, `motd(Component)`, permission-scoped `broadcast`, both current `createInventory` overloads, `savePlayers`, and `onlineMode` throw capability exceptions. |
| `CommandSender.java:21-23` | `server()` throws `UnsupportedCapabilityException(SERVER_INFO)`. |
| `BlockType.java:12-14` | `translationKey()` throws `UnsupportedCapabilityException(BLOCK_QUERY)`. |

---

# 2. API-ABSENT contracts and shape mismatches

These are the remaining report proposals that are not represented by the current `utilities-api` contract. They are separate from adapter stubs.

## 2.1 Inventory/API integration absent

- `DamageableItemMeta` type is absent. The report proposed `damage()`, `setDamage(int)`, `maxDamage()`, and `hasDamage()` extending `ItemMeta`.
- `ItemFactory` type is absent. The report proposed item-meta creation, item-stack creation, and metadata equality.
- `Server.itemFactory()` is absent.
- `Server.createInventory(InventoryHolder, int)` is absent. Current `Server` has only `createInventory(holder, int, Component)` and `createInventory(holder, InventoryType)` at `utilities-api/.../Server.java:124-132`.
- `Player.openInventory()`, `Player.openInventory(Inventory)`, and `Player.closeInventory()` are absent. `InventoryView` exists, but `Player` has no open/close inventory bridge.
- `ItemMeta.customName()`, `customName(Component)`, `hasCustomName()`, `itemName()`, `itemName(Component)`, and `hasItemName()` are absent. Current `ItemMeta` only exposes `displayName`/lore at `utilities-api/.../ItemMeta.java:19-31`.
- Paper’s stack-level data-component operations are absent from `ItemStack`: `getData`, `setData`, `hasData`, `dataTypes`/equivalent, and `resetData`. Current generic component operations are on `ItemMeta` at `utilities-api/.../ItemMeta.java:82-121`, not on `ItemStack`.

## 2.2 World/block/effect API integration absent

- `World.spawn(Location, Class<T>)` is absent. Current world spawning contract is `spawnEntity(Location, Key)` at `World.java:91-93`.
- The typed particle-data overload `<T> spawnParticle(..., T data)` is absent. Current `World` has only the no-data overload chain at `World.java:110-133`.
- The report’s `playSound(Location, Sound, SoundCategory, ...)` shape is absent. Current `World.playSound` uses Kyori `net.kyori.adventure.sound.Sound.Type` and `Sound.Source` at `World.java:98-108`. The custom `api.domain.effect.Sound` marker exists but is not the argument type, and there is no custom `SoundCategory` type.
- `Player.sendEntityEffect(EntityEffect, Entity)` or an equivalent status-effect producer is absent. `EntityEffect` exists, but no current entity/player API consumes it.
- `Block.tileState()` is absent. `TileBlockState` exists, but `Block` has no accessor connecting a world block to tile state.
- `VoxelShape` exists, but `Block` has no `collisionShape()`/equivalent contract that produces one. The type is currently disconnected from the block API.
- The report’s `Biome` type/signatures are not present. Current `Block` uses `Key biome()` / `setBiome(Key)` at `Block.java:93-96`; no platform-neutral `Biome.java` exists in the API inventory.

## 2.3 Attribute shape mismatches

- `EquipmentSlotGroup` exists at `utilities-api/src/main/java/org/aincraft/api/domain/datacomponent/item/EquipmentSlotGroup.java`, not under the report’s proposed `common.inventory` package.
- `Attributes` exists, but its shape is `Attribute get(Key)` plus static `Key` constants at `utilities-api/.../attribute/Attributes.java:16-60`; it is not the report’s proposed set of `Attribute maxHealth()`, `attackDamage()`, etc. methods.
- `AttributeModifierFactory` exists with two overloads accepting a `Key`, amount, operation, and either `EquipmentSlot` or `EquipmentSlotGroup` at `utilities-api/.../attribute/AttributeModifierFactory.java:27-48`. The report’s additional no-slot and UUID/name overloads are absent.

## 2.4 Location/vector decisions

- No custom `Vector3d` interface is required by the current design. `Location.direction()` and world ray-trace direction parameters use JOML `Vector3dc`; `Position` is the platform-neutral x/y/z contract.
- `Position` is **not** assignable to `Vector3dc`: `Position` declares only `x()`, `y()`, and `z()` at `utilities-api/.../location/Position.java:5-11` and does not extend JOML. Any platform API requiring `Vector3dc` still needs conversion.
- The report’s separate `Vector3i` type and integer arithmetic methods are not in the current API. They are an unadopted report proposal, not a defect in the current `Position`/JOML design.

---

# 3. Bukkit/Spigot adapter gaps

## 3.1 Block predicates

`BukkitBlockWrapper` explicitly throws because these predicates are absent from the Spigot compile surface:

| Method | Source evidence |
|---|---|
| `isReplaceable()` | `utilities-bukkit/.../BukkitBlockWrapper.java:158-162` — `UnsupportedCapabilityException(BLOCK_QUERY)` |
| `isCollidable()` | `:165-169` — `UnsupportedCapabilityException(BLOCK_QUERY)` |
| `isBuildable()` | `:172-176` — `UnsupportedCapabilityException(BLOCK_QUERY)` |
| `isSuffocating()` | `:184-188` — `UnsupportedCapabilityException(BLOCK_QUERY)` |

The same wrapper implements `setType` at `:78-85`, both `setState` forms at `:88-95`, burnability at `:179-181`, all power queries at `:191-218`, both `breakNaturally` forms at `:246-255`, `canPlace` at `:258-260`, `blockKey` at `:263-267`, and `breakSpeed` at `:271-273`. Those are not gaps.

`PaperBlockWrapper` resolves all four predicate gaps with native calls at `utilities-paper/.../PaperBlockWrapper.java:39-62`.

## 3.2 Block-state metadata

`BukkitBlockStateWrapper` implements copy/merge/match, light emission, occlusion, correct-tool metadata, sturdy-face checks, and piston reaction at `utilities-bukkit/.../BukkitBlockStateWrapper.java:41-68` and `:89-97`.

Three methods remain explicit adapter gaps:

| Method | Source evidence |
|---|---|
| `isReplaceable()` | `:71-74` — `UnsupportedCapabilityException(BLOCK_QUERY)` |
| `isRandomlyTicked()` | `:77-80` — `UnsupportedCapabilityException(BLOCK_QUERY)` |
| `destroySpeed(ItemStack)` | `:83-86` — `UnsupportedCapabilityException(BLOCK_QUERY)` |

There is no `PaperBlockStateWrapper`; Paper inherits this Bukkit behavior.

## 3.3 Bounding-box geometry

`BukkitBoundingBoxWrapper` implements `contains`, `intersects`, and six-sided `expand` at `utilities-bukkit/.../BukkitBoundingBoxWrapper.java:51-76`.

It does not override:

- `expand(BlockFace, double)`
- `shift(double,double,double)`
- `union(BoundingBox)`
- `intersection(BoundingBox)`
- `rayTrace(Vector3dc,Vector3dc,double)`

Those methods inherit `UnsupportedOperationException` from `BoundingBox.java:91-113`. There is no `PaperBoundingBoxWrapper`; Paper inherits these gaps. `intersects` is reimplemented with Java AABB comparisons rather than delegating to Bukkit `BoundingBox.overlaps`; that is a low-priority divergence risk, not an absent method.

## 3.4 World border

`BukkitWorldBorderWrapper.changeSize(double,long)` is real at `utilities-bukkit/.../BukkitWorldBorderWrapper.java:82-86`.

`BukkitWorldBorderWrapper.reset()` is the remaining Spigot border gap at `:88-97`: it throws `UnsupportedCapabilityException(WORLD_BORDER_ANIMATE)` because Spigot has no reset method.

`PaperWorldBorderWrapper.reset()` is native at `utilities-paper/.../PaperWorldBorderWrapper.java:23-26`. Paper inherits the real Bukkit `changeSize` implementation, so both are available on Paper.

## 3.5 Server methods

`BukkitServerWrapper` implements the report’s server additions except the two Paper-only methods:

| Method | Source evidence |
|---|---|
| `minecraftVersion()` | `utilities-bukkit/.../BukkitServerWrapper.java:134-138` — throws `UnsupportedCapabilityException(SERVER_INFO)` |
| `currentTick()` | `:158-162` — throws `UnsupportedCapabilityException(SERVER_TICK)` |

The same class implements `dispatchCommand` (`:141-144`), `entity(UUID)` (`:147-150`), `isPrimaryThread` (`:152-155`), both offline-player methods (`:164-172`), exact/partial player lookup (`:174-183`), default game mode (`:185-193`), MOTD (`:195-203`), permission broadcast (`:112-116`), both current inventory factories (`:205-223`), `savePlayers` (`:225-228`), and `onlineMode` (`:230-233`).

`PaperServerWrapper` extends `BukkitServerWrapper` at `utilities-paper/.../PaperServerWrapper.java:10` and supplies native `minecraftVersion()` at `:26-29` and `currentTick()` at `:31-34`.

## 3.6 Attributes

- `BukkitAttributeInstanceWrapper.defaultValue()` is real at `utilities-bukkit/.../BukkitAttributeInstanceWrapper.java:45-48`.
- `addTransientModifier` is attempted reflectively at `:60-74`; it throws `UnsupportedCapabilityException(ATTRIBUTE_MODIFIER)` at `:68-70` when the underlying Spigot runtime has no method. This is a conditional Spigot limitation, not an API absence.
- UUID modifier removal/lookup is real at `:76-99`.
- No Bukkit living-entity wrapper overrides `Attributable.registerAttribute(Attribute)`, so that call inherits the API `UnsupportedCapabilityException` at `utilities-api/.../Attributable.java:19-23`.
- `BukkitAttributeModifierWrapper` supplies slot and slot-group conversion at `utilities-bukkit/.../BukkitAttributeModifierWrapper.java:62-80`; null means the platform modifier has no corresponding single slot/group.
- No `BukkitAttributeRegistry.java` exists. No concrete Bukkit `AttributeRegistry` service is exposed.
- No concrete Bukkit `AttributeModifierFactory` adapter is exposed; the API factory interface exists but has no factory implementation in the adapter inventory.

## 3.7 Effects and potion metadata

- `BukkitEnchantmentWrapper.displayName(int)` is real at `utilities-bukkit/.../BukkitEnchantmentWrapper.java:58-61`.
- `BukkitEnchantmentWrapper.description()` explicitly throws `UnsupportedCapabilityException(ENCHANTMENT_METADATA)` at `:63-67`.
- `isTradeable()` and `isDiscoverable()` are not overridden and therefore inherit the API metadata stub.
- `BukkitPotionEffectTypeWrapper` implements `key`, `name`, `isInstant`, `category`, and `createEffect` at `utilities-bukkit/.../BukkitPotionEffectTypeWrapper.java:21-48`.
- It does not override `effectAttributes()` or `attributeModifierAmount(...)`; those calls inherit `UnsupportedCapabilityException(POTION_EFFECT_ATTRIBUTES)` from `utilities-api/.../PotionEffectType.java:26-32`.
- `BukkitPotionEffectWrapper` implements all current `PotionEffect` methods at `utilities-bukkit/.../BukkitPotionEffectWrapper.java:19-76`.
- `BukkitParticleWrapper` implements `asString`, namespace/value, and `dataType` at `utilities-bukkit/.../BukkitParticleWrapper.java:19-35`.
- `BukkitAdapters` has real PotionEffect, Enchantment, and Particle conversions at `utilities-bukkit/.../BukkitAdapters.java:405-499`.

## 3.8 Item meta and data-component limitations

`BukkitItemMetaWrapper` implements current attribute-slot methods at `utilities-bukkit/.../BukkitItemMetaWrapper.java:184-272`; the API-level ItemMeta slot defaults are therefore not Bukkit gaps.

Generic persistent-data support is real for recognized Java types, but these branches throw `UnsupportedOperationException`:

- `hasData` unknown type: `:290-298`, throw at `:295-296`
- `getData` unknown type: `:307-320`, throw at `:317-318`
- `setData` unknown type: `:328-346`, throw at `:343-344`

`resolveDataType` maps Boolean to `PersistentDataType.BYTE` at `utilities-bukkit/.../BukkitItemMetaWrapper.java:274-287`. `hasData` uses that helper, while `getData`/`setData` explicitly decode/encode Boolean at `:308-314` and `:328-340`. The unsupported branches at `:294-296`, `:316-318`, and `:342-344` apply to unrecognized Java value classes; Boolean is supported.

No typed Paper data-component wrappers exist for the large `api.domain.datacomponent` model; the generic Bukkit PDC bridge is not equivalent to Paper’s typed `ItemStack` component API.

## 3.9 Equipment-slot compatibility

`EquipmentSlot` already includes `BODY` and `SADDLE` at `utilities-api/.../inventory/EquipmentSlot.java:5-13`.

Bukkit/Spigot adapters have conditional unsupported paths:

- `BukkitEntityEquipmentWrapper`: reflective BODY/SADDLE conversion can throw `UnsupportedOperationException` at `utilities-bukkit/.../BukkitEntityEquipmentWrapper.java:140-146`.
- `BukkitPlayerInventoryWrapper`: BODY/SADDLE get/set paths throw at `utilities-bukkit/.../BukkitPlayerInventoryWrapper.java:135-162`.
- `BukkitEquipmentSlotGroupWrapper` returns null for slots that cannot map to the platform group (`:51`, `:65`) and has a defensive empty-string branch for a missing delegate name (`:37-39`). These are platform mapping limitations, not absent API types.

## 3.10 Legacy Adventure serialization

`BukkitCommandSenderWrapper.server()` is real at `utilities-bukkit/.../BukkitCommandSenderWrapper.java:44-47`.

`BukkitCommandSenderWrapper.sendMessage(Component)` serializes to legacy section text at `:49-52`. This is a Spigot compatibility path, not a Paper gap. `PaperCommandSenderWrapper` and `PaperConsoleCommandSenderWrapper` use native `sendMessage(Component)` at `utilities-paper/.../PaperCommandSenderWrapper.java:7-16` and `PaperConsoleCommandSenderWrapper.java:7-16`.

Other Bukkit wrappers also use legacy serialization for display names, lore, MOTD, inventory titles, and player messaging. Paper only fixes the paths it explicitly overrides:

- `PaperServerWrapper` overrides native broadcast, MOTD, and sender messaging at `utilities-paper/.../PaperServerWrapper.java:16-60`.
- `PaperServerWrapper` does **not** override `createInventory`; it inherits `BukkitServerWrapper.createInventory` and its legacy title serialization at `utilities-bukkit/.../BukkitServerWrapper.java:205-223`.
- `PaperPlayerWrapper` does **not** override `displayName()`/`displayName(Component)`; it inherits Bukkit’s legacy conversion from `utilities-bukkit/.../BukkitPlayerWrapper.java:34-45`.

---

# 4. Paper adapter inheritance matrix

## 4.1 Wrapper relationships

- `PaperWorldWrapper extends BukkitWorldWrapper` — `utilities-paper/.../PaperWorldWrapper.java:9`
- `PaperBlockWrapper extends BukkitBlockWrapper` — `utilities-paper/.../PaperBlockWrapper.java:12`
- `PaperServerWrapper extends BukkitServerWrapper` — `utilities-paper/.../PaperServerWrapper.java:10`
- `PaperPlayerWrapper extends BukkitPlayerWrapper` — `utilities-paper/.../PaperPlayerWrapper.java:8`
- `PaperInventoryViewWrapper extends BukkitInventoryViewWrapper`
- `PaperCommandSenderWrapper extends BukkitCommandSenderWrapper`
- `PaperConsoleCommandSenderWrapper extends BukkitConsoleCommandSenderWrapper`
- `PaperWorldBorderWrapper extends BukkitWorldBorderWrapper`

The Paper adapter directory contains exactly these nine adapter files. There is no `PaperLocationWrapper`, `PaperBlockStateWrapper`, or `PaperBoundingBoxWrapper`.

## 4.2 Paper-real paths

- `PaperWorldWrapper` adds native Adventure audience methods and Paper-specific block/world-border adaptation at `utilities-paper/.../PaperWorldWrapper.java:15-48`; all inherited Bukkit world methods are real delegates.
- `PaperBlockWrapper` resolves the four Spigot block predicates at `:39-62`.
- `PaperServerWrapper` resolves `minecraftVersion` and `currentTick` at `:26-34`, and native broadcast/MOTD/sender/entity paths at `:16-60`.
- `PaperWorldBorderWrapper.reset()` is native at `utilities-paper/.../PaperWorldBorderWrapper.java:23-26`.

## 4.3 Paper-inherited gaps

Because no Paper-specific wrappers exist for these types, Paper still uses the Bukkit implementations:

- `BlockState.isReplaceable`, `isRandomlyTicked`, `destroySpeed(ItemStack)` — Bukkit explicit throws.
- `BoundingBox.expand(BlockFace,double)`, `shift`, `union`, `intersection`, `rayTrace` — Bukkit wrapper does not override them.
- `Enchantment.description()` — Bukkit explicit throw.
- `Enchantment.isTradeable()` and `isDiscoverable()` — API defaults.
- `PotionEffectType.effectAttributes()` and `attributeModifierAmount(...)` — API defaults.
- `Attributable.registerAttribute(...)` — API default.
- `Server.createInventory(..., Component)` — inherited legacy-string implementation rather than a Paper-native title call.
- Player `displayName` accessors — inherited legacy-string conversion.

Therefore Paper is complete only for the explicitly checked World, Block-predicate, Server, WorldBorder-reset, and native messaging paths—not for the entire API surface.

---

# 5. Minestom adapter gaps

## 5.1 `MinestomWorldWrapper`

Source: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomWorldWrapper.java`.

### Explicit throws

- `spawnParticle(Particle,Location,int,double,double,double,double)` throws `UnsupportedOperationException` at `:188-197`.
- `getHighestBlockAt(int,int,HeightMap)` throws `UnsupportedOperationException` at `:199-202`.

The no-height-map highest-block overloads delegate to that terminal method through the API defaults. The `Location` height-map overload also reaches it.

### API defaults inherited without overrides

These methods are not overridden by `MinestomWorldWrapper` and therefore retain the `World.java` capability fallbacks:

- `rayTraceBlocks(...)`
- `rayTrace(...)`
- `nearbyEntities(Location,double,double,double)`
- `nearbyEntities(BoundingBox)`
- `entity(UUID)`
- `hasStorm`, `setStorm`, `isThundering`, `setThundering`, `weatherDuration`, `setWeatherDuration`
- `setTime`, `setFullTime`, `isDayTime`, `gameTime`
- `spawnLocation`, `setSpawnLocation`, `setDifficulty`
- `createExplosion`

### Placeholder/semantic gaps

- `minHeight()` returns hard-coded `-64` at `:84-87`.
- `maxHeight()` returns hard-coded `320` at `:89-92`.
- `environment()` always returns `Environment.NORMAL` at `:99-102`.
- `difficulty()` always returns `Difficulty.NORMAL` at `:104-107`.
- `time()` and `fullTime()` always return `0L` at `:109-117`.
- `spawnEntity` falls back to a ZOMBIE when the requested Minestom entity type cannot be resolved at `:130-137`; that silently changes the requested type.
- `spawnEntity` adapts the spawned object through the generic `MinestomAdapters.adapt(Entity)` path, which produces a base `MinestomEntityWrapper`, not a specialized living/player wrapper.

Sound, Adventure audience forwarding, player/entity/chunk enumeration, block/chunk lookup, and world-border construction are real delegates at `:49-187`.

## 5.2 `MinestomBlockWrapper`

Source: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomBlockWrapper.java`.

### Real paths

Coordinates, world/location/position/chunk, type, `setType(BlockType)`, state retrieval, air/liquid/solid/passable predicates, and `lightFromBlocks()` are implemented at `:42-120` and `:142-159`.

### API defaults inherited as gaps

`setType(BlockType,boolean)` is a real delegation through the implemented one-argument setter. The following remain unsupported because no override exists:

- `setState(BlockState)`
- `setState(BlockState,boolean)` (delegates to the unsupported one-argument method)
- `isReplaceable()`
- `isCollidable()`
- `isBuildable()`
- `isBurnable()`
- `isSuffocating()`
- `isPowered()`
- `isIndirectlyPowered()`
- `isFacePowered(BlockFace)`
- `isFaceIndirectlyPowered(BlockFace)`
- `blockPower()`
- `blockPower(BlockFace)`
- `breakNaturally()`
- `breakNaturally(ItemStack)`
- `canPlace(BlockState)`
- `blockKey()`
- `breakSpeed(Player)`

These fall through to the `Block.java` defaults at `utilities-api/.../world/Block.java:53-188`. `setType(BlockType)` is **not** in this list; it is implemented at `:82-86`.

### Explicit capability throws

- `biome()` at `:123-128`
- `setBiome(Key)` at `:130-135`
- `lightLevel()` at `:142-147`
- `lightFromSky()` at `:149-154`
- `drops()` at `:163-168`
- `drops(ItemStack)` at `:170-176`
- `drops(ItemStack,Entity)` at `:178-185`

All use `UnsupportedCapabilityException(BLOCK_QUERY)`.

### Semantic placeholder

`boundingBox()` returns a newly adapted fixed `1.0 × 1.0 × 1.0` box at `:118-121`, rather than the block’s actual collision shape. `isPassable()` is derived as `!solid` at `:113-116`, which is an approximation rather than a full collision/passability model.

## 5.3 `MinestomBlockStateWrapper`

Source: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomBlockStateWrapper.java:20-45`.

Only `type()` and `asString()` plus equality/hash/string methods are implemented. The following 11 API methods are not overridden and inherit capability throws:

- `copy()`
- `merge(BlockState)`
- `matches(BlockState)`
- `lightEmission()`
- `isOccluding()`
- `requiresCorrectToolForDrops()`
- `isReplaceable()`
- `isRandomlyTicked()`
- `destroySpeed(ItemStack)`
- `isFaceSturdy(BlockFace,BlockSupport)`
- `pistonMoveReaction()`

The wrapper stores only a type and state string at `:11-18`; it has no property-level bridge.

## 5.4 `MinestomLocationWrapper`

Source: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomLocationWrapper.java`.

Coordinate, rotation, direction, offset, block/center transforms, block key, and highest-location methods are real at `:28-111` and `:162-181`.

### Unsupported nearby/chunk methods

- `nearbyEntities(double,double,double)` throws at `:122-128`.
- `nearbyPlayers(double)` throws at `:130-136`.
- `nearbyPlayers(double,double,double)` throws at `:138-144`.
- `nearbyLivingEntities(double,double,double)` throws at `:146-152`.
- `isChunkLoaded()` throws at `:154-159`.
- `nearbyEntities(double)` delegates to the unsupported three-radius method at `:116-120`.

The semantic capability for the nearby-query methods is `LOCATION_NEARBY`. The current source messages pass `Capability.BLOCK_QUERY` in these throw bodies; that is a capability-label defect to correct while retaining the unsupported behavior. `isChunkLoaded()` is a separate location/chunk capability and is also currently unsupported.

## 5.5 `MinestomBoundingBoxWrapper`

Source: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomBoundingBoxWrapper.java:17-60`.

Bounds, `contains`, and `intersects` are implemented. None of the six geometry methods are overridden:

- `expand(6-arg)`
- `expand(BlockFace,double)`
- `shift(double,double,double)`
- `union(BoundingBox)`
- `intersection(BoundingBox)`
- `rayTrace(Vector3dc,Vector3dc,double)`

All inherit `UnsupportedOperationException` from `BoundingBox.java:77-114`.

## 5.6 `MinestomChunkWrapper`

Source: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomChunkWrapper.java`.

- `load()` and `load(boolean)` only return `chunk.isLoaded()` at `:50-58`; they do not initiate loading.
- `unload()` and `unload(boolean)` always return `false` at `:60-68`.
- `isGenerated()` throws `UnsupportedCapabilityException(BLOCK_QUERY)` at `:72-77`.
- `isForceLoaded()` throws the same at `:79-84`.
- `setForceLoaded(boolean)` throws the same at `:86-91`.
- Coordinate/block/entity enumeration and identity methods are real at `:24-57` and `:93-117`.

## 5.7 `MinestomWorldBorderWrapper`

Source: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomWorldBorderWrapper.java`.

Real size, center, warning, world, and containment paths are implemented at `:22-40` and `:59-100`.

Remaining gaps/placeholders:

- `damageBuffer()` returns `0` and `setDamageBuffer` is a no-op at `:43-49`.
- `damageAmount()` returns `0` and `setDamageAmount` is a no-op at `:51-57`.
- `changeSize(double,long)` throws `UnsupportedCapabilityException(WORLD_BORDER_ANIMATE)` at `:79-84`.
- `reset()` throws the same capability at `:87-91`.

## 5.8 `MinestomEntityWrapper`

Source: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomEntityWrapper.java`.

Identity, world/location/position/type, dimensions, entity id, validity/death, bounding box, velocity read, ground state, teleport, removal, rotation reads, and identity methods are real at `:30-141`.

### Explicit unsupported methods

- `setRotation(float,float)` — `:143-146`
- `setVelocity(Vector3dc)` — `:148-151`
- `nearbyEntities(double,double,double)` — `:153-157`
- `passengers()` — `:159-162`
- `addPassenger(Entity)` — `:164-167`
- `removePassenger(Entity)` — `:169-172`
- `eject()` — `:174-177`
- `leaveVehicle()` — `:184-187`
- `setGlowing(boolean)` — `:199-202`
- `setInvulnerable(boolean)` — `:209-212`
- `setCustomNameVisible(boolean)` — `:219-222`
- `customName(Component)` — `:229-233`

### False/nullable placeholders

- `isInsideVehicle()` always returns `false` at `:179-182`.
- `vehicle()` always returns null at `:189-192`.
- `isGlowing()` always returns `false` at `:194-197`.
- `isInvulnerable()` always returns `false` at `:204-207`.
- `isCustomNameVisible()` always returns `false` at `:214-217`.
- `customName()` always returns null at `:224-227`.

## 5.9 `MinestomLivingEntityWrapper`

Source: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomLivingEntityWrapper.java`.

Health, set health, maximum health, eye height/location, and base/value attribute reads are real at `:35-48`, `:113-122`, and `:71-90`.

### Explicit unsupported methods

- `damage(double)` / `damage(double,Entity)` — `:50-58`
- `setInvisible(boolean)` — `:173-176`
- `equipment()` — `:178-181`
- `attack(Entity)` — `:183-186`
- `swingMainHand()` — `:188-191`
- `swingOffHand()` — `:193-196`
- `clearActivePotionEffects()` — `:204-207`
- `setAbsorptionAmount(double)` — `:209-217`
- `kill()` — `:219-222`
- `addPotionEffect(PotionEffect,boolean)` — `:224-228`

### Constant/no-op effect and state placeholders

- `hasLineOfSight` always returns `true` at `:124-127`.
- `target()` returns null and `setTarget` is a no-op at `:129-136`.
- `isGliding`, `isSwimming`, and `isSleeping` always return false at `:137-150`.
- `activePotionEffects()` returns an empty collection at `:152-155`.
- `addPotionEffect(PotionEffect)` and `removePotionEffect(PotionEffectType)` are no-ops at `:157-162`.
- `hasPotionEffect` always returns false at `:163-166`.
- `isInvisible()` always returns false at `:168-171`.
- `potionEffect(PotionEffectType)` always returns null at `:198-202`.
- `absorptionAmount()` always returns `0` at `:209-212`.

### Attribute modifier stubs

The anonymous `AttributeInstance` created at `:71-110` delegates base/value operations but:

- `modifiers()` returns `Collections.emptyList()` at `:92-95`.
- `addModifier` is a no-op at `:97-99`.
- `removeModifier(AttributeModifier)` is a no-op at `:100-102`.
- `removeModifier(UUID)` is a no-op at `:103-105`.
- `getModifier(UUID)` always returns null at `:106-109`.
- `addTransientModifier` is not overridden and therefore inherits the API capability throw from `AttributeInstance.java:29-33`.

The wrapper also inherits `remainingAir`, `setRemainingAir`, `hasAI`, `setAI` capability throws from `LivingEntity.java:64-77`, and `registerAttribute` from `Attributable.java:19-23`.

## 5.10 `MinestomPlayerWrapper`

Source: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomPlayerWrapper.java`.

Username, online state, ping, food/saturation, level/experience, game mode, sneaking/sprinting/flying, permission level, kick, native messaging/title/sound, inventory construction, and ordinary respawn-point reads/writes are real at `:39-205` and `:257-282`.

Remaining gaps/placeholders:

- `allowFlight()` always returns false at `:212-215`.
- `setAllowFlight(boolean)` throws at `:217-220`.
- `enderChest()` throws `UnsupportedCapabilityException(ENDER_CHEST)` at `:222-227`.
- `itemOnCursor()` / `setItemOnCursor` throw `UnsupportedCapabilityException(CURSOR_ITEM)` at `:229-241`.
- `exhaustion()` / `setExhaustion` throw `UnsupportedCapabilityException(EXHAUSTION)` at `:243-255`.
- `setBedSpawnLocation(..., force=true)` throws `UnsupportedCapabilityException(BED_SPAWN_FORCE)` at `:265-272`; ordinary null/set behavior is real at `:273-282`.
- `displayName()` is synthesized from username at `:284-287`; `displayName(Component)` throws `UnsupportedOperationException` at `:289-292`.
- All inherited Minestom entity/living gaps in sections 5.8 and 5.9 apply to player instances because `MinestomPlayerWrapper extends MinestomLivingEntityWrapper` at `:18-19`.

## 5.11 `MinestomPlayerInventoryWrapper`

Source: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomPlayerInventoryWrapper.java`.

### Constant/null/no-op placeholders

- `size()` returns the platform constant at `:30-33`.
- `type()` always returns `InventoryType.PLAYER` at `:35-38`.
- `getItem(int)` returns null at `:40-43`.
- `setItem(int,ItemStack)` is a no-op at `:45-47`.
- `contents()` returns an empty array at `:48-51`.
- `setContents` is a no-op at `:53-55`.
- `isEmpty()` always returns false at `:61-64`.
- `location()` returns null at `:66-69`.
- `helmet`, `chestplate`, `leggings`, `boots`, main-hand, and off-hand getters return null at `:76-119`.
- All corresponding equipment setters are no-ops at `:80-122`.
- `getItem(EquipmentSlot)` returns null at `:139-142`.
- `armorContents()` and `extraContents()` return empty collections at `:150-165`.
- `heldItemSlot()` falls back to constant `0` when the holder is not a `MinestomPlayerWrapper` at `:124-130`; the platform-backed holder path is real at `:125-137`.
- `clear()` delegates to Minestom inventory at `:56-59`.

### Explicit unsupported methods

- `setItem(EquipmentSlot,ItemStack)` — `:144-148`
- `setArmorContents(...)` — `:156-160`
- `setExtraContents(...)` — `:168-172`
- `addItem(...)` — `:174-177`
- `removeItem(...)` — `:179-182`
- `contains(ItemType)` — `:184-187`
- `contains(ItemStack)` — `:189-192`
- `containsAtLeast(...)` — `:194-197`
- `first(ItemStack)` — `:199-202`
- `firstEmpty()` — `:204-207`

## 5.12 `MinestomRayTraceResultWrapper`

`hitPosition()` is backed, but `hitBlock()`, `hitBlockFace()`, and `hitEntity()` always return null at `utilities-minestom/.../MinestomRayTraceResultWrapper.java:26-43`. Minestom has no connected ray-trace producer in `MinestomWorldWrapper` or `MinestomAdapters`.

## 5.13 `MinestomAdapters` and missing conversion families

`MinestomAdapters` contains conversions for Location/Position/vector-to-Vec, BoundingBox, World/Instance, Block/Chunk, Entity/Player, BlockType, BlockState, and BlockFace at `utilities-minestom/.../MinestomAdapters.java:27-184`.

No Minestom adapter/conversion exists for:

- `ItemStack` / `ItemMeta`
- `Inventory` / `InventoryView` / `InventoryHolder` / `EntityEquipment`
- `PotionEffect` / `PotionEffectType` / `Enchantment` / `Particle` / custom `Sound`
- `Attribute` / `AttributeInstance` / `AttributeModifier` / `AttributeRegistry` / `AttributeModifierFactory`
- `Biome` or a biome conversion
- `TileBlockState`
- `ItemFactory`
- `Server`

There is no `MinestomServerWrapper.java` in the module. `adapt(net.minestom.server.entity.Entity)` always creates the base entity wrapper at `MinestomAdapters.java:102-110`; callers must use the separate Player overload to obtain `MinestomPlayerWrapper`.

## 5.14 `MinestomBlockTypeWrapper`

`MinestomBlockTypeWrapper` implements only key/equality/hash/string at `utilities-minestom/.../MinestomBlockTypeWrapper.java:18-37`. It does not override `translationKey()`, so that method inherits `UnsupportedCapabilityException(BLOCK_QUERY)` from `utilities-api/.../block/BlockType.java:12-14`.

---

# 6. Typed data-component API coverage

All files under `utilities-api/src/main/java/org/aincraft/api/domain/datacomponent/` are API contracts, but no Bukkit, Paper, or Minestom adapter class implements these typed component interfaces. The current adapter surface only provides generic `DataComponentType` persistence through `BukkitItemMetaWrapper`.

The unadapted typed families are:

- **Item component base/types:** `StandardNonValuedDataComponentType`, `StandardValuedDataComponentType`, `StandardDataComponentType`, `DataComponentTypes`
- **Item content/components:** `WrittenBookContent`, `WritableBookContent`, `Weapon`, `UseRemainder`, `UseEffects`, `UseCooldown`, `TooltipDisplay`, `Tool`, `SwingAnimation`, `SulfurCubeContent`, `SeededContainerLoot`, `ResolvableProfile`, `Repairable`, `PotDecorations`, `PlayerProfile`, `PiercingWeapon`, `OminousBottleAmplifier`, `MapItemColor`, `MapId`, `MapDecorations`, `LodestoneTracker`, `KineticWeapon`, `JukeboxPlayable`, `ItemLore`, `ItemEnchantments`, `ItemContainerContents`, `ItemArmorTrim`, `ItemAdventurePredicate`, `FoodProperties`, `Fireworks`, `FireworkEffect`, `Equippable`, `EquipmentSlotGroup`, `Enchantable`, `DyedItemColor`, `DeathProtection`, `DamageResistant`, `CustomModelData`, `ConsumeEffect`, `Consumable`, `ChargedProjectiles`, `BundleContents`, `BlocksAttacks`, `BlockItemDataProperties`, `BannerPatternLayers`, `AttackRange`
- **Blocks-attacks components:** `ItemDamageFunction`, `DamageReduction`
- **Attribute-display components:** `ItemAttributeModifiers`, `AttributeModifierDisplays`, `AttributeModifierDisplay`
- **Text components:** `Filtered`
- **Potion components:** `SuspiciousStewEffects`, `SuspiciousEffectEntry`, `PotionEffect`, `PotionContents`
- **Block components:** `BlockPredicate`

`AttributeModifierDisplays.Default.overrideText()` and `Hidden.overrideText()` return null at `utilities-api/.../datacomponent/item/attribute/AttributeModifierDisplays.java:42-71`; that null is intentional for DEFAULT/HIDDEN display types and is not an adapter gap.

---

# 7. Items the original gap report incorrectly called absent

The following contracts/types are present in `utilities-api` and must not be re-added:

- `EntityEquipment`, `InventoryView`, `ItemFlag`, inventory bulk operations, ItemStack clone/editMeta/quantity/enchant operations
- `HumanEntity`, `Nameable`, Entity rotation/velocity/passenger/vehicle/visibility contracts, LivingEntity combat/potion/air/AI contracts, Damageable absorption/kill contracts, Player bed-spawn/display/flight contracts
- `FluidCollisionMode`, `HeightMap`, `GameMode`, `Difficulty`, `Environment`, `WorldBorder`, `RayTraceResult`
- `TileBlockState`, `VoxelShape`, `BlockSupport`, `BlockFace`, `PistonMoveReaction`
- `Sound`, `Particle`, `EntityEffect`, `PotionEffectTypeCategory`, `PotionEffect` and core PotionEffectType contracts
- `Attribute`, `AttributeInstance`, `AttributeModifier`, `Attributes`, `AttributeRegistry`, `AttributeModifierFactory`, `Attributable`, and nested `Attribute.Sentiment`
- `Rotation`, `Position`

`EquipmentSlot` already includes `BODY` and `SADDLE` and implements hand/armor helpers at `utilities-api/.../inventory/EquipmentSlot.java:5-29`.

`Vector3d` is not an absent required type under the current design. `Position` is the common x/y/z abstraction; JOML `Vector3dc` is used for vector/direction signatures, with explicit adapter conversion where required.

---

# 8. Priority order for implementation

This order reflects cross-platform value and the current verified gaps:

1. **Minestom correctness blockers:** ItemStack/Inventory conversion layer; entity velocity/rotation/passengers/visibility; LivingEntity equipment/combat/potion/attribute modifiers; Player inventory; world ray/highest/particle/weather/time/configuration; block state/biome/light/drops; location nearby/chunk; server wrapper.
2. **Shared geometry:** implement `BoundingBox` geometry once in the API/default or in both Bukkit and Minestom wrappers; add the ray-trace result mapping contract.
3. **Paper inherited gaps:** add Paper-specific BlockState and BoundingBox paths or intentionally document their Bukkit limitations; add native Paper inventory-title and player-display-name paths.
4. **Bukkit capability edges:** retain explicit Spigot throws for the four block predicates and border reset; decide how to expose `registerAttribute` and transient modifiers; keep the unsupported-Java-type behavior in generic PDC methods explicit.
5. **API-absent contracts:** add `DamageableItemMeta`, `ItemFactory`, stack-level data components, Player inventory-view controls, tile-state access, typed particle data, generic class-based spawn, EntityEffect sending, and any intentionally adopted vector/integer-vector APIs.
6. **Typed data components:** add platform wrappers/converters for the component families listed above; generic PDC support is not a substitute for typed Paper component semantics.
