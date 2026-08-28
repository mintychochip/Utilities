package org.aincraft.api.domain.effect;

import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.text.Component;
import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.aincraft.api.domain.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface Enchantment extends Keyed {

  int maxLevel();

  int startLevel();

  boolean isCursed();

  boolean isTreasure();

  boolean conflictsWith(Enchantment other);

  boolean canEnchant(ItemStack item);

  default @NotNull Component displayName(int level) {
    throw new UnsupportedCapabilityException(Capability.ENCHANTMENT_METADATA);
  }

  default @NotNull Component description() {
    throw new UnsupportedCapabilityException(Capability.ENCHANTMENT_METADATA);
  }

  default boolean isTradeable() {
    throw new UnsupportedCapabilityException(Capability.ENCHANTMENT_METADATA);
  }

  default boolean isDiscoverable() {
    throw new UnsupportedCapabilityException(Capability.ENCHANTMENT_METADATA);
  }
}
