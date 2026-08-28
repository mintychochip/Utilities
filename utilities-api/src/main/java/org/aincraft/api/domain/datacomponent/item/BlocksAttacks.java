package org.aincraft.api.domain.datacomponent.item;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.datacomponent.item.blocksattacks.DamageReduction;
import org.aincraft.api.domain.datacomponent.item.blocksattacks.ItemDamageFunction;

import java.util.List;
import java.util.Set;

/**
 * Common contract for the {@code BlocksAttacks} data component, mirroring Paper's {@code
 * BlocksAttacks}.
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
