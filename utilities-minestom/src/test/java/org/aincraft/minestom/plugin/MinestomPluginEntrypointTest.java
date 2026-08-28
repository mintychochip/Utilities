package org.aincraft.minestom.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.aincraft.api.plugin.PluginContext;
import org.aincraft.api.plugin.PluginLifecycle;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class MinestomPluginEntrypointTest {

  @Test
  void delegatesManualCallbacksToSharedLifecycle() {
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

    MinestomPluginEntrypoint entrypoint = new MinestomPluginEntrypoint(lifecycle, context);
    entrypoint.onDisable();
    entrypoint.onEnable();
    entrypoint.onLoad();
    entrypoint.onDisable();
    entrypoint.onDisable();

    assertEquals(List.of("load", "enable", "disable"), calls);
  }
}
