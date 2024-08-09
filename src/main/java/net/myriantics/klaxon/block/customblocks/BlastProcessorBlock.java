package net.myriantics.klaxon.block.customblocks;

import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BlastProcessorBlock extends BlockWithEntity{

    public static final BooleanProperty LIT = BooleanProperty.of("lit");
    public static final BooleanProperty FUELED = BooleanProperty.of("fueled");
    public static final BooleanProperty HATCH_OPEN = BooleanProperty.of("hatch_open");
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");
    public static final DirectionProperty FACING = FacingBlock.FACING;

    public BlastProcessorBlock(Settings settings) {
        super(settings);

        setDefaultState(getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(POWERED, false)
                .with(LIT, false)
                .with(FUELED, false)
                .with(HATCH_OPEN, true));
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BlastProcessorBlockEntity) {
                ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        world.setBlockState(pos, state.with(LIT, false));

    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BlastProcessorBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

            if(screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    // TODO: Replace comparator output with fuel level
    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED, FACING, LIT, FUELED, HATCH_OPEN);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        if (Objects.requireNonNull(context.getPlayer()).isSneaking()) {
            return this.getDefaultState().with(FACING, context.getHorizontalPlayerFacing());
        } else {
            return this.getDefaultState().with(FACING, context.getHorizontalPlayerFacing().getOpposite());
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {

        boolean isPowered = world.isReceivingRedstonePower(pos);
        boolean isActivated = state.get(POWERED);
        if (isPowered && !isActivated) {
            //world.scheduleBlockTick(pos, this, 200);
            if (world.getBlockEntity(pos) instanceof BlastProcessorBlockEntity blastProcessor) {
                blastProcessor.onRedstoneImpulse();
            }
            world.setBlockState(pos, state.with(LIT, true));
            world.setBlockState(pos, state.with(POWERED, true));
        } else if(!isPowered && isActivated) {
            world.setBlockState(pos, state.with(POWERED, false));
        }
    }



    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type, BlockEntityType.BREWING_STAND, BrewingStandBlockEntity::tick);
    }

}
