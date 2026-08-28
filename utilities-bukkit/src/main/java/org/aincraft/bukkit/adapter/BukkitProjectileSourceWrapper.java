package org.aincraft.bukkit.adapter;

import org.aincraft.api.domain.entity.Projectile;
import org.aincraft.api.domain.entity.ProjectileSource;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BukkitProjectileSourceWrapper implements ProjectileSource {

  private final org.bukkit.projectiles.ProjectileSource source;

  public BukkitProjectileSourceWrapper(@NotNull org.bukkit.projectiles.ProjectileSource source) {
    this.source = Objects.requireNonNull(source, "source");
  }

  public @NotNull org.bukkit.projectiles.ProjectileSource getBukkitProjectileSource() {
    return source;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Projectile> @NotNull T launchProjectile(
      @NotNull Class<? extends T> projectileClass) {
    Objects.requireNonNull(projectileClass, "projectileClass");
    Class<? extends org.bukkit.entity.Projectile> bukkitClass = resolveBukkitClass(projectileClass);
    org.bukkit.entity.Projectile bProjectile = source.launchProjectile(bukkitClass);
    Projectile wrapped = BukkitAdapters.adapt(bProjectile);
    if (!projectileClass.isInstance(wrapped)) {
      throw new IllegalStateException(
          "Launched projectile "
              + bProjectile.getType()
              + " ("
              + bProjectile.getClass().getSimpleName()
              + ") is not an instance of requested "
              + projectileClass.getName());
    }
    return (T) wrapped;
  }

  private static final java.util.Set<String> SUPPORTED_PROJECTILES =
      java.util.Set.of(
          "Arrow",
          "SpectralArrow",
          "Trident",
          "Snowball",
          "Egg",
          "EnderPearl",
          "Fireball",
          "SmallFireball",
          "LargeFireball",
          "DragonFireball",
          "WitherSkull",
          "ShulkerBullet",
          "LlamaSpit",
          "ThrownPotion",
          "ExperienceBottle",
          "FishingHook",
          "WindCharge",
          "BreezeWindCharge",
          "Projectile");

  @SuppressWarnings("unchecked")
  private static Class<? extends org.bukkit.entity.Projectile> resolveBukkitClass(
      Class<? extends Projectile> commonClass) {
    String simple = commonClass.getSimpleName();
    if (!SUPPORTED_PROJECTILES.contains(simple) && commonClass != Projectile.class) {
      for (Class<?> iface : commonClass.getInterfaces()) {
        if (SUPPORTED_PROJECTILES.contains(iface.getSimpleName())) {
          simple = iface.getSimpleName();
          break;
        }
      }
      if (!SUPPORTED_PROJECTILES.contains(simple)) {
        throw new IllegalArgumentException(
            "Unsupported projectile type "
                + commonClass.getName()
                + "; supported common names: "
                + SUPPORTED_PROJECTILES
                + " (map to org.bukkit.entity.*)");
      }
    }
    if (commonClass == Projectile.class) {
      return org.bukkit.entity.Arrow.class;
    }
    try {
      Class<?> candidate = Class.forName("org.bukkit.entity." + simple);
      if (org.bukkit.entity.Projectile.class.isAssignableFrom(candidate)) {
        return (Class<? extends org.bukkit.entity.Projectile>) candidate;
      }
    } catch (ClassNotFoundException ignored) {
    }
    throw new IllegalArgumentException(
        "Cannot resolve Bukkit projectile class for common type "
            + commonClass.getName()
            + " (tried org.bukkit.entity."
            + simple
            + ")");
  }
}
