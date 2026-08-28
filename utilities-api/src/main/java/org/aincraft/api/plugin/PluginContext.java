package org.aincraft.api.plugin;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Logger;

/** Platform-neutral services available to a plugin lifecycle. */
public record PluginContext(
    @NotNull String name, @NotNull Path dataDirectory, @NotNull Logger logger) {

  public PluginContext {
    Objects.requireNonNull(name, "name cannot be null");
    if (name.isBlank()) {
      throw new IllegalArgumentException("name cannot be blank");
    }
    Objects.requireNonNull(dataDirectory, "dataDirectory cannot be null");
    Objects.requireNonNull(logger, "logger cannot be null");
  }

  /** Returns a usable context for hosts without platform metadata. */
  public static @NotNull PluginContext empty() {
    return new PluginContext("plugin", Path.of("."), Logger.getLogger("plugin"));
  }
}
