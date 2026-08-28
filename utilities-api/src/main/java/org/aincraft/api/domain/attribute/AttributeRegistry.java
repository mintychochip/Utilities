package org.aincraft.api.domain.attribute;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * A registry providing {@link Attribute} instances by key. Corresponds to the attribute registry
 * accessible via {@code
 * io.papermc.paper.registry.RegistryAccess.getRegistry(RegistryKey.ATTRIBUTE)} in Paper and the
 * Bukkit {@code Registry.ATTRIBUTE} equivalent.
 *
 * @see Attributes
 */
public interface AttributeRegistry {

  /** Looks up an attribute by its key, or {@code null} if not registered. */
  @Nullable
  Attribute get(@NotNull Key key);

  default @Nullable Attribute get(@NotNull String key) {
    return get(Key.key(key));
  }

  /** Returns all registered attributes. */
  @NotNull
  Collection<@NotNull Attribute> values();
}
