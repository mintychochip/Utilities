# Bukkit Event Bus Adapter — Design Spec

**Date:** 2026-08-28
**Status:** Approved in chat; awaiting written-spec review
**Target module:** `:utilities-bukkit`

## Context

`:utilities-api` defines the platform-neutral `org.aincraft.api.event.EventBus` contract, and `:utilities-common` provides its thread-safe `SimpleEventBus` implementation through `EventBuses.create()`. `:utilities-bukkit` currently has no bridge from Bukkit's event dispatcher to that bus. The repository also contains no `Bags`, `BagListener`, or bag-domain event classes, so this change must provide a reusable adapter rather than a Bag-specific integration.

Bukkit events and utility events are separate type systems. A Bukkit event cannot be passed directly to `EventBus.post`, because Bukkit's `org.bukkit.event.Event` does not implement the utility `org.aincraft.api.event.Event`. The adapter therefore needs an envelope that preserves the live Bukkit event while remaining a utility event.

## Goals

- Allow a Bukkit plugin to forward selected Bukkit event classes into the existing utility `EventBus`.
- Preserve the live Bukkit event object so handlers can inspect and mutate it.
- Preserve cancellation semantics in both directions for Bukkit cancellable events.
- Reuse all existing utility-bus behavior: `@Subscribe`, typed subscriptions, priorities, cancellation filtering, annotation registration, and asynchronous posting for utility events.
- Make platform registrations explicit and deterministically removable.
- Keep all Bukkit references inside `:utilities-bukkit`; do not change `:utilities-api` or `:utilities-common`.

## Non-goals

- No replacement or removal of Bukkit `@EventHandler` methods.
- No automatic observation of every Bukkit event class; event classes must be registered explicitly.
- No Bag-specific event types or Bag listener changes; those types are not present in this repository.
- No conversion of arbitrary utility events back into Bukkit events.
- No forced main-thread scheduling. The bridge forwards on the Bukkit callback thread; callers remain responsible for thread-affinity rules of the underlying event.

## Public API

### `BukkitEventBus`

`org.aincraft.bukkit.event.BukkitEventBus` implements `org.aincraft.api.event.EventBus` and delegates every utility-bus operation to an injected `EventBus`.

Constructors/factories:

- `BukkitEventBus(Plugin plugin)` creates a delegate with `EventBuses.create()`.
- `BukkitEventBus(Plugin plugin, EventBus delegate)` uses the supplied bus, allowing a plugin to share an existing bus or configure its async executor before construction.
- No additional factory is required; constructors keep ownership and delegate injection explicit.

Bukkit registration methods:

- `registerBukkitEvent(Class<E> eventType)` registers `E` at Bukkit `EventPriority.LOWEST`.
- `registerBukkitEvent(Class<E> eventType, org.bukkit.event.EventPriority capturePriority)` registers at the supplied Bukkit capture priority.
- Each event type has at most one active platform registration per bus. Re-registering the same class returns the existing active registration rather than installing duplicate executors.
- Registration uses Bukkit `ignoreCancelled=false`, so already-cancelled events still reach utility listeners and utility cancellation can be propagated.
- Each method returns a `BukkitEventRegistration<E>` handle with `eventType()`, `capturePriority()`, `isActive()`, and idempotent `unregister()`/`close()`.
- `BukkitEventBus` implements `AutoCloseable`; `close()` unregisters all platform registrations and is idempotent. It does not destroy or unregister listeners from an injected delegate, because the bus may be shared.

Typed convenience methods:

- `subscribeBukkitEvent(Class<E>, EventListener<? super BukkitEvent<E>>)`.
- Overloads mirror the utility bus's priority and `ignoreCancelled` options.
- These methods register the Bukkit event type if needed, then subscribe to the envelope bus with a type predicate. They return the underlying utility `Subscription`; its reported event type is `BukkitEvent.class`, because the utility bus receives envelopes rather than raw Bukkit classes.

All inherited `EventBus` methods retain their existing meaning. `post` and `postAsync` operate on utility events only; they do not dispatch through Bukkit's plugin manager.

### Event envelopes

`org.aincraft.bukkit.event.BukkitEvent<E extends org.bukkit.event.Event>` implements the utility `Event` interface and exposes:

- `E event()` — the original live Bukkit event.
- `Class<E> eventType()` — the registered/source Bukkit event class.

`org.aincraft.bukkit.event.BukkitCancellableEvent<E extends org.bukkit.event.Event>` extends `BukkitEvent<E>` and implements utility `Cancellable`. Its methods delegate to the wrapped `org.bukkit.event.Cancellable`. Non-cancellable Bukkit events use the base envelope and therefore are not falsely presented as utility-cancellable.

The adapter selects `BukkitCancellableEvent` when the incoming Bukkit event implements Bukkit `Cancellable`; otherwise it selects `BukkitEvent`. The base envelope is the common subscription type, so a normal `@Subscribe` method can receive all forwarded Bukkit events. A typed convenience subscription filters by the original Bukkit type. A listener that needs annotation registration for several raw Bukkit types can inspect `event.event()` with normal Java type checks.

## Data flow

1. The plugin constructs one `BukkitEventBus` during its enabled lifecycle.
2. The plugin calls `registerBukkitEvent(SomeBukkitEvent.class)` for each event class it wants bridged.
3. The adapter asks the plugin's `PluginManager` to register a dedicated listener/executor for that class at the configured capture priority; default capture is Bukkit `LOWEST`.
4. Bukkit invokes the executor with the live event.
5. The executor wraps the event as either `BukkitEvent` or `BukkitCancellableEvent` and calls the delegated utility bus synchronously with `post`.
6. Utility listeners run in the existing utility priority order. If a cancellable envelope is changed, its cancellation state is already backed by the live Bukkit event, so subsequent Bukkit handlers observe the change.
7. Closing the returned registration calls `HandlerList.unregisterAll` for that registration's dedicated Bukkit listener. Closing the bus closes every active registration.

The bridge does not queue or clone events. A Bukkit async event remains on its originating thread, and a synchronous event remains on the Bukkit callback thread. The existing `SimpleEventBus` contract governs listener exceptions and per-listener executors.

## Error handling and lifecycle

- Null plugin, delegate, event type, priority, and listener arguments fail immediately with the same null-check style used by the existing APIs.
- Invalid Bukkit event classes are rejected by Bukkit's `PluginManager`; the adapter must not leave a partially active registration if registration fails.
- Registration is inserted into the adapter's tracking map only after Bukkit registration succeeds.
- Registration and close operations are synchronized per bus so concurrent registration cannot create duplicate platform handlers or leak a failed handle.
- Utility listener failures retain `SimpleEventBus` behavior: one failure does not prevent later utility listeners. The adapter does not add a second exception-swallowing policy.
- The adapter never calls `HandlerList.unregisterAll(plugin)` because that would remove unrelated event handlers owned by the plugin.

## Testing strategy

Add focused `:utilities-bukkit` unit tests with Mockito and synthetic Bukkit events:

- default and explicit capture priority are passed to `PluginManager.registerEvent` with `ignoreCancelled=false`;
- the registered executor forwards the same live event instance inside the correct envelope;
- cancellable envelope changes are visible on the source Bukkit event, and source cancellation is visible through the envelope;
- non-cancellable envelopes do not implement utility `Cancellable`;
- typed subscriptions receive matching subtypes only and automatically activate their Bukkit registration;
- utility priority and `ignoreCancelled` behavior still work through forwarded envelopes;
- duplicate registration is idempotent;
- individual registration and bus close unregister only the adapter's own Bukkit listeners and are idempotent;
- ordinary utility `EventBus` operations are delegated to an injected mock/fake bus.

Run `./gradlew :utilities-bukkit:test` for focused behavior, followed by `./gradlew :utilities-bukkit:check` for formatting, package isolation, and dependency checks. Existing user modifications in unrelated files must remain untouched.
