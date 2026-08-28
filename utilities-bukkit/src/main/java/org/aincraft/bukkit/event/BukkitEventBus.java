package org.aincraft.bukkit.event;

import org.aincraft.api.event.Event;
import org.aincraft.api.event.EventBus;
import org.aincraft.api.event.EventListener;
import org.aincraft.api.event.EventPriority;
import org.aincraft.api.event.Subscription;
import org.aincraft.event.EventBuses;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/** Bridges explicitly registered Bukkit events into a platform-neutral event bus. */
public final class BukkitEventBus implements EventBus, AutoCloseable {
  private static final org.bukkit.event.EventPriority DEFAULT_CAPTURE_PRIORITY =
      org.bukkit.event.EventPriority.LOWEST;

  private final Plugin plugin;
  private final EventBus delegate;
  private final Map<Class<? extends org.bukkit.event.Event>, BukkitEventRegistration<?>>
      registrations = new HashMap<>();
  private boolean closed;

  /** Creates a bridge backed by a new default utility event bus. */
  public BukkitEventBus(@NotNull Plugin plugin) {
    this(plugin, EventBuses.create());
  }

  /** Creates a bridge backed by the supplied utility event bus. */
  public BukkitEventBus(@NotNull Plugin plugin, @NotNull EventBus delegate) {
    this.plugin = Objects.requireNonNull(plugin, "plugin");
    this.delegate = Objects.requireNonNull(delegate, "delegate");
  }

  /** Registers a Bukkit event at Bukkit's lowest capture priority. */
  public <E extends org.bukkit.event.Event> @NotNull BukkitEventRegistration<E> registerBukkitEvent(
      @NotNull Class<E> eventType) {
    return registerBukkitEvent(eventType, DEFAULT_CAPTURE_PRIORITY);
  }

  /** Registers a Bukkit event at the requested Bukkit capture priority. */
  public synchronized <E extends org.bukkit.event.Event>
      @NotNull BukkitEventRegistration<E> registerBukkitEvent(
          @NotNull Class<E> eventType, @NotNull org.bukkit.event.EventPriority capturePriority) {
    Objects.requireNonNull(eventType, "eventType");
    Objects.requireNonNull(capturePriority, "capturePriority");
    if (closed) {
      throw new IllegalStateException("Event bus is closed");
    }

    BukkitEventRegistration<?> existing = registrations.get(eventType);
    if (existing != null && existing.isActive()) {
      @SuppressWarnings("unchecked")
      BukkitEventRegistration<E> active = (BukkitEventRegistration<E>) existing;
      return active;
    }
    if (existing != null) {
      registrations.remove(eventType);
    }

    Listener listener = new Listener() {};
    EventExecutor executor = (ignored, event) -> dispatch(eventType, event);
    try {
      plugin
          .getServer()
          .getPluginManager()
          .registerEvent(eventType, listener, capturePriority, executor, plugin, false);
    } catch (RuntimeException | Error failure) {
      HandlerList.unregisterAll(listener);
      throw failure;
    }

    BukkitEventRegistration<E> registration =
        new BukkitEventRegistration<>(
            eventType,
            capturePriority,
            listener,
            () -> unregisterRegistration(eventType, listener));
    registrations.put(eventType, registration);
    return registration;
  }

  /** Subscribes to one Bukkit event type using normal utility-bus priority. */
  public <E extends org.bukkit.event.Event> @NotNull Subscription subscribeBukkitEvent(
      @NotNull Class<E> eventType, @NotNull EventListener<? super BukkitEvent<E>> listener) {
    return subscribeBukkitEvent(eventType, EventPriority.NORMAL, false, null, listener);
  }

  /** Subscribes to one Bukkit event type with a utility-bus priority. */
  public <E extends org.bukkit.event.Event> @NotNull Subscription subscribeBukkitEvent(
      @NotNull Class<E> eventType,
      @NotNull EventPriority priority,
      @NotNull EventListener<? super BukkitEvent<E>> listener) {
    return subscribeBukkitEvent(eventType, priority, false, null, listener);
  }

  /** Subscribes to one Bukkit event type with cancellation filtering. */
  public <E extends org.bukkit.event.Event> @NotNull Subscription subscribeBukkitEvent(
      @NotNull Class<E> eventType,
      @NotNull EventPriority priority,
      boolean ignoreCancelled,
      @NotNull EventListener<? super BukkitEvent<E>> listener) {
    return subscribeBukkitEvent(eventType, priority, ignoreCancelled, null, listener);
  }

  /** Subscribes to one Bukkit event type with all utility-bus dispatch options. */
  public synchronized <E extends org.bukkit.event.Event> @NotNull Subscription subscribeBukkitEvent(
      @NotNull Class<E> eventType,
      @NotNull EventPriority priority,
      boolean ignoreCancelled,
      @Nullable Executor executor,
      @NotNull EventListener<? super BukkitEvent<E>> listener) {
    Objects.requireNonNull(eventType, "eventType");
    Objects.requireNonNull(priority, "priority");
    Objects.requireNonNull(listener, "listener");
    registerBukkitEvent(eventType);
    return delegate.subscribe(
        BukkitEvent.class,
        priority,
        ignoreCancelled,
        executor,
        envelope -> {
          if (eventType.isInstance(envelope.event())) {
            @SuppressWarnings("unchecked")
            BukkitEvent<E> typedEnvelope = (BukkitEvent<E>) envelope;
            listener.handle(typedEnvelope);
          }
        });
  }

  @Override
  public <E extends Event> @NotNull Subscription subscribe(
      @NotNull Class<E> eventType,
      @NotNull EventPriority priority,
      boolean ignoreCancelled,
      @Nullable Executor executor,
      @NotNull EventListener<? super E> listener) {
    return delegate.subscribe(eventType, priority, ignoreCancelled, executor, listener);
  }

  @Override
  public @NotNull List<Subscription> register(@NotNull Object instance) {
    return delegate.register(instance);
  }

  @Override
  public void unregister(@NotNull Object instance) {
    delegate.unregister(instance);
  }

  @Override
  public void unsubscribe(@NotNull Subscription subscription) {
    delegate.unsubscribe(subscription);
  }

  @Override
  public <E extends Event> @NotNull E post(@NotNull E event) {
    return delegate.post(event);
  }

  @Override
  public <E extends Event> @NotNull CompletableFuture<E> postAsync(
      @NotNull E event, @Nullable Executor executor) {
    return delegate.postAsync(event, executor);
  }

  /** Unregisters every Bukkit listener owned by this bridge. */
  @Override
  public void close() {
    List<BukkitEventRegistration<?>> activeRegistrations;
    synchronized (this) {
      if (closed) {
        return;
      }
      closed = true;
      activeRegistrations = new ArrayList<>(registrations.values());
      registrations.clear();
    }
    for (BukkitEventRegistration<?> registration : activeRegistrations) {
      registration.unregister();
    }
  }

  private <E extends org.bukkit.event.Event> void dispatch(
      Class<E> eventType, org.bukkit.event.Event event) {
    if (!eventType.isInstance(event)) {
      return;
    }
    E typedEvent = eventType.cast(event);
    BukkitEvent<E> envelope =
        typedEvent instanceof org.bukkit.event.Cancellable
            ? new BukkitCancellableEvent<>(typedEvent, eventType)
            : new BukkitEvent<>(typedEvent, eventType);
    delegate.post(envelope);
  }

  private <E extends org.bukkit.event.Event> void unregisterRegistration(
      Class<E> eventType, Listener listener) {
    synchronized (this) {
      BukkitEventRegistration<?> existing = registrations.get(eventType);
      if (existing != null && existing.listener() == listener) {
        HandlerList.unregisterAll(listener);
        registrations.remove(eventType);
        return;
      }
      HandlerList.unregisterAll(listener);
    }
  }
}
