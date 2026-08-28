package org.aincraft.paper.adapter;

import net.kyori.adventure.text.Component;
import org.aincraft.api.domain.contract.AbstractPlayerContractTest;
import org.aincraft.api.domain.entity.Player;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PaperPlayerContractTest extends AbstractPlayerContractTest {

  private final List<Component> capturedMessages = new ArrayList<>();

  @Override
  protected Player createPlayerFixture(UUID uid, String username, boolean online) {
    capturedMessages.clear();

    org.bukkit.World bWorld =
        (org.bukkit.World)
            Proxy.newProxyInstance(
                org.bukkit.World.class.getClassLoader(),
                new Class<?>[] {org.bukkit.World.class},
                (proxy, method, args) -> {
                  return switch (method.getName()) {
                    case "getUID" -> UUID.randomUUID();
                    case "getName" -> "world";
                    case "getKey" -> NamespacedKey.minecraft("overworld");
                    default -> null;
                  };
                });

    Location bLoc = new Location(bWorld, 10.0, 64.0, -20.0, 90.0f, 0.0f);
    Location bEyeLoc = new Location(bWorld, 10.0, 65.62, -20.0, 90.0f, 0.0f);

    org.bukkit.entity.Player bPlayer =
        (org.bukkit.entity.Player)
            Proxy.newProxyInstance(
                org.bukkit.entity.Player.class.getClassLoader(),
                new Class<?>[] {org.bukkit.entity.Player.class},
                (proxy, method, args) -> {
                  return switch (method.getName()) {
                    case "getUniqueId" -> uid;
                    case "getName" -> username;
                    case "isOnline" -> online;
                    case "getType" -> EntityType.PLAYER;
                    case "getLocation" -> bLoc;
                    case "getEyeLocation" -> bEyeLoc;
                    case "sendMessage" -> {
                      if (args.length > 0 && args[0] instanceof Component c) {
                        capturedMessages.add(c);
                      }
                      yield null;
                    }
                    default -> null;
                  };
                });

    return PaperAdapters.adapt(bPlayer);
  }

  @Override
  protected List<Component> getCapturedPlayerMessages() {
    return capturedMessages;
  }
}
