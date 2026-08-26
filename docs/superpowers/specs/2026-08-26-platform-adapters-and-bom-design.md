# Platform Adapters (`utilities-bukkit`, `utilities-paper`) & Bill of Materials (`utilities-bom`) — Design Spec

**Date:** 2026-08-26  
**Status:** Approved  
**Target Modules:** `:utilities-bom`, `:utilities-bukkit`, `:utilities-paper`

## Context & Motivation

With pure domain-agnostic interfaces in `:common` (`World`, `Chunk`, `Block`, `Entity`, `Player`, `Location`, `Position`, `BlockFace`, `BoundingBox`, etc.), plugins need concrete platform bridges for Bukkit and Paper to adapt live server objects to these domain interfaces. Additionally, a central Bill of Materials (BOM) module is required so consumers can declare a single dependency constraint for the entire library suite without manually synchronizing version numbers across all submodules.

## Goals

1. **`utilities-bom` (Bill of Materials)**:
   - Built using Gradle `java-platform` plugin.
   - Publishes Maven BOM metadata declaring dependency management constraints across all published workspace modules:
     - `org.aincraft:common`
     - `org.aincraft:utilities-bukkit`
     - `org.aincraft:utilities-paper`
     - `org.aincraft:config`
     - `org.aincraft:db-core`
     - `org.aincraft:db-paper`
     - `org.aincraft:math`
     - `org.aincraft:registry`

2. **`utilities-bukkit` (Bukkit Platform Adapter)**:
   - Depends on `:common` via `api(project(":common"))`.
   - Compiles against Bukkit API (`compileOnly(libs.paper.api)`).
   - Adventure text bridge: uses `net.kyori:adventure-text-serializer-legacy` (`LegacyComponentSerializer.legacySection()`) to bridge Adventure `Audience` calls (`sendMessage`, `sendActionBar`, `showTitle`, `playSound`) to standard Bukkit API calls on non-Paper Bukkit servers.
   - Package: `org.aincraft.bukkit.adapter`.
   - Provides `BukkitAdapters` bidirectional facade:
     - Adapts `org.bukkit.Location` <-> `Location<World>`
     - Adapts `org.bukkit.block.Block` <-> `Block`
     - Adapts `org.bukkit.Chunk` <-> `Chunk`
     - Adapts `org.bukkit.World` <-> `World`
     - Adapts `org.bukkit.entity.Entity` <-> `Entity`
     - Adapts `org.bukkit.entity.Player` <-> `Player`
     - Adapts `org.bukkit.block.BlockFace` <-> `BlockFace`
     - Adapts `org.bukkit.util.BoundingBox` <-> `BoundingBox`
     - Adapts `org.bukkit.util.Vector` <-> `Position`
   - Wrapper classes delegating live calls to underlying Bukkit objects:
     - `BukkitWorldWrapper`, `BukkitChunkWrapper`, `BukkitBlockWrapper`, `BukkitEntityWrapper`, `BukkitPlayerWrapper`, `BukkitLocationWrapper`, `BukkitPositionWrapper`, `BukkitBoundingBoxWrapper`, `BukkitBlockTypeWrapper`, `BukkitBlockStateWrapper`.

3. **`utilities-paper` (Paper Platform Adapter)**:
   - Layered over `utilities-bukkit` via `api(project(":utilities-bukkit"))`.
   - Compiles against Paper API (`compileOnly(libs.paper.api)`).
   - Package: `org.aincraft.paper.adapter`.
   - Provides `PaperAdapters`:
     - Overrides `Player` and `World` adaptation to return `PaperPlayerWrapper` and `PaperWorldWrapper`.
     - Exploits Paper's native Adventure `Audience` implementation on `Player` and `World` (direct zero-conversion dispatch for `sendMessage`, `sendActionBar`, `showTitle`, `playSound`, `identity()`).
     - Paper-specific chunk helpers.

4. **Testing & Verification**:
   - Comprehensive unit and integration tests verifying bidirectional conversion, contract adherence, method delegation, coordinate translations, and audience dispatch.
   - Zero jar pollution and classpath isolation verification across all new submodules.

## Architecture & Layout

```
settings.gradle.kts (includes utilities-bom, utilities-bukkit, utilities-paper)

utilities-bom/
└── build.gradle.kts (java-platform)

utilities-bukkit/
├── build.gradle.kts
└── src/
    ├── main/java/org/aincraft/bukkit/adapter/
    │   ├── BukkitAdapters.java
    │   ├── BukkitBlockStateWrapper.java
    │   ├── BukkitBlockTypeWrapper.java
    │   ├── BukkitBlockWrapper.java
    │   ├── BukkitBoundingBoxWrapper.java
    │   ├── BukkitChunkWrapper.java
    │   ├── BukkitEntityWrapper.java
    │   ├── BukkitLocationWrapper.java
    │   ├── BukkitPlayerWrapper.java
    │   ├── BukkitPositionWrapper.java
    │   └── BukkitWorldWrapper.java
    └── test/java/org/aincraft/bukkit/adapter/
        └── BukkitAdaptersTest.java

utilities-paper/
├── build.gradle.kts
└── src/
    ├── main/java/org/aincraft/paper/adapter/
    │   ├── PaperAdapters.java
    │   ├── PaperPlayerWrapper.java
    │   └── PaperWorldWrapper.java
    └── test/java/org/aincraft/paper/adapter/
        └── PaperAdaptersTest.java
```

## Detailed Component Specifications

### 1. `utilities-bom/build.gradle.kts`
```kotlin
plugins {
    `java-platform`
    `maven-publish`
}

apply(from = rootProject.file("gradle/publish-conventions.gradle.kts"))

dependencies {
    constraints {
        api(project(":common"))
        api(project(":utilities-bukkit"))
        api(project(":utilities-paper"))
        api(project(":config"))
        api(project(":db-core"))
        api(project(":db-paper"))
        api(project(":math"))
        api(project(":registry"))
    }
}
```

### 2. `BukkitAdapters` Facade & Audience Bridge
```java
public final class BukkitAdapters {
  public static @NotNull Location<World> adapt(@NotNull org.bukkit.Location loc);
  public static @NotNull org.bukkit.Location toBukkit(@NotNull Location<?> loc);
  public static @NotNull Position adapt(@NotNull org.bukkit.util.Vector vec);
  public static @NotNull org.bukkit.util.Vector toBukkit(@NotNull Position pos);
  public static @NotNull BoundingBox adapt(@NotNull org.bukkit.util.BoundingBox box);
  public static @NotNull org.bukkit.util.BoundingBox toBukkit(@NotNull BoundingBox box);
  public static @NotNull Block adapt(@NotNull org.bukkit.block.Block block);
  public static @NotNull org.bukkit.block.Block toBukkit(@NotNull Block block);
  public static @NotNull Chunk adapt(@NotNull org.bukkit.Chunk chunk);
  public static @NotNull org.bukkit.Chunk toBukkit(@NotNull Chunk chunk);
  public static @NotNull World adapt(@NotNull org.bukkit.World world);
  public static @NotNull org.bukkit.World toBukkit(@NotNull World world);
  public static @NotNull Entity adapt(@NotNull org.bukkit.entity.Entity entity);
  public static @NotNull org.bukkit.entity.Entity toBukkit(@NotNull Entity entity);
  public static @NotNull Player adapt(@NotNull org.bukkit.entity.Player player);
  public static @NotNull org.bukkit.entity.Player toBukkit(@NotNull Player player);
  public static @NotNull BlockFace adapt(@NotNull org.bukkit.block.BlockFace face);
  public static @NotNull org.bukkit.block.BlockFace toBukkit(@NotNull BlockFace face);
}
```

### 3. `PaperAdapters` Facade
```java
public final class PaperAdapters {
  public static @NotNull Player adapt(@NotNull org.bukkit.entity.Player player) {
    return new PaperPlayerWrapper(player);
  }
  public static @NotNull World adapt(@NotNull org.bukkit.World world) {
    return new PaperWorldWrapper(world);
  }
}
```

## Spec Self-Review Checklist

1. **Placeholder Scan**: No TODOs, TBDs, or vague implementations.
2. **Internal Consistency**: Method names, conversion signatures, and package names match `:common` interface types.
3. **Layering**: `utilities-paper` directly reuses `utilities-bukkit` while specializing Paper/Adventure integration.
4. **BOM Completeness**: All 8 workspace modules constrained in `utilities-bom`.
