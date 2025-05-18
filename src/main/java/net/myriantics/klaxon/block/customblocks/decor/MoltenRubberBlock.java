package net.myriantics.klaxon.block.customblocks.decor;

import net.minecraft.block.BlockState;
import net.minecraft.block.Degradable;
import net.minecraft.block.MagmaBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.block.NeighborUpdater;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonFluidTags;
import org.jetbrains.annotations.Nullable;

public class MoltenRubberBlock extends MagmaBlock {
    public MoltenRubberBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();

        for (Direction dir : NeighborUpdater.UPDATE_ORDER) {
            if (testForColdness(world, pos.offset(dir))) {
                world.syncWorldEvent(WorldEvents.LAVA_EXTINGUISHED, pos, 0);
                return KlaxonBlocks.RUBBER_BLOCK.getDefaultState();
            }
        }

        return super.getPlacementState(ctx);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!world.isClient() && testForColdness(world, neighborPos)) {
            world.syncWorldEvent(WorldEvents.LAVA_EXTINGUISHED, pos, 0);
            return KlaxonBlocks.RUBBER_BLOCK.getDefaultState();
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private boolean testForColdness(WorldAccess world, BlockPos pos) {
        return world.getBlockState(pos).isIn(KlaxonBlockTags.COLD_BLOCKS) || world.getFluidState(pos).isIn(KlaxonFluidTags.COLD_FLUIDS);
    }
}
