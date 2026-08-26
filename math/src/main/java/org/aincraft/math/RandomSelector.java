package org.aincraft.math;

import java.util.random.RandomGenerator;

public interface RandomSelector<T> {

  T getObject(RandomGenerator randomGenerator) throws IllegalStateException;

  static <T> UniformRandomSelector<T> uniform() {
    return new UniformRandomSelectorImpl<>();
  }

  static <T> WeightedRandomSelector<T> weighted() {
    return new WeightedRandomSelectorImpl<>();
  }

  interface UniformRandomSelector<T> extends RandomSelector<T> {

    void addObject(T object);
  }

  interface WeightedRandomSelector<T> extends RandomSelector<T> {

    void addObject(double weight, T object) throws IllegalArgumentException;
  }
}
