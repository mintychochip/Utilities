package org.aincraft.math;

import com.google.common.base.Preconditions;
import org.aincraft.math.RandomSelector.UniformRandomSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

final class UniformRandomSelectorImpl<T> implements UniformRandomSelector<T> {

  private final List<T> objects = new ArrayList<>();

  @Override
  public T getObject(RandomGenerator randomGenerator) throws IllegalStateException {
    Preconditions.checkState(!objects.isEmpty(), "no elements to select from");
    int index = randomGenerator.nextInt(this.objects.size());
    return objects.get(index);
  }

  @Override
  public void addObject(T object) {
    objects.add(object);
  }
}
