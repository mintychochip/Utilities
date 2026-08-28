package org.aincraft.api.domain.inventory;

/** Item metadata carrying a durability value. */
public interface DamageableItemMeta extends ItemMeta {

  int damage();

  void setDamage(int damage);

  int maxDamage();

  boolean hasDamage();
}
