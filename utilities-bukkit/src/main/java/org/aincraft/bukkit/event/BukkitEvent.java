package org.aincraft.bukkit.event;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/** Utility-bus envelope for a live Bukkit event. */
public class BukkitEvent<E extends org.bukkit.event.Event> implements org.aincraft.api.event.Event {
  private final E event;
  private final Class<E> eventType;

  /** Creates an envelope whose type is the source event's runtime class. */
  public BukkitEvent(@NotNull E event) {
    this(event, runtimeType(Objects.requireNonNull(event, "event")));
  }

  BukkitEvent(@NotNull E event, @NotNull Class<E> eventType) {
    this.event = Objects.requireNonNull(event, "event");
    this.eventType = Objects.requireNonNull(eventType, "eventType");
  }

  /** Returns the original live Bukkit event. */
  public final @NotNull E event() {
    return event;
  }

  /** Returns the registered or source type associated with this envelope. */
  public final @NotNull Class<E> eventType() {
    return eventType;
  }

  @SuppressWarnings("unchecked")
  private static <E extends org.bukkit.event.Event> Class<E> runtimeType(E event) {
    return (Class<E>) event.getClass();
  }
}
