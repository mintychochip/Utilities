package org.aincraft.bukkit.event;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/** Bukkit event envelope that forwards utility cancellation to the live source event. */
public final class BukkitCancellableEvent<E extends org.bukkit.event.Event> extends BukkitEvent<E>
    implements org.aincraft.api.event.Cancellable {
  private final org.bukkit.event.Cancellable cancellable;

  /** Creates a cancellable envelope from a Bukkit cancellable event. */
  public BukkitCancellableEvent(@NotNull E event) {
    this(event, runtimeType(Objects.requireNonNull(event, "event")));
  }

  BukkitCancellableEvent(@NotNull E event, @NotNull Class<E> eventType) {
    super(event, eventType);
    if (!(event instanceof org.bukkit.event.Cancellable source)) {
      throw new IllegalArgumentException(
          "Event does not support cancellation: " + event.getClass().getName());
    }
    this.cancellable = source;
  }

  @Override
  public boolean isCancelled() {
    return cancellable.isCancelled();
  }

  @Override
  public void setCancelled(boolean cancelled) {
    cancellable.setCancelled(cancelled);
  }

  @SuppressWarnings("unchecked")
  private static <E extends org.bukkit.event.Event> Class<E> runtimeType(E event) {
    return (Class<E>) event.getClass();
  }
}
