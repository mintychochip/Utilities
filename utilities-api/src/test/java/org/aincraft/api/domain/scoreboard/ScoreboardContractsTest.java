package org.aincraft.api.domain.scoreboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.aincraft.api.domain.entity.Player;
import org.aincraft.api.domain.server.Server;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

class ScoreboardContractsTest {

  @Test
  void criteriaFactoryRejectsBlankNames() {
    assertThrows(IllegalArgumentException.class, () -> Criteria.of("  "));
  }

  @Test
  void criteriaExposesStandardDummyContract() {
    assertEquals("dummy", Criteria.DUMMY.name());
    assertEquals(RenderType.INTEGER, Criteria.DUMMY.defaultRenderType());
    assertTrue(!Criteria.DUMMY.isReadOnly());
  }

  @Test
  void criteriaExposesReadOnlyTriggerContract() {
    assertEquals("trigger", Criteria.TRIGGER.name());
    assertTrue(Criteria.TRIGGER.isReadOnly());
  }

  @Test
  void serverAndPlayerDefaultsReportUnsupportedScoreboardCapability() {
    Server server = defaultMethodProxy(Server.class);
    Player player = defaultMethodProxy(Player.class);

    UnsupportedCapabilityException serverFailure =
        assertThrows(UnsupportedCapabilityException.class, server::scoreboardManager);
    UnsupportedCapabilityException playerFailure =
        assertThrows(UnsupportedCapabilityException.class, player::scoreboard);

    assertEquals(Capability.SCOREBOARD, serverFailure.capability());
    assertEquals(Capability.SCOREBOARD, playerFailure.capability());
  }

  private static <T> T defaultMethodProxy(Class<T> type) {
    InvocationHandler handler =
        (proxy, method, args) -> {
          if (!method.isDefault()) {
            throw new UnsupportedOperationException(method.toString());
          }
          MethodHandles.Lookup lookup =
              MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
          return lookup
              .unreflectSpecial(method, method.getDeclaringClass())
              .bindTo(proxy)
              .invokeWithArguments(args == null ? new Object[0] : args);
        };
    return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, handler));
  }
}
