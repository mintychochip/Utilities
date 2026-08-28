package org.aincraft.api.plugin;

import org.jetbrains.annotations.NotNull;

/**
 * Platform-neutral plugin lifecycle callbacks.
 *
 * <p>Callbacks run in this order: {@link #onLoad(PluginContext)}, {@link #onEnable(PluginContext)},
 * and {@link #onDisable(PluginContext)}. Platform entrypoints ensure each callback runs at most
 * once per lifecycle.
 */
public interface PluginLifecycle {

  /** Called once before the plugin is enabled. */
  default void onLoad(@NotNull PluginContext context) {}

  /** Called once when the plugin becomes active. */
  void onEnable(@NotNull PluginContext context);

  /** Called once when the plugin is disabled after a successful enable. */
  default void onDisable(@NotNull PluginContext context) {}
}
