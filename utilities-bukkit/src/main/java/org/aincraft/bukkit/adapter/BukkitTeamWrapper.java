package org.aincraft.bukkit.adapter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.aincraft.api.domain.scoreboard.Scoreboard;
import org.aincraft.api.domain.scoreboard.Team;
import org.aincraft.api.domain.scoreboard.TeamOption;
import org.aincraft.api.domain.scoreboard.TeamOptionStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/** Spigot-backed scoreboard team. */
public class BukkitTeamWrapper implements Team {

  private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

  private final org.bukkit.scoreboard.Team team;

  public BukkitTeamWrapper(@NotNull org.bukkit.scoreboard.Team team) {
    this.team = team;
  }

  public @NotNull org.bukkit.scoreboard.Team getBukkitTeam() {
    return team;
  }

  @Override
  public @NotNull String name() {
    return team.getName();
  }

  @Override
  public @NotNull Component displayName() {
    return LEGACY.deserialize(team.getDisplayName());
  }

  @Override
  public void displayName(@NotNull Component displayName) {
    team.setDisplayName(LEGACY.serialize(displayName));
  }

  @Override
  public @NotNull Component prefix() {
    return LEGACY.deserialize(team.getPrefix());
  }

  @Override
  public void prefix(@NotNull Component prefix) {
    team.setPrefix(LEGACY.serialize(prefix));
  }

  @Override
  public @NotNull Component suffix() {
    return LEGACY.deserialize(team.getSuffix());
  }

  @Override
  public void suffix(@NotNull Component suffix) {
    team.setSuffix(LEGACY.serialize(suffix));
  }

  @Override
  public @Nullable NamedTextColor color() {
    return fromBukkit(team.getColor());
  }

  @Override
  public void color(@Nullable NamedTextColor color) {
    team.setColor(toBukkit(color));
  }

  @Override
  public @NotNull Set<String> entries() {
    return Set.copyOf(team.getEntries());
  }

  @Override
  public int size() {
    return team.getSize();
  }

  @Override
  public boolean addEntry(@NotNull String entry) {
    boolean wasPresent = team.hasEntry(entry);
    if (!wasPresent) {
      team.addEntry(entry);
    }
    return !wasPresent;
  }

  @Override
  public boolean removeEntry(@NotNull String entry) {
    return team.removeEntry(entry);
  }

  @Override
  public boolean hasEntry(@NotNull String entry) {
    return team.hasEntry(entry);
  }

  @Override
  public boolean allowFriendlyFire() {
    return team.allowFriendlyFire();
  }

  @Override
  public void setAllowFriendlyFire(boolean enabled) {
    team.setAllowFriendlyFire(enabled);
  }

  @Override
  public boolean canSeeFriendlyInvisibles() {
    return team.canSeeFriendlyInvisibles();
  }

  @Override
  public void setCanSeeFriendlyInvisibles(boolean enabled) {
    team.setCanSeeFriendlyInvisibles(enabled);
  }

  @Override
  public @NotNull TeamOptionStatus option(@NotNull TeamOption option) {
    return fromBukkit(team.getOption(toBukkit(option)));
  }

  @Override
  public void setOption(@NotNull TeamOption option, @NotNull TeamOptionStatus status) {
    team.setOption(toBukkit(option), toBukkit(status));
  }

  @Override
  public @Nullable Scoreboard scoreboard() {
    org.bukkit.scoreboard.Scoreboard scoreboard = team.getScoreboard();
    return scoreboard == null ? null : BukkitAdapters.adapt(scoreboard);
  }

  @Override
  public void unregister() {
    team.unregister();
  }

  private static org.bukkit.scoreboard.Team.Option toBukkit(@NotNull TeamOption option) {
    return switch (option) {
      case NAME_TAG_VISIBILITY -> org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY;
      case DEATH_MESSAGE_VISIBILITY -> org.bukkit.scoreboard.Team.Option.DEATH_MESSAGE_VISIBILITY;
      case COLLISION_RULE -> org.bukkit.scoreboard.Team.Option.COLLISION_RULE;
    };
  }

  private static org.bukkit.scoreboard.Team.OptionStatus toBukkit(
      @NotNull TeamOptionStatus status) {
    return switch (status) {
      case ALWAYS -> org.bukkit.scoreboard.Team.OptionStatus.ALWAYS;
      case NEVER -> org.bukkit.scoreboard.Team.OptionStatus.NEVER;
      case FOR_OTHER_TEAMS -> org.bukkit.scoreboard.Team.OptionStatus.FOR_OTHER_TEAMS;
      case FOR_OWN_TEAM -> org.bukkit.scoreboard.Team.OptionStatus.FOR_OWN_TEAM;
    };
  }

  private static TeamOptionStatus fromBukkit(
      @NotNull org.bukkit.scoreboard.Team.OptionStatus status) {
    return switch (status) {
      case ALWAYS -> TeamOptionStatus.ALWAYS;
      case NEVER -> TeamOptionStatus.NEVER;
      case FOR_OTHER_TEAMS -> TeamOptionStatus.FOR_OTHER_TEAMS;
      case FOR_OWN_TEAM -> TeamOptionStatus.FOR_OWN_TEAM;
    };
  }

  private static org.bukkit.ChatColor toBukkit(@Nullable NamedTextColor color) {
    if (color == null) {
      return org.bukkit.ChatColor.RESET;
    }
    if (color.equals(NamedTextColor.BLACK)) {
      return org.bukkit.ChatColor.BLACK;
    }
    if (color.equals(NamedTextColor.DARK_BLUE)) {
      return org.bukkit.ChatColor.DARK_BLUE;
    }
    if (color.equals(NamedTextColor.DARK_GREEN)) {
      return org.bukkit.ChatColor.DARK_GREEN;
    }
    if (color.equals(NamedTextColor.DARK_AQUA)) {
      return org.bukkit.ChatColor.DARK_AQUA;
    }
    if (color.equals(NamedTextColor.DARK_RED)) {
      return org.bukkit.ChatColor.DARK_RED;
    }
    if (color.equals(NamedTextColor.DARK_PURPLE)) {
      return org.bukkit.ChatColor.DARK_PURPLE;
    }
    if (color.equals(NamedTextColor.GOLD)) {
      return org.bukkit.ChatColor.GOLD;
    }
    if (color.equals(NamedTextColor.GRAY)) {
      return org.bukkit.ChatColor.GRAY;
    }
    if (color.equals(NamedTextColor.DARK_GRAY)) {
      return org.bukkit.ChatColor.DARK_GRAY;
    }
    if (color.equals(NamedTextColor.BLUE)) {
      return org.bukkit.ChatColor.BLUE;
    }
    if (color.equals(NamedTextColor.GREEN)) {
      return org.bukkit.ChatColor.GREEN;
    }
    if (color.equals(NamedTextColor.AQUA)) {
      return org.bukkit.ChatColor.AQUA;
    }
    if (color.equals(NamedTextColor.RED)) {
      return org.bukkit.ChatColor.RED;
    }
    if (color.equals(NamedTextColor.LIGHT_PURPLE)) {
      return org.bukkit.ChatColor.LIGHT_PURPLE;
    }
    if (color.equals(NamedTextColor.YELLOW)) {
      return org.bukkit.ChatColor.YELLOW;
    }
    if (color.equals(NamedTextColor.WHITE)) {
      return org.bukkit.ChatColor.WHITE;
    }
    throw new IllegalArgumentException("Unsupported named text color: " + color);
  }

  private static @Nullable NamedTextColor fromBukkit(@Nullable org.bukkit.ChatColor color) {
    if (color == null) {
      return null;
    }
    return switch (color) {
      case BLACK -> NamedTextColor.BLACK;
      case DARK_BLUE -> NamedTextColor.DARK_BLUE;
      case DARK_GREEN -> NamedTextColor.DARK_GREEN;
      case DARK_AQUA -> NamedTextColor.DARK_AQUA;
      case DARK_RED -> NamedTextColor.DARK_RED;
      case DARK_PURPLE -> NamedTextColor.DARK_PURPLE;
      case GOLD -> NamedTextColor.GOLD;
      case GRAY -> NamedTextColor.GRAY;
      case DARK_GRAY -> NamedTextColor.DARK_GRAY;
      case BLUE -> NamedTextColor.BLUE;
      case GREEN -> NamedTextColor.GREEN;
      case AQUA -> NamedTextColor.AQUA;
      case RED -> NamedTextColor.RED;
      case LIGHT_PURPLE -> NamedTextColor.LIGHT_PURPLE;
      case YELLOW -> NamedTextColor.YELLOW;
      case WHITE -> NamedTextColor.WHITE;
      default -> null;
    };
  }
}
