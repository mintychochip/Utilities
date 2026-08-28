package org.aincraft.paper.adapter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.aincraft.bukkit.adapter.BukkitPlayerWrapper;
import org.jetbrains.annotations.NotNull;

public class PaperPlayerWrapper extends BukkitPlayerWrapper {

  public PaperPlayerWrapper(@NotNull org.bukkit.entity.Player player) {
    super(player);
  }

  @Override
  public void sendMessage(@NotNull Component message) {
    getBukkitPlayer().sendMessage(message);
  }

  @Override
  public void sendActionBar(@NotNull Component message) {
    getBukkitPlayer().sendActionBar(message);
  }

  @Override
  public void showTitle(@NotNull Title title) {
    getBukkitPlayer().showTitle(title);
  }

  @Override
  public void clearTitle() {
    getBukkitPlayer().clearTitle();
  }

  @Override
  public void resetTitle() {
    getBukkitPlayer().resetTitle();
  }

  @Override
  public @NotNull Component displayName() {
    return getBukkitPlayer().displayName();
  }

  @Override
  public void displayName(@NotNull Component displayName) {
    getBukkitPlayer().displayName(displayName);
  }

  @Override
  public void sendEntityEffect(
      @NotNull org.aincraft.api.domain.effect.EntityEffect effect,
      @NotNull org.aincraft.api.domain.entity.Entity entity) {
    String name = effect.name();
    getBukkitPlayer()
        .sendEntityEffect(
            org.bukkit.EntityEffect.valueOf(name),
            org.aincraft.bukkit.adapter.BukkitAdapters.toBukkit(entity));
  }

  @Override
  public void kick(@NotNull Component reason) {
    getBukkitPlayer().kick(reason);
  }

  @Override
  public @org.jetbrains.annotations.Nullable org.aincraft.api.domain.effect.PotionEffect
      potionEffect(@NotNull org.aincraft.api.domain.effect.PotionEffectType type) {
    org.bukkit.potion.PotionEffect effect =
        getBukkitLivingEntity()
            .getPotionEffect(org.aincraft.bukkit.adapter.BukkitAdapters.toBukkit(type));
    return effect == null ? null : PaperAdapters.adapt(effect);
  }

  @Override
  public @NotNull java.util.Collection<? extends org.aincraft.api.domain.effect.PotionEffect>
      activePotionEffects() {
    return getBukkitLivingEntity().getActivePotionEffects().stream()
        .map(PaperAdapters::adapt)
        .toList();
  }

  @Override
  public void addPotionEffect(@NotNull org.aincraft.api.domain.effect.PotionEffect effect) {
    getBukkitLivingEntity()
        .addPotionEffect(org.aincraft.bukkit.adapter.BukkitAdapters.toBukkit(effect));
  }

  @Override
  public boolean addPotionEffect(
      @NotNull org.aincraft.api.domain.effect.PotionEffect effect, boolean force) {
    return getBukkitLivingEntity()
        .addPotionEffect(org.aincraft.bukkit.adapter.BukkitAdapters.toBukkit(effect), force);
  }

  @Override
  public boolean clearActivePotionEffects() {
    return getBukkitLivingEntity().clearActivePotionEffects();
  }

  @Override
  public void removePotionEffect(@NotNull org.aincraft.api.domain.effect.PotionEffectType type) {
    getBukkitLivingEntity()
        .removePotionEffect(org.aincraft.bukkit.adapter.BukkitAdapters.toBukkit(type));
  }

  @Override
  public boolean hasPotionEffect(@NotNull org.aincraft.api.domain.effect.PotionEffectType type) {
    return getBukkitLivingEntity()
        .hasPotionEffect(org.aincraft.bukkit.adapter.BukkitAdapters.toBukkit(type));
  }

  @Override
  public @NotNull org.aincraft.api.domain.inventory.PlayerInventory inventory() {
    return PaperAdapters.adapt(getBukkitPlayer().getInventory());
  }

  @Override
  public @NotNull org.aincraft.api.domain.inventory.InventoryView openInventory() {
    return PaperAdapters.adapt(getBukkitPlayer().getOpenInventory());
  }

  @Override
  public @NotNull org.aincraft.api.domain.inventory.InventoryView openInventory(
      @NotNull org.aincraft.api.domain.inventory.Inventory inventory) {
    getBukkitPlayer().openInventory(PaperAdapters.toBukkit(inventory));
    return openInventory();
  }

  @Override
  public String toString() {
    return "PaperPlayerWrapper{name=" + username() + ", uuid=" + uniqueId() + "}";
  }
}
