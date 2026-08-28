package org.aincraft.api.plugin;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/** Runs a {@link PluginLifecycle} with consistent ordering across platforms. */
public final class PluginLifecycleController {

  private enum State {
    NEW,
    LOADING,
    LOADED,
    ENABLING,
    ENABLED,
    DISABLING,
    DISABLED,
    FAILED
  }

  private final PluginLifecycle lifecycle;
  private final PluginContext context;
  private State state = State.NEW;

  public PluginLifecycleController(
      @NotNull PluginLifecycle lifecycle, @NotNull PluginContext context) {
    this.lifecycle = Objects.requireNonNull(lifecycle, "lifecycle cannot be null");
    this.context = Objects.requireNonNull(context, "context cannot be null");
  }

  /** Loads the lifecycle once. */
  public synchronized void onLoad() {
    if (state != State.NEW) {
      return;
    }
    state = State.LOADING;
    try {
      lifecycle.onLoad(context);
      state = State.LOADED;
    } catch (RuntimeException | Error failure) {
      state = State.FAILED;
      throw failure;
    }
  }

  /** Loads and enables the lifecycle once. */
  public synchronized void onEnable() {
    if (state == State.NEW) {
      onLoad();
    }
    if (state != State.LOADED) {
      return;
    }
    state = State.ENABLING;
    try {
      lifecycle.onEnable(context);
      state = State.ENABLED;
    } catch (RuntimeException | Error failure) {
      state = State.FAILED;
      throw failure;
    }
  }

  /** Disables the lifecycle once after a successful enable. */
  public synchronized void onDisable() {
    if (state != State.ENABLED) {
      return;
    }
    state = State.DISABLING;
    try {
      lifecycle.onDisable(context);
      state = State.DISABLED;
    } catch (RuntimeException | Error failure) {
      state = State.FAILED;
      throw failure;
    }
  }
}
