package org.aincraft.api.domain.block;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.aincraft.api.Capability;
import org.aincraft.api.UnsupportedCapabilityException;
import org.jetbrains.annotations.NotNull;

public interface BlockType extends Keyed {

  /** Vanilla translation key used by client-side localization. */
  default @NotNull String translationKey() {
    throw new UnsupportedCapabilityException(Capability.BLOCK_QUERY);
  }

  static @NotNull BlockType of(@NotNull Key key) {
    return new BlockType() {
      @Override
      public @NotNull Key key() {
        return key;
      }

      @Override
      public boolean equals(Object o) {
        return o instanceof BlockType other && key.equals(other.key());
      }

      @Override
      public int hashCode() {
        return key.hashCode();
      }

      @Override
      public String toString() {
        return "BlockType{" + key + "}";
      }
    };
  }
}
