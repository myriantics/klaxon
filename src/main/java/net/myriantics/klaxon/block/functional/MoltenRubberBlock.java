package net.myriantics.klaxon.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonFluidTags;
import org.jetbrains.annotations.Nullable;

public class MoltenRubberBlock extends MagmaBlock {
    public MoltenRubberBlock(Properties settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();

        for (Direction dir : NeighborUpdater.UPDATE_ORDER) {
            if (testForColdness(world, pos.relative(dir), dir)) {
                world.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0);
                return KlaxonBlocks.RUBBER_BLOCK.defaultBlockState();
            }
        }

        return super.getStateForPlacement(ctx);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (!world.isClientSide() && testForColdness(world, neighborPos, direction)) {
            world.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0);
            return KlaxonBlocks.RUBBER_BLOCK.defaultBlockState();
        }

        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    private boolean testForColdness(LevelAccessor world, BlockPos pos, Direction direction) {
        return world.getBlockState(pos).is(KlaxonBlockTags.COLD_BLOCKS) || (!direction.equals(Direction.DOWN) && world.getFluidState(pos).is(KlaxonFluidTags.COLD_FLUIDS));
    }
}
