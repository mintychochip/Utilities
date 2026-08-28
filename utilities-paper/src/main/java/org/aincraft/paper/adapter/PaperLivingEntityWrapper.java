package org.aincraft.paper.adapter;

import org.aincraft.api.domain.effect.PotionEffect;
import org.aincraft.api.domain.effect.PotionEffectType;
import org.aincraft.bukkit.adapter.BukkitAdapters;
import org.aincraft.bukkit.adapter.BukkitLivingEntityWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class PaperLivingEntityWrapper extends BukkitLivingEntityWrapper {

  public PaperLivingEntityWrapper(@NotNull org.bukkit.entity.LivingEntity entity) {
    super(entity);
  }

  @Override
  public @Nullable PotionEffect potionEffect(@NotNull PotionEffectType type) {
    org.bukkit.potion.PotionEffect effect =
        getBukkitLivingEntity().getPotionEffect(BukkitAdapters.toBukkit(type));
    return effect == null ? null : PaperAdapters.adapt(effect);
  }

  @Override
  public @NotNull Collection<? extends PotionEffect> activePotionEffects() {
    Collection<PotionEffect> result = new ArrayList<>();
    for (org.bukkit.potion.PotionEffect effect : getBukkitLivingEntity().getActivePotionEffects()) {
      result.add(PaperAdapters.adapt(effect));
    }
    return result;
  }
}
