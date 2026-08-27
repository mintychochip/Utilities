package org.aincraft.common.datacomponent.item;

import org.aincraft.common.effect.Enchantment;

import java.util.Map;

/** Common contract for item enchantments, mirroring Paper's {@code ItemEnchantments}. */
public interface ItemEnchantments {

  Map<Enchantment, Integer> enchantments();
}
