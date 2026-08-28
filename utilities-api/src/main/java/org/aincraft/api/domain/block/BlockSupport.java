package org.aincraft.api.domain.block;

/**
 * Controls which blocks count as a full-face support when checking block placement. Corresponds to
 * {@code org.bukkit.block.BlockSupport} in Paper 26.2.
 */
public enum BlockSupport {
  CENTER,
  FULL,
  RIGID
}
