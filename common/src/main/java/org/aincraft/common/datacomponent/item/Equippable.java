package org.aincraft.common.datacomponent.item;

import net.kyori.adventure.key.Key;
import org.aincraft.common.inventory.EquipmentSlot;

import java.util.Set;

/** Common contract for equippable item properties, mirroring Paper's {@code Equippable}. */
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
