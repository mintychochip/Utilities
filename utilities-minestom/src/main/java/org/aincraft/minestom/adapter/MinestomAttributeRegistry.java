package org.aincraft.minestom.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.attribute.Attribute;
import org.aincraft.api.domain.attribute.AttributeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public final class MinestomAttributeRegistry
    implements AttributeRegistry, org.aincraft.api.domain.attribute.Attributes {

  @Override
  public @Nullable Attribute get(@NotNull Key key) {
    net.minestom.server.entity.attribute.Attribute attribute =
        net.minestom.server.entity.attribute.Attribute.fromKey(key);
    return attribute == null ? null : MinestomAdapters.adapt(attribute);
  }

  @Override
  public @NotNull Collection<@NotNull Attribute> values() {
    return net.minestom.server.entity.attribute.Attribute.values().stream()
        .map(MinestomAdapters::adapt)
        .toList();
  }
}
