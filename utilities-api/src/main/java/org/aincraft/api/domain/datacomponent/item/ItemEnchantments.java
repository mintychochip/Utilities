package org.aincraft.api.domain.datacomponent.item;

import org.aincraft.api.domain.effect.Enchantment;

import java.util.Map;

/** Common contract for item enchantments, mirroring Paper's {@code ItemEnchantments}. */
public interface ItemEnchantments {

  Map<Enchantment, Integer> enchantments();
}
