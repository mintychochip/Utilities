package org.aincraft.bukkit.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.aincraft.api.event.Cancellable;
import org.aincraft.api.event.EventBus;
import org.aincraft.api.event.EventListener;
import org.aincraft.api.event.EventPriority;
import org.aincraft.api.event.Subscription;
import org.aincraft.event.EventBuses;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

class BukkitEventBusTest {
  private final Plugin plugin = mock(Plugin.class);
  private final Server server = mock(Server.class);
  private final PluginManager pluginManager = mock(PluginManager.class);

  @BeforeEach
  void setUp() {
    when(plugin.getServer()).thenReturn(server);
    when(server.getPluginManager()).thenReturn(pluginManager);
  }

  @Test
  void forwardsTheLiveEventAndRegisteredType() throws Exception {
    EventBus delegate = EventBuses.create();
    BukkitEventBus bus = new BukkitEventBus(plugin, delegate);
    List<BukkitEvent<?>> received = new ArrayList<>();
    delegate.subscribe(BukkitEvent.class, received::add);
    bus.registerBukkitEvent(TestEvent.class);

    EventExecutor executor = captureExecutor(TestEvent.class, EventPriority.LOWEST);
    Listener listener = captureListener(TestEvent.class, EventPriority.LOWEST);
    TestEvent source = new TestEvent();

    executor.execute(listener, source);

    assertEquals(1, received.size());
    assertSame(source, received.get(0).event());
    assertEquals(TestEvent.class, received.get(0).eventType());
    assertFalse(received.get(0) instanceof Cancellable);
  }

  @Test
  void cancellableForwardingSharesCancellationAndSkipsIgnoredListeners() throws Exception {
    EventBus delegate = EventBuses.create();
    BukkitEventBus bus = new BukkitEventBus(plugin, delegate);
    List<String> calls = new ArrayList<>();
    delegate.subscribe(
        BukkitEvent.class,
        EventPriority.LOWEST,
        event -> {
          assertInstanceOf(Cancellable.class, event);
          ((Cancellable) event).setCancelled(true);
          calls.add("cancel");
        });
    delegate.subscribe(BukkitEvent.class, EventPriority.HIGH, true, event -> calls.add("ignored"));
    bus.registerBukkitEvent(TestCancellableEvent.class);

    EventExecutor executor = captureExecutor(TestCancellableEvent.class, EventPriority.LOWEST);
    Listener listener = captureListener(TestCancellableEvent.class, EventPriority.LOWEST);
    TestCancellableEvent source = new TestCancellableEvent();
    executor.execute(listener, source);

    assertTrue(source.isCancelled());
    assertEquals(List.of("cancel"), calls);

    source.setCancelled(true);
    calls.clear();
    executor.execute(listener, source);
    assertEquals(List.of("cancel"), calls);
  }

  @Test
  void typedSubscriptionRegistersOnceAndFiltersByOriginalBukkitType() throws Exception {
    EventBus delegate = EventBuses.create();
    BukkitEventBus bus = new BukkitEventBus(plugin, delegate);
    List<TestEvent> received = new ArrayList<>();

    Subscription subscription =
        bus.subscribeBukkitEvent(TestEvent.class, envelope -> received.add(envelope.event()));

    assertTrue(subscription.isActive());
    verify(pluginManager, times(1))
        .registerEvent(
            eq(TestEvent.class),
            any(Listener.class),
            eq(org.bukkit.event.EventPriority.LOWEST),
            any(EventExecutor.class),
            eq(plugin),
            eq(false));

    EventExecutor executor = captureExecutor(TestEvent.class, EventPriority.LOWEST);
    Listener listener = captureListener(TestEvent.class, EventPriority.LOWEST);
    TestEvent matching = new TestEvent();
    executor.execute(listener, matching);
    delegate.post(new BukkitEvent<>(new OtherEvent(), OtherEvent.class));

    assertEquals(List.of(matching), received);
  }

  @Test
  void explicitCapturePriorityIsPassedToBukkit() {
    BukkitEventBus bus = new BukkitEventBus(plugin, EventBuses.create());

    BukkitEventRegistration<TestEvent> registration =
        bus.registerBukkitEvent(TestEvent.class, org.bukkit.event.EventPriority.MONITOR);

    assertEquals(org.bukkit.event.EventPriority.MONITOR, registration.capturePriority());
    verify(pluginManager)
        .registerEvent(
            eq(TestEvent.class),
            any(Listener.class),
            eq(org.bukkit.event.EventPriority.MONITOR),
            any(EventExecutor.class),
            eq(plugin),
            eq(false));
  }

  @Test
  void duplicateRegistrationReturnsExistingHandle() {
    BukkitEventBus bus = new BukkitEventBus(plugin, EventBuses.create());

    BukkitEventRegistration<TestEvent> first = bus.registerBukkitEvent(TestEvent.class);
    BukkitEventRegistration<TestEvent> second =
        bus.registerBukkitEvent(TestEvent.class, org.bukkit.event.EventPriority.HIGH);

    assertSame(first, second);
    verify(pluginManager, times(1))
        .registerEvent(
            eq(TestEvent.class),
            any(Listener.class),
            eq(org.bukkit.event.EventPriority.LOWEST),
            any(EventExecutor.class),
            eq(plugin),
            eq(false));
  }

  @Test
  void individualRegistrationCleanupIsIdempotent() {
    BukkitEventBus bus = new BukkitEventBus(plugin, EventBuses.create());
    BukkitEventRegistration<TestEvent> registration = bus.registerBukkitEvent(TestEvent.class);
    Listener listener = captureListener(TestEvent.class, EventPriority.LOWEST);

    try (var handlers = mockStatic(HandlerList.class)) {
      registration.unregister();
      registration.close();

      handlers.verify(() -> HandlerList.unregisterAll(listener), times(1));
    }
    assertFalse(registration.isActive());
  }

  @Test
  void replacementWaitsForPreviousListenerRemoval() throws Exception {
    BukkitEventBus bus = new BukkitEventBus(plugin, EventBuses.create());
    BukkitEventRegistration<TestEvent> first = bus.registerBukkitEvent(TestEvent.class);
    Listener listener = captureListener(TestEvent.class, EventPriority.LOWEST);
    CountDownLatch replacementStarted = new CountDownLatch(1);
    CountDownLatch replacementFinished = new CountDownLatch(1);
    AtomicReference<BukkitEventRegistration<TestEvent>> replacement = new AtomicReference<>();

    try (var handlers = mockStatic(HandlerList.class)) {
      handlers
          .when(() -> HandlerList.unregisterAll(listener))
          .thenAnswer(
              invocation -> {
                Thread replacementThread =
                    new Thread(
                        () -> {
                          replacementStarted.countDown();
                          replacement.set(bus.registerBukkitEvent(TestEvent.class));
                          replacementFinished.countDown();
                        });
                replacementThread.start();
                assertTrue(replacementStarted.await(1, TimeUnit.SECONDS));
                assertFalse(replacementFinished.await(100, TimeUnit.MILLISECONDS));
                return null;
              });
      first.unregister();
    }

    assertTrue(replacementFinished.await(1, TimeUnit.SECONDS));
    assertNotSame(first, replacement.get());
    verify(pluginManager, times(2))
        .registerEvent(
            eq(TestEvent.class),
            any(Listener.class),
            eq(org.bukkit.event.EventPriority.LOWEST),
            any(EventExecutor.class),
            eq(plugin),
            eq(false));
  }

  @Test
  void closeCleansAllPlatformRegistrationsButLeavesDelegateUntouched() {
    RecordingEventBus delegate = new RecordingEventBus();
    BukkitEventBus bus = new BukkitEventBus(plugin, delegate);
    bus.registerBukkitEvent(TestEvent.class);
    bus.registerBukkitEvent(OtherEvent.class);
    Listener first = captureListener(TestEvent.class, EventPriority.LOWEST);
    Listener second = captureListener(OtherEvent.class, EventPriority.LOWEST);

    try (var handlers = mockStatic(HandlerList.class)) {
      bus.close();
      bus.close();

      handlers.verify(() -> HandlerList.unregisterAll(first), times(1));
      handlers.verify(() -> HandlerList.unregisterAll(second), times(1));
    }
    assertEquals(0, delegate.postCalls);
    assertEquals(0, delegate.subscribeCalls);
    assertThrows(IllegalStateException.class, () -> bus.registerBukkitEvent(TestEvent.class));
  }

  @Test
  void failedPlatformRegistrationDoesNotPoisonTheEventType() {
    BukkitEventBus bus = new BukkitEventBus(plugin, EventBuses.create());
    doThrow(new IllegalArgumentException("invalid event"))
        .when(pluginManager)
        .registerEvent(
            eq(TestEvent.class),
            any(Listener.class),
            any(org.bukkit.event.EventPriority.class),
            any(EventExecutor.class),
            eq(plugin),
            eq(false));

    assertThrows(IllegalArgumentException.class, () -> bus.registerBukkitEvent(TestEvent.class));
    reset(pluginManager);
    BukkitEventRegistration<TestEvent> registration = bus.registerBukkitEvent(TestEvent.class);

    assertTrue(registration.isActive());
    verify(pluginManager)
        .registerEvent(
            eq(TestEvent.class),
            any(Listener.class),
            eq(org.bukkit.event.EventPriority.LOWEST),
            any(EventExecutor.class),
            eq(plugin),
            eq(false));
  }

  @Test
  void closeWaitsForTypedSubscriptionSetup() throws Exception {
    CountDownLatch subscribeStarted = new CountDownLatch(1);
    CountDownLatch allowSubscribe = new CountDownLatch(1);
    BlockingSubscribeEventBus delegate =
        new BlockingSubscribeEventBus(subscribeStarted, allowSubscribe);
    BukkitEventBus bus = new BukkitEventBus(plugin, delegate);
    AtomicReference<Subscription> result = new AtomicReference<>();
    AtomicReference<Throwable> failure = new AtomicReference<>();

    Thread subscribeThread =
        new Thread(
            () -> {
              try {
                result.set(bus.subscribeBukkitEvent(TestEvent.class, event -> {}));
              } catch (Throwable throwable) {
                failure.set(throwable);
              }
            });
    subscribeThread.start();
    assertTrue(subscribeStarted.await(1, TimeUnit.SECONDS));

    CountDownLatch closeReturned = new CountDownLatch(1);
    Thread closeThread =
        new Thread(
            () -> {
              bus.close();
              closeReturned.countDown();
            });
    closeThread.start();

    boolean closeWasBlocked = !closeReturned.await(100, TimeUnit.MILLISECONDS);
    allowSubscribe.countDown();
    subscribeThread.join(1_000);
    closeThread.join(1_000);

    assertTrue(closeWasBlocked);
    assertFalse(subscribeThread.isAlive());
    assertFalse(closeThread.isAlive());
    assertTrue(failure.get() == null, () -> "typed subscription failed: " + failure.get());
    assertTrue(result.get() != null);
  }

  private static final class BlockingSubscribeEventBus implements EventBus {
    private final EventBus actual = EventBuses.create();
    private final CountDownLatch subscribeStarted;
    private final CountDownLatch allowSubscribe;

    private BlockingSubscribeEventBus(
        CountDownLatch subscribeStarted, CountDownLatch allowSubscribe) {
      this.subscribeStarted = subscribeStarted;
      this.allowSubscribe = allowSubscribe;
    }

    @Override
    public <E extends org.aincraft.api.event.Event> Subscription subscribe(
        Class<E> eventType,
        EventPriority priority,
        boolean ignoreCancelled,
        Executor executor,
        EventListener<? super E> listener) {
      subscribeStarted.countDown();
      try {
        allowSubscribe.await();
      } catch (InterruptedException interrupted) {
        Thread.currentThread().interrupt();
        throw new AssertionError(interrupted);
      }
      return actual.subscribe(eventType, priority, ignoreCancelled, executor, listener);
    }

    @Override
    public List<Subscription> register(Object instance) {
      return actual.register(instance);
    }

    @Override
    public void unregister(Object instance) {
      actual.unregister(instance);
    }

    @Override
    public void unsubscribe(Subscription subscription) {
      actual.unsubscribe(subscription);
    }

    @Override
    public <E extends org.aincraft.api.event.Event> E post(E event) {
      return actual.post(event);
    }

    @Override
    public <E extends org.aincraft.api.event.Event> CompletableFuture<E> postAsync(
        E event, Executor executor) {
      return actual.postAsync(event, executor);
    }
  }

  @Test
  void failedTypedSubscriptionRollsBackOnlyNewBukkitRegistration() {
    RecordingEventBus delegate = new RecordingEventBus();
    IllegalStateException failure = new IllegalStateException("delegate rejected subscription");
    delegate.subscribeFailure = failure;
    BukkitEventBus bus = new BukkitEventBus(plugin, delegate);

    try (var handlers = mockStatic(HandlerList.class)) {
      assertThrows(
          IllegalStateException.class,
          () -> bus.subscribeBukkitEvent(TestEvent.class, event -> {}));
      Listener failedListener = captureListener(TestEvent.class, EventPriority.LOWEST);
      handlers.verify(() -> HandlerList.unregisterAll(failedListener), times(1));
    }

    delegate.subscribeFailure = null;
    BukkitEventRegistration<TestEvent> replacement = bus.registerBukkitEvent(TestEvent.class);
    assertTrue(replacement.isActive());
    verify(pluginManager, times(2))
        .registerEvent(
            eq(TestEvent.class),
            any(Listener.class),
            eq(org.bukkit.event.EventPriority.LOWEST),
            any(EventExecutor.class),
            eq(plugin),
            eq(false));
  }

  @Test
  void utilityEventsUseTheInjectedDelegateAndNeverEnterBukkit() {
    RecordingEventBus delegate = new RecordingEventBus();
    BukkitEventBus bus = new BukkitEventBus(plugin, delegate);
    UtilityEvent utilityEvent = new UtilityEvent();
    Object registeredListener = new Object();

    assertTrue(bus.register(registeredListener).isEmpty());
    bus.unregister(registeredListener);
    Subscription subscription = bus.subscribe(UtilityEvent.class, event -> {});
    bus.unsubscribe(subscription);
    assertSame(utilityEvent, bus.post(utilityEvent));
    assertSame(utilityEvent, bus.postAsync(utilityEvent).join());
    Executor explicitExecutor = Runnable::run;
    assertSame(utilityEvent, bus.postAsync(utilityEvent, explicitExecutor).join());

    assertEquals(1, delegate.registerCalls);
    assertSame(registeredListener, delegate.lastRegisteredInstance);
    assertEquals(1, delegate.unregisterCalls);
    assertSame(registeredListener, delegate.lastUnregisteredInstance);
    assertEquals(1, delegate.subscribeCalls);
    assertSame(subscription, delegate.lastSubscription);
    assertEquals(1, delegate.unsubscribeCalls);
    assertSame(subscription, delegate.lastUnsubscribedSubscription);
    assertEquals(1, delegate.postCalls);
    assertEquals(2, delegate.postAsyncCalls);
    assertSame(explicitExecutor, delegate.lastAsyncExecutor);
    verifyNoInteractions(pluginManager);
  }

  @Test
  void typedSubscriptionPassesPriorityCancellationAndExecutorToDelegate() {
    RecordingEventBus delegate = new RecordingEventBus();
    BukkitEventBus bus = new BukkitEventBus(plugin, delegate);
    Executor executor = Runnable::run;
    EventListener<? super BukkitEvent<TestEvent>> listener = event -> {};

    Subscription result =
        bus.subscribeBukkitEvent(TestEvent.class, EventPriority.HIGH, true, executor, listener);

    assertSame(delegate.lastSubscription, result);
    assertEquals(BukkitEvent.class, delegate.lastEventType);
    assertEquals(EventPriority.HIGH, delegate.lastPriority);
    assertTrue(delegate.lastIgnoreCancelled);
    assertSame(executor, delegate.lastExecutor);
  }

  private static final class RecordingEventBus implements EventBus {
    private final EventBus actual = EventBuses.create();
    private int registerCalls;
    private int unregisterCalls;
    private RuntimeException subscribeFailure;
    private int subscribeCalls;
    private int unsubscribeCalls;
    private int postCalls;
    private int postAsyncCalls;
    private Object lastRegisteredInstance;
    private Object lastUnregisteredInstance;
    private Subscription lastUnsubscribedSubscription;
    private Class<? extends org.aincraft.api.event.Event> lastEventType;
    private EventPriority lastPriority;
    private boolean lastIgnoreCancelled;
    private Executor lastExecutor;
    private Executor lastAsyncExecutor;
    private Subscription lastSubscription;

    @Override
    public <E extends org.aincraft.api.event.Event> Subscription subscribe(
        Class<E> eventType,
        EventPriority priority,
        boolean ignoreCancelled,
        Executor executor,
        EventListener<? super E> listener) {
      subscribeCalls++;
      if (subscribeFailure != null) {
        throw subscribeFailure;
      }
      lastEventType = eventType;
      lastPriority = priority;
      lastIgnoreCancelled = ignoreCancelled;
      lastExecutor = executor;
      lastSubscription = actual.subscribe(eventType, priority, ignoreCancelled, executor, listener);
      return lastSubscription;
    }

    public List<Subscription> register(Object instance) {
      registerCalls++;
      lastRegisteredInstance = instance;
      return actual.register(instance);
    }

    public void unregister(Object instance) {
      unregisterCalls++;
      lastUnregisteredInstance = instance;
      actual.unregister(instance);
    }

    public void unsubscribe(Subscription subscription) {
      unsubscribeCalls++;
      lastUnsubscribedSubscription = subscription;
      actual.unsubscribe(subscription);
    }

    public <E extends org.aincraft.api.event.Event> E post(E event) {
      postCalls++;
      return actual.post(event);
    }

    public <E extends org.aincraft.api.event.Event> CompletableFuture<E> postAsync(
        E event, Executor executor) {
      postAsyncCalls++;
      lastAsyncExecutor = executor;
      return actual.postAsync(event, executor);
    }
  }

  private EventExecutor captureExecutor(
      Class<? extends Event> eventType, EventPriority ignoredUtilityPriority) {
    ArgumentCaptor<EventExecutor> captor = ArgumentCaptor.forClass(EventExecutor.class);
    verify(pluginManager)
        .registerEvent(
            eq(eventType),
            any(Listener.class),
            eq(org.bukkit.event.EventPriority.LOWEST),
            captor.capture(),
            eq(plugin),
            eq(false));
    return captor.getValue();
  }

  private Listener captureListener(
      Class<? extends Event> eventType, EventPriority ignoredUtilityPriority) {
    ArgumentCaptor<Listener> captor = ArgumentCaptor.forClass(Listener.class);
    verify(pluginManager)
        .registerEvent(
            eq(eventType),
            captor.capture(),
            eq(org.bukkit.event.EventPriority.LOWEST),
            any(EventExecutor.class),
            eq(plugin),
            eq(false));
    return captor.getValue();
  }

  static class TestEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    TestEvent() {
      super(false);
    }

    @Override
    public HandlerList getHandlers() {
      return HANDLERS;
    }

    public static HandlerList getHandlerList() {
      return HANDLERS;
    }
  }

  static final class OtherEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    OtherEvent() {
      super(false);
    }

    @Override
    public HandlerList getHandlers() {
      return HANDLERS;
    }

    public static HandlerList getHandlerList() {
      return HANDLERS;
    }
  }

  static final class TestCancellableEvent extends Event implements org.bukkit.event.Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;

    TestCancellableEvent() {
      super(false);
    }

    @Override
    public boolean isCancelled() {
      return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
      return HANDLERS;
    }

    public static HandlerList getHandlerList() {
      return HANDLERS;
    }
  }

  static final class UtilityEvent implements org.aincraft.api.event.Event {}
}
