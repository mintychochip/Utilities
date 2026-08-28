package org.aincraft.api.domain.datacomponent.item.blocksattacks;

/** Common contract for an item damage function, mirroring Paper's {@code ItemDamageFunction}. */
public interface ItemDamageFunction {

  float threshold();

  float base();

  float factor();

  int damageToApply(float damage);
}
