package org.aincraft.common.datacomponent.item;

import net.kyori.adventure.text.format.TextColor;

import java.util.List;

/** Common contract for custom model data, mirroring Paper's {@code CustomModelData}. */
public interface CustomModelData {

  List<Float> floats();

  List<Boolean> flags();

  List<String> strings();

  List<TextColor> colors();
}
