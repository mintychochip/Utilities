package org.aincraft.bukkit.adapter;

import org.aincraft.api.domain.scoreboard.Criteria;
import org.aincraft.api.domain.scoreboard.RenderType;
import org.jetbrains.annotations.NotNull;

/** Spigot-backed scoreboard criteria. */
public class BukkitCriteriaWrapper implements Criteria {

  private final org.bukkit.scoreboard.Criteria criteria;

  public BukkitCriteriaWrapper(@NotNull org.bukkit.scoreboard.Criteria criteria) {
    this.criteria = criteria;
  }

  public @NotNull org.bukkit.scoreboard.Criteria getBukkitCriteria() {
    return criteria;
  }

  @Override
  public @NotNull String name() {
    return criteria.getName();
  }

  @Override
  public boolean isReadOnly() {
    return criteria.isReadOnly();
  }

  @Override
  public @NotNull RenderType defaultRenderType() {
    return switch (criteria.getDefaultRenderType()) {
      case INTEGER -> RenderType.INTEGER;
      case HEARTS -> RenderType.HEARTS;
    };
  }
}
