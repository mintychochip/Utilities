package org.aincraft.common.datacomponent.item;

import java.util.List;
import org.aincraft.common.datacomponent.Color;

/**
 * Common contract for custom model data, mirroring Paper's {@code CustomModelData}.
 */
public interface CustomModelData {

  List<Float> floats();

  List<Boolean> flags();

  List<String> strings();

  List<Color> colors();
}
