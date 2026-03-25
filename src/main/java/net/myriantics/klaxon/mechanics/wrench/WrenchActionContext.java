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

public sealed abstract class WrenchActionContext permits WrenchActionContext.Manual, WrenchActionContext.Dispenser {
    private final Level level;
    private final BlockState targetState;
    private final BlockPos targetPos;
    private final ItemStack wrenchStack;

    public WrenchActionContext(Level level, BlockState targetState, BlockPos targetPos, ItemStack wrenchStack) {
        this.level = level;
        this.targetState = targetState;
        this.targetPos = targetPos;
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
        private final InteractionHand hand;

        public Manual(Level level, BlockState targetState, BlockPos targetPos, ItemStack wrenchStack, Player player, BlockHitResult hitResult, InteractionHand hand) {
            super(level, targetState, targetPos, wrenchStack);
            this.player = player;
            this.hitResult = hitResult;
            this.hand = hand;
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
