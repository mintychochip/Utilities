package org.aincraft.bukkit.adapter;

import org.aincraft.api.domain.inventory.DamageableItemMeta;
import org.jetbrains.annotations.NotNull;

public final class BukkitDamageableItemMetaWrapper extends BukkitItemMetaWrapper
    implements DamageableItemMeta {

  public BukkitDamageableItemMetaWrapper(@NotNull org.bukkit.inventory.meta.Damageable meta) {
    super(meta);
  }

  private org.bukkit.inventory.meta.Damageable damageable() {
    return (org.bukkit.inventory.meta.Damageable) getBukkitItemMeta();
  }

  @Override
  public int damage() {
    return damageable().getDamage();
  }

  @Override
  public void setDamage(int damage) {
    damageable().setDamage(damage);
  }

  @Override
  public int maxDamage() {
    return damageable().getMaxDamage();
  }

  @Override
  public boolean hasDamage() {
    return damageable().hasDamage();
  }
}
