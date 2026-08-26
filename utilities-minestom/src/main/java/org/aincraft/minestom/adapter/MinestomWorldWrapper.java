package org.aincraft.minestom.adapter;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.minestom.server.instance.Instance;
import org.aincraft.common.entity.Entity;
import org.aincraft.common.entity.Player;
import org.aincraft.common.world.Block;
import org.aincraft.common.world.Chunk;
import org.aincraft.common.world.Difficulty;
import org.aincraft.common.world.Environment;
import org.aincraft.common.world.World;
import org.aincraft.common.world.WorldBorder;
import org.jetbrains.annotations.NotNull;

public class MinestomWorldWrapper implements World, ForwardingAudience.Single {

  private final Instance instance;
  private final Key key;

  public MinestomWorldWrapper(@NotNull Instance instance) {
    this.instance = Objects.requireNonNull(instance, "instance cannot be null");
    this.key = Key.key("minecraft", instance.getUuid().toString().toLowerCase());
  }

  public @NotNull Instance getMinestomInstance() {
    return instance;
  }

  @Override
  public @NotNull Audience audience() {
    return instance;
  }

  @Override
  public @NotNull UUID uid() {
    return instance.getUuid();
  }

  @Override
  public @NotNull String name() {
    return instance.getUuid().toString();
  }

  @Override
  public @NotNull Key key() {
    return key;
  }

  @Override
  public @NotNull Block getBlockAt(int x, int y, int z) {
    return MinestomAdapters.adapt(instance, x, y, z);
  }

  @Override
  public @NotNull Chunk getChunkAt(int chunkX, int chunkZ) {
    net.minestom.server.instance.Chunk chunk = instance.getChunk(chunkX, chunkZ);
    if (chunk == null) {
      throw new IllegalArgumentException("Chunk at (" + chunkX + ", " + chunkZ + ") is not loaded");
    }
    return MinestomAdapters.adapt(chunk);
  }

  @Override
  public boolean isChunkLoaded(int chunkX, int chunkZ) {
    return instance.isChunkLoaded(chunkX, chunkZ);
  }

  @Override
  public int minHeight() {
    return -64;
  }

  @Override
  public int maxHeight() {
    return 320;
  }

  @Override
  public @NotNull WorldBorder worldBorder() {
    return new MinestomWorldBorderWrapper(instance, this);
  }

  @Override
  public @NotNull Environment environment() {
    return Environment.NORMAL;
  }

  @Override
  public @NotNull Difficulty difficulty() {
    return Difficulty.NORMAL;
  }

  @Override
  public long time() {
    return 0L;
  }

  @Override
  public long fullTime() {
    return 0L;
  }

  @Override
  public @NotNull Collection<? extends Player> players() {
    return instance.getPlayers().stream()
        .map(MinestomAdapters::adapt)
        .toList();
  }

  @Override
  public @NotNull Collection<? extends Entity> entities() {
    return instance.getEntities().stream()
        .map(MinestomAdapters::adapt)
        .toList();
  }

  @Override
  public @NotNull Collection<? extends Chunk> loadedChunks() {
    return instance.getChunks().stream()
        .map(MinestomAdapters::adapt)
        .toList();
  }

  @Override
  public void sendMessage(@NotNull Component message) {
    instance.sendMessage(message);
  }

  @Override
  public void sendActionBar(@NotNull Component message) {
    instance.sendActionBar(message);
  }

  @Override
  public void showTitle(@NotNull Title title) {
    instance.showTitle(title);
  }

  @Override
  public void clearTitle() {
    instance.clearTitle();
  }

  @Override
  public void resetTitle() {
    instance.resetTitle();
  }

  @Override
  public void playSound(@NotNull Sound sound) {
    instance.playSound(sound);
  }

  @Override
  public void stopSound(@NotNull SoundStop stop) {
    instance.stopSound(stop);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof World that)) return false;
    return Objects.equals(uid(), that.uid());
  }

  @Override
  public int hashCode() {
    return uid().hashCode();
  }

  @Override
  public String toString() {
    return "MinestomWorldWrapper{name=" + name() + ", uid=" + uid() + "}";
  }
}
