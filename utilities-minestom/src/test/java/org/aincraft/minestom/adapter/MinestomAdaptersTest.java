package org.aincraft.minestom.adapter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.world.DimensionType;
import org.aincraft.common.block.BlockFace;
import org.aincraft.common.block.BlockState;
import org.aincraft.common.block.BlockType;
import org.aincraft.common.entity.Entity;
import org.aincraft.common.location.BoundingBox;
import org.aincraft.common.location.Location;
import org.aincraft.common.location.Position;
import org.aincraft.common.world.Chunk;
import org.aincraft.common.world.World;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MinestomAdaptersTest {

  @BeforeAll
  static void setup() {
    MinecraftServer.init();
  }

  @Test
  void testCoordinatesAndBoundingBox() {
    Instance instance = new InstanceContainer(UUID.randomUUID(), DimensionType.OVERWORLD);
    Pos pos = new Pos(1.0, 2.0, 3.0, 45f, 90f);
    Location loc = MinestomAdapters.adapt(instance, pos);
    assertEquals(1.0, loc.x());
    assertEquals(2.0, loc.y());
    assertEquals(3.0, loc.z());
    assertEquals(45f, loc.yaw());
    assertEquals(90f, loc.pitch());

    Pos backPos = MinestomAdapters.toMinestomPos(loc);
    assertEquals(1.0, backPos.x());
    assertEquals(2.0, backPos.y());
    assertEquals(3.0, backPos.z());

    Vec vec = new Vec(4.0, 5.0, 6.0);
    Position p = MinestomAdapters.adapt(vec);
    assertEquals(4.0, p.x());
    assertEquals(5.0, p.y());
    assertEquals(6.0, p.z());

    Vec backVec = MinestomAdapters.toMinestomVec(p);
    assertEquals(vec, backVec);

    net.minestom.server.collision.BoundingBox mBox = new net.minestom.server.collision.BoundingBox(1.0, 2.0, 3.0);
    BoundingBox box = MinestomAdapters.adapt(mBox);
    assertEquals(mBox.minX(), box.minX(), 1e-6);
    assertEquals(mBox.maxX(), box.maxX(), 1e-6);

    net.minestom.server.collision.BoundingBox backBox = MinestomAdapters.toMinestom(box);
    assertEquals(mBox.minX(), backBox.minX(), 1e-6);
  }

  @Test
  void testBlockAndBlockFace() {
    Block diamond = Block.DIAMOND_BLOCK;
    BlockType type = MinestomAdapters.adapt(diamond);
    assertEquals("minecraft:diamond_block", type.key().asString());
    assertSame(diamond, MinestomAdapters.toMinestom(type));

    BlockState state = MinestomAdapters.adaptState(diamond);
    assertSame(diamond, MinestomAdapters.toMinestom(state));

    assertEquals(BlockFace.UP, MinestomAdapters.adapt(net.minestom.server.instance.block.BlockFace.TOP));
    assertEquals(BlockFace.DOWN, MinestomAdapters.adapt(net.minestom.server.instance.block.BlockFace.BOTTOM));
  }

  @Test
  void testWorldAndChunk() {
    UUID uuid = UUID.randomUUID();
    Instance instance = new InstanceContainer(uuid, DimensionType.OVERWORLD);
    World world = MinestomAdapters.adapt(instance);
    assertEquals(uuid, world.uid());
    assertSame(instance, MinestomAdapters.toMinestom(world));

    instance.loadChunk(0, 0).join();
    net.minestom.server.instance.Chunk mChunk = instance.getChunk(0, 0);
    assertNotNull(mChunk);
    Chunk chunk = MinestomAdapters.adapt(mChunk);
    assertEquals(0, chunk.x());
    assertEquals(0, chunk.z());
    assertSame(mChunk, MinestomAdapters.toMinestom(chunk));
  }

  @Test
  void testPlayer() {
    Instance instance = new InstanceContainer(UUID.randomUUID(), DimensionType.OVERWORLD);
    UUID uuid = UUID.randomUUID();
    GameProfile profile = new GameProfile(uuid, "Alex");
    PlayerConnection conn = new PlayerConnection() {
      @Override public void sendPacket(net.minestom.server.network.packet.server.SendablePacket packet) {}
      @Override public java.net.SocketAddress getRemoteAddress() { return new java.net.InetSocketAddress("127.0.0.1", 25565); }
    };
    Player minestomPlayer = new Player(conn, profile);
    minestomPlayer.setInstance(instance, new Pos(0, 64, 0)).join();

    org.aincraft.common.entity.Player player = MinestomAdapters.adapt(minestomPlayer);
    assertEquals("Alex", player.username());
    assertSame(minestomPlayer, MinestomAdapters.toMinestom(player));
    assertSame(minestomPlayer, MinestomAdapters.toMinestom((Entity) player));
  }

  @Test
  void testNullChecks() {
    assertThrows(NullPointerException.class, () -> MinestomAdapters.adapt((Instance) null, new Pos(0, 0, 0)));
    assertThrows(NullPointerException.class, () -> MinestomAdapters.adapt(new InstanceContainer(UUID.randomUUID(), DimensionType.OVERWORLD), null));
    assertThrows(NullPointerException.class, () -> MinestomAdapters.toMinestomPos(null));
    assertThrows(NullPointerException.class, () -> MinestomAdapters.adapt((net.minestom.server.coordinate.Point) null));
    assertThrows(NullPointerException.class, () -> MinestomAdapters.toMinestomVec(null));
    assertThrows(NullPointerException.class, () -> MinestomAdapters.adapt((net.minestom.server.collision.BoundingBox) null));
    assertThrows(NullPointerException.class, () -> MinestomAdapters.toMinestom((BoundingBox) null));
    assertThrows(NullPointerException.class, () -> MinestomAdapters.adapt((Instance) null));
    assertThrows(NullPointerException.class, () -> MinestomAdapters.toMinestom((World) null));
    assertThrows(NullPointerException.class, () -> MinestomAdapters.adapt((net.minestom.server.instance.Chunk) null));
    assertThrows(NullPointerException.class, () -> MinestomAdapters.toMinestom((Chunk) null));
    assertThrows(NullPointerException.class, () -> MinestomAdapters.adapt((net.minestom.server.entity.Entity) null));
    assertThrows(NullPointerException.class, () -> MinestomAdapters.toMinestom((Entity) null));
    assertThrows(NullPointerException.class, () -> MinestomAdapters.adapt((Player) null));
    assertThrows(NullPointerException.class, () -> MinestomAdapters.toMinestom((org.aincraft.common.entity.Player) null));
    assertThrows(NullPointerException.class, () -> MinestomAdapters.adapt((Block) null));
    assertThrows(NullPointerException.class, () -> MinestomAdapters.toMinestom((BlockType) null));
    assertThrows(NullPointerException.class, () -> MinestomAdapters.adaptState((Block) null));
    assertThrows(NullPointerException.class, () -> MinestomAdapters.toMinestom((BlockState) null));
    assertThrows(NullPointerException.class, () -> MinestomAdapters.adapt((net.minestom.server.instance.block.BlockFace) null));
    assertThrows(NullPointerException.class, () -> MinestomAdapters.toMinestom((BlockFace) null));
  }
}
