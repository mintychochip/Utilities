# Common Domain Interfaces — Design Spec

**Date:** 2026-08-26  
**Status:** Approved  
**Target Module:** `:common` (new submodule)

## Context & Motivation

Currently, the `utilities` project contains various utility classes, some of which are coupled to the Bukkit/Paper API (such as `org.bukkit.plugin.Plugin`, `org.bukkit.configuration.ConfigurationSection`, etc.). To enable multi-platform support (Paper, Minestom, Fabric, Sponge, Velocity, standalone proxies, or offline tools) and domain-driven design, we need domain-agnostic interface abstractions representing fundamental Minecraft world and entity concepts without any Bukkit dependencies.

## Goals

1. **New Submodule `:common`**:
   - Register `include("common")` in `settings.gradle.kts`.
   - Create `common/build.gradle.kts` using Java 25 toolchain, maven-publish, nmcp, signing, and JUnit 5.
   - **Zero Bukkit / Paper / Spigot dependencies** in `:common`.
   - Domain-neutral ecosystem dependencies only: `net.kyori:adventure-api`, `net.kyori:adventure-key`, and `org.jetbrains:annotations`.

2. **Domain-Agnostic Interface Types**:
   - **Location & Math (`org.aincraft.common.location`)**:
     - `Position`: 3D double coordinate contract with convenience helpers (`x()`, `y()`, `z()`, `blockX()`, `blockY()`, `blockZ()`, `distance()`, `distanceSquared()`, `add()`, `subtract()`, `multiply()`, `toVector()`, etc.).
     - `Vector3d`: Immutable 3D double vector record (`x`, `y`, `z`).
     - `Vector3i`: Immutable 3D integer vector record (`x`, `y`, `z`).
     - `Location<W extends World>`: Represents a spatial `Position` anchored to a specific `World`, with `yaw` and `pitch` orientation.
     - `BoundingBox`: Axis-aligned bounding box record (`minX`, `minY`, `minZ`, `maxX`, `maxY`, `maxZ`) with intersection/containment tests.
   - **World & Chunk (`org.aincraft.common.world`)**:
     - `World`: Named, Keyed, and Identified domain contract representing a game world (`Key key()`, `UUID uid()`, `String name()`, `Block getBlockAt(int x, int y, int z)`, `Chunk getChunkAt(int chunkX, int chunkZ)`, `boolean isChunkLoaded(int chunkX, int chunkZ)`, `int minHeight()`, `int maxHeight()`). Extends `net.kyori.adventure.key.Keyed`, `net.kyori.adventure.identity.Identified`, `net.kyori.adventure.audience.Audience`.
     - `Chunk`: 16x16 vertical block slice contract (`int x()`, `int z()`, `World world()`, `Block getBlock(int x, int y, int z)`, `boolean isLoaded()`).
     - `Block`: Positioned block contract in a world (`World world()`, `Position position()`, `int x()`, `int y()`, `int z()`, `BlockType type()`, `BlockState state()`).
   - **Block Domain (`org.aincraft.common.block`)**:
     - `BlockType`: Keyed representation of a block type (`Key key()`).
     - `BlockState`: Immutable state / properties of a block (`BlockType type()`).
   - **Entity & Player Domain (`org.aincraft.common.entity`)**:
     - `Entity`: Base entity contract (`UUID uniqueId()`, `World world()`, `Location<World> location()`, `Key type()`, `boolean isValid()`). Extends `Identified`, `Keyed`.
     - `Player`: Player domain contract extending `Entity`, `net.kyori.adventure.audience.Audience`, `net.kyori.adventure.identity.Identified` (`UUID uniqueId()`, `String username()`, `Location<World> location()`, `boolean isOnline()`).

3. **Testing & Verification**:
   - Comprehensive unit tests in `common/src/test/java` covering:
     - Vector math, distance calculations, bounding box intersections, and immutable record invariants.
     - Location math and world coordinate projections.
     - Position block coordinate conversions (handling negative coordinates properly: `Math.floor`).
     - Mock/stub implementations of World, Chunk, Block, Player verifying contract ergonomics and Adventure Audience compatibility.
   - Gradle build validation ensuring `:common` compiles cleanly with zero warnings and passes all tests.

## Package & Class Layout

```
common/
├── build.gradle.kts
└── src/
    ├── main/java/org/aincraft/common/
    │   ├── block/
    │   │   ├── BlockState.java
    │   │   └── BlockType.java
    │   ├── entity/
    │   │   ├── Entity.java
    │   │   └── Player.java
    │   ├── location/
    │   │   ├── BoundingBox.java
    │   │   ├── Location.java
    │   │   ├── Position.java
    │   │   ├── Vector3d.java
    │   │   └── Vector3i.java
    │   └── world/
    │       ├── Block.java
    │       ├── Chunk.java
    │       └── World.java
    └── test/java/org/aincraft/common/
        ├── location/
        │   ├── BoundingBoxTest.java
        │   ├── LocationTest.java
        │   └── PositionTest.java
        └── world/
            └── DomainContractsTest.java
```

## Detailed Type Specifications

### 1. `org.aincraft.common.location`

#### `Position`
```java
package org.aincraft.common.location;

import org.jetbrains.annotations.NotNull;

public interface Position {
  double x();
  double y();
  double z();

  default int blockX() {
    return (int) Math.floor(x());
  }

  default int blockY() {
    return (int) Math.floor(y());
  }

  default int blockZ() {
    return (int) Math.floor(z());
  }

  default double distanceSquared(@NotNull Position other) {
    double dx = x() - other.x();
    double dy = y() - other.y();
    double dz = z() - other.z();
    return dx * dx + dy * dy + dz * dz;
  }

  default double distance(@NotNull Position other) {
    return Math.sqrt(distanceSquared(other));
  }

  default Position add(double dx, double dy, double dz) {
    return Position.of(x() + dx, y() + dy, z() + dz);
  }

  default Position subtract(double dx, double dy, double dz) {
    return Position.of(x() - dx, y() - dy, z() - dz);
  }

  default Position multiply(double factor) {
    return Position.of(x() * factor, y() * factor, z() * factor);
  }

  default Vector3d toVector() {
    return new Vector3d(x(), y(), z());
  }

  default Vector3i toBlockVector() {
    return new Vector3i(blockX(), blockY(), blockZ());
  }

  static Position of(double x, double y, double z) {
    return new PositionImpl(x, y, z);
  }
}
```

#### `Vector3d` & `Vector3i`
```java
public record Vector3d(double x, double y, double z) {
  public Vector3d add(Vector3d other) {
    return new Vector3d(x + other.x, y + other.y, z + other.z);
  }
  public Vector3d multiply(double factor) {
    return new Vector3d(x * factor, y * factor, z * factor);
  }
  public double lengthSquared() {
    return x * x + y * y + z * z;
  }
  public double length() {
    return Math.sqrt(lengthSquared());
  }
}

public record Vector3i(int x, int y, int z) {
  public Vector3i add(Vector3i other) {
    return new Vector3i(x + other.x, y + other.y, z + other.z);
  }
}
```

#### `Location<W extends World>`
```java
public interface Location<W extends World> {
  @NotNull W world();
  @NotNull Position position();
  float yaw();
  float pitch();

  default double x() { return position().x(); }
  default double y() { return position().y(); }
  default double z() { return position().z(); }
  default int blockX() { return position().blockX(); }
  default int blockY() { return position().blockY(); }
  default int blockZ() { return position().blockZ(); }

  Location<W> withPosition(@NotNull Position position);
  Location<W> withOrientation(float yaw, float pitch);
  <T extends World> Location<T> withWorld(@NotNull T world);

  static <W extends World> Location<W> of(@NotNull W world, @NotNull Position position, float yaw, float pitch) {
    return new LocationImpl<>(world, position, yaw, pitch);
  }

  static <W extends World> Location<W> of(@NotNull W world, double x, double y, double z) {
    return of(world, Position.of(x, y, z), 0.0f, 0.0f);
  }

  static <W extends World> Location<W> of(@NotNull W world, double x, double y, double z, float yaw, float pitch) {
    return of(world, Position.of(x, y, z), yaw, pitch);
  }
}
```

#### `BoundingBox`
```java
public record BoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
  public BoundingBox {
    if (minX > maxX || minY > maxY || minZ > maxZ) {
      throw new IllegalArgumentException("Minimum coordinates cannot exceed maximum coordinates");
    }
  }

  public static BoundingBox of(Position p1, Position p2) {
    return new BoundingBox(
      Math.min(p1.x(), p2.x()), Math.min(p1.y(), p2.y()), Math.min(p1.z(), p2.z()),
      Math.max(p1.x(), p2.x()), Math.max(p1.y(), p2.y()), Math.max(p1.z(), p2.z())
    );
  }

  public boolean contains(double x, double y, double z) {
    return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
  }

  public boolean contains(Position position) {
    return contains(position.x(), position.y(), position.z());
  }

  public boolean intersects(BoundingBox other) {
    return minX <= other.maxX && maxX >= other.minX &&
           minY <= other.maxY && maxY >= other.minY &&
           minZ <= other.maxZ && maxZ >= other.minZ;
  }
}
```

### 2. `org.aincraft.common.world` & `org.aincraft.common.block`

#### `World`
```java
public interface World extends Keyed, Identified, Audience {
  @NotNull UUID uid();
  @NotNull String name();

  @Override
  default @NotNull Identity identity() {
    return Identity.identity(uid());
  }

  @NotNull Block getBlockAt(int x, int y, int z);
  default @NotNull Block getBlockAt(@NotNull Position position) {
    return getBlockAt(position.blockX(), position.blockY(), position.blockZ());
  }
  default @NotNull Block getBlockAt(@NotNull Location<?> location) {
    return getBlockAt(location.position());
  }

  @NotNull Chunk getChunkAt(int chunkX, int chunkZ);
  boolean isChunkLoaded(int chunkX, int chunkZ);

  int minHeight();
  int maxHeight();
}
```

#### `Chunk`
```java
public interface Chunk {
  int x();
  int z();
  @NotNull World world();
  @NotNull Block getBlock(int x, int y, int z);
  boolean isLoaded();
}
```

#### `Block`
```java
public interface Block {
  @NotNull World world();
  @NotNull Position position();
  default int x() { return position().blockX(); }
  default int y() { return position().blockY(); }
  default int z() { return position().blockZ(); }
  @NotNull BlockType type();
  @NotNull BlockState state();
}
```

#### `BlockType` & `BlockState`
```java
public interface BlockType extends Keyed {
  static BlockType of(@NotNull Key key) {
    return new BlockTypeImpl(key);
  }
}

public interface BlockState {
  @NotNull BlockType type();
}
```

### 3. `org.aincraft.common.entity`

#### `Entity`
```java
public interface Entity extends Keyed, Identified {
  @NotNull UUID uniqueId();
  @NotNull World world();
  @NotNull Location<World> location();
  @NotNull Key type();
  boolean isValid();

  @Override
  default @NotNull Identity identity() {
    return Identity.identity(uniqueId());
  }

  @Override
  default @NotNull Key key() {
    return type();
  }
}
```

#### `Player`
```java
public interface Player extends Entity, Audience, Identified {
  @NotNull String username();
  boolean isOnline();
}
```
## Spec Self-Review Checklist

1. **Placeholder Scan**: No TODOs, TBDs, or vague placeholders.
2. **Internal Consistency**: Method names, types, records, and packages align across all domain interfaces.
3. **Scope Check**: Clear, self-contained domain contracts for `:common` submodule.
4. **Bukkit Decoupling**: Absolutely no Bukkit / Paper types imported or exposed in `:common`.
