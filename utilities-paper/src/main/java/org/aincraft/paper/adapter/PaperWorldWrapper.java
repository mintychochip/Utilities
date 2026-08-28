package org.aincraft.paper.adapter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.aincraft.api.domain.world.Block;
import org.aincraft.bukkit.adapter.BukkitWorldWrapper;
import org.jetbrains.annotations.NotNull;

public class PaperWorldWrapper extends BukkitWorldWrapper {

  public PaperWorldWrapper(@NotNull org.bukkit.World world) {
    super(world);
  }

  @Override
  public void sendMessage(@NotNull Component message) {
    getBukkitWorld().sendMessage(message);
  }

  @Override
  public void sendActionBar(@NotNull Component message) {
    getBukkitWorld().sendActionBar(message);
  }

  @Override
  public void showTitle(@NotNull Title title) {
    getBukkitWorld().showTitle(title);
  }

  @Override
  public void clearTitle() {
    getBukkitWorld().clearTitle();
  }

  @Override
  public void resetTitle() {
    getBukkitWorld().resetTitle();
  }

  @Override
  public @NotNull Block getBlockAt(int x, int y, int z) {
    return PaperAdapters.adapt(getBukkitWorld().getBlockAt(x, y, z));
  }

  @Override
  public @NotNull org.aincraft.api.domain.world.WorldBorder worldBorder() {
    return new PaperWorldBorderWrapper(getBukkitWorld().getWorldBorder());
  }

  @Override
  public @NotNull java.util.Collection<? extends org.aincraft.api.domain.entity.Player> players() {
    return getBukkitWorld().getPlayers().stream().map(PaperAdapters::adapt).toList();
  }

  @Override
  public @NotNull java.util.Collection<? extends org.aincraft.api.domain.entity.Entity> entities() {
    return getBukkitWorld().getEntities().stream().map(PaperAdapters::adapt).toList();
  }

  @Override
  public @NotNull org.aincraft.api.domain.entity.Entity spawnEntity(
      @NotNull org.aincraft.api.domain.location.Location location,
      @NotNull net.kyori.adventure.key.Key entityType) {
    org.bukkit.entity.EntityType bukkitType =
        org.bukkit.Registry.ENTITY_TYPE.get(
            new org.bukkit.NamespacedKey(entityType.namespace(), entityType.value()));
    if (bukkitType == null) bukkitType = org.bukkit.entity.EntityType.fromName(entityType.value());
    if (bukkitType == null)
      throw new IllegalArgumentException("Unknown entity type: " + entityType);
    org.bukkit.entity.Entity entity =
        getBukkitWorld().spawnEntity(PaperAdapters.toBukkit(location), bukkitType);
    return PaperAdapters.adapt(entity);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends org.aincraft.api.domain.entity.Entity> @NotNull T spawn(
      @NotNull org.aincraft.api.domain.location.Location location, @NotNull Class<T> type) {
    if (type != org.aincraft.api.domain.entity.Entity.class
        && type != org.aincraft.api.domain.entity.LivingEntity.class
        && type != org.aincraft.api.domain.entity.Player.class) {
      throw new org.aincraft.api.UnsupportedCapabilityException(
          org.aincraft.api.Capability.ENTITY_SPAWN,
          "Paper class-based spawning supports Entity, LivingEntity, and Player only.");
    }
    Class<? extends org.bukkit.entity.LivingEntity> bukkitType =
        type == org.aincraft.api.domain.entity.Player.class
            ? org.bukkit.entity.Player.class
            : org.bukkit.entity.LivingEntity.class;
    org.bukkit.entity.LivingEntity entity =
        getBukkitWorld().spawn(PaperAdapters.toBukkit(location), bukkitType);
    return (T) PaperAdapters.adapt(entity);
  }

  @Override
  public @org.jetbrains.annotations.Nullable org.aincraft.api.domain.entity.Entity entity(
      @NotNull java.util.UUID uniqueId) {
    org.bukkit.entity.Entity entity = getBukkitWorld().getEntity(uniqueId);
    return entity == null ? null : PaperAdapters.adapt(entity);
  }

  @Override
  public String toString() {
    return "PaperWorldWrapper{name=" + name() + ", uid=" + uid() + "}";
  }
}
