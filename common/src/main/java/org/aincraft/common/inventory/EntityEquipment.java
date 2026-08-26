package org.aincraft.common.inventory;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface EntityEquipment {

  @Nullable ItemStack get(@NotNull EquipmentSlot slot);

  void set(@NotNull EquipmentSlot slot, @Nullable ItemStack item);

  @NotNull Collection<@NotNull ItemStack> armorContents();

  void setArmorContents(@NotNull Collection<@Nullable ItemStack> items);

  @Nullable ItemStack helmet();

  void setHelmet(@Nullable ItemStack item);

  @Nullable ItemStack chestplate();

  void setChestplate(@Nullable ItemStack item);

  @Nullable ItemStack leggings();

  void setLeggings(@Nullable ItemStack item);

  @Nullable ItemStack boots();

  void setBoots(@Nullable ItemStack item);

  @Nullable ItemStack itemInMainHand();

  void setItemInMainHand(@Nullable ItemStack item);

  @Nullable ItemStack itemInOffHand();

  void setItemInOffHand(@Nullable ItemStack item);
}
