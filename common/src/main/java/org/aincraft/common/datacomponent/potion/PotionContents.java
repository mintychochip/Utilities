package org.aincraft.common.datacomponent.potion;

import java.util.List;
import net.kyori.adventure.key.Key;
import org.aincraft.common.datacomponent.Color;

/**
 * Common contract for potion contents, mirroring Paper's {@code PotionContents}
 * without depending on Bukkit.
 */
public interface PotionContents {

  Key potion();

  Color customColor();

  List<PotionEffect> customEffects();

  String customName();

  List<PotionEffect> allEffects();

  Color computeEffectiveColor();
}
