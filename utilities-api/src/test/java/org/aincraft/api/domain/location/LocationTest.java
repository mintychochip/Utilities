package org.aincraft.api.domain.location;

import static org.junit.jupiter.api.Assertions.*;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.aincraft.api.domain.block.BlockState;
import org.aincraft.api.domain.block.BlockType;
import org.aincraft.api.domain.effect.Particle;
import org.aincraft.api.domain.entity.Entity;
import org.aincraft.api.domain.entity.Player;
import org.aincraft.api.domain.world.Block;
import org.aincraft.api.domain.world.Chunk;
import org.aincraft.api.domain.world.Difficulty;
import org.aincraft.api.domain.world.Environment;
import org.aincraft.api.domain.world.World;
import org.aincraft.api.domain.world.WorldBorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.UUID;

class LocationTest {

  private static Position createPos(double x, double y, double z) {
    return new Position() {
      @Override
      public double x() {
        return x;
      }

      @Override
      public double y() {
        return y;
      }

      @Override
      public double z() {
        return z;
      }
    };
  }

  private static World createTestWorld(String name) {
    UUID uid = UUID.nameUUIDFromBytes(name.getBytes());
    Key key = Key.key("test", name);
    return new World() {
      @Override
      public UUID uid() {
        return uid;
      }

      @Override
      public String name() {
        return name;
      }

      @Override
      public Key key() {
        return key;
      }

      @Override
      public int minHeight() {
        return -64;
      }

      @Override
      public int maxHeight() {
        return 320;
      }

      @Override
      public WorldBorder worldBorder() {
        return null;
      }

      @Override
      public Environment environment() {
        return Environment.NORMAL;
      }

      @Override
      public Difficulty difficulty() {
        return Difficulty.NORMAL;
      }

      @Override
      public long time() {
        return 1000L;
      }

      @Override
      public long fullTime() {
        return 25000L;
      }

      @Override
      public Collection<? extends Player> players() {
        return java.util.List.of();
      }

      @Override
      public Collection<? extends Entity> entities() {
        return java.util.List.of();
      }

      @Override
      public Collection<? extends Chunk> loadedChunks() {
        return java.util.List.of();
      }

      @Override
      public void playSound(
          @NotNull Location location,
          @NotNull Sound.Type sound,
          @Nullable Sound.Source source,
          float volume,
          float pitch) {
        throw new UnsupportedOperationException();
      }

      @Override
      public void spawnParticle(
          @NotNull Particle particle,
          @NotNull Location location,
          int count,
          double offsetX,
          double offsetY,
          double offsetZ,
          double extra) {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean isChunkLoaded(int chunkX, int chunkZ) {
        return true;
      }

      @Override
      public Chunk getChunkAt(int chunkX, int chunkZ) {
        World currentWorld = this;
        return new Chunk() {
          @Override
          public int x() {
            return chunkX;
          }

          @Override
          public int z() {
            return chunkZ;
          }

          @Override
          public World world() {
            return currentWorld;
          }

          @Override
          public boolean isLoaded() {
            return true;
          }

          @Override
          public boolean load() {
            return true;
          }

          @Override
          public boolean load(boolean generate) {
            return true;
          }

          @Override
          public boolean unload() {
            return true;
          }

          @Override
          public boolean unload(boolean save) {
            return true;
          }

          @Override
          public Collection<? extends Entity> entities() {
            return java.util.List.of();
          }

          @Override
          public Block getBlock(int x, int y, int z) {
            return getBlockAt(x, y, z);
          }

          @Override
          public void setForceLoaded(boolean forceLoaded) {}

          @Override
          public boolean isForceLoaded() {
            return false;
          }

          @Override
          public boolean isGenerated() {
            return true;
          }
        };
      }

      @Override
      public Block getBlockAt(int x, int y, int z) {
        World currentWorld = this;
        return new Block() {
          @Override
          public int x() {
            return x;
          }

          @Override
          public int y() {
            return y;
          }

          @Override
          public int z() {
            return z;
          }

          @Override
          public World world() {
            return currentWorld;
          }

          @Override
          public Chunk chunk() {
            return getChunkAt(x >> 4, z >> 4);
          }

          @Override
          public Location location() {
            return createLocation(currentWorld, createPos(x, y, z), 0f, 0f);
          }

          @Override
          public Position position() {
            return createPos(x, y, z);
          }

          @Override
          public BlockType type() {
            return () -> Key.key("minecraft", "air");
          }

          @Override
          public BlockState state() {
            return new BlockState() {
              @Override
              public BlockType type() {
                return () -> Key.key("minecraft", "air");
              }

              @Override
              public String asString() {
                return "minecraft:air";
              }
            };
          }

          @Override
          public boolean isEmpty() {
            return true;
          }

          @Override
          public boolean isLiquid() {
            return false;
          }

          @Override
          public boolean isSolid() {
            return false;
          }

          @Override
          public boolean isAir() {
            return true;
          }

          @Override
          public boolean isPassable() {
            return true;
          }

          @Override
          public BoundingBox boundingBox() {
            return new BoundingBox() {
              @Override
              public double minX() {
                return x;
              }

              @Override
              public double minY() {
                return y;
              }

              @Override
              public double minZ() {
                return z;
              }

              @Override
              public double maxX() {
                return x + 1;
              }

              @Override
              public double maxY() {
                return y + 1;
              }

              @Override
              public double maxZ() {
                return z + 1;
              }
            };
          }

          @Override
          public Key biome() {
            throw new UnsupportedOperationException();
          }

          @Override
          public void setBiome(@NotNull Key biome) {
            throw new UnsupportedOperationException();
          }

          @Override
          public java.util.Collection<? extends org.aincraft.api.domain.inventory.ItemStack> drops(
              @Nullable org.aincraft.api.domain.inventory.ItemStack tool,
              @Nullable Entity breaker) {
            return java.util.List.of();
          }

          @Override
          public java.util.Collection<? extends org.aincraft.api.domain.inventory.ItemStack> drops(
              @Nullable org.aincraft.api.domain.inventory.ItemStack tool) {
            return java.util.List.of();
          }

          @Override
          public java.util.Collection<? extends org.aincraft.api.domain.inventory.ItemStack>
              drops() {
            return java.util.List.of();
          }

          @Override
          public int lightFromBlocks() {
            return 0;
          }

          @Override
          public int lightFromSky() {
            return 0;
          }

          @Override
          public int lightLevel() {
            return 0;
          }
        };
      }
    };
  }

  private static Location createLocation(World world, Position position, float yaw, float pitch) {
    return new Location() {
      @Override
      public World world() {
        return world;
      }

      @Override
      public Position position() {
        return position;
      }

      @Override
      public float yaw() {
        return yaw;
      }

      @Override
      public float pitch() {
        return pitch;
      }

      @Override
      public @NotNull org.joml.Vector3dc direction() {
        return new org.joml.Vector3d(0, 0, 1);
      }

      @Override
      public long toBlockKey() {
        return 0L;
      }

      @Override
      public boolean isChunkLoaded() {
        return false;
      }

      @Override
      public java.util.Collection<? extends org.aincraft.api.domain.entity.LivingEntity>
          nearbyLivingEntities(double xRadius, double yRadius, double zRadius) {
        return java.util.List.of();
      }

      @Override
      public java.util.Collection<? extends org.aincraft.api.domain.entity.Player> nearbyPlayers(
          double xRadius, double yRadius, double zRadius) {
        return java.util.List.of();
      }

      @Override
      public java.util.Collection<? extends org.aincraft.api.domain.entity.Player> nearbyPlayers(
          double radius) {
        return java.util.List.of();
      }

      @Override
      public java.util.Collection<? extends org.aincraft.api.domain.entity.Entity> nearbyEntities(
          double xRadius, double yRadius, double zRadius) {
        return java.util.List.of();
      }

      @Override
      public java.util.Collection<? extends org.aincraft.api.domain.entity.Entity> nearbyEntities(
          double radius) {
        return java.util.List.of();
      }

      @Override
      public Location toCenterLocation() {
        return this;
      }

      @Override
      public Location toBlockLocation() {
        return this;
      }

      @Override
      public Location withRotation(float yaw, float pitch) {
        return this;
      }

      @Override
      public Location withOffset(@NotNull org.joml.Vector3dc offset) {
        return this;
      }

      @Override
      public Location withOffset(double dx, double dy, double dz) {
        return this;
      }
    };
  }

  @Test
  void testLocationCoordinatesAndOrientation() {
    World world = createTestWorld("overworld");
    Location loc = createLocation(world, createPos(10.5, 64.0, -12.5), 90.0f, 45.0f);

    assertSame(world, loc.world());
    assertEquals(10.5, loc.x(), 1e-6);
    assertEquals(64.0, loc.y(), 1e-6);
    assertEquals(-12.5, loc.z(), 1e-6);
    assertEquals(10, loc.blockX());
    assertEquals(64, loc.blockY());
    assertEquals(-13, loc.blockZ());
    assertEquals(90.0f, loc.yaw(), 1e-6f);
    assertEquals(45.0f, loc.pitch(), 1e-6f);

    Block block = loc.block();
    assertEquals(10, block.x());
    assertEquals(64, block.y());
    assertEquals(-13, block.z());

    Chunk chunk = loc.chunk();
    assertEquals(10 >> 4, chunk.x());
    assertEquals(-13 >> 4, chunk.z());
  }

  @Test
  void testLocationDistance() {
    World world = createTestWorld("world");
    Location loc1 = createLocation(world, createPos(0, 0, 0), 0, 0);
    Location loc2 = createLocation(world, createPos(3, 4, 0), 0, 0);

    assertEquals(25.0, loc1.distanceSquared(loc2), 1e-6);
    assertEquals(5.0, loc1.distance(loc2), 1e-6);
  }
}
