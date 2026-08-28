package org.aincraft.api.math;

import java.util.random.RandomGenerator;

public interface RandomSelector<T> {

  T getObject(RandomGenerator randomGenerator) throws IllegalStateException;

  interface UniformRandomSelector<T> extends RandomSelector<T> {

    void addObject(T object);
  }

  interface WeightedRandomSelector<T> extends RandomSelector<T> {

    void addObject(double weight, T object) throws IllegalArgumentException;
  }
}
