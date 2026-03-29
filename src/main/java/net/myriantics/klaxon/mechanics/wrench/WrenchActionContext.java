package net.myriantics.klaxon.mechanics.wrench;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

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
            this.orientation = new GuiOrientation(this.interactedFace, this.interactedFace.getAxis().equals(Direction.Axis.Y) ? player.getMotionDirection() : Direction.UP);
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

        GuiOrientation(Direction attachedToFace, Direction guiUpDir) {
            this.facing = attachedToFace;
            this.guiUpDir = guiUpDir;
        }

        public float getClickedX(Vec3 fromCorner) {
            return (float) switch (this.facing) {
                case DOWN -> 1 - fromCorner.x;
                case UP -> 1 - fromCorner.x;
                case NORTH -> fromCorner.x;
                case SOUTH -> 1 - fromCorner.x;
                case WEST -> 1 - fromCorner.z;
                case EAST -> fromCorner.z;
            };
        }

        public float getClickedY(Vec3 fromCorner) {
            return (float) switch (this.facing) {
                case DOWN -> fromCorner.z;
                case UP -> 1 - fromCorner.z;
                case NORTH -> fromCorner.y;
                case SOUTH -> fromCorner.y;
                case WEST -> fromCorner.y;
                case EAST -> fromCorner.y;
            };
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
