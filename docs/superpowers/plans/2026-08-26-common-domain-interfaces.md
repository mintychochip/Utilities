# Common Domain Interfaces Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Create a domain-agnostic `:common` submodule providing pure interface types for Minecraft domain concepts (Location, Position, World, Chunk, Block, Entity, Player) with zero Bukkit/Paper dependencies.

**Architecture:** A standalone Gradle submodule `:common` publishing pure Java contracts. Integrates with standard ecosystem libraries (`net.kyori:adventure-api`, `net.kyori:adventure-key`, `org.jetbrains:annotations`) for keying and audience messaging, while keeping zero coupling to server platform internals.

**Tech Stack:** Java 25, Gradle 8.12, Adventure API (4.18.0), JetBrains Annotations (26.0.2), JUnit 5 (5.11.3).

## Global Constraints

- Absolutely no imports or dependencies on `org.bukkit.*` or `io.papermc.*` in `:common`.
- Target Java 25 toolchain.
- Pure interface / record contracts with sensible default methods and immutable value types.
- 100% test coverage of mathematical operations, coordinate transformations, and domain type contracts.

---

### Task 1: Scaffolding `:common` Submodule & Gradle Configuration

**Files:**
- Modify: `settings.gradle.kts`
- Create: `common/build.gradle.kts`

**Interfaces:**
- Produces: Gradle submodule `:common` capable of compilation and testing with Java 25 toolchain and Adventure API.

- [ ] **Step 1: Update `settings.gradle.kts` to include `:common`**

```kotlin
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "utilities"

include("common")
include("utilities")
include("test-plugin")
```

- [ ] **Step 2: Create `common/build.gradle.kts`**

```kotlin
plugins {
    id("signing")
    id("com.vanniktech.maven.publish") version "0.34.0"
    id("com.gradleup.nmcp") version "1.0.0"
    java
    `java-library`
    `maven-publish`
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    compileOnly("org.jetbrains:annotations:26.0.2")
    compileOnly("net.kyori:adventure-api:4.18.0")
    compileOnly("net.kyori:adventure-key:4.18.0")

    testImplementation(platform("org.junit:junit-bom:5.11.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.jetbrains:annotations:26.0.2")
    testImplementation("net.kyori:adventure-api:4.18.0")
    testImplementation("net.kyori:adventure-key:4.18.0")
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    pom {
        name.set("common")
        description.set("Domain agnostic interface types and contracts for minecraft")
        url.set("https://github.com/mintychochip/PacketBlocks")
        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        scm {
            url.set("https://github.com/mintychochip/PacketBlocks")
            connection.set("scm:git:https://github.com/mintychochip/PacketBlocks.git")
            developerConnection.set("scm:git:ssh://[email protected]:mintychochip/PacketBlocks.git")
        }
        developers {
            developer {
                id.set("mintychochip")
                name.set("mintychochip")
                email.set("[email protected]")
            }
        }
    }
}

signing {
    val key = providers.environmentVariable("SIGNING_KEY").orNull
    var password = providers.environmentVariable("SIGNING_PASSWORD").orNull
    if (key != null && password != null) {
        useInMemoryPgpKeys(key, password)
        sign(publishing.publications)
    } else {
        logger.warn("Signing disabled: SIGNING_KEY OR SIGNING_PASSWORD missing")
    }
}

nmcp {
    publishAllPublicationsToCentralPortal {
        username.set(
            providers.gradleProperty("mavenCentralUsername")
                .orElse(providers.environmentVariable("MAVEN_USERNAME"))
        )
        password.set(
            providers.gradleProperty("mavenCentralPassword")
                .orElse(providers.environmentVariable("MAVEN_PASSWORD"))
        )
        publishingType.set("AUTOMATIC")
    }
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            showStandardStreams = false
        }
    }
}
```

- [ ] **Step 3: Run Gradle check to verify project configuration**

Run: `./gradlew :common:tasks`
Expected: SUCCESS with `:common` tasks listed.

- [ ] **Step 4: Commit**

```bash
git add settings.gradle.kts common/build.gradle.kts
git commit -m "build: scaffold common submodule with gradle configuration"
```

---

### Task 2: Location & Math Domain Types

**Files:**
- Create: `common/src/main/java/org/aincraft/common/location/Position.java`
- Create: `common/src/main/java/org/aincraft/common/location/PositionImpl.java`
- Create: `common/src/main/java/org/aincraft/common/location/Vector3d.java`
- Create: `common/src/main/java/org/aincraft/common/location/Vector3i.java`
- Create: `common/src/main/java/org/aincraft/common/location/Location.java`
- Create: `common/src/main/java/org/aincraft/common/location/LocationImpl.java`
- Create: `common/src/main/java/org/aincraft/common/location/BoundingBox.java`
- Test: `common/src/test/java/org/aincraft/common/location/PositionTest.java`
- Test: `common/src/test/java/org/aincraft/common/location/LocationTest.java`
- Test: `common/src/test/java/org/aincraft/common/location/BoundingBoxTest.java`

**Interfaces:**
- Produces: `Position`, `Vector3d`, `Vector3i`, `Location<W>`, `BoundingBox`.

- [ ] **Step 1: Write failing unit tests for Position, Location, and BoundingBox**

`common/src/test/java/org/aincraft/common/location/PositionTest.java`:
```java
package org.aincraft.common.location;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PositionTest {
  @Test
  void testCoordinatesAndBlockConversion() {
    Position pos = Position.of(10.7, -5.2, 3.0);
    assertEquals(10.7, pos.x(), 1e-6);
    assertEquals(-5.2, pos.y(), 1e-6);
    assertEquals(3.0, pos.z(), 1e-6);

    assertEquals(10, pos.blockX());
    assertEquals(-6, pos.blockY()); // Math.floor(-5.2) == -6
    assertEquals(3, pos.blockZ());
  }

  @Test
  void testDistanceCalculations() {
    Position p1 = Position.of(0, 0, 0);
    Position p2 = Position.of(3, 4, 0);
    assertEquals(25.0, p1.distanceSquared(p2), 1e-6);
    assertEquals(5.0, p1.distance(p2), 1e-6);
  }

  @Test
  void testTransformations() {
    Position p = Position.of(1, 2, 3);
    Position pAdd = p.add(1, -1, 2);
    assertEquals(2, pAdd.x(), 1e-6);
    assertEquals(1, pAdd.y(), 1e-6);
    assertEquals(5, pAdd.z(), 1e-6);

    Position pSub = p.subtract(1, 1, 1);
    assertEquals(0, pSub.x(), 1e-6);
    assertEquals(1, pSub.y(), 1e-6);
    assertEquals(2, pSub.z(), 1e-6);

    Position pMul = p.multiply(2.5);
    assertEquals(2.5, pMul.x(), 1e-6);
    assertEquals(5.0, pMul.y(), 1e-6);
    assertEquals(7.5, pMul.z(), 1e-6);
  }

  @Test
  void testVectors() {
    Vector3d v3d = new Vector3d(1.0, 2.0, 2.0);
    assertEquals(9.0, v3d.lengthSquared(), 1e-6);
    assertEquals(3.0, v3d.length(), 1e-6);

    Vector3i v3i = new Vector3i(1, 2, 3);
    assertEquals(new Vector3i(2, 4, 6), v3i.add(new Vector3i(1, 2, 3)));
  }
}
```

`common/src/test/java/org/aincraft/common/location/LocationTest.java`:
```java
package org.aincraft.common.location;

import org.aincraft.common.world.World;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class LocationTest {
  @Test
  void testLocationCoordinatesAndOrientation() {
    World world = Mockito.mock(World.class);
    Location<World> loc = Location.of(world, 10.5, 64.0, -12.5, 90.0f, 45.0f);

    assertSame(world, loc.world());
    assertEquals(10.5, loc.x(), 1e-6);
    assertEquals(64.0, loc.y(), 1e-6);
    assertEquals(-12.5, loc.z(), 1e-6);
    assertEquals(10, loc.blockX());
    assertEquals(64, loc.blockY());
    assertEquals(-13, loc.blockZ());
    assertEquals(90.0f, loc.yaw(), 1e-6f);
    assertEquals(45.0f, loc.pitch(), 1e-6f);
  }

  @Test
  void testLocationWithModifications() {
    World world1 = Mockito.mock(World.class);
    World world2 = Mockito.mock(World.class);

    Location<World> loc = Location.of(world1, 0, 0, 0);
    Location<World> loc2 = loc.withPosition(Position.of(5, 10, 15));
    assertEquals(5, loc2.x(), 1e-6);

    Location<World> loc3 = loc.withOrientation(180f, -90f);
    assertEquals(180f, loc3.yaw(), 1e-6f);
    assertEquals(-90f, loc3.pitch(), 1e-6f);

    Location<World> loc4 = loc.withWorld(world2);
    assertSame(world2, loc4.world());
  }
}
```

`common/src/test/java/org/aincraft/common/location/BoundingBoxTest.java`:
```java
package org.aincraft.common.location;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoundingBoxTest {
  @Test
  void testContainsPoint() {
    BoundingBox box = new BoundingBox(0, 0, 0, 10, 10, 10);
    assertTrue(box.contains(5, 5, 5));
    assertTrue(box.contains(0, 0, 0));
    assertTrue(box.contains(10, 10, 10));
    assertFalse(box.contains(11, 5, 5));
    assertFalse(box.contains(-1, 5, 5));

    assertTrue(box.contains(Position.of(5, 5, 5)));
  }

  @Test
  void testIntersects() {
    BoundingBox box1 = new BoundingBox(0, 0, 0, 5, 5, 5);
    BoundingBox box2 = new BoundingBox(4, 4, 4, 8, 8, 8);
    BoundingBox box3 = new BoundingBox(6, 6, 6, 10, 10, 10);

    assertTrue(box1.intersects(box2));
    assertFalse(box1.intersects(box3));
  }

  @Test
  void testOfPositions() {
    Position p1 = Position.of(10, 20, 30);
    Position p2 = Position.of(0, 5, 40);
    BoundingBox box = BoundingBox.of(p1, p2);

    assertEquals(0, box.minX(), 1e-6);
    assertEquals(5, box.minY(), 1e-6);
    assertEquals(30, box.minZ(), 1e-6);
    assertEquals(10, box.maxX(), 1e-6);
    assertEquals(20, box.maxY(), 1e-6);
    assertEquals(40, box.maxZ(), 1e-6);
  }

  @Test
  void testInvalidBoundsThrows() {
    assertThrows(IllegalArgumentException.class, () -> new BoundingBox(10, 0, 0, 5, 0, 0));
  }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :common:test`
Expected: FAIL due to missing classes.

- [ ] **Step 3: Implement Position, Vector3d, Vector3i, Location, BoundingBox**

Implement classes in `common/src/main/java/org/aincraft/common/location/`.

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew :common:test`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add common/src/
git commit -m "feat(common): implement domain-agnostic location and vector math types"
```

---

### Task 3: World & Block Domain Types

**Files:**
- Create: `common/src/main/java/org/aincraft/common/world/World.java`
- Create: `common/src/main/java/org/aincraft/common/world/Chunk.java`
- Create: `common/src/main/java/org/aincraft/common/world/Block.java`
- Create: `common/src/main/java/org/aincraft/common/block/BlockType.java`
- Create: `common/src/main/java/org/aincraft/common/block/BlockTypeImpl.java`
- Create: `common/src/main/java/org/aincraft/common/block/BlockState.java`
- Test: `common/src/test/java/org/aincraft/common/world/WorldChunkBlockTest.java`

**Interfaces:**
- Produces: `World`, `Chunk`, `Block`, `BlockType`, `BlockState`.

- [ ] **Step 1: Write unit tests for World, Chunk, and Block contracts**

`common/src/test/java/org/aincraft/common/world/WorldChunkBlockTest.java`:
```java
package org.aincraft.common.world;

import net.kyori.adventure.key.Key;
import org.aincraft.common.block.BlockState;
import org.aincraft.common.block.BlockType;
import org.aincraft.common.location.Location;
import org.aincraft.common.location.Position;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WorldChunkBlockTest {
  @Test
  void testBlockTypeCreationAndKey() {
    Key stoneKey = Key.key("minecraft", "stone");
    BlockType stone = BlockType.of(stoneKey);
    assertEquals(stoneKey, stone.key());
  }

  @Test
  void testWorldBlockResolutionDefaults() {
    UUID uid = UUID.randomUUID();
    Key worldKey = Key.key("minecraft", "overworld");
    BlockType stoneType = BlockType.of(Key.key("minecraft", "stone"));
    BlockState state = () -> stoneType;

    World world = new World() {
      @Override public UUID uid() { return uid; }
      @Override public String name() { return "world"; }
      @Override public Key key() { return worldKey; }
      @Override public int minHeight() { return -64; }
      @Override public int maxHeight() { return 320; }
      @Override public boolean isChunkLoaded(int chunkX, int chunkZ) { return true; }
      @Override public Chunk getChunkAt(int chunkX, int chunkZ) { return null; }
      @Override
      public Block getBlockAt(int x, int y, int z) {
        return new Block() {
          @Override public World world() { return World.this; }
          @Override public Position position() { return Position.of(x, y, z); }
          @Override public BlockType type() { return stoneType; }
          @Override public BlockState state() { return state; }
        };
      }
    };

    Position pos = Position.of(12.3, 65.8, -4.2);
    Block block = world.getBlockAt(pos);
    assertEquals(12, block.x());
    assertEquals(65, block.y());
    assertEquals(-5, block.z());
    assertEquals(stoneType, block.type());

    Location<World> loc = Location.of(world, pos, 0f, 0f);
    Block locBlock = world.getBlockAt(loc);
    assertEquals(12, locBlock.x());
    assertEquals(65, locBlock.y());
    assertEquals(-5, locBlock.z());
  }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :common:test`
Expected: FAIL due to missing World/Block classes.

- [ ] **Step 3: Implement World, Chunk, Block, BlockType, BlockState**

Implement the interfaces in `common/src/main/java/org/aincraft/common/world/` and `common/src/main/java/org/aincraft/common/block/`.

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew :common:test`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add common/src/
git commit -m "feat(common): implement World, Chunk, Block, BlockType domain types"
```

---

### Task 4: Entity & Player Domain Types

**Files:**
- Create: `common/src/main/java/org/aincraft/common/entity/Entity.java`
- Create: `common/src/main/java/org/aincraft/common/entity/Player.java`
- Test: `common/src/test/java/org/aincraft/common/entity/EntityPlayerTest.java`

**Interfaces:**
- Produces: `Entity`, `Player` interfaces extending `Identified`, `Keyed`, and `Audience`.

- [ ] **Step 1: Write unit tests for Entity and Player**

`common/src/test/java/org/aincraft/common/entity/EntityPlayerTest.java`:
```java
package org.aincraft.common.entity;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.aincraft.common.location.Location;
import org.aincraft.common.location.Position;
import org.aincraft.common.world.World;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class EntityPlayerTest {
  @Test
  void testEntityAndPlayerContract() {
    UUID uuid = UUID.randomUUID();
    World world = Mockito.mock(World.class);
    Location<World> location = Location.of(world, 10, 64, 10);
    Key playerType = Key.key("minecraft", "player");
    AtomicBoolean messageSent = new AtomicBoolean(false);

    Player player = new Player() {
      @Override public UUID uniqueId() { return uuid; }
      @Override public String username() { return "Steve"; }
      @Override public boolean isOnline() { return true; }
      @Override public World world() { return world; }
      @Override public Location<World> location() { return location; }
      @Override public Key type() { return playerType; }
      @Override public boolean isValid() { return true; }
      @Override
      public void sendMessage(Component message) {
        messageSent.set(true);
      }
    };

    assertEquals(uuid, player.uniqueId());
    assertEquals(uuid, player.identity().uuid());
    assertEquals("Steve", player.username());
    assertTrue(player.isOnline());
    assertSame(world, player.world());
    assertEquals(playerType, player.key());

    player.sendMessage(Component.text("Hello World"));
    assertTrue(messageSent.get());
  }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :common:test`
Expected: FAIL due to missing Entity/Player classes.

- [ ] **Step 3: Implement Entity and Player interfaces**

Implement `Entity.java` and `Player.java` in `common/src/main/java/org/aincraft/common/entity/`.

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew :common:test`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add common/src/
git commit -m "feat(common): implement Entity and Player domain contracts"
```

---

### Task 5: Full Project Verification & Zero-Bukkit-Dependency Check

**Files:**
- Modify: `utilities/build.gradle.kts` (optional dependency `api(project(":utilities-common"))` if desired, or keep independent)

- [ ] **Step 1: Run comprehensive tests across the whole project**

Run: `./gradlew clean test`
Expected: All tests in `:common`, `:utilities`, and `:test-plugin` PASS.

- [ ] **Step 2: Verify zero Bukkit imports in `:common` via grep**

Run: `grep -rn "org.bukkit" common/src/main`
Expected: 0 matches.

- [ ] **Step 3: Final commit & cleanup**

```bash
git add .
git commit -m "chore: verify common module build and zero-bukkit boundaries"
```
