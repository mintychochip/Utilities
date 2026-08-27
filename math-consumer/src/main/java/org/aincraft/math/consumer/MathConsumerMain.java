package org.aincraft.math.consumer;

import org.aincraft.math.RandomSelector;
import org.aincraft.math.RandomSelector.UniformRandomSelector;

import java.util.Random;

public final class MathConsumerMain {

  public static void main(String[] args) {
    UniformRandomSelector<String> selector = RandomSelector.uniform();
    selector.addObject("only");
    String selected = selector.getObject(new Random(0));
    if (!"only".equals(selected)) {
      throw new AssertionError("expected sole added element, got: " + selected);
    }
    System.out.println("ok=" + selected);
  }
}
