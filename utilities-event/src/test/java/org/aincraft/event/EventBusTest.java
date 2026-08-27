package org.aincraft.event;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class EventBusTest {

    // ---- fixtures ----

    static class SimpleEvent implements Event {
        final String payload;
        SimpleEvent(String payload) { this.payload = payload; }
    }

    static class BaseEvent implements Event {}
    static class SubEvent extends BaseEvent {}
    static class SubSubEvent extends SubEvent {}

    static class CancellableTestEvent extends AbstractCancellableEvent {
        final String id;
        CancellableTestEvent(String id) { this.id = id; }
    }

    // ---- priority ordering ----

    @Test
    void priorityOrderingSync() {
        EventBus bus = EventBus.create();
        List<String> order = Collections.synchronizedList(new ArrayList<>());

        bus.subscribe(SimpleEvent.class, EventPriority.HIGHEST, e -> order.add("HIGHEST"));
        bus.subscribe(SimpleEvent.class, EventPriority.LOWEST, e -> order.add("LOWEST"));
        bus.subscribe(SimpleEvent.class, EventPriority.NORMAL, e -> order.add("NORMAL"));
        bus.subscribe(SimpleEvent.class, EventPriority.MONITOR, e -> order.add("MONITOR"));
        bus.subscribe(SimpleEvent.class, EventPriority.LOW, e -> order.add("LOW"));
        bus.subscribe(SimpleEvent.class, EventPriority.HIGH, e -> order.add("HIGH"));

        bus.post(new SimpleEvent("x"));

        assertEquals(List.of("LOWEST", "LOW", "NORMAL", "HIGH", "HIGHEST", "MONITOR"), order);
    }

    @Test
    void priorityOrderingAsyncPreservesOrder() throws Exception {
        // single-thread executor ensures deterministic ordering validation
        Executor exec = Executors.newSingleThreadExecutor();
        try {
            EventBus bus = EventBus.create(exec);
            List<String> order = Collections.synchronizedList(new ArrayList<>());

            bus.subscribe(SimpleEvent.class, EventPriority.HIGHEST, e -> order.add("HIGHEST"));
            bus.subscribe(SimpleEvent.class, EventPriority.LOWEST, e -> order.add("LOWEST"));
            bus.subscribe(SimpleEvent.class, EventPriority.NORMAL, e -> order.add("NORMAL"));

            SimpleEvent ev = new SimpleEvent("y");
            SimpleEvent result = bus.postAsync(ev).get(2, TimeUnit.SECONDS);

            assertSame(ev, result);
            assertEquals(List.of("LOWEST", "NORMAL", "HIGHEST"), order);
        } finally {
            ((java.util.concurrent.ExecutorService) exec).shutdownNow();
        }
    }

    @Test
    void samePriorityPreservesRegistrationOrder() {
        EventBus bus = EventBus.create();
        List<Integer> order = new ArrayList<>();
        bus.subscribe(SimpleEvent.class, EventPriority.NORMAL, e -> order.add(1));
        bus.subscribe(SimpleEvent.class, EventPriority.NORMAL, e -> order.add(2));
        bus.subscribe(SimpleEvent.class, EventPriority.NORMAL, e -> order.add(3));
        bus.post(new SimpleEvent("z"));
        assertEquals(List.of(1, 2, 3), order);
    }

    // ---- inheritance dispatch ----

    @Test
    void supertypeListenerReceivesSubtypeSync() {
        EventBus bus = EventBus.create();
        List<String> hit = new ArrayList<>();

        bus.subscribe(BaseEvent.class, e -> hit.add("base"));
        bus.subscribe(SubEvent.class, e -> hit.add("sub"));

        bus.post(new SubEvent());
        assertEquals(List.of("base", "sub"), hit);
        hit.clear();

        bus.post(new BaseEvent());
        assertEquals(List.of("base"), hit);
    }

    @Test
    void supertypeListenerReceivesSubtypeAsync() throws Exception {
        EventBus bus = EventBus.create();
        List<String> hit = Collections.synchronizedList(new ArrayList<>());

        bus.subscribe(BaseEvent.class, e -> hit.add("base"));
        bus.subscribe(SubEvent.class, e -> hit.add("sub"));

        // SubSubEvent should match both Base and Sub listeners
        bus.postAsync(new SubSubEvent()).get(2, TimeUnit.SECONDS);
        // order by registration sequence (both NORMAL so insertion order)
        assertEquals(List.of("base", "sub"), hit);
    }

    @Test
    void eventInterfaceListenerReceivesAll() {
        EventBus bus = EventBus.create();
        List<String> hit = new ArrayList<>();
        bus.subscribe(Event.class, e -> hit.add(e.getClass().getSimpleName()));
        bus.post(new SimpleEvent("a"));
        bus.post(new BaseEvent());
        assertEquals(List.of("SimpleEvent", "BaseEvent"), hit);
    }

    // ---- cancellation filtering ----

    @Test
    void cancellationSyncIgnoreCancelledFiltering() {
        EventBus bus = EventBus.create();
        List<String> called = new ArrayList<>();

        // canceller at NORMAL
        bus.subscribe(CancellableTestEvent.class, EventPriority.NORMAL, e -> {
            called.add("canceller");
            e.setCancelled(true);
        });
        // should be skipped when ignoreCancelled = true
        bus.subscribe(CancellableTestEvent.class, EventPriority.HIGH, true, e -> called.add("skipped"));
        // should still run when ignoreCancelled = false (default)
        bus.subscribe(CancellableTestEvent.class, EventPriority.HIGH, false, e -> called.add("runsDespiteCancelled"));
        // MONITOR that ignores cancelled should also be skipped
        bus.subscribe(CancellableTestEvent.class, EventPriority.MONITOR, true, e -> called.add("monitorSkipped"));

        CancellableTestEvent ev = new CancellableTestEvent("c1");
        bus.post(ev);

        assertTrue(ev.isCancelled());
        assertEquals(List.of("canceller", "runsDespiteCancelled"), called);
    }

    @Test
    void cancellationAsyncFilteringPropagatesSequentially() throws Exception {
        EventBus bus = EventBus.create();
        List<String> called = Collections.synchronizedList(new ArrayList<>());

        bus.subscribe(CancellableTestEvent.class, EventPriority.LOWEST, e -> {
            called.add("first");
            e.setCancelled(true);
        });
        bus.subscribe(CancellableTestEvent.class, EventPriority.NORMAL, true, e -> called.add("shouldSkip"));
        bus.subscribe(CancellableTestEvent.class, EventPriority.NORMAL, false, e -> called.add("shouldRun"));
        bus.subscribe(CancellableTestEvent.class, EventPriority.HIGHEST, true, e -> called.add("alsoSkip"));

        CancellableTestEvent ev = new CancellableTestEvent("c2");
        CancellableTestEvent result = bus.postAsync(ev).get(2, TimeUnit.SECONDS);

        assertTrue(result.isCancelled());
        assertEquals(List.of("first", "shouldRun"), called);
    }

    @Test
    void cancellableEventInitiallyNotCancelled() {
        EventBus bus = EventBus.create();
        CancellableTestEvent ev = new CancellableTestEvent("c3");
        assertFalse(ev.isCancelled());
        bus.post(ev);
        assertFalse(ev.isCancelled());
    }

    @Test
    void perListenerExecutorSyncPostStillRespectsCancellation() {
        // listener with custom executor is run and blocks during sync post, so cancellation still sequential
        Executor exec = Executors.newSingleThreadExecutor();
        try {
            EventBus bus = EventBus.create();
            List<String> called = Collections.synchronizedList(new ArrayList<>());
            bus.subscribe(CancellableTestEvent.class, EventPriority.LOWEST, false, exec, e -> {
                called.add("asyncExecutorListener");
                e.setCancelled(true);
            });
            bus.subscribe(CancellableTestEvent.class, EventPriority.HIGHEST, true, e -> called.add("skipped"));

            bus.post(new CancellableTestEvent("c4"));
            // give executor time (though post blocks)
            Thread.sleep(100);
            assertEquals(List.of("asyncExecutorListener"), called);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            fail(ie);
        } finally {
            ((java.util.concurrent.ExecutorService) exec).shutdownNow();
        }
    }

    // ---- async completion / threading ----

    @Test
    void asyncCompletionRunsOffCallerThread() throws Exception {
        Executor exec = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "event-bus-test-thread");
            t.setDaemon(true);
            return t;
        });
        try {
            EventBus bus = EventBus.create(exec);
            String caller = Thread.currentThread().getName();
            AtomicReference<String> listenerThread = new AtomicReference<>();
            CountDownLatch latch = new CountDownLatch(1);

            bus.subscribe(SimpleEvent.class, e -> {
                listenerThread.set(Thread.currentThread().getName());
                latch.countDown();
            });

            CompletableFuture<SimpleEvent> fut = bus.postAsync(new SimpleEvent("async"));

            SimpleEvent result = fut.get(2, TimeUnit.SECONDS);
            assertNotNull(result);
            assertTrue(latch.await(1, TimeUnit.SECONDS));
            assertNotEquals(caller, listenerThread.get());
            assertEquals("event-bus-test-thread", listenerThread.get());
            assertTrue(fut.isDone());
        } finally {
            ((java.util.concurrent.ExecutorService) exec).shutdownNow();
        }
    }

    @Test
    void asyncFutureCompletesWithSameEventInstance() throws Exception {
        EventBus bus = EventBus.create();
        SimpleEvent ev = new SimpleEvent("same");
        SimpleEvent returned = bus.postAsync(ev).get(2, TimeUnit.SECONDS);
        assertSame(ev, returned);
    }

    @Test
    void asyncWithOverrideExecutor() throws Exception {
        EventBus bus = EventBus.create(); // default commonPool
        Executor override = Executors.newSingleThreadExecutor(r -> new Thread(r, "override-thread"));
        try {
            AtomicReference<String> thread = new AtomicReference<>();
            bus.subscribe(SimpleEvent.class, e -> thread.set(Thread.currentThread().getName()));
            bus.postAsync(new SimpleEvent("override"), override).get(2, TimeUnit.SECONDS);
            assertEquals("override-thread", thread.get());
        } finally {
            ((java.util.concurrent.ExecutorService) override).shutdownNow();
        }
    }

    @Test
    void asyncPerListenerExecutorOverridesBusExecutor() throws Exception {
        Executor busExec = Executors.newSingleThreadExecutor(r -> new Thread(r, "bus-exec"));
        Executor listenerExec = Executors.newSingleThreadExecutor(r -> new Thread(r, "listener-exec"));
        try {
            EventBus bus = EventBus.create(busExec);
            AtomicReference<String> t1 = new AtomicReference<>();
            AtomicReference<String> t2 = new AtomicReference<>();

            // listener with no executor -> uses busExec
            bus.subscribe(SimpleEvent.class, EventPriority.LOWEST, false, null, e -> t1.set(Thread.currentThread().getName()));
            // listener with explicit executor -> uses listenerExec
            bus.subscribe(SimpleEvent.class, EventPriority.HIGHEST, false, listenerExec, e -> t2.set(Thread.currentThread().getName()));

            bus.postAsync(new SimpleEvent("perListener")).get(2, TimeUnit.SECONDS);

            assertEquals("bus-exec", t1.get());
            assertEquals("listener-exec", t2.get());
        } finally {
            ((java.util.concurrent.ExecutorService) busExec).shutdownNow();
            ((java.util.concurrent.ExecutorService) listenerExec).shutdownNow();
        }
    }

    // ---- annotation registration / unregistration ----

    static class AnnotatedListener {
        final List<String> calls = new ArrayList<>();
        boolean monitorCalled = false;

        @Subscribe(priority = EventPriority.LOWEST)
        public void onSimple(SimpleEvent e) {
            calls.add("simple:" + e.payload);
        }

        @Subscribe(priority = EventPriority.HIGH, ignoreCancelled = true)
        public void onCancellable(CancellableTestEvent e) {
            calls.add("cancellable:" + e.id);
        }

        @Subscribe(priority = EventPriority.MONITOR)
        public void onBase(BaseEvent e) {
            monitorCalled = true;
            calls.add("base");
        }
    }

    static class CancelTrigger {
        @Subscribe(priority = EventPriority.LOWEST)
        public void cancel(CancellableTestEvent e) {
            e.setCancelled(true);
        }
    }

    @Test
    void annotationRegistrationAndDispatch() {
        EventBus bus = EventBus.create();
        AnnotatedListener listener = new AnnotatedListener();
        var subs = bus.register(listener);
        assertEquals(3, subs.size());

        bus.post(new SimpleEvent("hello"));
        assertEquals(List.of("simple:hello"), listener.calls);
        listener.calls.clear();

        bus.post(new BaseEvent());
        assertTrue(listener.monitorCalled);
    }

    @Test
    void annotationIgnoreCancelledRespected() {
        EventBus bus = EventBus.create();
        AnnotatedListener annotated = new AnnotatedListener();
        CancelTrigger trigger = new CancelTrigger();
        bus.register(trigger); // cancels at LOWEST
        bus.register(annotated);

        CancellableTestEvent ev = new CancellableTestEvent("annoCancel");
        bus.post(ev);

        // annotated's HIGH ignoreCancelled listener should be skipped
        assertTrue(ev.isCancelled());
        assertTrue(annotated.calls.isEmpty(), "ignoreCancelled listener should have been skipped, got: " + annotated.calls);
    }

    @Test
    void annotationUnregisterRemovesListeners() {
        EventBus bus = EventBus.create();
        AnnotatedListener listener = new AnnotatedListener();
        bus.register(listener);

        bus.post(new SimpleEvent("before"));
        assertEquals(1, listener.calls.size());
        listener.calls.clear();

        bus.unregister(listener);
        bus.post(new SimpleEvent("after"));
        assertTrue(listener.calls.isEmpty());
    }

    @Test
    void annotationUnregisterIsolatedToInstance() {
        EventBus bus = EventBus.create();
        AnnotatedListener a1 = new AnnotatedListener();
        AnnotatedListener a2 = new AnnotatedListener();
        bus.register(a1);
        bus.register(a2);

        bus.unregister(a1);
        a1.calls.clear();
        a2.calls.clear();

        bus.post(new SimpleEvent("test"));
        assertTrue(a1.calls.isEmpty());
        assertEquals(1, a2.calls.size());
    }

    @Test
    void annotationPriorityOrdering() {
        EventBus bus = EventBus.create();

        class PrioListener {
            final List<String> order = new ArrayList<>();
            @Subscribe(priority = EventPriority.HIGHEST)
            public void high(SimpleEvent e) { order.add("HIGH"); }
            @Subscribe(priority = EventPriority.LOWEST)
            public void low(SimpleEvent e) { order.add("LOWEST"); }
            @Subscribe
            public void normal(SimpleEvent e) { order.add("NORMAL"); }
        }
        PrioListener pl = new PrioListener();
        bus.register(pl);
        bus.post(new SimpleEvent("p"));
        assertEquals(List.of("LOWEST", "NORMAL", "HIGH"), pl.order);
    }

    // ---- listener failure behavior ----

    @Test
    void failingListenerDoesNotPreventSubsequentListenersSync() {
        EventBus bus = EventBus.create();
        List<String> calls = new ArrayList<>();

        bus.subscribe(SimpleEvent.class, EventPriority.LOWEST, e -> { throw new RuntimeException("boom"); });
        bus.subscribe(SimpleEvent.class, EventPriority.NORMAL, e -> calls.add("second"));
        bus.subscribe(SimpleEvent.class, EventPriority.HIGHEST, e -> calls.add("third"));

        // should not throw
        assertDoesNotThrow(() -> bus.post(new SimpleEvent("failtest")));
        assertEquals(List.of("second", "third"), calls);
    }

    @Test
    void failingListenerDoesNotPreventSubsequentListenersAsync() throws Exception {
        EventBus bus = EventBus.create();
        List<String> calls = Collections.synchronizedList(new ArrayList<>());

        bus.subscribe(SimpleEvent.class, EventPriority.LOWEST, e -> { throw new IllegalStateException("async boom"); });
        bus.subscribe(SimpleEvent.class, EventPriority.NORMAL, e -> calls.add("second"));
        bus.subscribe(SimpleEvent.class, EventPriority.HIGHEST, e -> calls.add("third"));

        SimpleEvent result = bus.postAsync(new SimpleEvent("afail")).get(2, TimeUnit.SECONDS);
        assertNotNull(result);
        assertEquals(List.of("second", "third"), calls);
    }

    @Test
    void exceptionInCancellableChainStillPropagatesCancellation() throws Exception {
        EventBus bus = EventBus.create();
        List<String> calls = Collections.synchronizedList(new ArrayList<>());

        bus.subscribe(CancellableTestEvent.class, EventPriority.LOWEST, e -> {
            e.setCancelled(true);
            throw new RuntimeException("cancel then boom");
        });
        bus.subscribe(CancellableTestEvent.class, EventPriority.NORMAL, true, e -> calls.add("shouldSkip"));
        bus.subscribe(CancellableTestEvent.class, EventPriority.NORMAL, false, e -> calls.add("shouldRun"));

        bus.postAsync(new CancellableTestEvent("excCancel")).get(2, TimeUnit.SECONDS);
        assertEquals(List.of("shouldRun"), calls);
    }

    @Test
    void annotatedFailingListenerContinues() {
        EventBus bus = EventBus.create();
        List<String> calls = new ArrayList<>();

        class FailAnnotated {
            @Subscribe(priority = EventPriority.LOWEST)
            public void fail(SimpleEvent e) { throw new RuntimeException("anno boom"); }
        }
        class GoodAnnotated {
            @Subscribe(priority = EventPriority.HIGHEST)
            public void good(SimpleEvent e) { calls.add("good"); }
        }

        bus.register(new FailAnnotated());
        bus.register(new GoodAnnotated());

        assertDoesNotThrow(() -> bus.post(new SimpleEvent("mixedFail")));
        assertEquals(List.of("good"), calls);
    }

    // ---- subscription lifecycle ----

    @Test
    void subscriptionUnsubscribeStopsDelivery() {
        EventBus bus = EventBus.create();
        List<String> calls = new ArrayList<>();
        Subscription sub = bus.subscribe(SimpleEvent.class, e -> calls.add("hit"));
        bus.post(new SimpleEvent("1"));
        assertEquals(1, calls.size());

        sub.unsubscribe();
        assertFalse(sub.isActive());
        bus.post(new SimpleEvent("2"));
        assertEquals(1, calls.size());
    }

    @Test
    void subscriptionUnsubscribeIdempotent() {
        EventBus bus = EventBus.create();
        Subscription sub = bus.subscribe(SimpleEvent.class, e -> {});
        sub.unsubscribe();
        assertDoesNotThrow(sub::unsubscribe);
        assertFalse(sub.isActive());
    }

    @Test
    void busUnsubscribeViaBusMethod() {
        EventBus bus = EventBus.create();
        List<String> calls = new ArrayList<>();
        Subscription sub = bus.subscribe(SimpleEvent.class, e -> calls.add("x"));
        bus.unsubscribe(sub);
        assertFalse(sub.isActive());
        bus.post(new SimpleEvent("y"));
        assertTrue(calls.isEmpty());
    }

    @Test
    void postReturnsSameInstanceEvenWhenCancelled() {
        EventBus bus = EventBus.create();
        bus.subscribe(CancellableTestEvent.class, e -> e.setCancelled(true));
        CancellableTestEvent ev = new CancellableTestEvent("ret");
        CancellableTestEvent returned = bus.post(ev);
        assertSame(ev, returned);
        assertTrue(returned.isCancelled());
    }

    @Test
    void syncPostIsInlineOnCallerThread() {
        EventBus bus = EventBus.create();
        String caller = Thread.currentThread().getName();
        AtomicReference<String> listenerThread = new AtomicReference<>();
        bus.subscribe(SimpleEvent.class, e -> listenerThread.set(Thread.currentThread().getName()));
        bus.post(new SimpleEvent("inline"));
        assertEquals(caller, listenerThread.get());
    }
}
