package org.aincraft.common.datacomponent.item;

import java.util.List;
import java.util.Set;
import net.kyori.adventure.key.Key;
import org.aincraft.common.datacomponent.item.blocksattacks.DamageReduction;
import org.aincraft.common.datacomponent.item.blocksattacks.ItemDamageFunction;

/**
 * Common contract for the {@code BlocksAttacks} data component, mirroring Paper's {@code BlocksAttacks}.
 */
public interface BlocksAttacks {

  float blockDelaySeconds();

  float disableCooldownScale();

  List<DamageReduction> damageReductions();

  ItemDamageFunction itemDamage();

  Set<Key> bypassedBy();

  Key blockSound();

  Key disableSound();
}
