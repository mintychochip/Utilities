# Minestom Platform Adapter (`utilities-minestom`) — Design Spec

**Date:** 2026-08-26  
**Status:** In Review  
**Target Module:** `:utilities-minestom` (plus updates to `:utilities-bom` and `settings.gradle.kts`)

## Context & Motivation

The `:common` module defines pure, platform-neutral domain interfaces (`World`, `Chunk`, `Block`, `Entity`, `Player`, `Location`, `Position`, `BlockFace`, `BoundingBox`, `BlockType`, `BlockState`). While `:utilities-bukkit` and `:utilities-paper` bridge Bukkit/Spigot and Paper platforms to these interfaces, modern lightweight server architectures frequently use **Minestom** (`net.minestom:minestom`).

Minestom is a 100% async, multithreaded, zero-vanilla-code server implementation built natively on Kyori Adventure. To allow developers to write unified gameplay logic and utilities targeting both Bukkit/Paper and Minestom, this project introduces `:utilities-minestom`, a high-performance adapter module translating between Minestom's runtime objects and `:common` domain interfaces.

## Goals

1. **`utilities-minestom` Module Creation**:
   - Pure Java 25 library module applying standard build conventions (`gradle/java-conventions.gradle.kts` and `gradle/publish-conventions.gradle.kts`).
   - Compiles against `:common` (`api(project(":utilities-common"))`) and Minestom (`compileOnly(libs.minestom)`).
   - Strict platform isolation: `extra["bukkitFree"] = true` and `extra["paperFree"] = true`, preventing Bukkit/Paper classes from leaking into the classpath or codebase.
   - Strict jar packaging isolation: `extra["allowedAincraftPrefixes"] = listOf("org/aincraft/minestom/")`.

2. **`MinestomAdapters` Facade**:
   - Bidirectional adaptation between Minestom objects and `:common` domain models:
     - `net.minestom.server.coordinate.Pos` + `Instance` $\leftrightarrow$ `Location`
     - `net.minestom.server.coordinate.Point` / `Vec` $\leftrightarrow$ `Position`
     - `net.minestom.server.collision.BoundingBox` $\leftrightarrow$ `BoundingBox`
     - `net.minestom.server.instance.Instance` $\leftrightarrow$ `World`
     - `net.minestom.server.instance.Chunk` $\leftrightarrow$ `Chunk`
     - `net.minestom.server.instance.block.Block` $\leftrightarrow$ `Block` (when tied to an Instance)
     - `net.minestom.server.instance.block.Block` $\leftrightarrow$ `BlockType` and `BlockState`
     - `net.minestom.server.instance.block.BlockFace` $\leftrightarrow$ `BlockFace`
     - `net.minestom.server.entity.Entity` $\leftrightarrow$ `Entity`
     - `net.minestom.server.entity.Player` $\leftrightarrow$ `Player`

3. **Domain Wrappers with Native Adventure Forwarding**:
   - `MinestomWorldWrapper`: Wraps `net.minestom.server.instance.Instance`. Direct zero-overhead Kyori Adventure `Audience` forwarding (`sendMessage`, `sendActionBar`, `showTitle`, `playSound`, `stopSound`, etc.) and `Identified` forwarding (`identity()`).
   - `MinestomPlayerWrapper`: Wraps `net.minestom.server.entity.Player`. Direct `Audience` forwarding, live getters/setters (`health`, `foodLevel`, `gameMode`, `isSneaking`, `isSprinting`, `isFlying`, `kick`).
   - `MinestomEntityWrapper`: Wraps `net.minestom.server.entity.Entity`. Delegates coordinates, velocity, bounding box, tags/names, and removal.
   - `MinestomChunkWrapper`: Wraps `net.minestom.server.instance.Chunk`.
   - `MinestomBlockWrapper`, `MinestomBlockStateWrapper`, `MinestomBlockTypeWrapper`: Wraps Minestom `Block` instances and block properties.
   - `MinestomLocationWrapper`, `MinestomPositionWrapper`, `MinestomBoundingBoxWrapper`: Zero-drift coordinate wrappers.

4. **BOM & Version Management Integration**:
   - Add `minestom` coordinate to `gradle/libs.versions.toml`.
   - Include `:utilities-minestom` in `settings.gradle.kts`.
   - Register constraint `api(project(":utilities-minestom"))` in `utilities-bom/build.gradle.kts`.

5. **Testing & Quality Assurance**:
   - Comprehensive test suite in `MinestomAdaptersTest` testing bidirectional conversion, live delegation, audience message dispatch, edge cases, coordinate translation, and jar isolation.

## Architecture & Layout

```
settings.gradle.kts (includes utilities-minestom)

gradle/
└── libs.versions.toml (adds minestom)

utilities-bom/
└── build.gradle.kts (adds api(project(":utilities-minestom")))

utilities-minestom/
├── build.gradle.kts
└── src/
    ├── main/java/org/aincraft/minestom/adapter/
    │   ├── MinestomAdapters.java
    │   ├── MinestomBlockStateWrapper.java
    │   ├── MinestomBlockTypeWrapper.java
    │   ├── MinestomBlockWrapper.java
    │   ├── MinestomBoundingBoxWrapper.java
    │   ├── MinestomChunkWrapper.java
    │   ├── MinestomEntityWrapper.java
    │   ├── MinestomLocationWrapper.java
    │   ├── MinestomPlayerWrapper.java
    │   ├── MinestomPositionWrapper.java
    │   └── MinestomWorldWrapper.java
    └── test/java/org/aincraft/minestom/adapter/
        └── MinestomAdaptersTest.java
```

## Detailed Component Specifications

### 1. `utilities-minestom/build.gradle.kts`
```kotlin
plugins {
    `java-library`
    `maven-publish`
}

extra["allowedAincraftPrefixes"] = listOf("org/aincraft/minestom/")
extra["forbiddenAincraftPrefixes"] = listOf(
    "org/aincraft/bukkit/",
    "org/aincraft/paper/",
    "org/aincraft/config/",
    "org/aincraft/db/",
    "org/aincraft/math/",
    "org/aincraft/registry/",
)
extra["bukkitFree"] = true
extra["paperFree"] = true

apply(from = rootProject.file("gradle/java-conventions.gradle.kts"))
apply(from = rootProject.file("gradle/publish-conventions.gradle.kts"))

dependencies {
    api(project(":utilities-common"))
    compileOnly(libs.minestom)

    testImplementation(libs.minestom)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
}
```

### 2. `MinestomAdapters` Facade Contract
```java
package org.aincraft.minestom.adapter;

import java.util.Objects;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import org.aincraft.common.block.BlockFace;
import org.aincraft.common.block.BlockState;
import org.aincraft.common.block.BlockType;
import org.aincraft.common.entity.Entity;
import org.aincraft.common.entity.Player;
import org.aincraft.common.location.BoundingBox;
import org.aincraft.common.location.Location;
import org.aincraft.common.location.Position;
import org.aincraft.common.world.Block;
import org.aincraft.common.world.Chunk;
import org.aincraft.common.world.World;
import org.jetbrains.annotations.NotNull;

public final class MinestomAdapters {
  private MinestomAdapters() {}

  // Coordinates
  public static @NotNull Location adapt(@NotNull Instance instance, @NotNull Pos pos);
  public static @NotNull Pos toMinestomPos(@NotNull Location location);
  public static @NotNull Position adapt(@NotNull Point point);
  public static @NotNull Vec toMinestomVec(@NotNull Position position);
  public static @NotNull BoundingBox adapt(@NotNull net.minestom.server.collision.BoundingBox box);
  public static @NotNull net.minestom.server.collision.BoundingBox toMinestom(@NotNull BoundingBox box);

  // Blocks & World
  public static @NotNull Block adapt(@NotNull Instance instance, int x, int y, int z);
  public static @NotNull Chunk adapt(@NotNull net.minestom.server.instance.Chunk chunk);
  public static @NotNull net.minestom.server.instance.Chunk toMinestom(@NotNull Chunk chunk);
  public static @NotNull World adapt(@NotNull Instance instance);
  public static @NotNull Instance toMinestom(@NotNull World world);
  public static @NotNull BlockFace adapt(@NotNull net.minestom.server.instance.block.BlockFace face);
  public static @NotNull net.minestom.server.instance.block.BlockFace toMinestom(@NotNull BlockFace face);
  public static @NotNull BlockType adapt(@NotNull net.minestom.server.instance.block.Block block);
  public static @NotNull net.minestom.server.instance.block.Block toMinestom(@NotNull BlockType blockType);
  public static @NotNull BlockState adaptState(@NotNull net.minestom.server.instance.block.Block block);
  public static @NotNull net.minestom.server.instance.block.Block toMinestom(@NotNull BlockState blockState);

  // Entities & Players
  public static @NotNull Entity adapt(@NotNull net.minestom.server.entity.Entity entity);
  public static @NotNull net.minestom.server.entity.Entity toMinestom(@NotNull Entity entity);
  public static @NotNull Player adapt(@NotNull net.minestom.server.entity.Player player);
  public static @NotNull net.minestom.server.entity.Player toMinestom(@NotNull Player player);
}
```

### 3. Wrapper Implementations

- **`MinestomWorldWrapper`**:
  - Implements `org.aincraft.common.world.World`.
  - Wraps `Instance`.
  - `uid()` returns `instance.getUniqueId()`.
  - `name()` returns `instance.getDimensionName().asString()` or UUID string fallback.
  - `key()` returns `Key.key(instance.getDimensionName().asString())`.
  - Native Adventure Audience delegation: passes `sendMessage`, `sendActionBar`, `showTitle`, `playSound`, `stopSound`, `clearTitle`, `resetTitle` directly to `instance`.
  - `getBlockAt(x, y, z)` returns `new MinestomBlockWrapper(instance, x, y, z)`.
  - `getChunkAt(x, z)` returns `adapt(instance.getChunk(x, z))`.
  - `isChunkLoaded(x, z)` returns `instance.isChunkLoaded(x, z)`.
  - `players()` maps `instance.getPlayers()`.
  - `entities()` maps `instance.getEntities()`.
  - `loadedChunks()` maps `instance.getChunks()`.
  - `minHeight()` returns `instance.getDimension().minY()`.
  - `maxHeight()` returns `instance.getDimension().minY() + instance.getDimension().height()`.
  - `time()` returns `instance.getTime()`.
  - `fullTime()` returns `instance.getWorldAge()`.

- **`MinestomPlayerWrapper`**:
  - Implements `org.aincraft.common.entity.Player`.
  - Wraps `net.minestom.server.entity.Player`.
  - `username()` returns `player.getUsername()`.
  - `isOnline()` returns `player.isOnline()`.
  - `ping()` returns `player.getLatency()`.
  - `health()` returns `player.getHealth()`.
  - `maxHealth()` returns `player.getAttributeValue(Attribute.GENERIC_MAX_HEALTH)` or `player.getMaxHealth()`.
  - `foodLevel()` returns `player.getFood()`.
  - `saturation()` returns `player.getFoodSaturation()`.
  - `level()` returns `player.getLevel()`.
  - `exp()` returns `player.getExp()`.
  - `gameMode()` returns `Key.key("minecraft", player.getGameMode().name().toLowerCase(Locale.ROOT))`.
  - `isSneaking()` returns `player.isSneaking()`.
  - `isSprinting()` returns `player.isSprinting()`.
  - `isFlying()` returns `player.isFlying()`.
  - `setFlying(bool)`, `setSneaking(bool)`, `setSprinting(bool)`.
  - `kick(Component)` invokes `player.kick(reason)`.
  - Native Adventure `Audience` forwarding directly to `player`.

- **`MinestomEntityWrapper`**:
  - Implements `org.aincraft.common.entity.Entity`.
  - Wraps `net.minestom.server.entity.Entity`.
  - `uniqueId()` returns `entity.getUuid()`.
  - `location()` returns `adapt(entity.getInstance(), entity.getPosition())`.
  - `velocity()` returns `Position.of(entity.getVelocity().x(), entity.getVelocity().y(), entity.getVelocity().z())`.
  - `isOnGround()` returns `entity.isOnGround()`.
  - `isValid()` returns `!entity.isRemoved()`.
  - `remove()` calls `entity.remove()`.
  - `teleport(Location)` teleports to target position/instance.
  - `customName()`, `setCustomName()`, `isCustomNameVisible()`, `setCustomNameVisible()`.
  - `type()` returns `Key.key(entity.getEntityType().name())`.
  - `boundingBox()` returns `adapt(entity.getBoundingBox())`.

- **`MinestomChunkWrapper`**:
  - Implements `org.aincraft.common.world.Chunk`.
  - Wraps `net.minestom.server.instance.Chunk`.
  - `x()`, `z()`, `world()`, `isLoaded()`, `getBlock(x, y, z)`.

- **`MinestomBlockWrapper`**:
  - Implements `org.aincraft.common.world.Block`.
  - Bound to `Instance` and `(x, y, z)`.
  - `type()`, `state()`, `setType()`, `setState()`, `world()`, `location()`, `position()`.

- **`MinestomBlockTypeWrapper` and `MinestomBlockStateWrapper`**:
  - Wraps `net.minestom.server.instance.block.Block`.
  - Translates block namespace and state string representations.

- **`MinestomBlockFace` Translation**:
  - Direct 1:1 mapping between `net.minestom.server.instance.block.BlockFace` and `org.aincraft.common.block.BlockFace`:
    `NORTH`, `EAST`, `SOUTH`, `WEST`, `UP`, `DOWN`, `NORTH_EAST`, `NORTH_WEST`, `SOUTH_EAST`, `SOUTH_WEST`, `SELF`.

## Testing & Verification Strategy

1. **Adapter Bidirectional Tests (`MinestomAdaptersTest`)**:
   - `testCoordinateConversions`: verify `Pos` <-> `Location` and `Vec`/`Point` <-> `Position` round-trip with yaw/pitch.
   - `testBoundingBoxConversions`: verify min/max coordinates and dimensions round-trip.
   - `testBlockFaceConversions`: verify all block face enums match and throw on nulls.
   - `testBlockTypeAndStateConversions`: verify namespace keys and block property round-trips.
   - `testWorldWrapperDelegation`: mock `Instance`, verify chunk loading checks, dimension height calculations, time queries, and player/entity collections.
   - `testPlayerWrapperDelegation`: mock `Player`, verify health, food, flying/sneaking/sprinting state mutations, and kick dispatch.
   - `testAudienceNativePassThrough`: verify Kyori Adventure messages/titles/actionbars dispatch directly to Minestom `Audience`.

2. **Isolation & Check Tasks**:
   - `verifyJarIsolation`: Ensures zero foreign bytecode in `utilities-minestom.jar`.
   - `verifyNoBukkitImports` & `verifyNoPaperImports`: Confirms no Bukkit or Paper references exist in `utilities-minestom`.
   - Workspace build check: `./gradlew check` verifies all 10 submodules compile and pass tests cleanly.

## Spec Self-Review Checklist

1. **Placeholder Scan**: No TODOs, TBDs, or vague implementations.
2. **Internal Consistency**: Method names, conversion signatures, and package names match `:common` interface types.
3. **Isolation Enforced**: Explicit `bukkitFree` and `paperFree` checks.
4. **BOM Completeness**: All workspace modules including `utilities-minestom` constrained in `utilities-bom`.
