package org.aincraft.bukkit.event;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/** Handle for one event type registered with Bukkit by a {@link BukkitEventBus}. */
public final class BukkitEventRegistration<E extends org.bukkit.event.Event>
    implements AutoCloseable {
  private final Class<E> eventType;
  private final EventPriority capturePriority;
  private final Listener listener;
  private final Object lifecycleLock;
  private final Runnable unregisterAction;
  private volatile boolean active = true;

  BukkitEventRegistration(
      @NotNull Class<E> eventType,
      @NotNull EventPriority capturePriority,
      @NotNull Listener listener,
      @NotNull Runnable unregisterAction) {
    this(eventType, capturePriority, listener, new Object(), unregisterAction);
  }

  BukkitEventRegistration(
      @NotNull Class<E> eventType,
      @NotNull EventPriority capturePriority,
      @NotNull Listener listener,
      @NotNull Object lifecycleLock,
      @NotNull Runnable unregisterAction) {
    this.eventType = Objects.requireNonNull(eventType, "eventType");
    this.capturePriority = Objects.requireNonNull(capturePriority, "capturePriority");
    this.listener = Objects.requireNonNull(listener, "listener");
    this.lifecycleLock = Objects.requireNonNull(lifecycleLock, "lifecycleLock");
    this.unregisterAction = Objects.requireNonNull(unregisterAction, "unregisterAction");
  }

  public @NotNull Class<E> eventType() {
    return eventType;
  }

  public @NotNull EventPriority capturePriority() {
    return capturePriority;
  }

  public boolean isActive() {
    return active;
  }

  public void unregister() {
    synchronized (lifecycleLock) {
      if (!active) {
        return;
      }
      try {
        unregisterAction.run();
      } finally {
        active = false;
      }
    }
  }

  @Override
  public void close() {
    unregister();
  }

  Listener listener() {
    return listener;
  }
}
