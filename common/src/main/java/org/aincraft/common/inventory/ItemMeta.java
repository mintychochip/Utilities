package org.aincraft.common.inventory;

import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import org.aincraft.common.effect.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemMeta {

  @Nullable Component displayName();

  void setDisplayName(@Nullable Component name);

  @Nullable List<Component> lore();

  void setLore(@Nullable List<Component> lore);

  boolean hasDisplayName();

  boolean hasLore();

  boolean isUnbreakable();

  void setUnbreakable(boolean unbreakable);

  int customModelData();

  void setCustomModelData(int data);

  @NotNull Map<Enchantment, Integer> enchantments();

  boolean hasEnchant(@NotNull Enchantment enchantment);

  int enchantLevel(@NotNull Enchantment enchantment);

  void addEnchant(@NotNull Enchantment enchantment, int level, boolean ignoreLevelRestriction);

  void removeEnchant(@NotNull Enchantment enchantment);
}
