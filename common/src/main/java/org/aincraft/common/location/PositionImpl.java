package org.aincraft.common.location;

record PositionImpl(double x, double y, double z) implements Position {

  @Override
  public String toString() {
    return "Position{x=" + x + ", y=" + y + ", z=" + z + "}";
  }
}
