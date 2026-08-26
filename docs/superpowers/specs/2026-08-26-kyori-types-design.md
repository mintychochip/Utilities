# Kyori/Adventure Type Adoption in `:common`

## Goal
Replace custom `:common` types with nearest Adventure/Kyori equivalents where the semantics match, keeping `:common` Bukkit/Paper-free and aligning it with the existing Kyori-first design.

## Scope
- `org.aincraft.common.datacomponent.Color`
- `org.aincraft.common.effect.Sound`
- `org.aincraft.common.effect.SoundCategory`
- `org.aincraft.common.effect.Particle`
- `org.aincraft.common.effect.Biome`
- `org.aincraft.common.attribute.Attribute`

Out of scope: types that carry significant domain data beyond an identifier (e.g., `Enchantment`, `PotionEffectType`, `ItemType`, `BlockType`, `EquipmentSlot`, `ItemFlag`, `PotionEffect`, `Vector3d`, `Location`, etc.).

## Decisions

### 1. `Color` → `net.kyori.adventure.text.format.TextColor`
- All `Color` usages become `TextColor`.
- This drops the alpha channel from `Color`, `MapItemColor`, and `DyedItemColor` per the user’s “lose them for now” direction.
- `MapItemColor.color()` and `DyedItemColor.color()` return `TextColor`.
- `CustomModelData.colors()`, `FireworkEffect.colors()`/`fadeColors()`, and `PotionContents.customColor()`/`computeEffectiveColor()` return `TextColor`.

### 2. `Sound` → `net.kyori.adventure.sound.Sound.Type`
- `World.playSound(...)` uses `Sound.Type` for the sound name.
- `JukeboxPlayable.song()` remains `Key` (it already is).

### 3. `SoundCategory` → `net.kyori.adventure.sound.Sound.Source`
- Enum values map as follows:
  - `MASTER` → `MASTER`
  - `MUSIC` → `MUSIC`
  - `RECORDS` → `RECORD`
  - `WEATHER` → `WEATHER`
  - `BLOCKS` → `BLOCK`
  - `HOSTILE` → `HOSTILE`
  - `NEUTRAL` → `NEUTRAL`
  - `PLAYERS` → `PLAYER`
  - `AMBIENT` → `AMBIENT`
  - `VOICE` → `VOICE`
  - `UI` exists in Adventure but not in the current enum.

### 4. `Attribute` / `Biome` → `net.kyori.adventure.key.Key`
- These are pure registry identifiers.
- `Attributable`, `AttributeInstance`, `AttributeModifier`, `ItemAttributeModifiers`, and `Block.setBiome()`/`Block.biome()` switch to `Key`.

### 5. `Particle` stays as a custom `Key` subtype
- Adventure has no particle type, but `Particle.dataType()` is required for typed-particle payload validation in platform adapters (see `BukkitAdapters`, `BukkitParticleWrapper`, `BukkitWorldWrapper`, `BukkitAdaptersTest`).
- `Particle` becomes `interface Particle extends net.kyori.adventure.key.Key { @NotNull Class<?> dataType(); }`.
- `World.spawnParticle(...)` continues to take `Particle`, which is also a `Key`.
- `BukkitAdapters.toBukkit(Particle)` uses `particle.key()` to resolve the Bukkit `org.bukkit.Particle` and `particle.dataType()` to validate/select the correct spawn overload.
- Remove the existing `org.aincraft.common.effect.Particle extends Keyed` and replace it with the `Key`-based version.

## Constraints
- `:common` remains `bukkitFree` / `paperFree`; only Adventure types are introduced.
- No new custom wrapper classes for the replaced types, except that `Particle` is retained as a `Key` subtype because it carries platform-specific data-type metadata.
- `DataComponentTypes` and related data-component classes in `:common` stay unchanged except for value types listed above.

## Verification
- `:common:compileJava` passes.
- `:common:verifyNoBukkitImports`, `:common:verifyNoPaperImports`, `:common:verifyNoPaperOnCompileClasspath` pass.
- Existing `DataComponentTypesTest` passes or is updated to match the new types.
