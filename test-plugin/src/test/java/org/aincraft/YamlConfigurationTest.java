package org.aincraft;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.kyori.adventure.key.Keyed;
import org.aincraft.config.YamlConfiguration;
import org.aincraft.registry.Registry;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.plugin.PluginMock;

public class YamlConfigurationTest {

  private static ServerMock server;
  private static PluginMock plugin;

  @BeforeAll
  public static void setup() {
    server = MockBukkit.mock();
    plugin = MockBukkit.createMockPlugin();
  }

  @AfterAll
  public static void teardown() {
    MockBukkit.unmock();
  }

  @Test
  public void testConfiguration() {
    YamlConfiguration configuration = YamlConfiguration.single(plugin, "config.yml");
    assertEquals("value", configuration.getString("key", "not value"));
  }
}
