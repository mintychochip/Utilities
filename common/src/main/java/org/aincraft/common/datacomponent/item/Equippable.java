package org.aincraft.common.datacomponent.item;

import java.util.Set;
import net.kyori.adventure.key.Key;
import org.aincraft.common.inventory.EquipmentSlot;

/**
 * Common contract for equippable item properties, mirroring Paper's {@code Equippable}.
 */
public interface Equippable {

  EquipmentSlot slot();

  Key equipSound();

  Key assetId();

  Key cameraOverlay();

  Set<Key> allowedEntities();

  boolean dispensable();

  boolean swappable();

  boolean damageOnHurt();

  boolean equipOnInteract();

  boolean canBeSheared();

  Key shearSound();
}
