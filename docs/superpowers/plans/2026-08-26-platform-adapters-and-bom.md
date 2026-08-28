# Platform Adapters (`utilities-bukkit`, `utilities-paper`) & Bill of Materials (`utilities-bom`) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement platform adapter submodules `:utilities-bukkit` and `:utilities-paper` bridging live server objects to `:common` interfaces, and create `:utilities-bom` managing library versions across all published modules.

**Architecture:** 
- `:utilities-bom` publishes a Maven BOM (`java-platform`) declaring constraints for all published modules.
- `:utilities-bukkit` provides live delegating wrappers and a bidirectional `BukkitAdapters` facade, bridging Adventure `Audience` to Bukkit methods via `LegacyComponentSerializer`.
- `:utilities-paper` extends `:utilities-bukkit` and overrides audience/identity handling with Paper's native Adventure implementation.

**Tech Stack:** Java 25, Gradle 8.12 (`java-platform`, `java-library`, `maven-publish`), Paper API (`26.2.build.119-stable`), Adventure API / Legacy Serializer (`4.18.0`), JUnit 5 (`5.11.3`), Mockito (`5.14.2`).

## Global Constraints

- Java 25 toolchain across all submodules.
- `:utilities-bukkit` and `:utilities-paper` must use package isolation prefixes (`org/aincraft/bukkit/` and `org/aincraft/paper/`).
- Zero concrete backings in `:common` (all implementations live in adapter modules).
- Full unit test coverage for adapter mapping and audience dispatch.

---

### Task 1: Bill of Materials (`:utilities-bom`) & Gradle Versions

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `settings.gradle.kts`
- Create: `utilities-bom/build.gradle.kts`

- [ ] **Step 1: Update `gradle/libs.versions.toml` with adventure legacy serializer**

Add `adventure-text-serializer-legacy`:
```toml
adventure-text-serializer-legacy = { module = "net.kyori:adventure-text-serializer-legacy", version.ref = "adventure" }
```

- [ ] **Step 2: Update `settings.gradle.kts`**

Include `utilities-bom`, `utilities-bukkit`, and `utilities-paper`:
```kotlin
include("utilities-bom")
include("utilities-bukkit")
include("utilities-paper")
```

- [ ] **Step 3: Create `utilities-bom/build.gradle.kts`**

```kotlin
plugins {
    `java-platform`
    `maven-publish`
}

apply(from = rootProject.file("gradle/publish-conventions.gradle.kts"))

dependencies {
    constraints {
        api(project(":utilities-api"))
        api(project(":utilities-common"))
        api(project(":utilities-bukkit"))
        api(project(":utilities-paper"))
    }
}
```

- [ ] **Step 4: Verify BOM task in Gradle**

Run: `./gradlew :utilities-bom:tasks`
Expected: SUCCESS.

- [ ] **Step 5: Commit**

```bash
git add gradle/libs.versions.toml settings.gradle.kts utilities-bom/build.gradle.kts
git commit -m "feat: add utilities-bom submodule with java-platform constraints"
```

---

### Task 2: Implement `:utilities-bukkit` Platform Adapter

**Files:**
- Create: `utilities-bukkit/build.gradle.kts`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitAdapters.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitWorldWrapper.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitChunkWrapper.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitBlockWrapper.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitEntityWrapper.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitPlayerWrapper.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitLocationWrapper.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitPositionWrapper.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitBoundingBoxWrapper.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitBlockTypeWrapper.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitBlockStateWrapper.java`
- Test: `utilities-bukkit/src/test/java/org/aincraft/bukkit/adapter/BukkitAdaptersTest.java`

- [ ] **Step 1: Create `utilities-bukkit/build.gradle.kts`**

```kotlin
plugins {
    `java-library`
    `maven-publish`
}

extra["allowedAincraftPrefixes"] = listOf("org/aincraft/bukkit/")
extra["forbiddenAincraftPrefixes"] = listOf(
    "org/aincraft/paper/",
    "org/aincraft/config/",
    "org/aincraft/db/",
    "org/aincraft/math/",
    "org/aincraft/registry/",
)
extra["paperFree"] = true

apply(from = rootProject.file("gradle/java-conventions.gradle.kts"))
apply(from = rootProject.file("gradle/publish-conventions.gradle.kts"))

dependencies {
    api(project(":utilities-common"))
    api(libs.adventure.text.serializer.legacy)
    compileOnly(libs.spigot.api)

    testImplementation(libs.spigot.api)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
}
```

- [ ] **Step 2: Write failing unit test `BukkitAdaptersTest`**
- [ ] **Step 3: Implement wrapper classes and `BukkitAdapters`**
- [ ] **Step 4: Run tests to verify pass (`./gradlew :utilities-bukkit:check`)**
- [ ] **Step 5: Commit**

```bash
git add utilities-bukkit/
git commit -m "feat(utilities-bukkit): implement live Bukkit adapter wrappers and facade"
```

---

### Task 3: Implement `:utilities-paper` Platform Adapter

**Files:**
- Create: `utilities-paper/build.gradle.kts`
- Create: `utilities-paper/src/main/java/org/aincraft/paper/adapter/PaperAdapters.java`
- Create: `utilities-paper/src/main/java/org/aincraft/paper/adapter/PaperPlayerWrapper.java`
- Create: `utilities-paper/src/main/java/org/aincraft/paper/adapter/PaperWorldWrapper.java`
- Test: `utilities-paper/src/test/java/org/aincraft/paper/adapter/PaperAdaptersTest.java`

- [ ] **Step 1: Create `utilities-paper/build.gradle.kts`**

```kotlin
plugins {
    `java-library`
    `maven-publish`
}

extra["allowedAincraftPrefixes"] = listOf("org/aincraft/paper/")
extra["forbiddenAincraftPrefixes"] = listOf(
    "org/aincraft/config/",
    "org/aincraft/db/",
    "org/aincraft/math/",
    "org/aincraft/registry/",
)

apply(from = rootProject.file("gradle/java-conventions.gradle.kts"))
apply(from = rootProject.file("gradle/publish-conventions.gradle.kts"))

dependencies {
    api(project(":utilities-bukkit"))
    compileOnly(libs.paper.api)

    testImplementation(libs.paper.api)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
}
```

- [ ] **Step 2: Write failing unit test `PaperAdaptersTest`**
- [ ] **Step 3: Implement `PaperPlayerWrapper`, `PaperWorldWrapper`, and `PaperAdapters`**
- [ ] **Step 4: Run tests to verify pass (`./gradlew :utilities-paper:check`)**
- [ ] **Step 5: Commit**

```bash
git add utilities-paper/
git commit -m "feat(utilities-paper): implement Paper specialized adapter wrappers"
```

---

### Task 4: Full Multi-Project Build & Verification

- [ ] **Step 1: Run `./gradlew clean test check` across all modules**
- [ ] **Step 2: Verify zero package/jar isolation violations**
- [ ] **Step 3: Final commit**
