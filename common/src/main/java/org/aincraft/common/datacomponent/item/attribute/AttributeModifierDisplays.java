package org.aincraft.common.datacomponent.item.attribute;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

/**
 * Factory utilities for {@link AttributeModifierDisplay} values.
 */
public final class AttributeModifierDisplays {

  private AttributeModifierDisplays() {
    throw new AssertionError("utility class");
  }

  /**
   * Returns the default attribute-line display.
   *
   * @return a default display
   */
  public static @NotNull AttributeModifierDisplay reset() {
    return Default.INSTANCE;
  }

  /**
   * Returns a display that hides the attribute line entirely.
   *
   * @return a hidden display
   */
  public static @NotNull AttributeModifierDisplay hidden() {
    return Hidden.INSTANCE;
  }

  /**
   * Returns a display that overrides the attribute line with the given text.
   *
   * @param text the override text
   * @return an override display
   */
  public static @NotNull AttributeModifierDisplay override(@NotNull ComponentLike text) {
    return new OverrideText(text.asComponent());
  }

  static final class Default implements AttributeModifierDisplay {
    private static final Default INSTANCE = new Default();

    private Default() {
    }

    @Override
    public @NotNull Type type() {
      return Type.DEFAULT;
    }

    @Override
    public Component overrideText() {
      return null;
    }
  }

  static final class Hidden implements AttributeModifierDisplay {
    private static final Hidden INSTANCE = new Hidden();

    private Hidden() {
    }

    @Override
    public @NotNull Type type() {
      return Type.HIDDEN;
    }

    @Override
    public Component overrideText() {
      return null;
    }
  }

  static final class OverrideText implements AttributeModifierDisplay {
    private final Component text;

    OverrideText(@NotNull Component text) {
      this.text = text;
    }

    @Override
    public @NotNull Type type() {
      return Type.OVERRIDE;
    }

    @Override
    public @NotNull Component overrideText() {
      return text;
    }
  }
}
