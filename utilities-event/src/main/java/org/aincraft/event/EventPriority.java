package org.aincraft.event;

import org.jetbrains.annotations.ApiStatus.AvailableSince;

/**
 * Ordering for listeners.  Lower ordinal runs first.
 *
 * <p>Mirrors the familiar Bukkit/Paper ordering so existing mental models transfer:
 * LOWEST → LOW → NORMAL → HIGH → HIGHEST → MONITOR.</p>
 * <ul>
 *   <li>{@code LOWEST} … {@code HIGHEST}: normal mutation phases.</li>
 *   <li>{@code MONITOR}: read-only observation; should not mutate or cancel.</li>
 * </ul>
 */
@AvailableSince("2.0.0")
public enum EventPriority {
    LOWEST,
    LOW,
    NORMAL,
    HIGH,
    HIGHEST,
    MONITOR
}
