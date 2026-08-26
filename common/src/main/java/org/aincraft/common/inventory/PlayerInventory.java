package org.aincraft.common.inventory;

import org.jetbrains.annotations.Nullable;

public interface PlayerInventory extends Inventory {

  @Nullable ItemStack helmet();

  void setHelmet(@Nullable ItemStack helmet);

  @Nullable ItemStack chestplate();

  void setChestplate(@Nullable ItemStack chestplate);

  @Nullable ItemStack leggings();

  void setLeggings(@Nullable ItemStack leggings);

  @Nullable ItemStack boots();

  void setBoots(@Nullable ItemStack boots);

  @Nullable ItemStack itemInMainHand();

  void setItemInMainHand(@Nullable ItemStack item);

  @Nullable ItemStack itemInOffHand();

  void setItemInOffHand(@Nullable ItemStack item);

  int heldItemSlot();

  void setHeldItemSlot(int slot);
}
