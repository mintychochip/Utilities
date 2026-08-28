package org.aincraft.api.domain.datacomponent.item;

import net.kyori.adventure.key.Key;

/** Common contract for item armor trim, mirroring Paper's {@code ItemArmorTrim}. */
public interface ItemArmorTrim {

  Key material();

  Key pattern();
}
