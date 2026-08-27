package org.aincraft.common.datacomponent.item;

import net.kyori.adventure.key.Key;

import java.util.Map;

/** Common contract for map decorations, mirroring Paper's {@code MapDecorations}. */
public interface MapDecorations {

  Map<String, MapDecoration> decorations();

  MapDecoration decoration(String name);

  /**
   * Common contract for a single map decoration, mirroring Paper's {@code
   * MapDecorations.DecorationEntry}.
   */
  interface MapDecoration {

    Key type();

    double x();

    double z();

    float rotation();
  }
}
