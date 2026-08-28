package org.aincraft.api;

import org.jetbrains.annotations.NotNull;

/**
 * Unchecked exception thrown when an API call requires a capability the active adapter does not
 * support. Distinct from {@link UnsupportedOperationException}, which signals caller misuse or
 * unimplemented-but-planned methods.
 *
 * <p>The {@link #capability} field carries the missing capability so callers can branch on it.
 */
public class UnsupportedCapabilityException extends RuntimeException {

  private final Capability capability;

  public UnsupportedCapabilityException(@NotNull Capability capability) {
    super("Capability not supported: " + capability.name());
    this.capability = capability;
  }

  public UnsupportedCapabilityException(@NotNull Capability capability, @NotNull String detail) {
    super("Capability not supported: " + capability.name() + " — " + detail);
    this.capability = capability;
  }

  public @NotNull Capability capability() {
    return capability;
  }
}
