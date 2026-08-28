package org.aincraft.api.domain.inventory;

import org.jetbrains.annotations.Nullable;

public interface InventoryHolder {

  @Nullable
  Inventory inventory();
}
