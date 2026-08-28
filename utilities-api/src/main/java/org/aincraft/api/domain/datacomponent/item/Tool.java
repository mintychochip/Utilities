package org.aincraft.api.domain.datacomponent.item;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.util.TriState;

import java.util.List;
import java.util.Set;

/** Common contract for tool properties, mirroring Paper's {@code Tool}. */
public interface Tool {

  float defaultMiningSpeed();

  int damagePerBlock();

  List<Rule> rules();

  boolean canDestroyBlocksInCreative();

  /** Common contract for a tool rule, mirroring Paper's {@code Tool.Rule}. */
  interface Rule {

    Set<Key> blocks();

    Float speed();

    TriState correctForDrops();
  }
}
