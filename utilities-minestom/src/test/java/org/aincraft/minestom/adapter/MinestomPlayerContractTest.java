package org.aincraft.minestom.adapter;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.world.DimensionType;
import org.aincraft.api.domain.contract.AbstractPlayerContractTest;
import org.aincraft.api.domain.entity.Player;
import org.junit.jupiter.api.BeforeAll;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MinestomPlayerContractTest extends AbstractPlayerContractTest {

  private final List<Component> capturedMessages = new ArrayList<>();

  @BeforeAll
  static void initMinestom() {
    MinecraftServer.init();
  }

  @Override
  protected Player createPlayerFixture(UUID uid, String username, boolean online) {
    capturedMessages.clear();

    InstanceContainer instance =
        MinecraftServer.getInstanceManager().createInstanceContainer(DimensionType.OVERWORLD);

    PlayerConnection dummyConn =
        new PlayerConnection() {
          @Override
          public void sendPacket(net.minestom.server.network.packet.server.SendablePacket packet) {
            if (packet
                instanceof net.minestom.server.network.packet.server.play.SystemChatPacket chat) {
              capturedMessages.add(chat.message());
            }
          }

          @Override
          public java.net.SocketAddress getRemoteAddress() {
            return new java.net.InetSocketAddress("127.0.0.1", 25565);
          }
        };

    net.minestom.server.entity.Player mPlayer =
        new net.minestom.server.entity.Player(dummyConn, new GameProfile(uid, username)) {
          @Override
          public boolean isOnline() {
            return online;
          }
        };

    mPlayer.setInstance(instance, new Pos(10.0, 64.0, -20.0)).join();

    return MinestomAdapters.adapt(mPlayer);
  }

  @Override
  protected List<Component> getCapturedPlayerMessages() {
    return capturedMessages;
  }
}
