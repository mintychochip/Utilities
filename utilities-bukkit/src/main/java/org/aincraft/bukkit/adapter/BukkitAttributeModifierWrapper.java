package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.common.attribute.AttributeModifier;
import org.aincraft.common.inventory.EquipmentSlot;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class BukkitAttributeModifierWrapper implements AttributeModifier {

  private final org.bukkit.attribute.AttributeModifier modifier;
  private final Key key;

  public BukkitAttributeModifierWrapper(@NotNull org.bukkit.attribute.AttributeModifier modifier) {
    this.modifier = Objects.requireNonNull(modifier, "modifier cannot be null");
    NamespacedKey nKey = modifier.getKey();
    this.key =
        nKey != null
            ? Key.key(nKey.getNamespace(), nKey.getKey())
            : Key.key("minecraft", modifier.getName().toLowerCase(Locale.ROOT));
  }

  public @NotNull org.bukkit.attribute.AttributeModifier getBukkitAttributeModifier() {
    return modifier;
  }

  @Override
  public @NotNull Key key() {
    return key;
  }

  @Override
  public @NotNull UUID id() {
    return modifier.getUniqueId();
  }

  @Override
  public @NotNull String name() {
    return modifier.getName();
  }

  @Override
  public double amount() {
    return modifier.getAmount();
  }

  @Override
  public @NotNull Operation operation() {
    return switch (modifier.getOperation()) {
      case ADD_NUMBER -> Operation.ADD_NUMBER;
      case ADD_SCALAR -> Operation.ADD_SCALAR;
      case MULTIPLY_SCALAR_1 -> Operation.MULTIPLY_SCALAR_1;
    };
  }

  @Override
  public @Nullable EquipmentSlot slot() {
    if (modifier.getSlot() == null) return null;
    return switch (modifier.getSlot()) {
      case HEAD -> EquipmentSlot.HEAD;
      case CHEST -> EquipmentSlot.CHEST;
      case LEGS -> EquipmentSlot.LEGS;
      case FEET -> EquipmentSlot.FEET;
      case HAND -> EquipmentSlot.HAND;
      case OFF_HAND -> EquipmentSlot.OFF_HAND;
      default -> null;
    };
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AttributeModifier that)) return false;
    return Objects.equals(key, that.key()) && Objects.equals(id(), that.id());
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, id());
  }

  @Override
  public String toString() {
    return "BukkitAttributeModifierWrapper{key="
        + key
        + ", id="
        + id()
        + ", name="
        + name()
        + ", amount="
        + amount()
        + "}";
  }
}
