package org.aincraft.math;

import org.aincraft.api.math.RandomSelector;

public final class RandomSelectors {

  private RandomSelectors() {}

  public static <T> RandomSelector.UniformRandomSelector<T> uniform() {
    return new UniformRandomSelectorImpl<>();
  }

  public static <T> RandomSelector.WeightedRandomSelector<T> weighted() {
    return new WeightedRandomSelectorImpl<>();
  }
}
