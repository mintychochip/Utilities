# Task: Trace Bukkit/Paper/Spigot API and extend common + adapters

You are a domain scout for the aincraft Utilities project.
The project provides platform-agnostic Minecraft abstractions in `:common` and live adapters in `:utilities-bukkit`, `:utilities-paper`, and `:utilities-minestom`.

## Constraints
- `:common` must contain only interfaces (no concrete backings).
- Method signatures should be domain-agnostic, preferring Kyori Adventure types (`Component`, `Key`, `Audience`) over Bukkit-specific types.
- Use `org.jetbrains.annotations.NotNull` / `Nullable` where applicable.
- Return types should map to common interfaces where possible (e.g. `org.aincraft.common.location.Location`, not `org.bukkit.Location`).
- Do NOT propose Bukkit-only types in `:common`.
- Adapters in `:utilities-bukkit` and `:utilities-paper` can use Bukkit/Paper types internally.

## Current `:common` packages
{
  "common.location": [
    "Vector3d",
    "Vector3i",
    "Position",
    "BoundingBox",
    "Location"
  ],
  "common.world": [
    "World",
    "Chunk",
    "Block",
    "Environment",
    "Difficulty",
    "GameMode",
    "WorldBorder",
    "RayTraceResult"
  ],
  "common.block": [
    "BlockType",
    "BlockState",
    "BlockFace"
  ],
  "common.entity": [
    "Entity",
    "Player",
    "Damageable",
    "LivingEntity",
    "Projectile",
    "ProjectileSource",
    "EntityType"
  ],
  "common.effect": [
    "PotionEffectType",
    "PotionEffect",
    "Enchantment",
    "Particle",
    "SoundCategory",
    "Biome"
  ],
  "common.inventory": [
    "ItemType",
    "ItemMeta",
    "ItemStack",
    "InventoryType",
    "EquipmentSlot",
    "InventoryHolder",
    "Inventory",
    "PlayerInventory",
    "DataComponentType"
  ],
  "common.server": [
    "CommandSender",
    "ConsoleCommandSender",
    "Server"
  ],
  "common.attribute": [
    "Attribute",
    "AttributeModifier",
    "AttributeInstance",
    "Attributable"
  ]
}

## API method signatures by domain
See `/home/jlo/dev/Utilities/docs/superpowers/context-api-trace.md` for the full extracted Bukkit/Paper/Spigot public API signatures.
Also see `/home/jlo/dev/Utilities/docs/superpowers/context-api-trace.json` for machine-readable form.

## Adapter coverage
- Bukkit wrappers: ['BukkitAdapters', 'BukkitAttributeInstanceWrapper', 'BukkitAttributeModifierWrapper', 'BukkitAttributeWrapper', 'BukkitBlockStateWrapper', 'BukkitBlockTypeWrapper', 'BukkitBlockWrapper', 'BukkitBoundingBoxWrapper', 'BukkitChunkWrapper', 'BukkitCommandSenderWrapper', 'BukkitConsoleCommandSenderWrapper', 'BukkitEntityWrapper', 'BukkitInventoryWrapper', 'BukkitItemMetaWrapper', 'BukkitItemStackWrapper', 'BukkitItemTypeWrapper', 'BukkitLivingEntityWrapper', 'BukkitLocationWrapper', 'BukkitPlayerInventoryWrapper', 'BukkitPlayerWrapper', 'BukkitPositionWrapper', 'BukkitServerWrapper', 'BukkitWorldBorderWrapper', 'BukkitWorldWrapper']
- Paper wrappers: ['PaperAdapters', 'PaperPlayerWrapper', 'PaperServerWrapper', 'PaperWorldWrapper']
- Minestom wrappers: ['MinestomAdapters', 'MinestomBlockStateWrapper', 'MinestomBlockTypeWrapper', 'MinestomBlockWrapper', 'MinestomBoundingBoxWrapper', 'MinestomChunkWrapper', 'MinestomEntityWrapper', 'MinestomLivingEntityWrapper', 'MinestomLocationWrapper', 'MinestomPlayerInventoryWrapper', 'MinestomPlayerWrapper', 'MinestomPositionWrapper', 'MinestomWorldBorderWrapper', 'MinestomWorldWrapper']

## Goal
For your assigned domain, identify the most important missing API interfaces/methods in `:common` that should be added to make the coverage more extensive.
For each proposed addition, specify:
1. Whether it belongs in a new interface or an existing one.
2. Exact Java method signature(s) for the `:common` interface.
3. Which Bukkit/Spigot/Paper class/method it maps from.
4. Whether a Bukkit adapter wrapper needs updating/creating.

Prioritize widely-used, domain-agnostic gameplay utilities. Skip niche event/scheduler/scoreboard/permissions plugin APIs unless they are trivial and clearly fit.
