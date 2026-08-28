package org.aincraft.bukkit.event;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/** Handle for one event type registered with Bukkit by a {@link BukkitEventBus}. */
public final class BukkitEventRegistration<E extends org.bukkit.event.Event>
    implements AutoCloseable {
  private final Class<E> eventType;
  private final EventPriority capturePriority;
  private final Listener listener;
  private final Runnable unregisterAction;
  private final AtomicBoolean active = new AtomicBoolean(true);

  BukkitEventRegistration(
      @NotNull Class<E> eventType,
      @NotNull EventPriority capturePriority,
      @NotNull Listener listener,
      @NotNull Runnable unregisterAction) {
    this.eventType = Objects.requireNonNull(eventType, "eventType");
    this.capturePriority = Objects.requireNonNull(capturePriority, "capturePriority");
    this.listener = Objects.requireNonNull(listener, "listener");
    this.unregisterAction = Objects.requireNonNull(unregisterAction, "unregisterAction");
  }

  public @NotNull Class<E> eventType() {
    return eventType;
  }

  public @NotNull EventPriority capturePriority() {
    return capturePriority;
  }

  public boolean isActive() {
    return active.get();
  }

  public void unregister() {
    if (active.compareAndSet(true, false)) {
      unregisterAction.run();
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
