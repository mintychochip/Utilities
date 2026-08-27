package org.aincraft.bukkit.adapter;

import net.kyori.adventure.key.Key;
import org.aincraft.common.effect.PotionEffect;
import org.aincraft.common.effect.PotionEffectType;
import org.aincraft.common.effect.PotionEffectTypeCategory;
import org.jetbrains.annotations.NotNull;

public class BukkitPotionEffectTypeWrapper implements PotionEffectType {

  private final org.bukkit.potion.PotionEffectType type;

  public BukkitPotionEffectTypeWrapper(@NotNull org.bukkit.potion.PotionEffectType type) {
    this.type = type;
  }

  public @NotNull org.bukkit.potion.PotionEffectType getBukkitPotionEffectType() {
    return type;
  }

  @Override
  public @NotNull Key key() {
    return Key.key(type.getKey().getNamespace(), type.getKey().getKey());
  }

  @Override
  public @NotNull String name() {
    return type.getName();
  }

  @Override
  public boolean isInstant() {
    return type.isInstant();
  }

  @Override
  public @NotNull PotionEffectTypeCategory category() {
    return switch (type.getCategory()) {
      case BENEFICIAL -> PotionEffectTypeCategory.BENEFICIAL;
      case HARMFUL -> PotionEffectTypeCategory.HARMFUL;
      case NEUTRAL -> PotionEffectTypeCategory.NEUTRAL;
    };
  }

  @Override
  public @NotNull PotionEffect createEffect(int duration, int amplifier) {
    return BukkitAdapters.adapt(type.createEffect(duration, amplifier));
  }
}
