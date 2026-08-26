package org.aincraft.common.inventory;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
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

  @NotNull Collection<@Nullable ItemStack> armorContents();

  void setArmorContents(@NotNull Collection<@Nullable ItemStack> items);

  @NotNull Collection<@Nullable ItemStack> extraContents();

  void setExtraContents(@NotNull Collection<@Nullable ItemStack> items);

  @Nullable ItemStack getItem(@NotNull EquipmentSlot slot);

  void setItem(@NotNull EquipmentSlot slot, @Nullable ItemStack item);

  int heldItemSlot();

  void setHeldItemSlot(int slot);
}
