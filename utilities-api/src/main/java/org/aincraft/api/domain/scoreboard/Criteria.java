package org.aincraft.api.domain.scoreboard;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/** A scoreboard criteria, either built in or custom. */
public interface Criteria {

  @NotNull Criteria DUMMY = new Value("dummy");
  @NotNull Criteria TRIGGER = new Value("trigger", true);

  /** Returns the native criteria identifier. */
  @NotNull
  String name();

  /** Returns whether the server owns the score values for this criteria. */
  default boolean isReadOnly() {
    return false;
  }

  /** Returns the native default rendering mode for this criteria. */
  default @NotNull RenderType defaultRenderType() {
    return RenderType.INTEGER;
  }

  /** Creates a writable custom criteria value. */
  static @NotNull Criteria of(@NotNull String name) {
    Objects.requireNonNull(name, "name");
    if (name.isBlank()) {
      throw new IllegalArgumentException("Criteria name must not be blank");
    }
    return new Value(name);
  }

  /** Basic criteria value used when no native criteria object is available. */
  record Value(@NotNull String name, boolean readOnly) implements Criteria {
    public Value(@NotNull String name) {
      this(name, false);
    }

    public Value {
      Objects.requireNonNull(name, "name");
      if (name.isBlank()) {
        throw new IllegalArgumentException("Criteria name must not be blank");
      }
    }

    @Override
    public boolean isReadOnly() {
      return readOnly;
    }
  }
}
