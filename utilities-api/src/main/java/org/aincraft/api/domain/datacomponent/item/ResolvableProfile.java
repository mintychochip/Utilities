package org.aincraft.api.domain.datacomponent.item;

import net.kyori.adventure.key.Key;

/**
 * Common contract for a resolvable player profile, mirroring Paper's {@code
 * io.papermc.paper.datacomponent.item.ResolvableProfile}.
 */
public interface ResolvableProfile extends PlayerProfile {

  boolean dynamic();

  SkinPatch skinPatch();

  /**
   * Common contract for the skin patch of a resolvable profile, mirroring {@code
   * io.papermc.paper.datacomponent.item.ResolvableProfile.SkinPatch}.
   */
  interface SkinPatch {

    Key body();

    Key cape();

    Key elytra();

    SkinModel model();
  }

  enum SkinModel {
    CLASSIC,
    SLIM
  }
}
