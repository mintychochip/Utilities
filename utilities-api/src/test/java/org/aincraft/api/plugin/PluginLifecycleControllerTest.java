package org.aincraft.api.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class PluginLifecycleControllerTest {

  @Test
  void enforcesLifecycleOrderAndAtMostOnceCallbacks() {
    PluginContext context = PluginContext.empty();
    List<String> calls = new ArrayList<>();
    PluginLifecycle lifecycle =
        new PluginLifecycle() {
          @Override
          public void onLoad(PluginContext actualContext) {
            assertSame(context, actualContext);
            calls.add("load");
          }

          @Override
          public void onEnable(PluginContext actualContext) {
            assertSame(context, actualContext);
            calls.add("enable");
          }

          @Override
          public void onDisable(PluginContext actualContext) {
            assertSame(context, actualContext);
            calls.add("disable");
          }
        };

    PluginLifecycleController controller = new PluginLifecycleController(lifecycle, context);
    controller.onDisable();
    controller.onEnable();
    controller.onLoad();
    controller.onEnable();
    controller.onDisable();
    controller.onDisable();

    assertEquals(List.of("load", "enable", "disable"), calls);
  }

  @Test
  void ignoresReentrantLifecycleCalls() {
    PluginContext context = PluginContext.empty();
    List<String> calls = new ArrayList<>();
    PluginLifecycleController[] holder = new PluginLifecycleController[1];
    PluginLifecycle lifecycle =
        new PluginLifecycle() {
          @Override
          public void onLoad(PluginContext ignored) {
            calls.add("load");
            holder[0].onEnable();
          }

          @Override
          public void onEnable(PluginContext ignored) {
            calls.add("enable");
            holder[0].onEnable();
          }

          @Override
          public void onDisable(PluginContext ignored) {
            calls.add("disable");
            holder[0].onDisable();
          }
        };
    holder[0] = new PluginLifecycleController(lifecycle, context);

    holder[0].onEnable();
    holder[0].onDisable();

    assertEquals(List.of("load", "enable", "disable"), calls);
  }
}
