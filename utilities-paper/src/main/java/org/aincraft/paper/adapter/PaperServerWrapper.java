package org.aincraft.paper.adapter;

import net.kyori.adventure.text.Component;
import org.aincraft.api.domain.entity.Entity;
import org.aincraft.api.domain.inventory.Inventory;
import org.aincraft.api.domain.inventory.InventoryHolder;
import org.aincraft.bukkit.adapter.BukkitServerWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PaperServerWrapper extends BukkitServerWrapper {

  public PaperServerWrapper(@NotNull org.bukkit.Server server) {
    super(server);
  }

  @Override
  public void broadcast(@NotNull Component message) {
    getBukkitServer().broadcast(message);
  }

  @Override
  public int broadcast(@NotNull Component message, @NotNull String permission) {
    return getBukkitServer().broadcast(message, permission);
  }

  @Override
  public @NotNull String minecraftVersion() {
    return getBukkitServer().getMinecraftVersion();
  }

  @Override
  public int currentTick() {
    return getBukkitServer().getCurrentTick();
  }

  @Override
  public @NotNull Inventory createInventory(
      @org.jetbrains.annotations.Nullable InventoryHolder holder,
      int size,
      @NotNull Component title) {
    org.bukkit.inventory.Inventory inventory =
        getBukkitServer()
            .createInventory(
                holder == null ? null : org.aincraft.bukkit.adapter.BukkitAdapters.toBukkit(holder),
                size,
                title);
    return PaperAdapters.adapt(inventory);
  }

  @Override
  public @NotNull org.aincraft.api.domain.inventory.ItemFactory itemFactory() {
    return new PaperItemFactoryWrapper(getBukkitServer().getItemFactory());
  }

  @Override
  public @NotNull Component motd() {
    return getBukkitServer().motd();
  }

  @Override
  public @org.jetbrains.annotations.Nullable Entity entity(@NotNull UUID uniqueId) {
    org.bukkit.entity.Entity entity = getBukkitServer().getEntity(uniqueId);
    return entity == null ? null : PaperAdapters.adapt(entity);
  }

  @Override
  public void sendMessage(@NotNull Component message) {
    getBukkitServer().sendMessage(message);
  }

  @Override
  public String toString() {
    return "PaperServerWrapper{name=" + name() + ", version=" + version() + "}";
  }
}
