package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.attribute.Attribute;
import org.aincraft.api.domain.attribute.AttributeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public final class BukkitAttributeRegistry
    implements AttributeRegistry, org.aincraft.api.domain.attribute.Attributes {

  @Override
  public @Nullable Attribute get(@NotNull Key key) {
    org.bukkit.attribute.Attribute attribute =
        org.bukkit.Registry.ATTRIBUTE.get(org.bukkit.NamespacedKey.fromString(key.asString()));
    return attribute == null ? null : BukkitAdapters.adaptAttribute(attribute);
  }

  @Override
  public @NotNull Collection<@NotNull Attribute> values() {
    return org.bukkit.Registry.ATTRIBUTE.stream().map(BukkitAdapters::adaptAttribute).toList();
  }
}
