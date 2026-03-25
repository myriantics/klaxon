package net.myriantics.klaxon.mechanics.wrench.interaction;

import net.minecraft.core.Direction;

public class BlockFaceInteraction {
    /*
    private final ;
    private final float clickedX;
    private final float clickedY;

     */
    public enum GuiOrientation {
        UP_NORTH(Direction.UP, Direction.NORTH),
        UP_SOUTH(Direction.UP, Direction.SOUTH),
        UP_EAST(Direction.UP, Direction.EAST),
        UP_WEST(Direction.UP, Direction.WEST),
        DOWN_NORTH(Direction.DOWN, Direction.NORTH),
        DOWN_SOUTH(Direction.DOWN, Direction.SOUTH),
        DOWN_EAST(Direction.DOWN, Direction.EAST),
        DOWN_WEST(Direction.DOWN, Direction.WEST),
        NORTH_UP(Direction.NORTH, Direction.UP),
        SOUTH_UP(Direction.SOUTH, Direction.UP),
        EAST_UP(Direction.EAST, Direction.UP),
        WEST_UP(Direction.WEST, Direction.UP);

        private final Direction attachedToFace;
        private final Direction guiUpDir;

        GuiOrientation(Direction attachedToFace, Direction guiUpDir) {
            this.attachedToFace = attachedToFace;
            this.guiUpDir = guiUpDir;
        }

        public Direction getAttachedToFace() {
            return this.attachedToFace;
        }

        public Direction getGuiUpDir() {
            return this.guiUpDir;
        }

        public Direction getFacingDir() {
            return this.attachedToFace.getOpposite();
        }
    }
}
