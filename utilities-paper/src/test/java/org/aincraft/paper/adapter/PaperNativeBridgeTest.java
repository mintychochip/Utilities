package org.aincraft.paper.adapter;

import static org.junit.jupiter.api.Assertions.*;

import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;

class PaperNativeBridgeTest {

  @Test
  void itemMetaUsesNativeAdventureNameAccessors() {
    AtomicReference<Component> custom = new AtomicReference<>(Component.text("Custom"));
    AtomicReference<Component> item = new AtomicReference<>(Component.text("Item"));
    org.bukkit.inventory.meta.ItemMeta nativeMeta =
        (org.bukkit.inventory.meta.ItemMeta)
            Proxy.newProxyInstance(
                org.bukkit.inventory.meta.ItemMeta.class.getClassLoader(),
                new Class<?>[] {org.bukkit.inventory.meta.ItemMeta.class},
                (proxy, method, args) -> {
                  if (method.getName().equals("customName")) {
                    if (args == null || args.length == 0) return custom.get();
                    custom.set((Component) args[0]);
                    return null;
                  }
                  if (method.getName().equals("itemName")) {
                    if (args == null || args.length == 0) return item.get();
                    item.set((Component) args[0]);
                    return null;
                  }
                  return switch (method.getName()) {
                    case "hasCustomName", "hasItemName" -> true;
                    default -> null;
                  };
                });

    org.aincraft.api.domain.inventory.ItemMeta domain = PaperAdapters.adapt(nativeMeta);
    assertEquals(Component.text("Custom"), domain.customName());
    assertEquals(Component.text("Item"), domain.itemName());
    assertTrue(domain.hasCustomName());
    assertTrue(domain.hasItemName());

    domain.customName(Component.text("updated"));
    domain.itemName(Component.text("updated item"));
    assertEquals(Component.text("updated"), custom.get());
    assertEquals(Component.text("updated item"), item.get());
  }
}
