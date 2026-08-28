package org.aincraft.api.domain.contract;

import static org.junit.jupiter.api.Assertions.*;

import net.kyori.adventure.text.Component;
import org.aincraft.api.domain.entity.Player;
import org.aincraft.api.domain.location.Location;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

/**
 * Shared contract test suite verifying that any platform adapter's {@link Player} implementation
 * conforms to the expected {@code :utilities-api} behavior.
 */
public abstract class AbstractPlayerContractTest {

  protected abstract Player createPlayerFixture(UUID uid, String username, boolean online);

  protected abstract List<Component> getCapturedPlayerMessages();

  @Test
  void testPlayerIdentityAndState() {
    UUID uid = UUID.randomUUID();
    Player player = createPlayerFixture(uid, "Alex", true);

    assertNotNull(player);
    assertEquals(uid, player.uniqueId(), "Player UUID must match");
    assertEquals(uid, player.identity().uuid(), "Identity UUID must match player UUID");
    assertEquals("Alex", player.username(), "Username must match");
    assertTrue(player.isOnline(), "Player online status must match fixture");
  }

  @Test
  void testPlayerLocationContracts() {
    UUID uid = UUID.randomUUID();
    Player player = createPlayerFixture(uid, "Steve", true);

    Location loc = player.location();
    assertNotNull(loc, "Player location must not be null");
    assertNotNull(loc.world(), "Player location world must not be null");
    assertNotNull(loc.position(), "Player location position must not be null");

    Location eyeLoc = player.eyeLocation();
    assertNotNull(eyeLoc, "Player eye location must not be null");
  }

  @Test
  void testPlayerAudienceMessaging() {
    UUID uid = UUID.randomUUID();
    Player player = createPlayerFixture(uid, "Steve", true);

    player.sendMessage(Component.text("Direct player message"));
    List<Component> messages = getCapturedPlayerMessages();
    assertNotNull(messages);
    assertFalse(messages.isEmpty(), "Player must have received direct message");
  }
}
