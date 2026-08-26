package org.aincraft.common.datacomponent.item;

import java.util.Map;
import org.aincraft.common.effect.Enchantment;

/**
 * Common contract for item enchantments, mirroring Paper's {@code ItemEnchantments}.
 */
public interface ItemEnchantments {

  Map<Enchantment, Integer> enchantments();
}
