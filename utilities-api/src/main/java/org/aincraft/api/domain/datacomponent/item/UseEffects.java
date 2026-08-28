package org.aincraft.api.domain.datacomponent.item;

/** Common contract for item use effects, mirroring Paper's {@code UseEffects}. */
public interface UseEffects {

  boolean canSprint();

  boolean interactVibrations();

  float speedMultiplier();
}
