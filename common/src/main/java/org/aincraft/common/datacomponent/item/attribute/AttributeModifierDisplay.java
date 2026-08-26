package org.aincraft.common.datacomponent.item.attribute;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Common contract for how an attribute modifier entry should be displayed.
 *
 * <p>The three possible states are:</p>
 * <ul>
 *   <li>{@link Type#DEFAULT} – use the default attribute-line formatting</li>
 *   <li>{@link Type#HIDDEN} – do not show the attribute line at all</li>
 *   <li>{@link Type#OVERRIDE} – replace the attribute line with custom text</li>
 * </ul>
 *
 * <p>Factories are provided by {@link AttributeModifierDisplays}.</p>
 *
 * <p>Mirrors Paper's {@code AttributeModifierDisplay}.</p>
 */
public sealed interface AttributeModifierDisplay permits
    AttributeModifierDisplays.Default,
    AttributeModifierDisplays.Hidden,
    AttributeModifierDisplays.OverrideText {

  /**
   * Returns the display type of this attribute modifier.
   *
   * @return the display type
   */
  @NotNull
  Type type();

  /**
   * Returns the override text for an {@link Type#OVERRIDE} display, or {@code null}
   * for {@link Type#DEFAULT} and {@link Type#HIDDEN}.
   *
   * @return the override component, or {@code null}
   */
  @Nullable
  Component overrideText();

  /**
   * Possible attribute modifier display modes.
   */
  enum Type {
    DEFAULT,
    HIDDEN,
    OVERRIDE
  }
}
