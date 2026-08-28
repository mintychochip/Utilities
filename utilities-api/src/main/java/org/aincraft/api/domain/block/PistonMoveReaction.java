package org.aincraft.api.domain.block;

/** How a block reacts to being pushed or pulled by a piston. */
public enum PistonMoveReaction {
  NORMAL,
  DESTROY,
  BLOCK,
  IGNORE,
  PUSH_ONLY
}
