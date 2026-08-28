package org.aincraft.minestom.adapter;

import org.aincraft.api.domain.inventory.EntityEquipment;
import org.aincraft.api.domain.inventory.EquipmentSlot;
import org.aincraft.api.domain.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public final class MinestomEntityEquipmentWrapper implements EntityEquipment {

  private final net.minestom.server.entity.LivingEntity livingEntity;

  public MinestomEntityEquipmentWrapper(
      @NotNull net.minestom.server.entity.LivingEntity livingEntity) {
    this.livingEntity = Objects.requireNonNull(livingEntity, "livingEntity cannot be null");
  }

  public @NotNull net.minestom.server.entity.LivingEntity getMinestomLivingEntity() {
    return livingEntity;
  }

  @Override
  public @Nullable ItemStack get(@NotNull EquipmentSlot slot) {
    Objects.requireNonNull(slot, "slot cannot be null");
    return fromMinestom(livingEntity.getEquipment(toMinestomSlot(slot)));
  }

  @Override
  public void set(@NotNull EquipmentSlot slot, @Nullable ItemStack item) {
    Objects.requireNonNull(slot, "slot cannot be null");
    livingEntity.setEquipment(
        toMinestomSlot(slot),
        item == null ? net.minestom.server.item.ItemStack.AIR : MinestomAdapters.toMinestom(item));
  }

  @Override
  public @NotNull Collection<@NotNull ItemStack> armorContents() {
    return Arrays.asList(boots(), leggings(), chestplate(), helmet());
  }

  @Override
  public void setArmorContents(@NotNull Collection<@Nullable ItemStack> items) {
    Objects.requireNonNull(items, "items cannot be null");
    if (items.size() != 4) throw new IllegalArgumentException("Expected four armor items");
    java.util.Iterator<@Nullable ItemStack> iterator = items.iterator();
    setBoots(iterator.next());
    setLeggings(iterator.next());
    setChestplate(iterator.next());
    setHelmet(iterator.next());
  }

  @Override
  public @Nullable ItemStack helmet() {
    return get(EquipmentSlot.HEAD);
  }

  @Override
  public void setHelmet(@Nullable ItemStack item) {
    set(EquipmentSlot.HEAD, item);
  }

  @Override
  public @Nullable ItemStack chestplate() {
    return get(EquipmentSlot.CHEST);
  }

  @Override
  public void setChestplate(@Nullable ItemStack item) {
    set(EquipmentSlot.CHEST, item);
  }

  @Override
  public @Nullable ItemStack leggings() {
    return get(EquipmentSlot.LEGS);
  }

  @Override
  public void setLeggings(@Nullable ItemStack item) {
    set(EquipmentSlot.LEGS, item);
  }

  @Override
  public @Nullable ItemStack boots() {
    return get(EquipmentSlot.FEET);
  }

  @Override
  public void setBoots(@Nullable ItemStack item) {
    set(EquipmentSlot.FEET, item);
  }

  @Override
  public @Nullable ItemStack itemInMainHand() {
    return get(EquipmentSlot.HAND);
  }

  @Override
  public void setItemInMainHand(@Nullable ItemStack item) {
    set(EquipmentSlot.HAND, item);
  }

  @Override
  public @Nullable ItemStack itemInOffHand() {
    return get(EquipmentSlot.OFF_HAND);
  }

  @Override
  public void setItemInOffHand(@Nullable ItemStack item) {
    set(EquipmentSlot.OFF_HAND, item);
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
