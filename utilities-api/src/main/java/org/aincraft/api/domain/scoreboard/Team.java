package org.aincraft.api.domain.scoreboard;

import java.util.Set;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A group of scoreboard entries sharing display and collision properties. */
public interface Team extends Audience {

  @NotNull String name();

  @NotNull Component displayName();

  void displayName(@NotNull Component displayName);

  @NotNull Component prefix();

  void prefix(@NotNull Component prefix);

  @NotNull Component suffix();

  void suffix(@NotNull Component suffix);

  @Nullable NamedTextColor color();

  void color(@Nullable NamedTextColor color);

  @NotNull Set<String> entries();

  int size();

  boolean addEntry(@NotNull String entry);

  boolean removeEntry(@NotNull String entry);

  boolean hasEntry(@NotNull String entry);

  boolean allowFriendlyFire();

  void setAllowFriendlyFire(boolean enabled);

  boolean canSeeFriendlyInvisibles();

  void setCanSeeFriendlyInvisibles(boolean enabled);

  @NotNull TeamOptionStatus option(@NotNull TeamOption option);

  void setOption(@NotNull TeamOption option, @NotNull TeamOptionStatus status);

  @Nullable Scoreboard scoreboard();

  void unregister();
}
