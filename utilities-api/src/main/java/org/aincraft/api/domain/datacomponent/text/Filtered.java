package org.aincraft.api.domain.datacomponent.text;

/**
 * Common contract for a raw/filtered value pair, mirroring Paper's {@code Filtered} without
 * depending on Bukkit.
 *
 * @param <T> the value type
 */
public interface Filtered<T> {

  T raw();

  T filtered();
}
