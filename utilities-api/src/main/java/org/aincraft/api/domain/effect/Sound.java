package org.aincraft.api.domain.effect;

import net.kyori.adventure.key.Keyed;

/**
 * Platform-agnostic keyed sound abstraction. Corresponds to {@code org.bukkit.Sound} in
 * Bukkit/Paper. Implementations are provided via adapters in {@code utilities-bukkit} and {@code
 * utilities-paper}; Minestom maps to its registry-backed sound type.
 */
public interface Sound extends Keyed {}
