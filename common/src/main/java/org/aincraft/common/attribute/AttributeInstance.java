package org.aincraft.common.attribute;

import java.util.Collection;
import java.util.UUID;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AttributeInstance {

  @NotNull Key attribute();

  double baseValue();

  void setBaseValue(double value);

  double value();

  @NotNull Collection<? extends AttributeModifier> modifiers();

  void addModifier(@NotNull AttributeModifier modifier);

  void removeModifier(@NotNull AttributeModifier modifier);

  void removeModifier(@NotNull UUID id);

  @Nullable AttributeModifier getModifier(@NotNull UUID id);
}
