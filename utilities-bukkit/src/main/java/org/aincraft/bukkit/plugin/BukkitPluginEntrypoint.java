package org.aincraft.bukkit.plugin;

import org.aincraft.api.plugin.PluginContext;
import org.aincraft.api.plugin.PluginLifecycle;
import org.aincraft.api.plugin.PluginLifecycleController;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit {@link JavaPlugin} bridge for a platform-neutral {@link PluginLifecycle}.
 *
 * <p>Extend this class from the plugin main class and return the shared lifecycle from {@link
 * #createLifecycle()}. The same entrypoint works on Bukkit, Spigot, and Paper.
 */
public abstract class BukkitPluginEntrypoint extends JavaPlugin {

  private PluginLifecycleController controller;

  /** Creates the lifecycle implementation owned by this plugin instance. */
  protected abstract @NotNull PluginLifecycle createLifecycle();

  /** Creates the context passed to lifecycle callbacks. */
  protected @NotNull PluginContext createContext() {
    return new PluginContext(getName(), getDataFolder().toPath(), getLogger());
  }

  private @NotNull PluginLifecycleController controller() {
    if (controller == null) {
      controller = new PluginLifecycleController(createLifecycle(), createContext());
    }
    return controller;
  }

  @Override
  public final void onLoad() {
    controller().onLoad();
  }

  @Override
  public final void onEnable() {
    controller().onEnable();
  }

  @Override
  public final void onDisable() {
    if (controller != null) {
      controller.onDisable();
    }
  }
}
