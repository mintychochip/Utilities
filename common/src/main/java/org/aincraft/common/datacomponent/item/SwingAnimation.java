package org.aincraft.common.datacomponent.item;

/**
 * Common contract for the {@code SwingAnimation} data component, mirroring Paper's {@code
 * SwingAnimation}.
 */
public interface SwingAnimation {

  Animation type();

  int duration();

  /**
   * Common contract for a swing animation type, mirroring Paper's {@code SwingAnimation.Animation}.
   */
  enum Animation {
    NONE,
    WHACK,
    STAB
  }
}
