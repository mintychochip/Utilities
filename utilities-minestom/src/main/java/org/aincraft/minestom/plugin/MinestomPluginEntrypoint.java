package org.aincraft.minestom.plugin;

import org.aincraft.api.plugin.PluginContext;
import org.aincraft.api.plugin.PluginLifecycle;
import org.aincraft.api.plugin.PluginLifecycleController;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Manual Minestom bridge for a platform-neutral {@link PluginLifecycle}.
 *
 * <p>Minestom has no {@code JavaPlugin} lifecycle. Create this entrypoint during server startup and
 * call {@link #onLoad()}, {@link #onEnable()}, and {@link #onDisable()} from the host application.
 */
public final class MinestomPluginEntrypoint {

  private final PluginLifecycleController controller;

  public MinestomPluginEntrypoint(@NotNull PluginLifecycle lifecycle) {
    this(lifecycle, PluginContext.empty());
  }

  public MinestomPluginEntrypoint(
      @NotNull PluginLifecycle lifecycle, @NotNull PluginContext context) {
    this.controller =
        new PluginLifecycleController(
            Objects.requireNonNull(lifecycle, "lifecycle cannot be null"),
            Objects.requireNonNull(context, "context cannot be null"));
  }

  public void onLoad() {
    controller.onLoad();
  }

  public void onEnable() {
    controller.onEnable();
  }

  public void onDisable() {
    controller.onDisable();
  }
}
