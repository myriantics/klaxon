package net.myriantics.klaxon.mechanics.wrench;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.util.BlockFaceRegion;
import net.myriantics.klaxon.util.KlaxonMathHelper;
import org.jetbrains.annotations.Nullable;

public sealed abstract class WrenchActionContext permits WrenchActionContext.Manual, WrenchActionContext.Dispenser {
    private final Level level;
    private final BlockState targetState;
    private final BlockPos targetPos;
    private final ItemStack wrenchStack;

    public WrenchActionContext(Level level, BlockState targetState, BlockPos targetPos, ItemStack wrenchStack) {
        this.level = level;
        this.targetState = targetState;
        this.targetPos = targetPos.immutable();
        this.wrenchStack = wrenchStack;
    }

    public Level level() {
        return this.level;
    }

    public BlockState getTargetState() {
        return this.targetState;
    }

    public BlockPos getTargetPos() {
        return this.targetPos;
    }

    public ItemStack getWrenchStack() {
        return this.wrenchStack;
    }

    public @Nullable Entity getUser() {
        return this instanceof Manual manual ? manual.getPlayer() : null;
    }

    public static final class Manual extends WrenchActionContext {
        private final Player player;
        private final BlockHitResult hitResult;
        private final Vec3 clickPosFromCenter;
        private final Vec3 clickPosFromCorner;
        private final InteractionHand hand;
        private final Direction interactedFace;
        private final GuiOrientation orientation;

        public Manual(Level level, BlockState targetState, BlockPos targetPos, ItemStack wrenchStack, Player player, BlockHitResult hitResult, InteractionHand hand) {
            super(level, targetState, targetPos, wrenchStack);
            this.player = player;
            this.hitResult = hitResult;
            this.hand = hand;
            this.clickPosFromCenter = hitResult.getLocation().subtract(targetPos.getCenter());
            this.clickPosFromCorner = hitResult.getLocation().subtract(targetPos.getX(), targetPos.getY(), targetPos.getZ());
            this.interactedFace = targetState.getBlock() instanceof Wrenchable wrenchable ? wrenchable.getManualInteractedFace(targetState, hitResult.getDirection(), this.clickPosFromCenter) : hitResult.getDirection();
            this.orientation = new  GuiOrientation(this.interactedFace, this.interactedFace.getAxis().equals(Direction.Axis.Y) ? player.getMotionDirection() : Direction.UP, clickPosFromCorner, player.getMotionDirection());
        }

        public Player getPlayer() {
            return this.player;
        }

        public BlockHitResult getHitResult() {
            return this.hitResult;
        }

        public InteractionHand getHand() {
            return this.hand;
        }

        public Direction clickedDirection() {
            return this.interactedFace;
        }

        public Vec3 getClickPosFromCenter() {
            return this.clickPosFromCenter;
        }

        public Vec3 getClickPosFromCorner() {
            return clickPosFromCorner;
        }

        public GuiOrientation getGuiOrientation() {
            return orientation;
        }
    }

    public static class GuiOrientation {

        private final Direction facing;
        private final Direction guiUpDir;
        private final Direction.Axis sidesAxis;
        private final float clickedX;
        private final float clickedY;

        GuiOrientation(Direction attachedToFace, Direction guiUpDir, Vec3 fromCorner, Direction playerHorizontalFacing) {
            this.facing = attachedToFace;
            this.guiUpDir = guiUpDir;
            this.sidesAxis = KlaxonMathHelper.neither(attachedToFace.getAxis(), guiUpDir.getAxis());
            this.clickedX = (float) switch (this.facing) {
                case DOWN, UP -> switch (this.guiUpDir) {
                    case SOUTH -> fromCorner.x;
                    case WEST -> 1 - fromCorner.z;
                    case EAST -> fromCorner.z;
                    default -> 1 - fromCorner.x;
                };
                case NORTH -> fromCorner.x;
                case SOUTH -> 1 - fromCorner.x;
                case WEST -> 1 - fromCorner.z;
                case EAST -> fromCorner.z;
            };
            this.clickedY = (float) switch (this.facing) {
                case DOWN, UP -> switch (guiUpDir) {
                    case DOWN -> 1 - fromCorner.y;
                    case UP -> fromCorner.y;
                    case SOUTH -> 1 - fromCorner.z;
                    case WEST -> 1 - fromCorner.x;
                    case EAST -> fromCorner.x;
                    case NORTH -> fromCorner.z;
                };
                case NORTH, EAST, SOUTH, WEST -> fromCorner.y;
            };

            /*
            KlaxonCommon.LOGGER.info("Wrench Click Cords: [{}, {}]", clickedX, clickedY);
            KlaxonCommon.LOGGER.info("Gui Top: [{}], Gui Facing; [{}]", guiUpDir, facing);

             */
        }

        public boolean matches(FrontAndTop frontAndTop) {
            return this.matches(frontAndTop.front(), frontAndTop.top());
        }

        public boolean matches(Direction facing, Direction up) {
            return this.facing.equals(facing) && this.guiUpDir.equals(up);
        }

        public float getClickedX(BlockFaceRegion.Rotation rotation) {
            return switch (rotation) {
                case R0 -> this.clickedX;
                case R90 -> 1 - this.clickedY;
                case R180 -> 1 - this.clickedX;
                case R270 -> this.clickedY;
            };
        }

        public float getClickedY(BlockFaceRegion.Rotation rotation) {
            return switch (rotation) {
                case R0 -> this.clickedY;
                case R90 -> 1 - this.clickedX;
                case R180 -> 1 - this.clickedY;
                case R270 -> this.clickedX;
            };
        }

        public float getClickedX() {
            return this.clickedX;
        }

        public float getClickedY() {
            return this.clickedY;
        }

        public Direction getFacing() {
            return this.facing;
        }

        public Direction getGuiUpDir() {
            return this.guiUpDir;
        }

        public Direction getFacingDir() {
            return this.facing.getOpposite();
        }

        public Direction.Axis getSidesAxis() {
            return sidesAxis;
        }
    }

    public static final class Dispenser extends WrenchActionContext {
        private final Direction dispenserFacing;
        private final BlockSource dispenserBlockSource;

        public Dispenser(Level level, BlockState targetState, BlockPos targetPos, ItemStack wrenchStack, Direction dispenserFacing, BlockSource dispenserBlockSource) {
            super(level, targetState, targetPos, wrenchStack);
            this.dispenserFacing = dispenserFacing;
            this.dispenserBlockSource = dispenserBlockSource;
        }

        public Direction getDispenserFacing() {
            return this.dispenserFacing;
        }

        public BlockSource getDispenserBlockSource() {
            return this.dispenserBlockSource;
        }
    }
}
