package org.aincraft.math;

import com.google.common.base.Preconditions;
import org.aincraft.api.math.RandomSelector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;

final class WeightedRandomSelectorImpl<T> implements RandomSelector.WeightedRandomSelector<T> {

  private final List<Double> weights = new ArrayList<>();
  private final List<T> objects = new ArrayList<>();
  private double cumulative = 0.0;

  @Override
  public T getObject(RandomGenerator randomGenerator) throws IllegalStateException {
    Preconditions.checkState(objects.size() == weights.size());
    Preconditions.checkState(!objects.isEmpty());
    Preconditions.checkState(cumulative > 0.0);
    double random = randomGenerator.nextDouble(0, cumulative);
    int index = Collections.binarySearch(weights, random);
    if (index < 0) {
      index = -(index + 1);
    }
    return objects.get(index);
  }

  @Override
  public void addObject(double weight, T object) throws IllegalArgumentException {
    Preconditions.checkArgument(!Double.isNaN(weight) && weight >= 0.0);
    objects.add(object);
    weights.add(cumulative += weight);
  }
}
