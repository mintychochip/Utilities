package org.aincraft.minestom.adapter;

import org.aincraft.api.domain.inventory.EquipmentSlot;
import org.aincraft.api.domain.inventory.InventoryHolder;
import org.aincraft.api.domain.inventory.ItemStack;
import org.aincraft.api.domain.inventory.PlayerInventory;
import org.aincraft.api.domain.location.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MinestomPlayerInventoryWrapper extends MinestomInventoryWrapper
    implements PlayerInventory {

  private final net.minestom.server.inventory.PlayerInventory inventory;
  private final InventoryHolder holder;

  public MinestomPlayerInventoryWrapper(
      @NotNull net.minestom.server.inventory.PlayerInventory inventory,
      @Nullable InventoryHolder holder) {
    super(inventory, holder);
    this.inventory = Objects.requireNonNull(inventory, "inventory cannot be null");
    this.holder = holder;
  }

  public @NotNull net.minestom.server.inventory.PlayerInventory getMinestomPlayerInventory() {
    return inventory;
  }

  @Override
  public @Nullable Location location() {
    return holder instanceof MinestomPlayerWrapper player ? player.location() : null;
  }

  @Override
  public @Nullable InventoryHolder holder() {
    return holder;
  }

  @Override
  public @Nullable ItemStack helmet() {
    return getItem(EquipmentSlot.HEAD);
  }

  @Override
  public void setHelmet(@Nullable ItemStack helmet) {
    setItem(EquipmentSlot.HEAD, helmet);
  }

  @Override
  public @Nullable ItemStack chestplate() {
    return getItem(EquipmentSlot.CHEST);
  }

  @Override
  public void setChestplate(@Nullable ItemStack chestplate) {
    setItem(EquipmentSlot.CHEST, chestplate);
  }

  @Override
  public @Nullable ItemStack leggings() {
    return getItem(EquipmentSlot.LEGS);
  }

  @Override
  public void setLeggings(@Nullable ItemStack leggings) {
    setItem(EquipmentSlot.LEGS, leggings);
  }

  @Override
  public @Nullable ItemStack boots() {
    return getItem(EquipmentSlot.FEET);
  }

  @Override
  public void setBoots(@Nullable ItemStack boots) {
    setItem(EquipmentSlot.FEET, boots);
  }

  @Override
  public @Nullable ItemStack itemInMainHand() {
    return getItem(EquipmentSlot.HAND);
  }

  @Override
  public void setItemInMainHand(@Nullable ItemStack item) {
    setItem(EquipmentSlot.HAND, item);
  }

  @Override
  public @Nullable ItemStack itemInOffHand() {
    return getItem(EquipmentSlot.OFF_HAND);
  }

  @Override
  public void setItemInOffHand(@Nullable ItemStack item) {
    setItem(EquipmentSlot.OFF_HAND, item);
  }

  @Override
  public @NotNull java.util.Collection<@Nullable ItemStack> armorContents() {
    return java.util.Arrays.asList(boots(), leggings(), chestplate(), helmet());
  }

  @Override
  public void setArmorContents(@NotNull java.util.Collection<@Nullable ItemStack> items) {
    Objects.requireNonNull(items, "items cannot be null");
    if (items.size() != 4) {
      throw new IllegalArgumentException("Expected four armor items, got " + items.size());
    }
    java.util.Iterator<@Nullable ItemStack> iterator = items.iterator();
    setBoots(iterator.next());
    setLeggings(iterator.next());
    setChestplate(iterator.next());
    setHelmet(iterator.next());
  }

  @Override
  public @NotNull java.util.Collection<@Nullable ItemStack> extraContents() {
    return java.util.Collections.singletonList(itemInOffHand());
  }

  @Override
  public void setExtraContents(@NotNull java.util.Collection<@Nullable ItemStack> items) {
    Objects.requireNonNull(items, "items cannot be null");
    if (items.size() > 1) {
      throw new IllegalArgumentException("Player inventory has one extra slot");
    }
    setItemInOffHand(items.isEmpty() ? null : items.iterator().next());
  }

  @Override
  public @Nullable ItemStack getItem(@NotNull EquipmentSlot slot) {
    Objects.requireNonNull(slot, "slot cannot be null");
    if (!(holder instanceof MinestomPlayerWrapper player)) return null;
    return fromMinestom(player.getMinestomPlayer().getEquipment(toMinestomSlot(slot)));
  }

  @Override
  public void setItem(@NotNull EquipmentSlot slot, @Nullable ItemStack item) {
    Objects.requireNonNull(slot, "slot cannot be null");
    if (!(holder instanceof MinestomPlayerWrapper player)) {
      throw new IllegalStateException("Player inventory has no Minestom player holder");
    }
    player
        .getMinestomPlayer()
        .setEquipment(
            toMinestomSlot(slot),
            item == null
                ? net.minestom.server.item.ItemStack.AIR
                : MinestomAdapters.toMinestom(item));
  }

  @Override
  public int heldItemSlot() {
    return holder instanceof MinestomPlayerWrapper player
        ? player.getMinestomPlayer().getHeldSlot()
        : 0;
  }

  @Override
  public void setHeldItemSlot(int slot) {
    if (slot < 0 || slot > 8)
      throw new IllegalArgumentException("Held slot must be between 0 and 8");
    if (holder instanceof MinestomPlayerWrapper player) {
      player.getMinestomPlayer().setHeldItemSlot((byte) slot);
    } else {
      throw new IllegalStateException("Player inventory has no Minestom player holder");
    }
  }

  private static net.minestom.server.entity.EquipmentSlot toMinestomSlot(EquipmentSlot slot) {
    return switch (slot) {
      case HAND -> net.minestom.server.entity.EquipmentSlot.MAIN_HAND;
      case OFF_HAND -> net.minestom.server.entity.EquipmentSlot.OFF_HAND;
      case FEET -> net.minestom.server.entity.EquipmentSlot.BOOTS;
      case LEGS -> net.minestom.server.entity.EquipmentSlot.LEGGINGS;
      case CHEST -> net.minestom.server.entity.EquipmentSlot.CHESTPLATE;
      case HEAD -> net.minestom.server.entity.EquipmentSlot.HELMET;
      case BODY -> net.minestom.server.entity.EquipmentSlot.BODY;
      case SADDLE -> net.minestom.server.entity.EquipmentSlot.SADDLE;
    };
  }

  private static @Nullable ItemStack fromMinestom(net.minestom.server.item.ItemStack item) {
    return item == null || item.isAir() || item.amount() <= 0 ? null : MinestomAdapters.adapt(item);
  }
}
