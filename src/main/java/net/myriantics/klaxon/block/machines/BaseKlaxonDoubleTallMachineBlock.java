package net.myriantics.klaxon.block.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;

public abstract class BaseKlaxonDoubleTallMachineBlock extends Block {

    public final Part part;

    public BaseKlaxonDoubleTallMachineBlock(Properties properties, Part part) {
        super(properties);
        this.part = part;
    }

    protected abstract Holder<? extends Block> getCounterpartBlock();

    protected abstract @Nullable BlockState getCounterpartStateForState(BlockState state);

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        @Nullable BlockState desiredCounterpartState = this.getCounterpartStateForState(state);
        if (desiredCounterpartState == null || !this.canPlaceCounterpartAt(placer, level, pos.relative(this.part.counterpartOffsetDirection), desiredCounterpartState)) {
            if (!level.isClientSide()) {
                if (!this.part.isDominant()) {
                    level.setBlock(pos, this.getCounterpartBlock().value().defaultBlockState(), Block.UPDATE_INVISIBLE);
                }
            }
            level.destroyBlock(pos, true);
        } else {
            level.setBlockAndUpdate(pos.relative(this.part.counterpartOffsetDirection), desiredCounterpartState);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, (Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS));
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!level.getBlockState(pos.relative(this.part.counterpartOffsetDirection)).is(this.getCounterpartBlock().value())) {
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public Item asItem() {
        if (this.part.isDominant()) {
            return super.asItem();
        } else {
            return this.getCounterpartBlock().value().asItem();
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        if (this.part.isDominant()) {
            return super.getCloneItemStack(level, pos, state);
        } else {
            BlockPos counterpartPos = pos.relative(this.part.counterpartOffsetDirection);
            return this.getCounterpartBlock().value().getCloneItemStack(level, counterpartPos, level.getBlockState(counterpartPos));
        }
    }

    protected boolean canPlaceCounterpartAt(LivingEntity livingEntity, Level level, BlockPos pos, BlockState state) {
        if (pos.getY() >= level.getMaxBuildHeight()) {
            return false;
        }

        if (!level.getBlockState(pos).canBeReplaced()) {
            return false;
        }

        if (!state.canSurvive(level, pos)) {
            return false;
        }
        CollisionContext collisionContext = livingEntity == null ? CollisionContext.empty() : CollisionContext.of(livingEntity);
        if (!level.isUnobstructed(state, pos, collisionContext)) {
            return false;
        }

        return true;
    }

    public enum Part {
        TOP(Direction.DOWN),
        BOTTOM(Direction.UP);

        public final Direction counterpartOffsetDirection;

        Part(Direction counterpartOffsetDirection) {
            this.counterpartOffsetDirection = counterpartOffsetDirection;
        }

        public boolean isDominant() {
            return this == BOTTOM;
        }
    }
}
