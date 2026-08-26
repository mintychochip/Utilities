package org.aincraft.common.datacomponent.item;

import java.util.List;
import net.kyori.adventure.text.format.TextColor;

/**
 * Common contract for custom model data, mirroring Paper's {@code CustomModelData}.
 */
public interface CustomModelData {

  List<Float> floats();

  List<Boolean> flags();

  List<String> strings();

  List<TextColor> colors();
}
