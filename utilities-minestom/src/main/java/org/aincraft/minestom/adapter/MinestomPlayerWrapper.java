package org.aincraft.minestom.adapter;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.minestom.server.coordinate.Pos;
import org.aincraft.api.domain.entity.Player;
import org.aincraft.api.domain.inventory.PlayerInventory;
import org.aincraft.api.domain.world.GameMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MinestomPlayerWrapper extends MinestomLivingEntityWrapper
    implements Player, ForwardingAudience.Single {

  private final net.minestom.server.entity.Player player;
  private final PlayerInventory inventory;

  public MinestomPlayerWrapper(@NotNull net.minestom.server.entity.Player player) {
    super(player);
    this.player = player;
    this.inventory = new MinestomPlayerInventoryWrapper(player.getInventory(), this);
  }

  public @NotNull net.minestom.server.entity.Player getMinestomPlayer() {
    return player;
  }

  @Override
  public @NotNull Audience audience() {
    return player;
  }

  @Override
  public @NotNull String username() {
    return player.getUsername();
  }

  @Override
  public boolean isOnline() {
    return player.isOnline();
  }

  @Override
  public int ping() {
    return player.getLatency();
  }

  @Override
  public int foodLevel() {
    return player.getFood();
  }

  @Override
  public void setFoodLevel(int foodLevel) {
    player.setFood(foodLevel);
  }

  @Override
  public float saturation() {
    return player.getFoodSaturation();
  }

  @Override
  public void setSaturation(float saturation) {
    player.setFoodSaturation(saturation);
  }

  @Override
  public int level() {
    return player.getLevel();
  }

  @Override
  public void setLevel(int level) {
    player.setLevel(level);
  }

  @Override
  public float exp() {
    return player.getExp();
  }

  @Override
  public void setExp(float exp) {
    player.setExp(exp);
  }

  @Override
  public @NotNull GameMode gameMode() {
    return switch (player.getGameMode()) {
      case SURVIVAL -> GameMode.SURVIVAL;
      case CREATIVE -> GameMode.CREATIVE;
      case ADVENTURE -> GameMode.ADVENTURE;
      case SPECTATOR -> GameMode.SPECTATOR;
    };
  }

  @Override
  public void setGameMode(@NotNull GameMode gameMode) {
    Objects.requireNonNull(gameMode, "gameMode cannot be null");
    net.minestom.server.entity.GameMode mMode =
        switch (gameMode) {
          case SURVIVAL -> net.minestom.server.entity.GameMode.SURVIVAL;
          case CREATIVE -> net.minestom.server.entity.GameMode.CREATIVE;
          case ADVENTURE -> net.minestom.server.entity.GameMode.ADVENTURE;
          case SPECTATOR -> net.minestom.server.entity.GameMode.SPECTATOR;
        };
    player.setGameMode(mMode);
  }

  @Override
  public boolean isSneaking() {
    return player.isSneaking();
  }

  @Override
  public void setSneaking(boolean sneaking) {
    player.setSneaking(sneaking);
  }

  @Override
  public boolean isSprinting() {
    return player.isSprinting();
  }

  @Override
  public void setSprinting(boolean sprinting) {
    player.setSprinting(sprinting);
  }

  @Override
  public boolean isFlying() {
    return player.isFlying();
  }

  @Override
  public void setFlying(boolean flying) {
    player.setFlying(flying);
  }

  @Override
  public @NotNull PlayerInventory inventory() {
    return inventory;
  }

  @Override
  public @NotNull org.aincraft.api.domain.inventory.InventoryView openInventory() {
    return new MinestomInventoryViewWrapper(player);
  }

  @Override
  public @NotNull org.aincraft.api.domain.inventory.InventoryView openInventory(
      @NotNull org.aincraft.api.domain.inventory.Inventory inventory) {
    net.minestom.server.inventory.AbstractInventory minestomInventory =
        MinestomAdapters.toMinestom(inventory);
    if (!(minestomInventory instanceof net.minestom.server.inventory.Inventory menu)
        || !player.openInventory(menu)) {
      throw new IllegalStateException("Minestom refused to open the inventory");
    }
    return openInventory();
  }

  @Override
  public void closeInventory() {
    player.closeInventory();
  }

  @Override
  public boolean hasPermission(@NotNull String permission) {
    return isOp();
  }

  @Override
  public boolean isOp() {
    return player.getPermissionLevel() >= 4;
  }

  @Override
  public void setOp(boolean op) {
    player.setPermissionLevel(op ? 4 : 0);
  }

  @Override
  public @NotNull org.aincraft.api.domain.server.Server server() {
    return MinestomAdapters.adaptServer();
  }

  @Override
  public void kick(@NotNull Component reason) {
    player.kick(reason);
  }

  @Override
  public void sendMessage(@NotNull Component message) {
    player.sendMessage(message);
  }

  @Override
  public void sendActionBar(@NotNull Component message) {
    player.sendActionBar(message);
  }

  @Override
  public void showTitle(@NotNull Title title) {
    player.showTitle(title);
  }

  @Override
  public void clearTitle() {
    player.clearTitle();
  }

  @Override
  public void resetTitle() {
    player.resetTitle();
  }

  @Override
  public void playSound(@NotNull Sound sound) {
    player.playSound(sound);
  }

  @Override
  public void stopSound(@NotNull SoundStop stop) {
    player.stopSound(stop);
  }

  @Override
  public String toString() {
    return "MinestomPlayerWrapper{username=" + username() + ", uuid=" + uniqueId() + "}";
  }

  @Override
  public boolean allowFlight() {
    return player.isAllowFlying();
  }

  @Override
  public void setAllowFlight(boolean allow) {
    player.setAllowFlying(allow);
  }

  @Override
  public @NotNull org.aincraft.api.domain.inventory.Inventory enderChest() {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.ENDER_CHEST,
        "Minestom has no separate ender-chest inventory; Player inventory is the only container.");
  }

  @Override
  public @Nullable org.aincraft.api.domain.inventory.ItemStack itemOnCursor() {
    net.minestom.server.item.ItemStack item = player.getInventory().getCursorItem();
    return item == null || item.isAir() || item.amount() <= 0 ? null : MinestomAdapters.adapt(item);
  }

  @Override
  public void setItemOnCursor(@Nullable org.aincraft.api.domain.inventory.ItemStack item) {
    player
        .getInventory()
        .setCursorItem(
            item == null
                ? net.minestom.server.item.ItemStack.AIR
                : MinestomAdapters.toMinestom(item));
  }

  @Override
  public float exhaustion() {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.EXHAUSTION,
        "Minestom does not model Bukkit-style food-exhaustion; only food level and saturation are exposed.");
  }

  @Override
  public void setExhaustion(float exhaustion) {
    throw new org.aincraft.api.UnsupportedCapabilityException(
        org.aincraft.api.Capability.EXHAUSTION,
        "Minestom does not model Bukkit-style food-exhaustion; only food level and saturation are exposed.");
  }

  @Override
  public @Nullable org.aincraft.api.domain.location.Location bedSpawnLocation() {
    Pos pos = player.getRespawnPoint();
    if (pos == null) return null;
    org.aincraft.api.domain.world.World world = MinestomAdapters.adapt(player.getInstance());
    return new MinestomLocationWrapper(world, pos);
  }

  @Override
  public void setBedSpawnLocation(
      @Nullable org.aincraft.api.domain.location.Location location, boolean force) {
    if (location == null) {
      player.setRespawnPoint(null);
      return;
    }
    org.aincraft.api.domain.location.Position p = location.position();
    player.setRespawnPoint(new Pos(p.x(), p.y(), p.z()));
  }

  @Override
  public void sendEntityEffect(
      @NotNull org.aincraft.api.domain.effect.EntityEffect effect,
      @NotNull org.aincraft.api.domain.entity.Entity entity) {
    byte status =
        switch (effect) {
          case RABBIT_JUMP -> 1;
          case ENTITY_DEATH -> 3;
          case IRON_GOLEN_ATTACK -> 4;
          case WOLF_SMOKE -> 6;
          case WOLF_HEARTS -> 7;
          case WOLF_SHAKE -> 8;
          case SHEEP_EAT -> 10;
          case IRON_GOLEM_ROSE -> 11;
          case VILLAGER_HEART -> 12;
          case VILLAGER_ANGRY -> 13;
          case VILLAGER_HAPPY -> 14;
          case WITCH_MAGIC -> 15;
          case ZOMBIE_TRANSFORM -> 16;
          case FIREWORK_EXPLODE -> 17;
          case LOVE_HEARTS -> 18;
          case SQUID_ROTATE -> 19;
          case ENTITY_POOF -> 20;
          case GUARDIAN_TARGET -> 21;
          case SHIELD_BLOCK -> 29;
          case SHIELD_BREAK -> 30;
          case ARMOR_STAND_HIT -> 32;
          case THORNS_HURT -> 33;
          case TOTEM_RESURRECT -> 35;
          case DOLPHIN_FED -> 38;
          case RAVAGER_STUNNED -> 39;
          case CAT_TAME_FAIL -> 40;
          case CAT_TAME_SUCCESS -> 41;
          case VILLAGER_SPLASH -> 42;
          case PLAYER_BAD_OMEN_RAID -> 43;
          case FOX_CHEW -> 45;
          case TELEPORT_ENDER -> 46;
          case SWAP_HAND_ITEMS -> 55;
          case WOLF_SHAKE_STOP -> 56;
          case GOAT_LOWER_HEAD -> 58;
          case GOAT_RAISE_HEAD -> 59;
          case SPAWN_DEATH_SMOKE -> 60;
          case SNIFFER_DIG -> 63;
          default ->
              throw new org.aincraft.api.UnsupportedCapabilityException(
                  org.aincraft.api.Capability.ENTITY_EFFECT,
                  "Minestom has no status mapping for " + effect);
        };
    MinestomAdapters.toMinestom(entity).triggerStatus(status);
  }

  @Override
  public @NotNull net.kyori.adventure.text.Component displayName() {
    return Objects.requireNonNullElseGet(player.getDisplayName(), () -> Component.text(username()));
  }

  @Override
  public void displayName(@NotNull net.kyori.adventure.text.Component displayName) {
    player.setDisplayName(Objects.requireNonNull(displayName, "displayName cannot be null"));
  }
}
