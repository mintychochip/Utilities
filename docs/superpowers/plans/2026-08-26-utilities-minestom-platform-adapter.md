# Minestom Platform Adapter (`utilities-minestom`) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement the `:utilities-minestom` platform adapter module to adapt Minestom runtime instances, players, entities, chunks, blocks, and coordinates to `:common` domain interfaces, fully registered in the Gradle workspace and BOM.

**Architecture:** A standalone `:utilities-minestom` Gradle module compiling against `:common` and `net.minestom:minestom`. A central bidirectional facade `MinestomAdapters` delegates live operations to Minestom records/objects through specialized wrappers with direct Kyori Adventure `Audience` forwarding, isolated from Bukkit/Paper via build-level isolation checks.

**Tech Stack:** Java 25, Gradle (Kotlin DSL), Minestom (`net.minestom:minestom:2026.08.16-26.2`), Kyori Adventure 4.18.0, JUnit 5.11.3, Mockito 5.14.2.

## Global Constraints

- **Platform Isolation:** `extra["bukkitFree"] = true` and `extra["paperFree"] = true` must be declared in `utilities-minestom/build.gradle.kts`. No Bukkit/Paper classes or imports may exist in `:utilities-minestom`.
- **Jar Packaging:** `extra["allowedAincraftPrefixes"] = listOf("org/aincraft/minestom/")`.
- **BOM Management:** `:utilities-minestom` must be included in `settings.gradle.kts` and constrained in `utilities-bom/build.gradle.kts`.
- **Package Base:** `org.aincraft.minestom.adapter`.

---

### Task 1: Gradle Workspace, Dependencies & Module Setup

**Files:**
- Modify: `settings.gradle.kts:27-37`
- Modify: `gradle/libs.versions.toml:1-36`
- Modify: `utilities-bom/build.gradle.kts:8-18`
- Create: `utilities-minestom/build.gradle.kts`

**Interfaces:**
- Consumes: `:common`
- Produces: `:utilities-minestom` module build configuration and BOM constraints

- [ ] **Step 1: Update `gradle/libs.versions.toml` with Minestom version and library alias**

In `gradle/libs.versions.toml`:
```toml
[versions]
minestom = "2026.08.16-26.2"
# ... existing versions

[libraries]
minestom = { module = "net.minestom:minestom", version.ref = "minestom" }
# ... existing libraries
```

- [ ] **Step 2: Add `:utilities-minestom` to `settings.gradle.kts`**

In `settings.gradle.kts`:
```kotlin
include("utilities-minestom")
```

- [ ] **Step 3: Add constraint to `utilities-bom/build.gradle.kts`**

In `utilities-bom/build.gradle.kts`:
```kotlin
dependencies {
    constraints {
        api(project(":common"))
        api(project(":utilities-bukkit"))
        api(project(":utilities-paper"))
        api(project(":utilities-minestom"))
        api(project(":config"))
        api(project(":db-core"))
        api(project(":db-paper"))
        api(project(":math"))
        api(project(":registry"))
    }
}
```

- [ ] **Step 4: Create `utilities-minestom/build.gradle.kts`**

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
    api(project(":common"))
    compileOnly(libs.minestom)

    testImplementation(libs.minestom)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
}
```

- [ ] **Step 5: Run `./gradlew :utilities-minestom:dependencies` to verify configuration**

Run: `./gradlew :utilities-minestom:dependencies --configuration compileClasspath`
Expected: Output shows `:common` and `net.minestom:minestom`.

- [ ] **Step 6: Commit**

```bash
git add settings.gradle.kts gradle/libs.versions.toml utilities-bom/build.gradle.kts utilities-minestom/build.gradle.kts
git commit -m "build(utilities-minestom): configure Minestom module and BOM constraints"
```

---

### Task 2: Math, Coordinate & Collision Adapters

**Files:**
- Create: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomLocationWrapper.java`
- Create: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomPositionWrapper.java`
- Create: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomBoundingBoxWrapper.java`
- Create: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomAdapters.java`
- Create: `utilities-minestom/src/test/java/org/aincraft/minestom/adapter/MinestomAdaptersCoordinateTest.java`

**Interfaces:**
- Consumes: `org.aincraft.common.location.Location`, `Position`, `BoundingBox`, `Point`, `Pos`, `Vec`
- Produces: `MinestomAdapters.adapt(Instance, Pos)`, `MinestomAdapters.toMinestomPos(Location)`, `MinestomAdapters.adapt(Point)`, `MinestomAdapters.toMinestomVec(Position)`, `MinestomAdapters.adapt(BoundingBox)`, `MinestomAdapters.toMinestom(BoundingBox)`

- [ ] **Step 1: Write the failing unit tests for coordinate & bounding box adapters**

In `utilities-minestom/src/test/java/org/aincraft/minestom/adapter/MinestomAdaptersCoordinateTest.java`:
```java
package org.aincraft.minestom.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import org.aincraft.common.location.BoundingBox;
import org.aincraft.common.location.Location;
import org.aincraft.common.location.Position;
import org.aincraft.common.world.World;
import org.junit.jupiter.api.Test;

class MinestomAdaptersCoordinateTest {

  @Test
  void testPositionConversion() {
    Vec vec = new Vec(1.5, 2.5, 3.5);
    Position pos = MinestomAdapters.adapt(vec);

    assertEquals(1.5, pos.x());
    assertEquals(2.5, pos.y());
    assertEquals(3.5, pos.z());
    assertEquals(1, pos.blockX());
    assertEquals(2, pos.blockY());
    assertEquals(3, pos.blockZ());

    Vec back = MinestomAdapters.toMinestomVec(pos);
    assertEquals(vec, back);
  }

  @Test
  void testLocationConversion() {
    Instance instance = mock(Instance.class);
    when(instance.getUniqueId()).thenReturn(UUID.randomUUID());
    Pos pos = new Pos(10.0, 64.0, -20.0, 90.0f, 45.0f);

    Location loc = MinestomAdapters.adapt(instance, pos);
    assertEquals(10.0, loc.x());
    assertEquals(64.0, loc.y());
    assertEquals(-20.0, loc.z());
    assertEquals(90.0f, loc.yaw());
    assertEquals(45.0f, loc.pitch());
    assertNotNull(loc.world());

    Pos back = MinestomAdapters.toMinestomPos(loc);
    assertEquals(10.0, back.x());
    assertEquals(64.0, back.y());
    assertEquals(-20.0, back.z());
    assertEquals(90.0f, back.yaw());
    assertEquals(45.0f, back.pitch());
  }

  @Test
  void testBoundingBoxConversion() {
    net.minestom.server.collision.BoundingBox minestomBox =
        new net.minestom.server.collision.BoundingBox(0.6, 1.8, 0.6);
    BoundingBox box = MinestomAdapters.adapt(minestomBox);

    assertEquals(minestomBox.minX(), box.minX(), 1e-6);
    assertEquals(minestomBox.minY(), box.minY(), 1e-6);
    assertEquals(minestomBox.minZ(), box.minZ(), 1e-6);
    assertEquals(minestomBox.maxX(), box.maxX(), 1e-6);
    assertEquals(minestomBox.maxY(), box.maxY(), 1e-6);
    assertEquals(minestomBox.maxZ(), box.maxZ(), 1e-6);

    net.minestom.server.collision.BoundingBox back = MinestomAdapters.toMinestom(box);
    assertEquals(minestomBox.minX(), back.minX(), 1e-6);
    assertEquals(minestomBox.maxX(), back.maxX(), 1e-6);
  }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :utilities-minestom:test`
Expected: Compilation failure due to missing classes.

- [ ] **Step 3: Implement `MinestomPositionWrapper`, `MinestomLocationWrapper`, `MinestomBoundingBoxWrapper`, and `MinestomAdapters`**

Implement wrappers delegating coordinate and bounding box calculations, and scaffold initial `MinestomAdapters.java`.

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :utilities-minestom:test --tests MinestomAdaptersCoordinateTest`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add utilities-minestom/src/main/java/ utilities-minestom/src/test/java/
git commit -m "feat(utilities-minestom): implement coordinate, position, and bounding box adapters"
```

---

### Task 3: Block, BlockState, BlockType & BlockFace Adapters

**Files:**
- Create: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomBlockTypeWrapper.java`
- Create: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomBlockStateWrapper.java`
- Create: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomBlockWrapper.java`
- Modify: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomAdapters.java`
- Create: `utilities-minestom/src/test/java/org/aincraft/minestom/adapter/MinestomAdaptersBlockTest.java`

**Interfaces:**
- Consumes: `org.aincraft.common.block.BlockFace`, `BlockState`, `BlockType`, `org.aincraft.common.world.Block`, `net.minestom.server.instance.block.Block`
- Produces: `MinestomAdapters.adapt(BlockFace)`, `MinestomAdapters.toMinestom(BlockFace)`, `MinestomAdapters.adapt(Block)`, `MinestomAdapters.adaptState(Block)`, `MinestomAdapters.adapt(Instance, int, int, int)`

- [ ] **Step 1: Write the failing block and block face tests**

In `utilities-minestom/src/test/java/org/aincraft/minestom/adapter/MinestomAdaptersBlockTest.java`:
```java
package org.aincraft.minestom.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.aincraft.common.block.BlockFace;
import org.aincraft.common.block.BlockState;
import org.aincraft.common.block.BlockType;
import org.junit.jupiter.api.Test;

class MinestomAdaptersBlockTest {

  @Test
  void testBlockFaceMapping() {
    for (BlockFace face : BlockFace.values()) {
      net.minestom.server.instance.block.BlockFace minestomFace = MinestomAdapters.toMinestom(face);
      assertNotNull(minestomFace);
      assertEquals(face, MinestomAdapters.adapt(minestomFace));
    }
  }

  @Test
  void testBlockTypeAndState() {
    Block stone = Block.STONE;
    BlockType type = MinestomAdapters.adapt(stone);
    assertEquals("minecraft:stone", type.key().asString());

    BlockState state = MinestomAdapters.adaptState(stone);
    assertEquals(type, state.type());
    assertNotNull(state.asString());

    Block back = MinestomAdapters.toMinestom(state);
    assertEquals(stone, back);
  }

  @Test
  void testBlockWrapper() {
    Instance instance = mock(Instance.class);
    when(instance.getBlock(1, 2, 3)).thenReturn(Block.OAK_LOG);

    org.aincraft.common.world.Block block = MinestomAdapters.adapt(instance, 1, 2, 3);
    assertEquals(1, block.x());
    assertEquals(2, block.y());
    assertEquals(3, block.z());
    assertEquals("minecraft:oak_log", block.type().key().asString());
  }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :utilities-minestom:test --tests MinestomAdaptersBlockTest`
Expected: FAIL.

- [ ] **Step 3: Implement `MinestomBlockTypeWrapper`, `MinestomBlockStateWrapper`, `MinestomBlockWrapper`, and `MinestomAdapters` block methods**

Implement block wrappers and bidirectional conversions.

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :utilities-minestom:test --tests MinestomAdaptersBlockTest`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add utilities-minestom/src/main/java/ utilities-minestom/src/test/java/
git commit -m "feat(utilities-minestom): implement block, state, type, and block face adapters"
```

---

### Task 4: Chunk & World Adapters

**Files:**
- Create: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomChunkWrapper.java`
- Create: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomWorldWrapper.java`
- Modify: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomAdapters.java`
- Create: `utilities-minestom/src/test/java/org/aincraft/minestom/adapter/MinestomAdaptersWorldTest.java`

**Interfaces:**
- Consumes: `org.aincraft.common.world.World`, `Chunk`, `net.minestom.server.instance.Instance`, `net.minestom.server.instance.Chunk`
- Produces: `MinestomAdapters.adapt(Instance)`, `MinestomAdapters.toMinestom(World)`, `MinestomAdapters.adapt(Chunk)`, `MinestomAdapters.toMinestom(Chunk)`

- [ ] **Step 1: Write the failing world and chunk tests**

In `utilities-minestom/src/test/java/org/aincraft/minestom/adapter/MinestomAdaptersWorldTest.java`:
```java
package org.aincraft.minestom.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.minestom.server.instance.Instance;
import net.minestom.server.world.DimensionType;
import org.aincraft.common.world.World;
import org.junit.jupiter.api.Test;

class MinestomAdaptersWorldTest {

  @Test
  void testWorldWrapperPropertiesAndAudience() {
    Instance instance = mock(Instance.class);
    UUID uuid = UUID.randomUUID();
    DimensionType dim = mock(DimensionType.class);
    when(dim.minY()).thenReturn(-64);
    when(dim.height()).thenReturn(384);

    when(instance.getUniqueId()).thenReturn(uuid);
    when(instance.getDimension()).thenReturn(dim);
    when(instance.getTime()).thenReturn(1000L);
    when(instance.getWorldAge()).thenReturn(5000L);
    when(instance.getPlayers()).thenReturn(Collections.emptyList());
    when(instance.getEntities()).thenReturn(Collections.emptyList());

    World world = MinestomAdapters.adapt(instance);
    assertEquals(uuid, world.uid());
    assertEquals(-64, world.minHeight());
    assertEquals(320, world.maxHeight());
    assertEquals(1000L, world.time());
    assertEquals(5000L, world.fullTime());

    Component msg = Component.text("Hello World");
    world.sendMessage(msg);
    verify(instance).sendMessage(msg);

    assertSame(instance, MinestomAdapters.toMinestom(world));
  }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :utilities-minestom:test --tests MinestomAdaptersWorldTest`
Expected: FAIL.

- [ ] **Step 3: Implement `MinestomChunkWrapper`, `MinestomWorldWrapper`, and `MinestomAdapters` world methods**

Implement world and chunk wrappers with Kyori Adventure Audience passthrough.

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :utilities-minestom:test --tests MinestomAdaptersWorldTest`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add utilities-minestom/src/main/java/ utilities-minestom/src/test/java/
git commit -m "feat(utilities-minestom): implement world and chunk adapter wrappers"
```

---

### Task 5: Entity & Player Adapters

**Files:**
- Create: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomEntityWrapper.java`
- Create: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomPlayerWrapper.java`
- Modify: `utilities-minestom/src/main/java/org/aincraft/minestom/adapter/MinestomAdapters.java`
- Create: `utilities-minestom/src/test/java/org/aincraft/minestom/adapter/MinestomAdaptersEntityPlayerTest.java`

**Interfaces:**
- Consumes: `org.aincraft.common.entity.Entity`, `Player`, `net.minestom.server.entity.Entity`, `net.minestom.server.entity.Player`
- Produces: `MinestomAdapters.adapt(Entity)`, `MinestomAdapters.toMinestom(Entity)`, `MinestomAdapters.adapt(Player)`, `MinestomAdapters.toMinestom(Player)`

- [ ] **Step 1: Write the failing entity & player adapter tests**

In `utilities-minestom/src/test/java/org/aincraft/minestom/adapter/MinestomAdaptersEntityPlayerTest.java`:
```java
package org.aincraft.minestom.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import org.junit.jupiter.api.Test;

class MinestomAdaptersEntityPlayerTest {

  @Test
  void testPlayerWrapperDelegationAndAudience() {
    Player minestomPlayer = mock(Player.class);
    UUID uuid = UUID.randomUUID();
    Instance instance = mock(Instance.class);
    when(instance.getUniqueId()).thenReturn(UUID.randomUUID());

    when(minestomPlayer.getUuid()).thenReturn(uuid);
    when(minestomPlayer.getUsername()).thenReturn("Steve");
    when(minestomPlayer.isOnline()).thenReturn(true);
    when(minestomPlayer.getLatency()).thenReturn(42);
    when(minestomPlayer.getHealth()).thenReturn(20.0f);
    when(minestomPlayer.getMaxHealth()).thenReturn(20.0f);
    when(minestomPlayer.getFood()).thenReturn(20);
    when(minestomPlayer.getFoodSaturation()).thenReturn(5.0f);
    when(minestomPlayer.getLevel()).thenReturn(30);
    when(minestomPlayer.getExp()).thenReturn(0.5f);
    when(minestomPlayer.getGameMode()).thenReturn(GameMode.SURVIVAL);
    when(minestomPlayer.isSneaking()).thenReturn(false);
    when(minestomPlayer.isSprinting()).thenReturn(true);
    when(minestomPlayer.isFlying()).thenReturn(false);
    when(minestomPlayer.getInstance()).thenReturn(instance);
    when(minestomPlayer.getPosition()).thenReturn(new Pos(0, 64, 0));
    when(minestomPlayer.getVelocity()).thenReturn(new Vec(0, 0, 0));

    org.aincraft.common.entity.Player player = MinestomAdapters.adapt(minestomPlayer);
    assertEquals("Steve", player.username());
    assertTrue(player.isOnline());
    assertEquals(42, player.ping());
    assertEquals(20.0, player.health());
    assertEquals(20.0, player.maxHealth());
    assertEquals("minecraft:survival", player.gameMode().asString());
    assertTrue(player.isSprinting());
    assertFalse(player.isSneaking());

    player.setFlying(true);
    verify(minestomPlayer).setFlying(true);

    Component kickReason = Component.text("Disconnected");
    player.kick(kickReason);
    verify(minestomPlayer).kick(kickReason);

    Component chat = Component.text("Hello!");
    player.sendMessage(chat);
    verify(minestomPlayer).sendMessage(chat);

    assertSame(minestomPlayer, MinestomAdapters.toMinestom(player));
  }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :utilities-minestom:test --tests MinestomAdaptersEntityPlayerTest`
Expected: FAIL.

- [ ] **Step 3: Implement `MinestomEntityWrapper` and `MinestomPlayerWrapper`**

Implement `MinestomEntityWrapper` and `MinestomPlayerWrapper` with full property delegation and direct Kyori Adventure Audience pass-through.

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :utilities-minestom:test --tests MinestomAdaptersEntityPlayerTest`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add utilities-minestom/src/main/java/ utilities-minestom/src/test/java/
git commit -m "feat(utilities-minestom): implement entity and player adapter wrappers"
```

---

### Task 6: Full Verification, Jar Isolation & Integration Suite

**Files:**
- Create: `utilities-minestom/src/test/java/org/aincraft/minestom/adapter/MinestomAdaptersTest.java`

**Interfaces:**
- Consumes: All `MinestomAdapters` and wrapper classes
- Produces: Complete integration verification and jar isolation enforcement across the workspace

- [ ] **Step 1: Write the combined suite `MinestomAdaptersTest`**

Ensure `MinestomAdaptersTest` covers all bidirectional methods and null-handling edge cases in one centralized test class.

- [ ] **Step 2: Run full build and checks on `:utilities-minestom`**

Run: `./gradlew :utilities-minestom:check`
Expected: `verifyJarIsolation`, `verifyNoBukkitImports`, `verifyNoPaperImports`, and all unit tests PASS.

- [ ] **Step 3: Run full workspace build and test suite**

Run: `./gradlew check`
Expected: All 11 workspace submodules pass compile, isolation checks, and unit tests cleanly.

- [ ] **Step 4: Commit**

```bash
git add utilities-minestom/src/test/java/
git commit -m "test(utilities-minestom): add comprehensive test suite and verify jar isolation"
```
