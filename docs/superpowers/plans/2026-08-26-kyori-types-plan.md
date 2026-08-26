# Kyori/Adventure Type Adoption Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the scoped custom `:common` types (`Color`, `Attribute`, `Biome`, `Sound`, `SoundCategory`) with nearest Kyori/Adventure types, and turn `Particle` into a `Key` subtype with its `dataType()` contract intact.

**Architecture:** Keep `:common` Bukkit/Paper-free. Replace custom interfaces with Kyori interfaces (`TextColor`, `Key`, `Sound.Type`, `Sound.Source`) and adjust platform adapters/tests. `Particle` becomes `interface Particle extends Key { Class<?> dataType(); }` so platform adapters keep payload validation.

**Tech Stack:** Java 21, Gradle 8.10, Kyori Adventure 5.2.0, Bukkit/Paper platform adapters.

## Global Constraints
- `:common` must remain `bukkitFree` and `paperFree`.
- No `org.bukkit` imports in `:common`; platform-specific logic stays in `utilities-bukkit`/`utilities-paper`.
- `DataComponentTypes` catalog must continue to compile and reference the correct value types.
- `DataComponentTypesTest` must pass.

---

### Task 1: `Color` → `net.kyori.adventure.text.format.TextColor`

**Files:**
- Delete: `common/src/main/java/org/aincraft/common/datacomponent/Color.java`
- Modify: `common/src/main/java/org/aincraft/common/datacomponent/item/CustomModelData.java`
- Modify: `common/src/main/java/org/aincraft/common/datacomponent/item/FireworkEffect.java`
- Modify: `common/src/main/java/org/aincraft/common/datacomponent/item/DyedItemColor.java`
- Modify: `common/src/main/java/org/aincraft/common/datacomponent/item/MapItemColor.java`
- Modify: `common/src/main/java/org/aincraft/common/datacomponent/potion/PotionContents.java`
- Modify: `common/src/main/java/org/aincraft/common/datacomponent/DataComponentTypes.java` (if any `Color.class` references exist)

**Interfaces:**
- `CustomModelData.colors()` returns `List<TextColor>`.
- `FireworkEffect.colors()` / `fadeColors()` return `List<TextColor>`.
- `DyedItemColor.color()` returns `TextColor`.
- `MapItemColor.color()` returns `TextColor`.
- `PotionContents.customColor()` and `computeEffectiveColor()` return `TextColor`.

- [ ] **Step 1.1: Delete `Color.java`**

```bash
git rm common/src/main/java/org/aincraft/common/datacomponent/Color.java
```

- [ ] **Step 1.2: Update `CustomModelData.java`**

```java
package org.aincraft.common.datacomponent.item;

import java.util.List;
import net.kyori.adventure.text.format.TextColor;

public interface CustomModelData {
  List<TextColor> colors();
}
```

- [ ] **Step 1.3: Update `FireworkEffect.java`**

```java
package org.aincraft.common.datacomponent.item;

import java.util.List;
import net.kyori.adventure.text.format.TextColor;

public interface FireworkEffect {
  List<TextColor> colors();
  List<TextColor> fadeColors();
  boolean trail();
  boolean flicker();
  FireworkEffect.Type type();

  enum Type { ... }
}
```

- [ ] **Step 1.4: Update `DyedItemColor.java`**

```java
package org.aincraft.common.datacomponent.item;

import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public interface DyedItemColor {
  @NotNull TextColor color();
}
```

- [ ] **Step 1.5: Update `MapItemColor.java`**

```java
package org.aincraft.common.datacomponent.item;

import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public interface MapItemColor {
  @NotNull TextColor color();
}
```

- [ ] **Step 1.6: Update `PotionContents.java`**

```java
package org.aincraft.common.datacomponent.potion;

import net.kyori.adventure.text.format.TextColor;

public interface PotionContents {
  TextColor customColor();
  TextColor computeEffectiveColor();
  List<PotionEffect> customEffects();
  PotionEffectType potion();
  int getPotionDurationScale();
}
```

- [ ] **Step 1.7: Compile check**

Run: `./gradlew :common:compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 1.8: Commit**

```bash
git add -u
git commit -m "refactor: replace Color with TextColor"
```

---

### Task 2: `Attribute` → `net.kyori.adventure.key.Key`

**Files:**
- Delete: `common/src/main/java/org/aincraft/common/attribute/Attribute.java`
- Modify: `common/src/main/java/org/aincraft/common/attribute/Attributable.java`
- Modify: `common/src/main/java/org/aincraft/common/attribute/AttributeInstance.java`
- Modify: `common/src/main/java/org/aincraft/common/datacomponent/item/attribute/ItemAttributeModifiers.java`
- Modify: `common/src/main/java/org/aincraft/common/inventory/ItemMeta.java`
- Modify: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitAdapters.java`
- Modify: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitAttributeInstanceWrapper.java`
- Modify: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitAttributeWrapper.java`
- Modify: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitItemMetaWrapper.java`
- Modify: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitLivingEntityWrapper.java`
- Modify: `utilities-paper/src/main/java/org/aincraft/paper/adapter/PaperAdapters.java`

**Interfaces:**
- `Attributable.getAttribute(Key)`
- `Attributable.getAttributeValue(Key)`
- `Attributable.setAttributeBaseValue(Key, double)`
- `AttributeInstance.attribute()` returns `Key`
- `ItemAttributeModifiers.Entry.attribute()` returns `Key`
- `ItemMeta.attributeModifiers()` returns `Map<Key, Collection<AttributeModifier>>`
- `ItemMeta.getAttributeModifiers(Key)`, `addAttributeModifier(Key, ...)`, etc.

- [ ] **Step 2.1: Delete `Attribute.java`**

```bash
git rm common/src/main/java/org/aincraft/common/attribute/Attribute.java
```

- [ ] **Step 2.2: Update `Attributable.java`**

```java
package org.aincraft.common.attribute;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Attributable {
  @Nullable AttributeInstance getAttribute(@NotNull Key attribute);

  default double getAttributeValue(@NotNull Key attribute) {
    AttributeInstance inst = getAttribute(attribute);
    return inst != null ? inst.value() : 0.0;
  }

  default void setAttributeBaseValue(@NotNull Key attribute, double value) {
    AttributeInstance inst = getAttribute(attribute);
    if (inst != null) {
      inst.setBaseValue(value);
    }
  }
}
```

- [ ] **Step 2.3: Update `AttributeInstance.java`**

```java
package org.aincraft.common.attribute;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public interface AttributeInstance {
  @NotNull Key attribute();
  double baseValue();
  void setBaseValue(double baseValue);
  double value();
}
```

- [ ] **Step 2.4: Update `ItemAttributeModifiers.java`**

```java
package org.aincraft.common.datacomponent.item.attribute;

import java.util.List;
import net.kyori.adventure.key.Key;
import org.aincraft.common.attribute.AttributeModifier;
import org.aincraft.common.datacomponent.item.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;

public interface ItemAttributeModifiers {
  @NotNull List<Entry> modifiers();

  interface Entry {
    @NotNull Key attribute();
    @NotNull AttributeModifier modifier();
    @NotNull EquipmentSlotGroup group();
    @NotNull AttributeModifierDisplay display();
  }
}
```

- [ ] **Step 2.5: Update `ItemMeta.java`**

```java
// Change imports and signatures:
import net.kyori.adventure.key.Key;

@NotNull Map<Key, Collection<AttributeModifier>> attributeModifiers();
@Nullable Collection<AttributeModifier> getAttributeModifiers(@NotNull Key attribute);
void addAttributeModifier(@NotNull Key attribute, @NotNull AttributeModifier modifier);
void removeAttributeModifier(@NotNull Key attribute);
void removeAttributeModifier(@NotNull Key attribute, @NotNull AttributeModifier modifier);
```

- [ ] **Step 2.6: Update Bukkit adapters**

Replace `Attribute` type parameters and parameters with `Key` in `BukkitAttributeWrapper`, `BukkitAttributeInstanceWrapper`, `BukkitLivingEntityWrapper`, `BukkitItemMetaWrapper`, and `BukkitAdapters` factory methods.

- [ ] **Step 2.7: Update Paper adapters**

Mirror the `Attribute` → `Key` changes in `PaperAdapters` and any `Paper*` wrappers.

- [ ] **Step 2.8: Compile check**

Run: `./gradlew :common:compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 2.9: Commit**

```bash
git add -u
git commit -m "refactor: replace Attribute with Key"
```

---

### Task 3: `Biome` → `net.kyori.adventure.key.Key`

**Files:**
- Delete: `common/src/main/java/org/aincraft/common/effect/Biome.java`
- Modify: `common/src/main/java/org/aincraft/common/world/Block.java`
- Modify: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitAdapters.java`
- Modify: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitBiomeWrapper.java`
- Modify: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitBlockWrapper.java`

**Interfaces:**
- `Block.biome()` returns `Key`
- `Block.setBiome(@NotNull Key)`

- [ ] **Step 3.1: Delete `Biome.java`**

```bash
git rm common/src/main/java/org/aincraft/common/effect/Biome.java
```

- [ ] **Step 3.2: Update `Block.java`**

```java
import net.kyori.adventure.key.Key;

@NotNull Key biome();
void setBiome(@NotNull Key biome);
```

- [ ] **Step 3.3: Update Bukkit adapters**

Change `Biome` parameters/returns to `Key` in `BukkitBiomeWrapper`, `BukkitBlockWrapper`, and `BukkitAdapters`.

- [ ] **Step 3.4: Compile check**

Run: `./gradlew :common:compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3.5: Commit**

```bash
git add -u
git commit -m "refactor: replace Biome with Key"
```

---

### Task 4: `Sound` → `net.kyori.adventure.sound.Sound.Type`

**Files:**
- Delete: `common/src/main/java/org/aincraft/common/effect/Sound.java`
- Modify: `common/src/main/java/org/aincraft/common/world/World.java`
- Modify: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitAdapters.java`
- Modify: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitSoundWrapper.java`
- Modify: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitWorldWrapper.java`

**Interfaces:**
- `World.playSound(Location, Sound.Type, float, float)` (default)
- `World.playSound(Location, Sound.Type, Sound.Source, float, float)` (abstract)

- [ ] **Step 4.1: Delete `Sound.java`**

```bash
git rm common/src/main/java/org/aincraft/common/effect/Sound.java
```

- [ ] **Step 4.2: Update `World.java`**

```java
import net.kyori.adventure.sound.Sound;

default void playSound(@NotNull Location location, @NotNull Sound.Type sound, float volume, float pitch) {
  playSound(location, sound, null, volume, pitch);
}

void playSound(@NotNull Location location, @NotNull Sound.Type sound, @Nullable Sound.Source source, float volume, float pitch);
```

- [ ] **Step 4.3: Update `BukkitSoundWrapper`**

Change `BukkitSoundWrapper implements Sound.Type` and implement `key()`.

- [ ] **Step 4.4: Update `BukkitAdapters`**

Change `adapt(org.bukkit.Sound)` and `toBukkit(Sound.Type)` to use `Sound.Type`.

- [ ] **Step 4.5: Update `BukkitWorldWrapper.playSound`**

Resolve Bukkit `Sound` from `sound.key()` and Bukkit `SoundCategory` from `source`.

- [ ] **Step 4.6: Compile check**

Run: `./gradlew :common:compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4.7: Commit**

```bash
git add -u
git commit -m "refactor: replace Sound with Sound.Type"
```

---

### Task 5: `SoundCategory` → `net.kyori.adventure.sound.Sound.Source`

**Files:**
- Delete: `common/src/main/java/org/aincraft/common/effect/SoundCategory.java`
- Modify: `common/src/main/java/org/aincraft/common/world/World.java`
- Modify: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitAdapters.java`
- Modify: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitWorldWrapper.java`

**Interfaces:**
- `World.playSound(Location, Sound.Type, Sound.Source, float, float)`

- [ ] **Step 5.1: Delete `SoundCategory.java`**

```bash
git rm common/src/main/java/org/aincraft/common/effect/SoundCategory.java
```

- [ ] **Step 5.2: Update `World.java`**

Use `Sound.Source` instead of `SoundCategory`.

- [ ] **Step 5.3: Update `BukkitAdapters` and `BukkitWorldWrapper`**

Convert between `Sound.Source` and Bukkit `SoundCategory` using the value name mapping.

- [ ] **Step 5.4: Compile check**

Run: `./gradlew :common:compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5.5: Commit**

```bash
git add -u
git commit -m "refactor: replace SoundCategory with Sound.Source"
```

---

### Task 6: `Particle` → `Key` subtype with `dataType()`

**Files:**
- Modify: `common/src/main/java/org/aincraft/common/effect/Particle.java`
- Modify: `common/src/main/java/org/aincraft/common/world/World.java`
- Modify: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitAdapters.java`
- Modify: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitParticleWrapper.java`
- Modify: `utilities-bukkit/src/main/java/org/aincraft/bukkit/adapter/BukkitWorldWrapper.java`
- Modify: `utilities-bukkit/src/test/java/org/aincraft/bukkit/adapter/BukkitAdaptersTest.java`

**Interfaces:**
- `Particle extends Key { @NotNull Class<?> dataType(); }`
- `World.spawnParticle(Particle, Location, int, double, double, double, double)`

- [ ] **Step 6.1: Update `Particle.java`**

```java
package org.aincraft.common.effect;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public interface Particle extends Key {
  @NotNull Class<?> dataType();
}
```

- [ ] **Step 6.2: Update `World.java`**

Use `Particle` (which is a `Key`) instead of `org.aincraft.common.effect.Particle` — signature unchanged because `Particle` is still the type.

- [ ] **Step 6.3: Update `BukkitParticleWrapper`**

```java
package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.common.effect.Particle;
import org.jetbrains.annotations.NotNull;

public class BukkitParticleWrapper implements Particle {
  private final org.bukkit.Particle particle;
  private final Key key;

  public BukkitParticleWrapper(@NotNull org.bukkit.Particle particle) {
    this.particle = particle;
    this.key = Key.key(particle.getKey().toString());
  }

  public @NotNull org.bukkit.Particle getBukkitParticle() {
    return particle;
  }

  @Override public @NotNull Key key() { return key; }
  @Override public @NotNull String asString() { return key.asString(); }
  @Override public @NotNull String namespace() { return key.namespace(); }
  @Override public @NotNull String value() { return key.value(); }

  @Override public @NotNull Class<?> dataType() {
    return particle.getDataType();
  }
}
```

- [ ] **Step 6.4: Update `BukkitAdapters.toBukkit(Particle)`**

Resolve Bukkit `Particle` via `org.bukkit.Registry.PARTICLE_TYPE.get(NamespacedKey.fromString(particle.asString()))` and validate `dataType()` matches.

- [ ] **Step 6.5: Compile check**

Run: `./gradlew :common:compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 6.6: Commit**

```bash
git add -u
git commit -m "refactor: make Particle a Key subtype with dataType"
```

---

### Task 7: Update `:common` tests

**Files:**
- Modify: `common/src/test/java/org/aincraft/common/location/LocationTest.java`
- Modify: `common/src/test/java/org/aincraft/common/world/WorldChunkBlockTest.java`
- Modify: `common/src/test/java/org/aincraft/common/entity/EntityPlayerTest.java`
- Modify: `common/src/test/java/org/aincraft/common/inventory/DataComponentTypesTest.java`

- [ ] **Step 7.1: Update test stubs**

Replace `org.aincraft.common.effect.Sound` with `net.kyori.adventure.sound.Sound.Type` and `org.aincraft.common.effect.SoundCategory` with `net.kyori.adventure.sound.Sound.Source` in anonymous `World`/`Block` stubs.

Implement `setBiome(@NotNull Key)`, `biome()` returns `Key`, and `spawnParticle(@NotNull Particle, ...)` in stubs.

- [ ] **Step 7.2: Run `compileTestJava` and `test`**

Run: `./gradlew :common:compileTestJava :common:test`
Expected: All `:common` tests pass.

- [ ] **Step 7.3: Commit**

```bash
git add -u
git commit -m "test: update stubs for Kyori types"
```

---

### Task 8: Platform-specific adapter and test updates

**Files:**
- Modify all `Bukkit*` and `Paper*` wrappers/adapters affected by the type changes.
- Modify `utilities-bukkit/src/test/java/org/aincraft/bukkit/adapter/BukkitAdaptersTest.java`.

- [ ] **Step 8.1: Run `utilities-bukkit:compileTestJava` and `utilities-paper:compileJava`**

Run: `./gradlew :utilities-bukkit:compileTestJava :utilities-paper:compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 8.2: Run `utilities-bukkit:test`**

Run: `./gradlew :utilities-bukkit:test`
Expected: All tests pass.

- [ ] **Step 8.3: Commit**

```bash
git add -u
git commit -m "refactor: update Bukkit/Paper adapters for Kyori types"
```

---

### Task 9: Full build and free checks

- [ ] **Step 9.1: Run platform isolation checks**

Run: `./gradlew :common:verifyNoBukkitImports :common:verifyNoPaperImports :common:verifyNoPaperOnCompileClasspath`
Expected: BUILD SUCCESSFUL

- [ ] **Step 9.2: Run full build**

Run: `./gradlew clean test check generatePomFileForMavenPublication`
Expected: BUILD SUCCESSFUL

- [ ] **Step 9.3: Final commit if any fixes**

```bash
git add -u
git commit -m "fix: any remaining Kyori migration issues"
```

---

## Self-Review Checklist

- [ ] `Color` no longer exists in `:common`; all references use `TextColor`.
- [ ] `Attribute` no longer exists in `:common`; all references use `Key`.
- [ ] `Biome` no longer exists in `:common`; all references use `Key`.
- [ ] `Sound` no longer exists in `:common`; all references use `Sound.Type`.
- [ ] `SoundCategory` no longer exists in `:common`; all references use `Sound.Source`.
- [ ] `Particle` exists and `extends Key` with `dataType()`.
- [ ] No `org.aincraft.common.datacomponent.Color` or deleted type usage remains in `:common`.
- [ ] `:common:compileJava`, `:common:test`, `:common:check` pass.
- [ ] `:common:bukkitFree` and `:common:paperFree` checks pass.
