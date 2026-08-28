# Bukkit Event Bus Adapter Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a reusable `:utilities-bukkit` adapter that forwards explicitly registered Bukkit events through the existing platform-neutral `EventBus`.

**Architecture:** `BukkitEventBus` implements and delegates `org.aincraft.api.event.EventBus`. Each explicitly registered Bukkit event class gets a dedicated Bukkit listener/executor at a configurable capture priority (default `LOWEST`), and the executor posts a live `BukkitEvent` envelope to the delegate. Cancellable Bukkit events use a `BukkitCancellableEvent` envelope that directly backs utility cancellation with the source event.

**Tech Stack:** Java 25, Gradle, Spigot API `1.21.4-R0.1-SNAPSHOT`, existing `EventBus`/`EventBuses`, JUnit 5, Mockito 5.14.2, JetBrains annotations.

**Spec:** `docs/superpowers/specs/2026-08-28-bukkit-event-bus-design.md`

## Global Constraints

- Keep all Bukkit references under `:utilities-bukkit`; do not modify `:utilities-api` or `:utilities-common`.
- Preserve the existing `org.aincraft.api.event.EventBus` semantics, including annotation registration, priority ordering, cancellation filtering, and delegate lifecycle ownership.
- Register only explicitly requested Bukkit event classes; never call `HandlerList.unregisterAll(plugin)`.
- Forward the original live Bukkit event; do not clone, queue, or force a main-thread hop.
- Use Bukkit capture `EventPriority.LOWEST` and `ignoreCancelled=false` by default.
- Preserve unrelated user modifications in the worktree.
- Skip formatters, linters, and project-wide test suites until the final verification task.

---

### Task 1: Implement Bukkit event envelopes and registration handles

**Files:**
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/event/BukkitEvent.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/event/BukkitCancellableEvent.java`
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/event/BukkitEventRegistration.java`
- Test: `utilities-bukkit/src/test/java/org/aincraft/bukkit/event/BukkitEventEnvelopeTest.java`

**Interfaces:**
- Consumes: `org.aincraft.api.event.Event`, `org.aincraft.api.event.Cancellable`, and `org.bukkit.event.Event`/`org.bukkit.event.Cancellable`.
- Produces:
  - `public class BukkitEvent<E extends org.bukkit.event.Event> implements org.aincraft.api.event.Event` with `BukkitEvent(E event)`, `E event()`, and `Class<E> eventType()`.
- Package-visible constructor `BukkitEvent(E event, Class<E> eventType)` lets the bus preserve the registered source type.
- `public final class BukkitEventRegistration<E extends org.bukkit.event.Event> implements AutoCloseable` with `Class<E> eventType()`, `org.bukkit.event.EventPriority capturePriority()`, `boolean isActive()`, `void unregister()`, and idempotent `close()`.
- Package-visible registration constructor: `BukkitEventRegistration(Class<E>, org.bukkit.event.EventPriority, Listener, Runnable unregisterAction)`. The handle owns an `AtomicBoolean`; it invokes `unregisterAction` only after the first successful active-to-inactive transition.

- [ ] **Step 1: Write failing envelope and registration tests**

Create synthetic Bukkit events with the standard static `HandlerList`, `getHandlers()`, and `getHandlerList()` methods. Cover the observable contract directly:

```java
@Test
void envelopeKeepsTheLiveEventAndRegisteredType() {
  TestEvent source = new TestEvent();
  BukkitEvent<TestEvent> envelope = new BukkitEvent<>(source, TestEvent.class);

  assertSame(source, envelope.event());
  assertEquals(TestEvent.class, envelope.eventType());
  assertInstanceOf(org.aincraft.api.event.Event.class, envelope);
}

@Test
void cancellableEnvelopeDelegatesBothDirections() {
  TestCancellableEvent source = new TestCancellableEvent();
  BukkitCancellableEvent<TestCancellableEvent> envelope =
      new BukkitCancellableEvent<>(source, TestCancellableEvent.class);

  assertFalse(envelope.isCancelled());
  source.setCancelled(true);
  assertTrue(envelope.isCancelled());
  envelope.setCancelled(false);
  assertFalse(source.isCancelled());
}

@Test
void cancellableEnvelopeRejectsNonCancellableSource() {
  assertThrows(
      IllegalArgumentException.class,
      () -> new BukkitCancellableEvent<>(new TestEvent(), TestEvent.class));
}
```

Instantiate the package-visible registration constructor with a dedicated mock/fake `Listener` and an `AtomicInteger`-backed `Runnable`. Assert the handle exposes the supplied event type and capture priority, starts active, invokes the callback exactly once across repeated `unregister()`/`close()` calls, and reports inactive afterward.

- [ ] **Step 2: Run the focused test to verify it fails**

Run: `./gradlew :utilities-bukkit:test --tests org.aincraft.bukkit.event.BukkitEventEnvelopeTest`

Expected: FAIL during test compilation because the three production types do not exist yet.

- [ ] **Step 3: Implement the envelope types**

In `BukkitEvent`, require both constructor arguments with `Objects.requireNonNull`, store the original event and the source/registered event class, and make the public one-argument constructor derive the class from `event.getClass()` with one localized unchecked cast. Expose only the live event and source type; do not add conversion or cloning behavior.

In `BukkitCancellableEvent`, call the package-visible base constructor, verify `event instanceof org.bukkit.event.Cancellable`, retain that interface reference, and implement utility `Cancellable` by direct delegation. This class must not silently support non-cancellable sources.

In `BukkitEventRegistration`, store the event type, capture priority, dedicated Bukkit `Listener`, `Runnable unregisterAction`, and an `AtomicBoolean` active flag. `unregister()` must invoke the callback only on the first successful active-to-inactive transition; `close()` calls `unregister()`.

- [ ] **Step 4: Run the focused test to verify it passes**

Run: `./gradlew :utilities-bukkit:test --tests org.aincraft.bukkit.event.BukkitEventEnvelopeTest`

Expected: PASS.

- [ ] **Step 5: Commit the envelope unit**

```bash
git add utilities-bukkit/src/main/java/org/aincraft/bukkit/event utilities-bukkit/src/test/java/org/aincraft/bukkit/event
git commit -m "feat: add Bukkit event envelopes"
```

---

### Task 2: Implement the delegating Bukkit event bus

**Files:**
- Create: `utilities-bukkit/src/main/java/org/aincraft/bukkit/event/BukkitEventBus.java`
- Test: `utilities-bukkit/src/test/java/org/aincraft/bukkit/event/BukkitEventBusTest.java`

**Interfaces:**
- Consumes: Task 1 envelope and registration types, `org.aincraft.event.EventBuses`, `org.aincraft.api.event.EventBus`, and a Bukkit `Plugin`/`PluginManager`.
- Produces:
  - `public BukkitEventBus(Plugin plugin)` using `EventBuses.create()`.
  - `public BukkitEventBus(Plugin plugin, EventBus delegate)` using the supplied delegate without taking ownership of it.
  - `<E extends org.bukkit.event.Event> BukkitEventRegistration<E> registerBukkitEvent(Class<E> eventType)` using Bukkit `LOWEST`.
  - `<E extends org.bukkit.event.Event> BukkitEventRegistration<E> registerBukkitEvent(Class<E> eventType, org.bukkit.event.EventPriority capturePriority)`.
  - Typed convenience overloads:

```java
<E extends org.bukkit.event.Event> Subscription subscribeBukkitEvent(
    Class<E> eventType,
    EventListener<? super BukkitEvent<E>> listener);

<E extends org.bukkit.event.Event> Subscription subscribeBukkitEvent(
    Class<E> eventType,
    org.aincraft.api.event.EventPriority priority,
    EventListener<? super BukkitEvent<E>> listener);

<E extends org.bukkit.event.Event> Subscription subscribeBukkitEvent(
    Class<E> eventType,
    org.aincraft.api.event.EventPriority priority,
    boolean ignoreCancelled,
    EventListener<? super BukkitEvent<E>> listener);

<E extends org.bukkit.event.Event> Subscription subscribeBukkitEvent(
    Class<E> eventType,
    org.aincraft.api.event.EventPriority priority,
    boolean ignoreCancelled,
    Executor executor,
    EventListener<? super BukkitEvent<E>> listener);
```

  - Every inherited `EventBus` method delegates to the injected bus unchanged.
  - `BukkitEventBus implements AutoCloseable`; `close()` unregisters only this bus's active platform registrations, is idempotent, and rejects new Bukkit registrations after close.

- [ ] **Step 1: Write failing forwarding and delegation tests**

Build the test fixture with Mockito `Plugin`, `Server`, and `PluginManager` mocks. Stub `plugin.getServer()` and `server.getPluginManager()`. Capture the `EventExecutor` and `Listener` arguments passed to `registerEvent`.

Cover the following behaviors with actual utility-bus dispatch where cancellation/order matters:

```java
@Test
void defaultRegistrationUsesLowestAndReceivesAlreadyCancelledEvents() throws Exception {
  BukkitEventBus bus = new BukkitEventBus(plugin, EventBuses.create());
  bus.registerBukkitEvent(TestCancellableEvent.class);

  verify(pluginManager).registerEvent(
      eq(TestCancellableEvent.class),
      any(Listener.class),
      eq(org.bukkit.event.EventPriority.LOWEST),
      executorCaptor.capture(),
      eq(plugin),
      eq(false));

  TestCancellableEvent source = new TestCancellableEvent();
  source.setCancelled(true);
  List<Boolean> observed = new ArrayList<>();
  bus.subscribeBukkitEvent(
      TestCancellableEvent.class, envelope -> observed.add(envelope.isCancelled()));

  executorCaptor.getValue().execute(listenerCaptor.getValue(), source);
  assertEquals(List.of(true), observed);
}

@Test
void utilityCancellationMutatesTheLiveBukkitEventAndIgnoreCancelledSkipsLaterListeners()
    throws Exception {
  EventBus delegate = EventBuses.create();
  BukkitEventBus bus = new BukkitEventBus(plugin, delegate);
  bus.registerBukkitEvent(TestCancellableEvent.class);
  List<String> calls = new ArrayList<>();
  delegate.subscribe(
      BukkitEvent.class,
      org.aincraft.api.event.EventPriority.LOWEST,
      event -> {
        ((org.aincraft.api.event.Cancellable) event).setCancelled(true);
        calls.add("cancel");
      });
  delegate.subscribe(
      BukkitEvent.class,
      org.aincraft.api.event.EventPriority.HIGH,
      true,
      event -> calls.add("ignored"));

  TestCancellableEvent source = new TestCancellableEvent();
  executorCaptor.getValue().execute(listenerCaptor.getValue(), source);

  assertTrue(source.isCancelled());
  assertEquals(List.of("cancel"), calls);
}

@Test
void typedSubscriptionRegistersOnceAndFiltersByOriginalBukkitType() throws Exception {
  BukkitEventBus bus = new BukkitEventBus(plugin, EventBuses.create());
  List<TestEvent> received = new ArrayList<>();

  Subscription subscription = bus.subscribeBukkitEvent(TestEvent.class, e -> received.add(e.event()));
  assertTrue(subscription.isActive());
  verify(pluginManager, times(1)).registerEvent(
      eq(TestEvent.class), any(Listener.class), eq(org.bukkit.event.EventPriority.LOWEST),
      any(EventExecutor.class), eq(plugin), eq(false));

  EventExecutor executor = capturedExecutorFor(TestEvent.class);
  executor.execute(capturedListenerFor(TestEvent.class), new TestEvent());
  executor.execute(capturedListenerFor(TestEvent.class), new OtherEvent());

  assertEquals(1, received.size());
}
```

Also test: forwarding preserves the exact live source instance and registered type; non-cancellable source envelopes do not implement utility `Cancellable`; explicit capture priority is passed through; duplicate registration returns the same active handle and calls `registerEvent` once; a registration failure does not leave an active map entry; individual `unregister()` and bus `close()` call `HandlerList.unregisterAll` once per dedicated listener and never affect unrelated listeners; repeated close is harmless; utility `post`/`subscribe` calls reach an injected delegate; and a delegate utility event is not sent to Bukkit's plugin manager.

Use Mockito 5 static verification in a try-with-resources `MockedStatic<HandlerList>` for cleanup assertions. Capture each dedicated `Listener`, call the registration or bus close, verify `HandlerList.unregisterAll(capturedListener)` exactly once per handle, and verify repeated cleanup does not add calls. Do not use a fallback assertion that weakens the cleanup contract.

- [ ] **Step 2: Run the focused test to verify it fails**

Run: `./gradlew :utilities-bukkit:test --tests org.aincraft.bukkit.event.BukkitEventBusTest`

Expected: FAIL during test compilation because `BukkitEventBus` does not exist.

- [ ] **Step 3: Implement `BukkitEventBus` minimally**

Store the plugin, injected delegate, a synchronized map keyed by Bukkit event class, and a closed flag. Null-check constructor and method arguments before side effects.

`registerBukkitEvent` must:

1. reject calls after `close()`;
2. return the existing active handle for an already registered class, ignoring a later capture-priority argument;
3. create a dedicated `Listener` and `EventExecutor` for a new class;
4. call `plugin.getServer().getPluginManager().registerEvent(eventType, listener, capturePriority, executor, plugin, false)`;
5. remove the dedicated listener with `HandlerList.unregisterAll(listener)` if registration throws; and
6. add the handle to the map only after successful Bukkit registration.

The executor must defensively ignore events outside `eventType`, create `BukkitCancellableEvent` when the source implements Bukkit `Cancellable`, otherwise `BukkitEvent`, and call `delegate.post(envelope)` synchronously. Do not catch or reclassify delegate exceptions.

The typed convenience method must null-check first, ensure `registerBukkitEvent(eventType)` is active, and subscribe to `BukkitEvent.class` on the delegate. Its listener invokes the typed listener only when `eventType.isInstance(envelope.event())`, casting the envelope only after that predicate. Pass through utility priority, `ignoreCancelled`, and executor options. The returned subscription is the delegate subscription and therefore reports `BukkitEvent.class`.

Implement all `EventBus` methods as direct delegate calls, including `register(Object)`, `unregister(Object)`, `unsubscribe(Subscription)`, `post`, and both `postAsync` forms. `close()` must atomically mark the bus closed, snapshot and clear the map, then unregister each dedicated registration exactly once. It must not call delegate `unregister` or otherwise tear down injected utility listeners.

- [ ] **Step 4: Run forwarding and adapter tests**

Run: `./gradlew :utilities-bukkit:test --tests org.aincraft.bukkit.event.BukkitEventBusTest --tests org.aincraft.bukkit.event.BukkitEventEnvelopeTest`

Expected: PASS.

- [ ] **Step 5: Commit the bus unit**

```bash
git add utilities-bukkit/src/main/java/org/aincraft/bukkit/event/BukkitEventBus.java utilities-bukkit/src/test/java/org/aincraft/bukkit/event/BukkitEventBusTest.java
git commit -m "feat: bridge Bukkit events to utility bus"
```

---

### Task 3: Run final module verification and review the public surface

**Files:**
- Review: `utilities-bukkit/src/main/java/org/aincraft/bukkit/event/BukkitEvent.java`
- Review: `utilities-bukkit/src/main/java/org/aincraft/bukkit/event/BukkitCancellableEvent.java`
- Review: `utilities-bukkit/src/main/java/org/aincraft/bukkit/event/BukkitEventRegistration.java`
- Review: `utilities-bukkit/src/main/java/org/aincraft/bukkit/event/BukkitEventBus.java`
- Review: `utilities-bukkit/src/test/java/org/aincraft/bukkit/event/BukkitEventEnvelopeTest.java`
- Review: `utilities-bukkit/src/test/java/org/aincraft/bukkit/event/BukkitEventBusTest.java`

**Interfaces:**
- Consumes: the complete adapter from Tasks 1–2 and the approved design spec.
- Produces: verified, formatted, package-isolated `:utilities-bukkit` event adapter with no changes to unrelated worktree files.

- [ ] **Step 1: Run the focused module tests**

Run: `./gradlew :utilities-bukkit:test`

Expected: all `:utilities-bukkit` tests pass, including the new event envelope and bus tests.

- [ ] **Step 2: Run module checks**

Run: `./gradlew :utilities-bukkit:check`

Expected: tests, Spotless checks, jar isolation, no-Paper checks, and no-Bukkit-free checks pass for the module. The new classes remain under the allowed `org/aincraft/bukkit/` prefix.

- [ ] **Step 3: Review the final diff and worktree boundary**

Inspect only the new adapter/spec/plan files and confirm no unrelated pre-existing modifications were changed. Confirm the public API has no placeholder methods, no Bag-specific references, no `HandlerList.unregisterAll(plugin)`, and no platform imports in API/common modules.

- [ ] **Step 4: Commit any formatting-only corrections**

If `check` identifies only formatting changes in the new files, run the project formatter for `:utilities-bukkit`, re-run `./gradlew :utilities-bukkit:check`, and commit only those new-file corrections with:

```bash
git add utilities-bukkit/src/main/java/org/aincraft/bukkit/event utilities-bukkit/src/test/java/org/aincraft/bukkit/event
git commit -m "style: format Bukkit event adapter"
```

No formatter or cleanup should touch unrelated user modifications.
