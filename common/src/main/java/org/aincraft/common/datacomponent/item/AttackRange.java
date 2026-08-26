package org.aincraft.common.datacomponent.item;

/**
 * Common contract for the {@code AttackRange} data component, mirroring Paper's {@code AttackRange}.
 */
public interface AttackRange {

  float minReach();

  float maxReach();

  float minCreativeReach();

  float maxCreativeReach();

  float hitboxMargin();

  float mobFactor();
}
