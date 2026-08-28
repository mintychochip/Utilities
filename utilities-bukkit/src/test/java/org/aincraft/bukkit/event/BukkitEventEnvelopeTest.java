package org.aincraft.bukkit.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

class BukkitEventEnvelopeTest {

  @Test
  void envelopeKeepsTheLiveEventAndRegisteredType() {
    TestEvent source = new TestEvent();
    BukkitEvent<TestEvent> envelope = new BukkitEvent<>(source, TestEvent.class);

    assertSame(source, envelope.event());
    assertEquals(TestEvent.class, envelope.eventType());
    assertInstanceOf(org.aincraft.api.event.Event.class, envelope);
  }

  @Test
  void publicEnvelopeConstructorDerivesRuntimeType() {
    TestEvent source = new TestEvent();
    BukkitEvent<TestEvent> envelope = new BukkitEvent<>(source);

    assertEquals(TestEvent.class, envelope.eventType());
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

  @Test
  void registrationUnregistersOnceAndExposesMetadata() {
    Listener listener = new Listener() {};
    AtomicInteger unregisterCalls = new AtomicInteger();
    BukkitEventRegistration<TestEvent> registration =
        new BukkitEventRegistration<>(
            TestEvent.class, EventPriority.HIGH, listener, unregisterCalls::incrementAndGet);

    assertEquals(TestEvent.class, registration.eventType());
    assertEquals(EventPriority.HIGH, registration.capturePriority());
    assertTrue(registration.isActive());

    registration.unregister();
    registration.unregister();
    registration.close();

    assertEquals(1, unregisterCalls.get());
    assertFalse(registration.isActive());
  }

  @Test
  void registrationStaysActiveUntilCleanupCompletes() throws Exception {
    CountDownLatch cleanupStarted = new CountDownLatch(1);
    CountDownLatch allowCleanup = new CountDownLatch(1);
    AtomicBoolean cleanupFinished = new AtomicBoolean();
    BukkitEventRegistration<TestEvent> registration =
        new BukkitEventRegistration<>(
            TestEvent.class,
            EventPriority.NORMAL,
            new Listener() {},
            () -> {
              cleanupStarted.countDown();
              try {
                allowCleanup.await();
              } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                throw new AssertionError(interrupted);
              }
              cleanupFinished.set(true);
            });

    Thread unregisterThread = new Thread(registration::unregister);
    unregisterThread.start();
    assertTrue(cleanupStarted.await(1, TimeUnit.SECONDS));
    assertTrue(registration.isActive());
    assertFalse(cleanupFinished.get());

    allowCleanup.countDown();
    unregisterThread.join(1_000);

    assertTrue(cleanupFinished.get());
    assertFalse(registration.isActive());
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

  static final class TestCancellableEvent extends Event implements Cancellable {
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
}
